#!/bin/bash
echo making servlet
echo old:
ls -l bwana.class
rm bwana.class
. ./classpath.sh
javac -deprecation bwana.java
echo new:
ls -l bwana.class
unset CLASSPATH
cp bwana.class WEB-INF/classes/
echo making war
rm dc.war
touch dc.war
jar uvf dc.war bwana.css bwana.js favicon.ico index.html login.css login.html login.js resources survival.txt bwana.xsl xml2html.xsl WEB-INF/web.xml WEB-INF/classes/bwana.class META-INF/context.xml 
echo done
