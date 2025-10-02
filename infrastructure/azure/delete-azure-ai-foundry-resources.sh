#!/usr/bin/env bash

# Execute this script to delete the Azure AI Foundry resources used by the application.
# For this, you need Azure CLI installed: https://learn.microsoft.com/cli/azure/install-azure-cli


echo "----------------------------------"
echo "Setting up environment variables..."
echo "----------------------------------"
export UNIQUE_IDENTIFIER=${GITHUB_USER:-$(whoami)}
export PROJECT="vintagestore$UNIQUE_IDENTIFIER"
export RESOURCE_GROUP="rg-$PROJECT"
export AZURE_AI_FOUNDRY_NAME="ai-$PROJECT"


echo "Deleting Azure AI Foundry..."
echo "------------------------------"
az cognitiveservices account delete \
  --resource-group "$RESOURCE_GROUP" \
  --name "$AZURE_AI_FOUNDRY_NAME"

az cognitiveservices account purge \
  --resource-group "$RESOURCE_GROUP" \
  --location "$LOCATION" \
  --name "$AZURE_AI_FOUNDRY_NAME"


echo "Deleting the resource group..."
echo "------------------------------"
az group delete \
  --name "$RESOURCE_GROUP" \
  --yes
