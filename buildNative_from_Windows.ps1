echo "Setting working dir to current script location: " $PSScriptRoot
cd $PSScriptRoot
echo "Starting Maven Build to single Jar file."
mvn -B -ntp clean install compile package "-Djava.awt.headless=false" "-Dmaven.tests.skip=true" "-Dorg.slf4j.simpleLogger.defaultLogLevel=WARN"
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