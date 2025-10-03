#!/bin/bash

# Install finCalc to Applications folder

echo "======================================"
echo "Installing finCalc"
echo "======================================"
echo ""

if [ ! -d "target/finCalc.app" ]; then
    echo "Error: finCalc.app not found in target/"
    echo "Please run ./build-mac-app.sh first"
    exit 1
fi

echo "Copying finCalc.app to /Applications/..."
cp -r target/finCalc.app /Applications/

if [ -d "/Applications/finCalc.app" ]; then
    echo "✓ Successfully installed!"
    echo ""
    echo "You can now:"
    echo "  • Launch finCalc from Spotlight (Cmd+Space, type 'finCalc')"
    echo "  • Find it in your Applications folder"
    echo "  • Add it to your Dock by dragging from Applications"
    echo ""
else
    echo "✗ Installation failed"
    exit 1
fi

echo "======================================"
