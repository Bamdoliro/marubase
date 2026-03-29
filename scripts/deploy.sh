#!/usr/bin/env bash
REPOSITORY=/home/ubuntu/apps

echo "> check application pid"
CURRENT_PID=$(pgrep -fla java | grep maru | awk '{print $1}')

if [ -z "$CURRENT_PID" ]; then
  echo "No running application."
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> new application deploy"
JAR_NAME=$(ls -tr $REPOSITORY/*SNAPSHOT.jar | tail -n 1)

echo "> JAR NAME: $JAR_NAME"
chmod +x $JAR_NAME

echo "> running $JAR_NAME"
nohup env $(cat $REPOSITORY/.env | xargs) \
  java -Xmx768m \
  -Dspring.profiles.active=prod \
  -jar $JAR_NAME >> $REPOSITORY/nohup.out 2>&1 &
