#!/bin/bash

# Convert markdown to PDF using pandoc
echo "Converting: Demo.md"
pandoc "DEMO.md" -o "DEMO.docx"
