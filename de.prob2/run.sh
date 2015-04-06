#!/bin/bash

if [ -z "$NW_PATH" ]
then
    echo "Please set \$NW_PATH to your node webkit binary"
    
else
    $NW_PATH resources/public/
fi
