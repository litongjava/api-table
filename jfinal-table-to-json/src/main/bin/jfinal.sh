#!/bin/sh
# chkconfig: 345 99 01
# description:jfinal

##########################
# get app home start
###########################
PRG="$0"
while [ -h "$PRG" ] ; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done
##########################
# get app home end
###########################

##########################
# custom variables start
###########################
JAVA_HOME=/usr/java/jdk1.8.0_211
APP_HOME=`dirname "$PRG"`
APP_NAME=`basename "$PRG"`
PID_FILE=$APP_HOME/$APP_NAME.pid
CP=$APP_HOME/boot/*:$APP_HOME/lib/*:$APP_HOME/config:$APP_HOME/static
# 启动入口类，该脚本文件用于别的项目时要改这里
MAIN_CLASS=com.litong.jfinal.Application
# Java 命令行参数，根据需要开启下面的配置，改成自己需要的，注意等号前后不能有空格
# JAVA_OPTS="-Xms256m -Xmx1024m -Dundertow.port=80 -Dundertow.host=0.0.0.0"
# JAVA_OPTS="-Dundertow.port=80 -Dundertow.host=0.0.0.0"
CMD="$JAVA_HOME/bin/java -Xverify:none ${JAVA_OPTS} -cp ${CP} ${MAIN_CLASS}"
###########################
# custom variables end
###########################
source /etc/init.d/functions
#########################
# define funcation start
##########################
if [[ "$MAIN_CLASS" == "com.yourpackage.YourMainClass" ]]; then
echo "请先修改 MAIN_CLASS 的值为你自己项目启动Class，然后再执行此脚本。"
  exit 0
fi
lock_dir=/var/lock/subsys
lock_file=$lock_dir/$APP_NAME
createLockFile(){
  [ -w $lock_dir ] && touch $lock_file
}

start(){
  [ -e $APP_HOME/logs ] || mkdir $APP_HOME/logs -p
  
  if [ -f $PID_FILE ]
  then
    echo 'alread running...'
  else
    echo $CMD
    nohup $CMD >> $APP_HOME/logs/$APP_NAME.log 2>&1 &
    echo $! > $PID_FILE
    createLockFile
    echo_success
  fi
}

stop(){
  if [ -f $PID_FILE ]
  then
    killproc -p $PID_FILE
    rm -f $PID_FILE
    echo_success
  else
    echo 'not running...'
  fi
}

restart(){
  stop
  start
}

status(){
  cat $PID_FILE
}
##########################
# define function end
##########################
ACTION=$1
case $ACTION in
  start)
    start
    ;;
  stop)
    stop
    ;;
  restart)
    restart
    ;;
  status)
    status
    ;;
  *)
    echo usage "{start|stop|restart|status}"
    ;;
esac