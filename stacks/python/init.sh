#!/bin/bash

DIR="/opt/data/.mq/.ssh"
if [ -d "$DIR" ]; then
  mv $DIR /root/.ssh
fi