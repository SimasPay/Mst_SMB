#!/bin/bash

help(){
	echo "this is help"
	
}

WARNING=60
CRITICAL=90

let CCC=40

let COUNT=`ps ax | grep ISOJPOSServer | wc -l`

if [ $COUNT -gt 1 ]; then
	echo "ISO Mock is running"
	CCC=40;
else
	echo "ISO Mock is not running"
	CCC=95;
fi

#Normal Status
[ $CCC -lt $WARNING ] && exit 0  

#Warning Status
[ $CCC -lt $CRITICAL ] && exit 1

#Critical Status
[ $CCC -gt $CRITICAL ] && exit 2
