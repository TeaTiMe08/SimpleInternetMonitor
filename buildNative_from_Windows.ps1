echo "Setting working dir to current script location: " $PSScriptRoot
cd $PSScriptRoot
echo "Starting Maven Build to single Jar file."
mvn -B -ntp clean install compile package "-Djava.awt.headless=false" "-Dmaven.tests.skip=true" "-Dorg.slf4j.simpleLogger.defaultLogLevel=WARN"
echo "Generated contents in target:"
ls .\target
echo "Creating target-native dir"
New-Item -ItemType Directory -Force -Path target-native
echo "Cleaning target-native directory"
rm target-native\*
echo "Starting Native Build on Windows."
native-image `
    -march=compatibility `
    "-Djava.awt.headless=false" `
    --no-fallback `
    --enable-http `
    --enable-https `
    --enable-url-protocols=ws,wss `
    "-H:+UnlockExperimentalVMOptions" `
    "-H:ResourceConfigurationFiles=native-resources-configuration.json" `
    -jar .\target\SimpleInternetMonitor.jar `
    .\target-native\SimpleInternetMonitor
echo "Compressing target-native executable into zip."
Compress-Archive -Path .\target-native\* -DestinationPath .\target-native\SimpleInternetMonitor.zip
echo "Generated contents in target-native:"
ls .\target-native