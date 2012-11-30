#!/usr/bin/bash

#
# Creates a binary distribution of the PHIX product
# in the current directory, using the Subversion repository
# and a staging directory.
#
# Use Cygwin to run this bash shell script under Windows.
#

BUILD_NAME=PHIX_1_3_1_0
SVN_REPO=~/c/PHIX/phix-src/trunk
WORK_DIR=`pwd`

echo "Building $BUILD_NAME"
echo "SVN_REPO: $SVN_REPO"
echo "Working directory: $WORK_DIR"

# create staging directory
echo "CREATING STAGING DIRECTORY $BUILD_NAME ..."
mkdir $BUILD_NAME

# NOTE: mss removed from binary package by CR012, Jan. 2012.

# mss, jboss, phix svcs
#echo "EXTRACTING MSS, JBOSS, PHIX SERVICES..."
#cd $SVN_REPO/dependencies/mss/
#unzip mss.zip
#mv mss35 $WORK_DIR/$BUILD_NAME
#cd $WORK_DIR

# PHIX services
echo "COPYING PHIX SERVICES..."
mkdir $BUILD_NAME/services
cp $SVN_REPO/ComponentRoutingService/dist/ComponentRoutingService.war $BUILD_NAME/services/
cp $SVN_REPO/StructuralValidationService/dist/StructuralValidationService.war $BUILD_NAME/services/

# databases
echo "COPYING DATABASES..."
mkdir $BUILD_NAME/db
cp $SVN_REPO/db/hub.backup $BUILD_NAME/db/
cp $SVN_REPO/db/BioSenseLinker.backup $BUILD_NAME/db/

# documentation
echo "COPYING DOCUMENTATION..."
mkdir $BUILD_NAME/doc
cp $SVN_REPO/doc/*.docx $BUILD_NAME/doc/
cp $SVN_REPO/doc/*.pdf $BUILD_NAME/doc/

# mirth channels
echo "COPYING MIRTH CHANNELS..."
mkdir -p $BUILD_NAME/mirth_channels/test
cp $SVN_REPO/mirth_channels/01_PHIX*xml $BUILD_NAME/mirth_channels/
cp $SVN_REPO/mirth_channels/test/*.xml $BUILD_NAME/mirth_channels/test/

# copy demo structure and source messages
echo "COPYING SOURCE MESSAGES / DEMO STRUCTURE..."
mkdir -p $BUILD_NAME/demo/phixdata/{doc,HL7_ERROR,HL7_IN,HL7_PROCESSED,HL7_TRANSFORMED,logs,src\ msgs}
cp $SVN_REPO/demo/doc/*.xlsx $BUILD_NAME/demo/phixdata/doc/
cp $SVN_REPO/demo/src\ msgs/*.txt $BUILD_NAME/demo/phixdata/src\ msgs/

# DIRECT REST
echo "COPYING DIRECT REST..."
cp -r $SVN_REPO/dependencies/direct/nhin-d-rest $BUILD_NAME/.

# DIRECT REST - MIRTH CONNECT INTERFACE 
echo "COPYING DIRECT REST - MIRTH CONNECT INTERFACE ..."
mkdir -p $BUILD_NAME/DirectRESTIface/lib
cp $SVN_REPO/DirectRESTIface/lib/*.jar $BUILD_NAME/DirectRESTIface/lib/

# Linker
echo "COPYING LINKER..."
mkdir $BUILD_NAME/Linker
cp -r $SVN_REPO/dependencies/Linker/Tomcat $BUILD_NAME/Linker/

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
