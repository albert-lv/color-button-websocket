#!/bin/bash

# 配置变量
REMOTE_USER="root"
REMOTE_HOST="120.77.57.14"
REMOTE_DIR="/opt/color-button-app"
APP_NAME="color-button-websocket"
JAR_NAME="color-button-websocket-0.0.1-SNAPSHOT.jar"

# 检查参数
if [ -z "$REMOTE_USER" ] || [ "$REMOTE_USER" = "your_username" ] || \
   [ -z "$REMOTE_HOST" ] || [ "$REMOTE_HOST" = "your_server_ip" ]; then
    echo "错误: 请先在脚本中配置 REMOTE_USER 和 REMOTE_HOST"
    exit 1
fi

# 构建应用
echo "正在构建应用..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "构建失败！"
    exit 1
fi

# 确保远程目录存在
echo "正在创建远程目录..."
ssh $REMOTE_USER@$REMOTE_HOST "sudo mkdir -p $REMOTE_DIR && sudo chown $REMOTE_USER:$REMOTE_USER $REMOTE_DIR"

# 复制文件到远程服务器
echo "正在复制文件到远程服务器..."
scp target/$JAR_NAME $REMOTE_USER@$REMOTE_HOST:$REMOTE_DIR/
scp service.sh $REMOTE_USER@$REMOTE_HOST:$REMOTE_DIR/

# 设置权限
echo "正在设置权限..."
ssh $REMOTE_USER@$REMOTE_HOST "sudo chmod +x $REMOTE_DIR/service.sh"

# 停止旧的应用实例
echo "正在停止现有应用..."
ssh $REMOTE_USER@$REMOTE_HOST "cd $REMOTE_DIR && sudo ./service.sh stop"

# 启动新的应用实例
echo "正在启动新应用..."
ssh $REMOTE_USER@$REMOTE_HOST "cd $REMOTE_DIR && sudo ./service.sh start"

# 检查应用状态
echo "正在检查应用状态..."
ssh $REMOTE_USER@$REMOTE_HOST "cd $REMOTE_DIR && ./service.sh status"

echo "部署完成！"
echo "使用以下命令可以管理应用："
echo "  查看状态: ssh $REMOTE_USER@$REMOTE_HOST 'cd $REMOTE_DIR && ./service.sh status'"
echo "  查看日志: ssh $REMOTE_USER@$REMOTE_HOST 'cd $REMOTE_DIR && ./service.sh logs'"
echo "  重启应用: ssh $REMOTE_USER@$REMOTE_HOST 'cd $REMOTE_DIR && sudo ./service.sh restart'"
