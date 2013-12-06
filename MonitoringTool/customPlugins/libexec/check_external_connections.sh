#!/bin/bash

if [ $# -ne 6 ]; then
	echo "Usage: $0 -c <localhost_name> -p <port> -s <Service>" ; exit 2;
fi


while getopts c:p:s: OPTION
do
	case ${OPTION} in
	c) LOCALHOST=${OPTARG};;
	p) PORT=${OPTARG};;
	s) SERVICE=${OPTARG};;
	\?) echo "Usage: $0 -c <localhost_name> -p <port> -s <Service>" ; exit 2;;
	esac
done

PID=`ps ax | grep $SERVICE | grep -v $0 |grep -v grep | cut -d' ' -f 1`


#echo "PID= $PID"
#echo "CONNCOUNT= $CONNCOUNT"


CRITICAL=0
 
#CONNCOUNT=`/usr/sbin/lsof -p $PID | grep $LOCALHOST:$PORT | grep ESTABLISHED | wc -l`
cmd1=`/usr/sbin/lsof -p $PID`
cmd2=`echo $cmd1 | grep $LOCALHOST:$PORT`
cmd3=`echo $cmd2 | grep ESTABLISHED`
CONNCOUNT=`echo $cmd3 | wc -l`

if [ $CONNCOUNT -gt $CRITICAL ]; then
	echo "Service is Up and running"
fi

if [ $CONNCOUNT -le $CRITICAL ]; then
	echo "Service is Down"
fi


#Normal Status
	[ $CONNCOUNT -gt $CRITICAL ]  && exit 0
	
#Critical Status
	[ $CONNCOUNT -le $CRITICAL ]  && exit 2
