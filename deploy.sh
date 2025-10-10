#!/bin/bash

echo "🚀 Building app..."
./mvnw clean package -DskipTests || { echo "❌ Build failed"; exit 1; }

SERVER_IP=34.56.57.56      # IP VPS Google Cloud
USER=truon                 # Username trên VPS
KEY_PATH=/c/Users/truon/.ssh/id_ed25519
REMOTE_TMP=/home/$USER/tmp_deploy
REMOTE_DIR=/var/www/be

echo "📦 Deploying files to server..."
# Tạo thư mục tạm trước khi upload
ssh -i "$KEY_PATH" $USER@$SERVER_IP "mkdir -p $REMOTE_TMP"
# Upload file .jar
scp -i "$KEY_PATH" target/be.jar $USER@$SERVER_IP:$REMOTE_TMP/ || { echo "❌ SCP failed"; exit 1; }

echo "🔗 Connecting to server..."
ssh -i "$KEY_PATH" $USER@$SERVER_IP <<EOF
set -e

echo "🧹 Preparing directories..."
sudo mkdir -p $REMOTE_DIR
sudo mv $REMOTE_TMP/be.jar $REMOTE_DIR/be.jar

pid=\$(sudo lsof -t -i:8080)
if [ -z "\$pid" ]; then
    echo "▶ Starting server..."
else
    echo "🔁 Restarting server (\$pid)..."
    sudo kill -9 "\$pid"
fi

cd $REMOTE_DIR
echo "🚀 Running JAR..."
nohup sudo java -jar be.jar > app.log 2>&1 & disown

echo "✅ Server started successfully."
exit
EOF

echo "🎉 Done!"
