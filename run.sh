#!/bin/bash
LOCATION1="./sageBackdoorClient.jar"
LOCATION2="./sageBackdoorClient.jar"
if [ -f "$LOCATION1" ]
then
    java -jar $LOCATION1
else
    java -jar $LOCATION2
fi
