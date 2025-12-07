#!/bin/bash

# Exit on error
set -e

# -------------------------------
# 🧩 Variables
# -------------------------------
DOWNLOAD_URL=$1                # The tar file download link (pass as argument)
DOWNLOAD_FILE="/tmp/react_dist.tar.gz"
EXTRACT_PATH="/home/ubuntu/apps/dist"
REACT_CONF="/home/ubuntu/react.conf"
NGINX_CONF="/etc/nginx/sites-available/react.conf"
NGINX_LINK="/etc/nginx/sites-enabled/react.conf"
APP_DEST="/var/www/react"

# -------------------------------
# 🚀 Step 0: Validate input
# -------------------------------
if [ -z "$DOWNLOAD_URL" ]; then
    echo "❌ ERROR: No download URL provided."
    echo "Usage: ./deploy.sh <download_url>"
    exit 1
fi

echo "Starting React app deployment..."
echo "Download link: $DOWNLOAD_URL"

# -------------------------------
# 1️⃣ Install Nginx if not installed
# -------------------------------
if ! command -v nginx &> /dev/null; then
    echo "Installing Nginx..."
    sudo apt-get update -y
    sudo apt-get install nginx -y
fi

# -------------------------------
# 2️⃣ Download tar file from S3 or external URL
# -------------------------------
echo "Downloading build file..."
wget -O "$DOWNLOAD_FILE" "$DOWNLOAD_URL"

# -------------------------------
# 3️⃣ Extract the tar file
# -------------------------------
echo "Extracting tar file..."
mkdir -p "$EXTRACT_PATH"
rm -rf "$EXTRACT_PATH"/*
tar -xzf "$DOWNLOAD_FILE" -C "$EXTRACT_PATH"

echo "Extraction complete."

# -------------------------------
# 4️⃣ Move build to Nginx web root
# -------------------------------
echo "Deploying build to $APP_DEST..."
sudo mkdir -p "$APP_DEST"
sudo rm -rf "$APP_DEST"/*
sudo cp -rT "$EXTRACT_PATH" "$APP_DEST"

# -------------------------------
# 5️⃣ Set up Nginx config
# -------------------------------
echo "Configuring Nginx..."
sudo cp "$REACT_CONF" "$NGINX_CONF"

# Enable the new site
if [ ! -L "$NGINX_LINK" ]; then
    sudo ln -s "$NGINX_CONF" "$NGINX_LINK"
fi

# Remove default Nginx site if exists
if [ -f /etc/nginx/sites-enabled/default ]; then
    sudo rm /etc/nginx/sites-enabled/default
fi

# -------------------------------
# 6️⃣ Test and reload Nginx
# -------------------------------
echo "Testing Nginx configuration..."
sudo nginx -t

echo "Reloading Nginx..."
sudo systemctl restart nginx

# -------------------------------
# ✅ Done
# -------------------------------
echo "✅ Deployment complete! React app should be accessible via your EC2 IP."



