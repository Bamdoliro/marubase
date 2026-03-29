#!/usr/bin/env bash
mkdir -p /home/ubuntu/apps

aws ssm get-parameters-by-path \
  --path "/maru" \
  --with-decryption \
  --region ap-northeast-2 \
  --query "Parameters[*].[Name,Value]" \
  --output text | \
while read key value; do
  echo "${key##*/}=$value"
done > /home/ubuntu/apps/.env

echo "Environment variables installed successfully"
