#!/usr/bin/env bash

# Execute this script to upload the PDF to Azure Storage.

printf "%s\n" "-----------------------------------"
printf "%s\n" "Setting up environment variables..."
printf "%s\n" "-----------------------------------"
export UNIQUE_IDENTIFIER=${GITHUB_USER:-$(whoami)}
export PROJECT="vintagestorefoundry"
export RESOURCE_GROUP="rg-$PROJECT"
export LOCATION="swedencentral"
export TAG="$PROJECT"

# Storage
export STORAGE_ACCOUNT="st$PROJECT"
export STORAGE_CONTAINER="$PROJECT"

# PDF files path
PDF_PATH=~/Documents/Code/AGoncal/agoncal-application-vintagestore/rag/src/main/resources/pdf

printf "%s\n" "-------------------------------"
printf "%s\n" "Upload PDFs to Azure Storage..."
printf "%s\n" "-------------------------------"
az storage blob upload \
  --account-name "$STORAGE_ACCOUNT" \
  --container-name "$STORAGE_CONTAINER" \
  --name acceptable-use-policy.pdf \
  --file "$PDF_PATH/acceptable-use-policy.pdf" \
  --auth-mode login

az storage blob upload \
  --account-name "$STORAGE_ACCOUNT" \
  --container-name "$STORAGE_CONTAINER" \
  --name disclaimer.pdf \
  --file "$PDF_PATH/disclaimer.pdf" \
  --auth-mode login

az storage blob upload \
  --account-name "$STORAGE_ACCOUNT" \
  --container-name "$STORAGE_CONTAINER" \
  --name end-user-license-agreement.pdf \
  --file "$PDF_PATH/end-user-license-agreement.pdf" \
  --auth-mode login

az storage blob upload \
  --account-name "$STORAGE_ACCOUNT" \
  --container-name "$STORAGE_CONTAINER" \
  --name privacy.pdf \
  --file "$PDF_PATH/privacy.pdf" \
  --auth-mode login

az storage blob upload \
  --account-name "$STORAGE_ACCOUNT" \
  --container-name "$STORAGE_CONTAINER" \
  --name terms.pdf \
  --file "$PDF_PATH/terms.pdf" \
  --auth-mode login









