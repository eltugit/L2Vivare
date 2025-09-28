#!/bin/bash

err=1
until [ $err == 0 ]; 
do
	[ -f log/java0.log.0 ] && mv log/java0.log.0 "log/`date +%Y-%m-%d_%H-%M-%S`_java.log"
	[ -f log/stdout.log ] && mv log/stdout.log "log/`date +%Y-%m-%d_%H-%M-%S`_stdout.log"
	java -server -Dfile.encoding=UTF-8 -Xmx900m -cp config/xml:./../libs/*:login.jar l2r.loginserver.L2LoginServer > log/stdout.log 2>&1
	err=$?
#	/etc/init.d/mysql restart
	sleep 2;
done
