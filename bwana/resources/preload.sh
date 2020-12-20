#!/bin/bash
let r=1
for i in $*
do
strid=`identify -size 1280x1024 $i`
set $strid
x=${3%x*}
y=${3#*x}
echo "Image${r}= new Image(${x},${y});
Image${r}.src = \"$i\";
"
let r=$r+1
done

