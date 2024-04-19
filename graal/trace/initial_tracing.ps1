echo "Setting working dir to current script location: " $PSScriptRoot
cd $PSScriptRoot
echo "Creating tracing dir"
New-Item -ItemType Directory -Force -Path .\windows
java  -Dfile.encoding=UTF-8 -agentlib:native-image-agent=config-output-dir=.\windows\. -jar ..\..\target\SimpleInternetMonitor.jar
# For multi time tracing start
java  -Dfile.encoding=UTF-8 -agentlib:native-image-agent=config-merge-dir=.\windows\. -jar ..\..\target\SimpleInternetMonitor.jar
