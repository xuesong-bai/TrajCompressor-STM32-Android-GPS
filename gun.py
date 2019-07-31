#!/usr/bin/env python
# -*- coding: utf-8 -*-
import gevent.monkey
gevent.monkey.patch_all()
import multiprocessing

debug = True
loglevel = 'debug'
bind = '0.0.0.0:10086'
accesslog = './file_and_log/gunicorn_access.log'
errorlog = './file_and_log/gunicorn_error.log'
timeout = 600
#启动的进程数"
workers = 2
worker_class = 'gevent'

x_forwarded_for_header = 'X-FORWARDED-FOR'
#gunicorn -c gun.py app:app
