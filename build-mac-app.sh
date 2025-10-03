#!/bin/bash

# Build macOS Application Bundle for finCalc
# This script creates a native macOS .app that can be launched from the desktop

set -e  # Exit on error

echo "======================================"
echo "Building finCalc macOS Application"
echo "======================================"
echo ""

# Step 1: Clean and build the JAR
echo "Step 1: Building JAR with Maven..."
mvn clean package -DskipTests

# Check if JAR was created
if [ ! -f "target/finCalc-1.0.0-SNAPSHOT.jar" ]; then
    echo "Error: JAR file not found!"
    exit 1
fi

echo "✓ JAR built successfully"
echo ""

# Step 2: Create the app bundle using jpackage
echo "Step 2: Creating macOS app bundle..."

# Remove old app if exists
rm -rf target/finCalc.app

# Create the .app bundle
jpackage \
    --input target \
    --name finCalc \
    --main-jar finCalc-1.0.0-SNAPSHOT.jar \
    --main-class com.finCalc.ui.CalculatorApp \
    --type app-image \
    --dest target \
    --app-version 1.0.0 \
    --vendor "Richard" \
    --description "Financial Calculator for Real Estate and Investment Analysis" \
    --mac-package-name finCalc \
    --icon src/main/resources/icon.icns 2>/dev/null || \
    jpackage \
        --input target \
        --name finCalc \
        --main-jar finCalc-1.0.0-SNAPSHOT.jar \
        --main-class com.finCalc.ui.CalculatorApp \
        --type app-image \
        --dest target \
        --app-version 1.0.0 \
        --vendor "Richard" \
        --description "Financial Calculator for Real Estate and Investment Analysis" \
        --mac-package-name finCalc

if [ ! -d "target/finCalc.app" ]; then
    echo "Error: App bundle not created!"
    exit 1
fi

echo "✓ App bundle created successfully"
echo ""

# Step 3: Copy to Applications folder (optional)
echo "Step 3: Installation options"
echo ""
echo "The finCalc.app has been created in: target/finCalc.app"
echo ""
echo "To install finCalc to your Applications folder, run:"
echo "  cp -r target/finCalc.app /Applications/"
echo ""
echo "Or drag target/finCalc.app to your Applications folder in Finder."
echo ""
echo "======================================"
echo "Build Complete!"
echo "======================================"
