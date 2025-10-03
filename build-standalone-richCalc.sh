#!/bin/bash

# Build standalone richCalc app using jpackage

echo "======================================"
echo "Building standalone richCalc.app"
echo "======================================"

# Clean up any previous build
rm -rf richCalc.app
rm -rf /Applications/richCalc.app

# Build the JAR first
echo "Building JAR with dependencies..."
mvn clean package -DskipTests

# JavaFX module paths
JAVAFX_MODS="$HOME/.m2/repository/org/openjfx"

# Create module path with all JavaFX JARs
MODULE_PATH="$JAVAFX_MODS/javafx-base/21.0.1/javafx-base-21.0.1-mac-aarch64.jar:\
$JAVAFX_MODS/javafx-controls/21.0.1/javafx-controls-21.0.1-mac-aarch64.jar:\
$JAVAFX_MODS/javafx-graphics/21.0.1/javafx-graphics-21.0.1-mac-aarch64.jar:\
$JAVAFX_MODS/javafx-fxml/21.0.1/javafx-fxml-21.0.1-mac-aarch64.jar"

# Use jpackage to create the app
echo "Creating standalone app with jpackage..."
jpackage \
  --input target \
  --name richCalc \
  --main-jar finCalc-1.0.0.jar \
  --main-class com.finCalc.ui.CalculatorApp \
  --type app-image \
  --module-path "$MODULE_PATH" \
  --add-modules javafx.controls,javafx.fxml \
  --icon AppIcon.icns \
  --app-version 1.0 \
  --vendor "Richard" \
  --dest .

# Move to Applications
if [ -d "richCalc.app" ]; then
    echo "Moving richCalc.app to /Applications..."
    mv richCalc.app /Applications/
    echo "✓ richCalc.app installed successfully!"
    echo "You can now launch it from /Applications/richCalc.app"
else
    echo "✗ Failed to create richCalc.app"
    exit 1
fi
