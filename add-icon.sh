#!/bin/bash

# Add icon to finCalc.app
# First, save your icon image as icon.png in this directory

if [ ! -f "icon.png" ]; then
    echo "Error: Please save your icon as icon.png in this directory first"
    exit 1
fi

echo "======================================"
echo "Adding icon to finCalc.app"
echo "======================================"

# Create iconset directory
mkdir -p icon.iconset

# Generate all required icon sizes using sips (built-in macOS tool)
sips -z 16 16     icon.png --out icon.iconset/icon_16x16.png
sips -z 32 32     icon.png --out icon.iconset/icon_16x16@2x.png
sips -z 32 32     icon.png --out icon.iconset/icon_32x32.png
sips -z 64 64     icon.png --out icon.iconset/icon_32x32@2x.png
sips -z 128 128   icon.png --out icon.iconset/icon_128x128.png
sips -z 256 256   icon.png --out icon.iconset/icon_128x128@2x.png
sips -z 256 256   icon.png --out icon.iconset/icon_256x256.png
sips -z 512 512   icon.png --out icon.iconset/icon_256x256@2x.png
sips -z 512 512   icon.png --out icon.iconset/icon_512x512.png
sips -z 1024 1024 icon.png --out icon.iconset/icon_512x512@2x.png

# Convert to .icns format
iconutil -c icns icon.iconset -o AppIcon.icns

# Copy to app bundle
cp AppIcon.icns /Applications/finCalc.app/Contents/Resources/

# Clean up
rm -rf icon.iconset

echo "✓ Icon added successfully!"
echo ""
echo "Restart finCalc to see the new icon:"
echo "  1. Quit finCalc if it's running"
echo "  2. Open it again from Applications"
echo ""
echo "======================================"
