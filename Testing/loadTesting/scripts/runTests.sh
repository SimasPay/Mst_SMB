#!/bin/bash
/home/hudson/mfino_install/jakarta-jmeter-2.5.1/bin



for i in {1..10000}
do
	sh /home/hudson/mfino_install/jakarta-jmeter-2.5.1/bin/jmeter -n -t /home/hudson/loadTesting/tests/test.jmx

done
