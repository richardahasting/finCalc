#!/bin/bash
# Launch finCalc Electron app in production mode

cd "$(dirname "$0")" || exit 1

# Check if node_modules exists
if [ ! -d "node_modules" ]; then
    echo "Installing dependencies..."
    npm install
fi

# Build for Electron (uses relative paths)
if [ ! -d "dist" ] || [ "src/App.tsx" -nt "dist/index.html" ]; then
    echo "Building..."
    ELECTRON=true npm run build
fi

# Run Electron in production mode
NODE_ENV=production npx electron .
