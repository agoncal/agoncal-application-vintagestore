#!/bin/bash

# Create the PDF output directory if it doesn't exist
PDF_DIR="../pdf"
mkdir -p "$PDF_DIR"

# Find all markdown files in the current directory and subdirectories
find . -name "*.md" -type f | while read -r md_file; do
    # Get the relative path and filename without extension
    relative_path="${md_file#./}"
    filename=$(basename "$md_file" .md)
    dir_path=$(dirname "$relative_path")

    # Create corresponding directory structure in PDF folder
    if [ "$dir_path" != "." ]; then
        mkdir -p "$PDF_DIR/$dir_path"
        output_pdf="$PDF_DIR/$dir_path/$filename.pdf"
    else
        output_pdf="$PDF_DIR/$filename.pdf"
    fi

    # Convert markdown to PDF using pandoc
    echo "Converting: $md_file -> $output_pdf"
    pandoc "$md_file" -o "$output_pdf" \
       --pdf-engine=pdflatex \
       -V geometry:margin=0.5in \
       -V fontsize=12pt \
       -V mainfont="Times New Roman" \
       -V monofont="Courier New" \
       -V colorlinks=true \
       -V linkcolor=blue \
       -V urlcolor=blue

    if [ $? -eq 0 ]; then
        echo "✓ Successfully converted: $filename.md"
    else
        echo "✗ Failed to convert: $filename.md"
    fi
done

echo "Conversion complete! PDFs saved in $PDF_DIR"
