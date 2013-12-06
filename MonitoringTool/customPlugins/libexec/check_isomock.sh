#!/bin/bash

JPOSLOGLOC=/home/mfino/mfino_install/servicemix-4.4.0/data/log/jpos.log
WARNING=5
CRITICAL=10

START=$(date +%s)
END=`stat --format=%X $JPOSLOGLOC` 

DIFF=$(($START-$END))


if [ $DIFF -gt 100 ]; then 
	NUMERRS=`tail $JPOSLOGLOC -n 1000 | grep "field id=\"39\" value=\"06\"" | wc -l`
	echo "NUMERRS = $NUMERRS"
	
	#if [ $NUMERRS -lt $WARNING ]; then
	#	echo "ISOMock is running normally"
	#else if [ $NUMERRS -gt $WARNING ] && [ $NUMERRS -lt $CRITICAL ]; then
	#	echo "ISOMock is in Warning State"
	#else
	#	echo "ISOMock is in critical state and needs attention"
	#fi

	#Normal Status
	[ $NUMERRS -lt $WARNING ] && echo "ISOMock is running normally" && exit 0
	
	#Warning Status
	[ $NUMERRS -gt $WARNING ] && [ $NUMERRS -lt $CRITICAL ] && echo "ISOMock is in Warning State" && exit 1

	#Critical Status
	[ $NUMERRS -gt $CRITICAL ] &&  echo "ISOMock is in critical state and needs attention"&& exit 2
else
	echo "ISOMock is running normally"
fi
