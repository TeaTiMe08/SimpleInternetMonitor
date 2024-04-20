echo "Setting working dir to current script location: " $PSScriptRoot
cd $PSScriptRoot
echo "Copying over Windows traces"
Copy-Item -Path .\windows\*.json -Destination ..\..\\src\main\resources\META-INF\native-image\
echo "Starting Maven Build to single Jar file."
cd ..\..\
mvn -B -ntp clean install compile package "-Djava.awt.headless=false" "-Dmaven.tests.skip=true" "-Dorg.slf4j.simpleLogger.defaultLogLevel=WARN"
cd $PSScriptRoot
# Merge new config to already existing META-INF
java "-Dfile.encoding=UTF-8" -agentlib:native-image-agent=config-merge-dir=.\windows\. -jar ..\..\target\SimpleInternetMonitor.jar
echo "Ended Merging Session, now ready to commit new traces at graal/trace/windows"