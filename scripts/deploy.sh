#!/usr/bin/env bash

REPOSITORY=/home/ec2-user/apps

echo "> check applicatoin pid"

CURRENT_PID=$(pgrep -fla java | grep maru | awk '{print $1}')

echo "Running application pid: $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
  echo "No running application. We don't kill that pid"
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> new application deploy"

JAR_NAME=$(ls -tr $REPOSITORY/*SNAPSHOT.jar | tail -n 1)

echo "> JAR NAME: $JAR_NAME"

echo "> add a role to $JAR_NAME"

chmod +x $JAR_NAME

echo "> running $JAR_NAME"

nohup env $(cat ~/apps/.env | xargs) java -jar $JAR_NAME >> $REPOSITORY/nohup.out 2>&1 &
