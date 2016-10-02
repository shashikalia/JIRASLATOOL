@echo off

echo "starting Jira SLA Tool "
cd ..
java -Xms1G -Xmx1G -cp conf;lib/* com.main.Main

echo "Task completed"
@echo on