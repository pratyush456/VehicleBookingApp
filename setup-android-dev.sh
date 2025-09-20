#!/bin/bash

echo "🚀 Setting up Android Development Environment..."

# Install Homebrew if not installed
if ! command -v brew &> /dev/null; then
    echo "Installing Homebrew..."
    /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
fi

# Install Java
echo "Installing Java JDK..."
brew install openjdk@11

# Add Java to PATH
echo 'export PATH="/opt/homebrew/opt/openjdk@11/bin:$PATH"' >> ~/.zshrc
export PATH="/opt/homebrew/opt/openjdk@11/bin:$PATH"

# Install Android Studio
echo "Installing Android Studio..."
brew install --cask android-studio

# Install Android SDK command line tools
echo "Installing Android SDK..."
brew install android-sdk

echo "✅ Setup complete!"
echo ""
echo "Next steps:"
echo "1. Restart your terminal (close and reopen)"
echo "2. Run: source ~/.zshrc"
echo "3. Open Android Studio and complete the setup wizard"
echo "4. Open your project: /Users/pooja/VehicleBookingApp"
echo ""
echo "🎯 Then you can test the 'View My Bookings' feature!"