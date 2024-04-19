echo "Setting working dir to current script location: " $PSScriptRoot
cd $PSScriptRoot
# Merge new config to already existing META-INF
java "-Dfile.encoding=UTF-8" -agentlib:native-image-agent=config-merge-dir=.\windows\. -jar ..\..\target\SimpleInternetMonitor.jar
