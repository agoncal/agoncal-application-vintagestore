#!/usr/bin/env bash

# Execute this script to deploy the needed Microsoft Foundry resources to execute the application.
# For this, you need Azure CLI installed: https://learn.microsoft.com/cli/azure/install-azure-cli
# If already installed, check the version with `az --version` and make sure it is up to date with `az upgrade`
# Check the resource providers that are installed with `az provider list --query "[?registrationState=='Registered'].{Namespace:namespace,State:registrationState}" --output table`
# Register the CognitiveServices provider if it's not registered with `az provider register --namespace 'Microsoft.CognitiveServices'`
# Register the Redis provider if it's not registered with `az provider register --namespace 'Microsoft.Cache'`
# Register the Azure AI Search provider if it's not registered with `az provider register --namespace 'Microsoft.Search'`

# Following the Abbreviation recommendations for Azure resources https://learn.microsoft.com/en-us/azure/cloud-adoption-framework/ready/azure-best-practices/resource-abbreviations

# Check the following environment variables before executing the script:
# AZURE_SUBSCRIPTION_ID

printf "%s\n" "-----------------------------------"
printf "%s\n" "Setting up environment variables..."
printf "%s\n" "-----------------------------------"
export UNIQUE_IDENTIFIER=${GITHUB_USER:-$(whoami)}
export PROJECT="vintagestorefoundry"
export RESOURCE_GROUP="rg-$PROJECT"
export LOCATION="swedencentral" # check https://learn.microsoft.com/azure/ai-foundry/reference/region-support
export TAG="$PROJECT"

# Microsoft Foundry
export MICROSOFT_FOUNDRY="ai-$PROJECT"
export MICROSOFT_FOUNDRY_IQ="iq-$PROJECT"

# Storage
export STORAGE_ACCOUNT="st$PROJECT"
export STORAGE_CONTAINER="$PROJECT"

# Redis
export REDIS_NAME="redis-$PROJECT"

# Chat Model
export CHAT="vintagestore-chat"
export CHAT_DEPLOYMENT="$CHAT-model"
export CHAT_MODEL_FORMAT="OpenAI"
export CHAT_MODEL_NAME="gpt-5-chat"
export CHAT_MODEL_VERSION="2025-08-07"
export CHAT_SKU_CAPACITY="10"
export CHAT_SKU_NAME="GlobalStandard"

# Moderation
# Check Azure-AI-Content-Safety
export MODERATION="vintagestore-moderation"
export MODERATION_DEPLOYMENT="$MODERATION-model"
export MODERATION_MODEL_FORMAT="Microsoft"
export MODERATION_MODEL_NAME="Phi-4"
export MODERATION_MODEL_VERSION="7"
export MODERATION_SKU_CAPACITY="1"
export MODERATION_SKU_NAME="GlobalStandard"

# Embedding
export EMBEDDING="vintagestore-embedding"
export EMBEDDING_DEPLOYMENT="$EMBEDDING-model"
export EMBEDDING_MODEL_FORMAT="Cohere"
export EMBEDDING_MODEL_NAME="Cohere-embed-v3-english"
export EMBEDDING_MODEL_VERSION="1"
export EMBEDDING_SKU_CAPACITY="1"
export EMBEDDING_SKU_NAME="GlobalStandard"

# Query Router
export QUERY_ROUTER="vintagestore-query-router"
export QUERY_ROUTER_DEPLOYMENT="$EMBEDDING-model"
export QUERY_ROUTER_MODEL_FORMAT="Meta"
export QUERY_ROUTER_MODEL_NAME="Llama-4-Maverick-17B-128E-Instruct-FP8"
export QUERY_ROUTER_MODEL_VERSION="1"
export QUERY_ROUTER_SKU_CAPACITY="1"
export QUERY_ROUTER_SKU_NAME="GlobalStandard"

# Summarization
export SUMMARIZATION="vintagestore-summarization"
export SUMMARIZATION_DEPLOYMENT="$SUMMARIZATION-model"
export SUMMARIZATION_MODEL_FORMAT="Mistral AI"
export SUMMARIZATION_MODEL_NAME="mistral-small-2503"
export SUMMARIZATION_MODEL_VERSION="1"
export SUMMARIZATION_SKU_CAPACITY="1"
export SUMMARIZATION_SKU_NAME="GlobalStandard"

# Setting verbose to true will display extra information
verbose=false

