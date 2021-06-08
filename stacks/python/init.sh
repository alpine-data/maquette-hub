#!/bin/bash

DIR="/opt/data/.mq/.ssh"
if [ -d "$DIR" ]; then
  mv $DIR /root/.ssh
  chmod 400 /root/.ssh/*
fi