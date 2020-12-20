# description: Linus Sphinx's Dark Continent
# processname: dark
# config: $DARK/bwana.properties
export DARK=/home/linus/workspace/dark
export CMDO=/home/linus/workspace/commando
export TOMCAT_HOME=/usr/share/tomcat
export JAVA_HOME=/usr/lib/jvm/java
unset CLASSPATH
CLASSPATH=/usr/lib/jvm/java-11-openjdk-11.0.9.11-0.fc33.x86_64/lib/jrt-fs.jar
for i in ${TOMCAT_HOME}/lib/*.jar ; do
    CLASSPATH=${CLASSPATH}:$i
done
export CLASSPATH=${CLASSPATH}:${DARK}:${CMDO}

case "$1" in
  start)
    	if [[ -f /tmp/dark ]]
	then
		echo "Dark Continent already running."
		exit 0
	fi
    	echo -n "Starting Dark Continent: "
	cd $DARK
	$JAVA_HOME/bin/java -jar dark.jar -server -cp $CLASSPATH &
	# $JAVA_HOME/bin/jdb -classpath $CLASSPATH -sourcepath `pwd` -launch dc
    	touch /tmp/dark
    ;;
  stop)
    echo -n "Stopping Dark Continent: "
	cd $CMDO
	$JAVA_HOME/bin/java -client -cp $CLASSPATH commandline quit 2&>1 /dev/null
    rm -f /tmp/dark
    ;;
  status)
    echo "status unknown"
    ;;
  restart)
        $0 stop
        $0 start
    ;;
  *)
    echo "Usage: dark {start|stop|status|restart}"
    ;;
esac
echo "done"
exit 0
