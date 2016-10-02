#!/bin/sh

cd ..

#################################################
# Build the classpath with ESLS bundled libraries.
#################################################
PS=":"
CLASSPATH="etc:lib/*"
echo "CLASSPATH="${CLASSPATH}

status=0;
######################################################################
# check migtool
######################################################################
 JAVA_PID=`ps -eafo pid,ppid,comm,args | grep jiraslatool | grep java | awk '{ print $"1" }'`
 JAVA_PPID=`ps -eafo pid,ppid,comm,args | grep jiraslatool | grep java | awk '{ print $"2" }'`
 if [ $JAVA_PID ]
   then
     echo "Jira SLA tool is running.. ( PUID: " $JAVA_PID ")";
 else
    status=1;
 fi


if [ $status -ne 1 ]
then
        echo "Jira SLA tool is runnign please kill it before restarting";
        exit 1
fi


######################################################################
# Lets kill any java process already running with the command jiraslatool
######################################################################
 JAVA_PID=`ps -eafo pid,ppid,comm,args | grep jiraslatool | grep java | awk '{ print $"1" }'`
 JAVA_PPID=`ps -eafo pid,ppid,comm,args | grep jiraslatool | grep java | awk '{ print $"2" }'`
 if [ $JAVA_PID ] 
   then
   if [ $JAVA_PPID -eq 1 ]
   then
      echo "Going to kill hanging process of Location Server with process id : " $JAVA_PID;
      kill -9 $JAVA_PID
      echo "Killed.";
   fi
 fi

#####################################################
# run
#####################################################

VMARGS="-Downer=jiraslatool -Xms128M -Xmx1G -d64 -Djava.io.tmpdir=./tmp"
VMGC="-XX:NewRatio=4 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -Dsun.rmi.dgc.client.gcinterval=600000 "
VMGC2="-XX:+CMSParallelRemarkEnabled -XX:+DisableExplicitGC"
VMGCPRINT="-verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:-PrintTenuringDistribution"
java $VMARGS $VMGC $VMGC2 \
  -cp $CLASSPATH \
   com.main.Main 