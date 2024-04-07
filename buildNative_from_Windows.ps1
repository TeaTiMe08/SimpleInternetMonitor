echo "Setting working dir to current script location: " $PSScriptRoot
cd $PSScriptRoot
echo "Starting Maven Build to single Jar file."
mvn clean install compile package "-Dmaven.tests.skip=true"
echo "Creating target-native dir"
New-Item -ItemType Directory -Force -Path target-native
echo "Cleaning target-native directory"
rm target-native\*
echo "Starting Native Build on Windows."
native-image `
    "-Djava.awt.headless=false" `
    --enable-http `
    --enable-https `
    "-H:ResourceConfigurationFiles=native-resources-configuration.json" `
    -march=native `
    -jar .\target\SimpleInternetMonitor.jar `
    .\target-native\SimpleInternetMonitor
Compress-Archive -Path target-native\* -DestinationPath SimpleInternetMonitor-Windows-bin.zip