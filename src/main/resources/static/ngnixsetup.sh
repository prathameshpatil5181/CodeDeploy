#!/bin/bash

# Exit on error
set -e

# Variables
REACT_DIST="/home/ubuntu/apps/dist"
REACT_CONF="/home/ubuntu/react.conf"
NGINX_CONF="/etc/nginx/sites-available/react.conf"
NGINX_LINK="/etc/nginx/sites-enabled/react.conf"
APP_DEST="/var/www/react"


echo " Deploying React app..."

# 1️⃣ Install Nginx if not installed
if ! command -v nginx &> /dev/null; then
    echo "Installing Nginx..."
    sudo apt-get update -y
    sudo apt-get install nginx -y
fi

# 2️⃣ Copy React build to /var/www/react
echo "Moving app to destination..."
sudo mkdir -p "$APP_DEST"
sudo rm -rf "$APP_DEST"/*   # clean old build
sudo cp -rT "$REACT_DIST" "$APP_DEST"


#Set permissions for React build folder
#echo "Setting permissions for $REACT_DIST..."
# Ensure directories are accessible
#sudo find "$REACT_DIST" -type d -exec chmod 755 {} \;
# Ensure files are readable
#sudo find "$REACT_DIST" -type f -exec chmod 644 {} \;
# Change group to www-data so Nginx can read
#sudo chown -R ubuntu:www-data "$REACT_DIST"



# Copy React Nginx config
echo "Setting up Nginx config..."
# Make sure your react.conf is in current folder
sudo cp "$REACT_CONF" "$NGINX_CONF"

# Enable site
if [ ! -L "$NGINX_LINK" ]; then
    sudo ln -s "$NGINX_CONF" "$NGINX_LINK"
fi

# Remove default site if exists
if [ -f /etc/nginx/sites-enabled/default ]; then
    sudo rm /etc/nginx/sites-enabled/default
fi

# 4️⃣ Test Nginx config
echo "Testing Nginx configuration..."
sudo nginx -t

# 5️⃣ Reload Nginx
echo "Reloading Nginx..."
sudo systemctl restart nginx

echo "✅ React app should now be accessible via EC2 IP."