#printf "\n%s\n" "Logging in..."
#printf "%s\n"   "-------------"
#az login


if [ "$verbose" = true ]; then
    printf "\n%s\n" "Checking the Azure account..."
    printf "%s\n"   "-----------------------------"
    az account show
    az account subscription list
fi

export SUBSCRIPTION_ID=$(
    az account show --query "id" -o tsv
)
echo "SUBSCRIPTION_ID=$SUBSCRIPTION_ID"



if [ "$verbose" = true ]; then
    printf "\n%s\n" "Displaying Azure locations..."
    printf "%s\n"   "-----------------------------"
    az account list-locations \
      --query "sort_by([].{Name:name, DisplayName:displayName, RegionalDisplayName:regionalDisplayName}, &Name)" --output table
fi


printf "\n%s\n" "Creating the resource group..."
printf "%s\n"   "------------------------------"
az group create \
  --name "$RESOURCE_GROUP" \
  --location "$LOCATION" \
  --tags system="$TAG"


printf "\n%s\n" "Creating the Redis cache..."
printf "%s\n"   "---------------------------"
az redis create \
  --name "$REDIS_NAME" \
  --resource-group "$RESOURCE_GROUP" \
  --location "$LOCATION" \
  --tags system="$TAG" \
  --sku Basic \
  --vm-size c0

REDIS_HOSTNAME=$(
  az redis show \
    --name "$REDIS_NAME" \
    --resource-group "$RESOURCE_GROUP" \
    --query "hostName" \
    --output tsv
)
echo "REDIS_HOSTNAME=$REDIS_HOSTNAME"


printf "\n%s\n" "Creating the Azure Storage..."
printf "%s\n"   "-----------------------------"
az storage account create \
  --name "$STORAGE_ACCOUNT" \
  --resource-group "$RESOURCE_GROUP" \
  --location "$LOCATION" \
  --sku Standard_RAGRS \
  --encryption-services blob \
  --kind BlobStorage \
  --allow-blob-public-access \
  --public-network-access Enabled \

az ad signed-in-user show --query id -o tsv | az role assignment create \
    --role "Storage Blob Data Contributor" \
    --assignee @- \
    --scope "/subscriptions/$SUBSCRIPTION_ID/resourceGroups/$RESOURCE_GROUP/providers/Microsoft.Storage/storageAccounts/$STORAGE_ACCOUNT"

az storage container create \
    --account-name "$STORAGE_ACCOUNT" \
    --name "$STORAGE_CONTAINER" \
    --public-access blob \
    --auth-mode login


printf "\n%s\n" "Creating the AI Search Service..."
printf "%s\n"   "---------------------------------"
az search service create \
  --name "$MICROSOFT_FOUNDRY_IQ" \
  --resource-group "$RESOURCE_GROUP" \
  --location "$LOCATION" \
  --sku Standard \
  --partition-count 1 \
  --replica-count 1


echo "Storing the key and endpoint in environment variables..."
echo "--------------------------------------------------------"
AZURE_SEARCH_ENDPOINT="https://$MICROSOFT_FOUNDRY_IQ.search.windows.net"
AZURE_SEARCH_KEY=$(
    az search admin-key show \
      --service-name "$MICROSOFT_FOUNDRY_IQ" \
      --resource-group "$RESOURCE_GROUP" \
      | jq -r .primaryKey
)

echo "AZURE_SEARCH_ENDPOINT=$AZURE_SEARCH_ENDPOINT"
echo "AZURE_SEARCH_KEY=$AZURE_SEARCH_KEY"


if [ "$verbose" = true ]; then
    printf "\n%s\n" "Checking the SKUs..."
    printf "%s\n"   "--------------------"
    az cognitiveservices account list-skus --location "$LOCATION" --output table
fi


printf "\n%s\n" "Creating Microsoft Foundry service..."
printf "%s\n"   "------------------------------------"
az cognitiveservices account create \
  --resource-group "$RESOURCE_GROUP" \
  --location "$LOCATION" \
  --name "$MICROSOFT_FOUNDRY" \
  --custom-domain "$MICROSOFT_FOUNDRY" \
  --kind AIServices \
  --sku S0

if [ "$verbose" = true ]; then
    printf "\n%s\n" "Checking all the available models..."
    printf "%s\n"   "------------------------------------"
    az cognitiveservices account list-models \
      --resource-group "$RESOURCE_GROUP" \
      --name "$MICROSOFT_FOUNDRY" \
      --query "sort_by(@, &format)[].{Format:format,Name:name,Version:version,Sku:skus[0].name,Capacity:skus[0].capacity.default}" \
      --output table
