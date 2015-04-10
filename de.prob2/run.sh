#!/bin/bash

P=`pwd`

if [ -z "$NW_PATH" ]
then
    echo "Please set \$NW_PATH to your node webkit app"
    
else
    open "$NW_PATH" --args "$P/resources/public/"
fi
