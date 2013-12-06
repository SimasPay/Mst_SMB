#!/bin/bash
OUTPUT_DIR=/tmp/escrow/mFinoSource/phase2

if [ ! -d "$OUTPUT_DIR" ]; then
	mkdir -p $OUTPUT_DIR 
fi

# Remove the existing code.
clean_source()
{
	echo ""
	local item_output_dir=$OUTPUT_DIR/$1
	if [ -d "$item_output_dir" ]; then
		echo "Deleting $item_output_dir"
		rm -rf $item_output_dir 
	fi
}

# App ToCopy IGNORE_PATTERN
copy_source(){
	local app=$1
	local ToCopy=`echo "$2"`
	local ignore_pattern=$3
	echo "Copying $app to $OUTPUT_DIR/$app"

	# find will result in full path from the current directory
	# cpio -d will create leading directories if required. 

	for item in ${ToCopy[@]}
	do
		exec `find $app/$item -depth | grep -v $ignore_pattern | cpio -pmd $OUTPUT_DIR` 
	done

}

#Copy Backend
APP=mFinoMultiXTpmServer
ToCopy=(
	'*.pem'
	'MultiXTpm.wsdl'
	'TpmConfig.xml' 
	'include'
	'DBManager'
	'FIXMessageProcessor'
	'FIXMLCodeGenerator'
	'gSoapMobileAgentAPI'
	'ISO8583AcquirerGatewayFE'
	'ISO8583Shared'
	'mFinoDBCreator'
	'mFinoFIXServer'
	'mFinoHTTPServer'
	'mFinoMailer'
	'mFinoMobileBackEndServer'
	'mFinoMobileOperatorGatewayInterface'
	'mFinoShared'
	'mFinoTransfersManager'
	)
IGNORE_PATTERN="-e \.svn -e \.[cs|vc]proj$ -e \.pro$ -e \.cs -e \.make -e \.log$ -e \.pidb$ -e \.sln -e Makefile -e Debug -e Release" 
array_to_pass=`echo ${ToCopy[@]}`
clean_source $APP
copy_source $APP "$array_to_pass" "$IGNORE_PATTERN"

#Copy Core
APP=Core
ToCopy=( 
	'src' 
	'settings'
	)
IGNORE_PATTERN="-e \.svn" 
array_to_pass=`echo ${ToCopy[@]}`
clean_source $APP
copy_source $APP "$array_to_pass" "$IGNORE_PATTERN"

#Copy Web
APP=Web/AdminApplication
ToCopy=( 'src' 'web')
IGNORE_PATTERN="-e \.svn" 
array_to_pass=`echo ${ToCopy[@]}`
clean_source $APP
copy_source $APP "$array_to_pass" "$IGNORE_PATTERN"

#Copy webapi
APP=Web/webapi
ToCopy=( 'src' 'web')
IGNORE_PATTERN="-e \.svn -e test" 
array_to_pass=`echo ${ToCopy[@]}`
clean_source $APP
copy_source $APP "$array_to_pass" "$IGNORE_PATTERN"

#Copy Scheduler
APP=Web/Scheduler
ToCopy=( 'src' 'web')
IGNORE_PATTERN="-e \.svn -e test" 
array_to_pass=`echo ${ToCopy[@]}`
clean_source $APP
copy_source $APP "$array_to_pass" "$IGNORE_PATTERN"

#Copy Tools
APP=Tools/JavaScriptFileConcatenationTool
ToCopy=( 'src')
IGNORE_PATTERN="-e \.svn" 
array_to_pass=`echo ${ToCopy[@]}`
clean_source $APP
copy_source $APP "$array_to_pass" "$IGNORE_PATTERN"

#Copy CreditCardPayment
APP=Tools/CreditCardPayment
ToCopy=( 'src')
IGNORE_PATTERN="-e \.svn -e test" 
array_to_pass=`echo ${ToCopy[@]}`
clean_source $APP
copy_source $APP "$array_to_pass" "$IGNORE_PATTERN"

#Copy SMSProxy
APP=Tools/SMSProxy
ToCopy=( 'src')
IGNORE_PATTERN="-e \.svn -e test" 
array_to_pass=`echo ${ToCopy[@]}`
clean_source $APP
copy_source $APP "$array_to_pass" "$IGNORE_PATTERN"

#Copy DataPushServer
APP=Tools/DataPushServer
ToCopy=( 'src' 'web')
IGNORE_PATTERN="-e \.svn -e test" 
array_to_pass=`echo ${ToCopy[@]}`
clean_source $APP
copy_source $APP "$array_to_pass" "$IGNORE_PATTERN"



