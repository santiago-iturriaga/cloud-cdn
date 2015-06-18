#!/bin/sh 

echo `wc -l $1 | awk '{print $1}'`

