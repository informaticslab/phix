#!/usr/bin/bash

#
# Creates a binary distribution of the ELR Translation Tool
# in the current directory, using the Subversion repository
# and a staging directory.
#
# Use Cygwin to run this bash shell script under Windows.
#

BUILD_NAME=ELR_1_0_0_1
SVN_REPO=~/c/PHIX/phix-src/trunk
WORK_DIR=`pwd`

echo "Building $BUILD_NAME"
echo "SVN_REPO: $SVN_REPO"
echo "Working directory: $WORK_DIR"

# create staging directory
echo "CREATING STAGING DIRECTORY $BUILD_NAME ..."
mkdir $BUILD_NAME

# PHIX services
echo "COPYING PHIX SERVICES..."
mkdir $BUILD_NAME/services
cp $SVN_REPO/ComponentRoutingService/dist/ComponentRoutingService.war $BUILD_NAME/services/
cp $SVN_REPO/StructuralValidationService/dist/StructuralValidationService.war $BUILD_NAME/services/

# databases
echo "COPYING DATABASES..."
mkdir $BUILD_NAME/db
cp $SVN_REPO/db/hub_elr.backup $BUILD_NAME/db/

# documentation
echo "COPYING DOCUMENTATION..."
mkdir $BUILD_NAME/doc/
cp $SVN_REPO/doc/elr/* $BUILD_NAME/doc/

# mirth channels
echo "COPYING MIRTH CHANNELS..."
mkdir $BUILD_NAME/mirth_channels
cp $SVN_REPO/mirth_channels/elr/*.xml $BUILD_NAME/mirth_channels/

# copy demo structure and source messages
echo "COPYING SOURCE MESSAGES / DEMO STRUCTURE..."
mkdir -p $BUILD_NAME/demo/phixdata/{HL7_ERROR,HL7_IN,HL7_PROCESSED,HL7_TRANSFORMED,logs,elr\ src\ msgs}
cp $SVN_REPO/demo/elr\ src\ msgs/*.txt $BUILD_NAME/demo/phixdata/elr\ src\ msgs/

# Apache v2 license file
echo "COPYING LICENSES..."
cp $SVN_REPO/../../LICENSE.txt $BUILD_NAME
cp $SVN_REPO/../../NOTICE.txt $BUILD_NAME

# create zip file
echo "CREATING ZIP FILE $BUILD_NAME.zip ..."
zip $BUILD_NAME.zip -r $BUILD_NAME

# delete staging directory
echo "DELETING STAGING DIRECTORY..."
rm -rf $BUILD_NAME
