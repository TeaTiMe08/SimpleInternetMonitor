echo "Setting working dir to current script location: " $PSScriptRoot
cd $PSScriptRoot
echo "Starting Maven Build to single Jar file."
mvn -B -ntp clean install compile -Pnative package "-Djava.awt.headless=false" "-Dmaven.tests.skip=true" "-Dorg.slf4j.simpleLogger.defaultLogLevel=WARN"
echo "Generated contents in target:"
ls .\target
echo "Creating target-native dir"
New-Item -ItemType Directory -Force -Path target-native
echo "Cleaning target-native directory"
rm target-native\*
echo "Starting Native Build on Windows."
native-image `
    -g -O0 `
    "-Djava.awt.headless=false" `
    --link-at-build-time `
    --enable-http `
    --enable-https `
    "-H:+UnlockExperimentalVMOptions" `
    "-H:ResourceConfigurationFiles=native-resources-configuration.json" `
    -jar .\target\SimpleInternetMonitor.jar `
    .\target-native\SimpleInternetMonitor
echo "Generated contents in target-native:"
ls .\target-native