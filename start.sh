#!/bin/bash
set -e
touch /file_and_log/gunicorn_access.log
touch /file_and_log/gunicorn_error.log

echo Starting...
# 为了使更改过的环境变量生效
source ~/.bash_profile
# gunicorn -c gun.py app:app
