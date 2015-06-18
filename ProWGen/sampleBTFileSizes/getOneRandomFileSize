#!/bin/bash
if [ $# -ne 1 ]
then
echo "Syntax: $0 FILE"
echo $0 - display a random line from FILE.
exit 1
fi
RAND=`cat /proc/sys/kernel/random/uuid | cut -c1-4 | od -d | head -1 | cut -d' ' -f2`
LINES=`cat "$1" | wc -l`
LINE=`expr $RAND % $LINES + 1`
head -$LINE $1 | tail -1
