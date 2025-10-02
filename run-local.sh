#!/bin/bash

echo "🚀 Job Hunter Bot - Local Runner"
echo "================================"

# Check if .env file exists
if [ ! -f .env ]; then
    echo "❌ .env file not found. Please create one from .env.example"
    exit 1
fi

echo "✅ Found .env configuration"
echo "🔍 Starting job search..."

# Run in API-only mode for faster local execution
export API_ONLY_MODE=true
export DEBUG_MODE=true

./gradlew smartApply --no-daemon --console=plain

echo ""
echo "📊 Job search complete! Check the output/ directory for results."
echo "📁 Generated files:"
ls -la output/ | grep "$(date +%Y-%m-%d)" 2>/dev/null || echo "   No files generated today"