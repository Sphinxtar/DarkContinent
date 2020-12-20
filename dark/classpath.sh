# description: Linus Sphinx's Dark Continent
# processname: dark
# config: $DARK/bwana.properties

# Source function library.
export HERE=`pwd`
export TOMCAT_HOME=/usr/share/tomcat
export JVM_HOME=/usr/lib/jvm
unset CLASSPATH
CLASSPATH=${JVM_HOME}/java-11-openjdk-11.0.9.11-0.fc33.x86_64/lib/jrt-fs.jar
for i in ${TOMCAT_HOME}/lib/*.jar ; do
    CLASSPATH=${CLASSPATH}:$i
done
export CLASSPATH=${CLASSPATH}:${HERE}
