#!/bin/bash

# Create a simple macOS app launcher for finCalc

echo "======================================"
echo "Creating finCalc.app launcher"
echo "======================================"

# Create the app bundle structure
APP_DIR="/Applications/finCalc.app"
rm -rf "$APP_DIR"
mkdir -p "$APP_DIR/Contents/MacOS"
mkdir -p "$APP_DIR/Contents/Resources"

# Create the launcher script that uses mvn javafx:run
cat > "$APP_DIR/Contents/MacOS/finCalc" << 'EOF'
#!/bin/bash

# finCalc Launcher - runs via Maven
cd /Users/richard/projects/finCalc

# Run using Maven JavaFX plugin (no output window)
mvn javafx:run > /dev/null 2>&1 &
EOF

# Make launcher executable
chmod +x "$APP_DIR/Contents/MacOS/finCalc"

# Create Info.plist
cat > "$APP_DIR/Contents/Info.plist" << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>CFBundleExecutable</key>
    <string>finCalc</string>
    <key>CFBundleIdentifier</key>
    <string>com.finCalc</string>
    <key>CFBundleName</key>
    <string>finCalc</string>
    <key>CFBundleDisplayName</key>
    <string>finCalc</string>
    <key>CFBundleVersion</key>
    <string>1.0.0</string>
    <key>CFBundleShortVersionString</key>
    <string>1.0.0</string>
    <key>CFBundlePackageType</key>
    <string>APPL</string>
    <key>LSMinimumSystemVersion</key>
    <string>10.14</string>
    <key>NSHighResolutionCapable</key>
    <true/>
    <key>CFBundleIconFile</key>
    <string>AppIcon</string>
</dict>
</plist>
EOF

echo "✓ finCalc.app created successfully!"
echo ""
echo "Launch with: open /Applications/finCalc.app"
echo "Or use Spotlight: Cmd+Space, type 'finCalc'"
echo ""
echo "======================================"