fi


printf "\n%s\n" "Deploying the Chat model..."
printf "%s\n"   "---------------------------"
az cognitiveservices account deployment create \
  --resource-group "$RESOURCE_GROUP" \
  --name "$MICROSOFT_FOUNDRY" \
  --deployment-name "$CHAT_DEPLOYMENT" \
  --model-format "$CHAT_MODEL_FORMAT" \
  --model-name "$CHAT_MODEL_NAME" \
  --model-version "$CHAT_MODEL_VERSION" \
  --sku-capacity "$CHAT_SKU_CAPACITY" \
  --sku-name "$CHAT_SKU_NAME"


printf "\n%s\n" "Deploying the Moderation model..."
printf "%s\n"   "---------------------------------"
az cognitiveservices account deployment create \
  --resource-group "$RESOURCE_GROUP" \
  --name "$MICROSOFT_FOUNDRY" \
  --deployment-name "$MODERATION_DEPLOYMENT" \
  --model-format "$MODERATION_MODEL_FORMAT" \
  --model-name "$MODERATION_MODEL_NAME" \
  --model-version "$MODERATION_MODEL_VERSION" \
  --sku-capacity "$MODERATION_SKU_CAPACITY" \
  --sku-name "$MODERATION_SKU_NAME"


printf "\n%s\n" "Deploying the Embedding model..."
printf "%s\n"   "--------------------------------"
az cognitiveservices account deployment create \
  --resource-group "$RESOURCE_GROUP" \
  --name "$MICROSOFT_FOUNDRY" \
  --deployment-name "$EMBEDDING_DEPLOYMENT" \
  --model-format "$EMBEDDING_MODEL_FORMAT" \
  --model-name "$EMBEDDING_MODEL_NAME" \
  --model-version "$EMBEDDING_MODEL_VERSION" \
  --sku-capacity "$EMBEDDING_SKU_CAPACITY" \
  --sku-name "$EMBEDDING_SKU_NAME"

printf "\n%s\n" "Deploying the Query Router model..."
printf "%s\n"   "--------------------------------"
az cognitiveservices account deployment create \
  --resource-group "$RESOURCE_GROUP" \
  --name "$MICROSOFT_FOUNDRY" \
  --deployment-name "$QUERY_ROUTER_DEPLOYMENT" \
  --model-format "$QUERY_ROUTER_MODEL_FORMAT" \
  --model-name "$QUERY_ROUTER_MODEL_NAME" \
  --model-version "$QUERY_ROUTER_MODEL_VERSION" \
  --sku-capacity "$QUERY_ROUTER_SKU_CAPACITY" \
  --sku-name "$QUERY_ROUTER_SKU_NAME"


printf "\n%s\n" "Deploying the Summarization model..."
printf "%s\n"   "------------------------------------"
az cognitiveservices account deployment create \
  --resource-group "$RESOURCE_GROUP" \
  --name "$MICROSOFT_FOUNDRY" \
  --deployment-name "$SUMMARIZATION_DEPLOYMENT" \
  --model-format "$SUMMARIZATION_MODEL_FORMAT" \
  --model-name "$SUMMARIZATION_MODEL_NAME" \
  --model-version "$SUMMARIZATION_MODEL_VERSION" \
  --sku-capacity "$SUMMARIZATION_SKU_CAPACITY" \
  --sku-name "$SUMMARIZATION_SKU_NAME"


printf "\n%s\n" "Displaying environment variables..."
printf "%s\n"   "-----------------------------------"
export MICROSOFT_FOUNDRY_KEY=$(az cognitiveservices account keys list \
  --name "$MICROSOFT_FOUNDRY" \
  --resource-group "$RESOURCE_GROUP" \
  --query "key1" \
  --output tsv)

# Appending `models` at the end of the URL
export MICROSOFT_FOUNDRY_ENDPOINT=$(az cognitiveservices account show \
  --name "$MICROSOFT_FOUNDRY" \
  --resource-group "$RESOURCE_GROUP" \
  --query "properties.endpoint" \
  --output tsv)models

echo "MICROSOFT_FOUNDRY_KEY=$MICROSOFT_FOUNDRY_KEY"
echo "MICROSOFT_FOUNDRY_ENDPOINT=$MICROSOFT_FOUNDRY_ENDPOINT"
