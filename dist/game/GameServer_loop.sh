
#!/bin/bash

# exit codes of GameServer:
#  0 normal shutdown
#  2 reboot attempt

export LD_PRELOAD=$PWD/active_pr64.so
export INTPTR=29023
export GSID=1
export INTIPADDR=127.0.0.1

while :; do
	[ -f log/java0.log.0 ] && mv log/java0.log.0 "log/`date +%Y-%m-%d_%H-%M-%S`_java.log"
	[ -f log/stdout.log ] && mv log/stdout.log "log/`date +%Y-%m-%d_%H-%M-%S`_stdout.log"
	java -server -Dfile.encoding=UTF-8 -Xmx5G -cp config/xml:./../libs/*:core.jar l2r.gameserver.GameServer > log/stdout.log 2>&1
	[ $? -ne 2 ] && break
#	/etc/init.d/mysql restart
	sleep 2
done
