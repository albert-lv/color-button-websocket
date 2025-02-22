#!/bin/bash

# 应用配置
APP_DIR="/opt/color-button-app"
JAR_NAME="color-button-websocket-0.0.1-SNAPSHOT.jar"
LOG_FILE="$APP_DIR/app.log"
PID_FILE="$APP_DIR/app.pid"

# JVM 配置
JAVA_OPTS="-Xms256m -Xmx512m -XX:+HeapDumpOnOutOfMemoryError"
SPRING_PROFILE="prod"

# 检查是否以root用户运行
check_root() {
    if [ "$(id -u)" != "0" ]; then
        echo "请使用root用户或sudo运行此脚本"
        exit 1
    fi
}

# 检查应用状态
check_status() {
    if [ -f "$PID_FILE" ]; then
        pid=$(cat "$PID_FILE")
        if ps -p $pid > /dev/null; then
            echo "应用正在运行，PID: $pid"
            return 0
        else
            echo "应用未运行（PID文件存在但进程不存在）"
            rm "$PID_FILE"
            return 1
        fi
    else
        echo "应用未运行"
        return 1
    fi
}

# 启动应用
start() {
    echo "正在启动应用..."
    
    # 检查应用是否已经运行
    if [ -f "$PID_FILE" ]; then
        pid=$(cat "$PID_FILE")
        if ps -p $pid > /dev/null; then
            echo "应用已经在运行中，PID: $pid"
            return 1
        else
            rm "$PID_FILE"
        fi
    fi

    # 确保目录存在
    mkdir -p "$APP_DIR"
    
    # 启动应用
    nohup java $JAVA_OPTS \
        -Dspring.profiles.active=$SPRING_PROFILE \
        -jar "$APP_DIR/$JAR_NAME" > "$LOG_FILE" 2>&1 &

    # 保存PID
    echo $! > "$PID_FILE"
    
    echo "应用启动成功，PID: $(cat $PID_FILE)"
    echo "日志文件位置: $LOG_FILE"
}

# 停止应用
stop() {
    echo "正在停止应用..."
    
    if [ -f "$PID_FILE" ]; then
        pid=$(cat "$PID_FILE")
        if ps -p $pid > /dev/null; then
            echo "正在停止PID为 $pid 的应用..."
            kill $pid
            
            # 等待进程停止
            count=0
            while ps -p $pid > /dev/null; do
                sleep 1
                ((count++))
                if [ $count -gt 30 ]; then
                    echo "应用未响应，强制终止..."
                    kill -9 $pid
                    break
                fi
            done
            
            echo "应用已停止"
        else
            echo "应用未运行"
        fi
        rm "$PID_FILE"
    else
        echo "PID文件不存在，应用可能未运行"
    fi
}

# 重启应用
restart() {
    echo "正在重启应用..."
    stop
    sleep 2
    start
}

# 查看日志
view_logs() {
    if [ -f "$LOG_FILE" ]; then
        tail -f "$LOG_FILE"
    else
        echo "日志文件不存在"
    fi
}

# 使用说明
usage() {
    echo "使用方法: $0 {start|stop|restart|status|logs}"
    echo "  start   - 启动应用"
    echo "  stop    - 停止应用"
    echo "  restart - 重启应用"
    echo "  status  - 查看应用状态"
    echo "  logs    - 查看应用日志"
}

# 主逻辑
case "$1" in
    start)
        check_root
        start
        ;;
    stop)
        check_root
        stop
        ;;
    restart)
        check_root
        restart
        ;;
    status)
        check_status
        ;;
    logs)
        view_logs
        ;;
    *)
        usage
        exit 1
        ;;
esac

exit 0
