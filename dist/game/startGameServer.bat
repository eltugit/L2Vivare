@mode con:cols=150 lines=800
@echo off
COLOR 0b
title L2jGabDev Game Server Console
:start
echo Starting L2J Game Server.
echo.

REM innodb_buffer_pool_size=5G
REM max_connections=2000 
REM my.config e no seu game server tbm

set JAVA_OPTS=%JAVA_OPTS% -Xmn2048m
set JAVA_OPTS=%JAVA_OPTS% -Xms4096m
set JAVA_OPTS=%JAVA_OPTS% -Xmx8192m

set JAVA_OPTS=%JAVA_OPTS% -Xnoclassgc
set JAVA_OPTS=%JAVA_OPTS% -XX:+AggressiveOpts
set JAVA_OPTS=%JAVA_OPTS% -XX:TargetSurvivorRatio=90
set JAVA_OPTS=%JAVA_OPTS% -XX:SurvivorRatio=16
set JAVA_OPTS=%JAVA_OPTS% -XX:MaxTenuringThreshold=12
set JAVA_OPTS=%JAVA_OPTS% -XX:+UseParNewGC
set JAVA_OPTS=%JAVA_OPTS% -XX:+UseConcMarkSweepGC

set JAVA_OPTS=%JAVA_OPTS% -XX:UseSSE=3
set JAVA_OPTS=%JAVA_OPTS% -XX:+UseFastAccessorMethods

java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5065 -server -Dfile.encoding=UTF-8 -Xmx16G -XX:+UseConcMarkSweepGC -XX:+UseTLAB -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:./gc.log -cp config/xml;./../libs/*;core.jar l2r.gameserver.GameServer

if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Server restarted ...
echo.
goto start
:error
echo.
echo Server terminated abnormaly ...
echo.
:end
echo.
echo Server terminated ...
echo.