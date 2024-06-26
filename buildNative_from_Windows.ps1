echo "Setting working dir to current script location: " $PSScriptRoot
cd $PSScriptRoot
echo "Copying over Windows traces"
Copy-Item -Path .\graal\trace\windows\*.json -Destination .\src\main\resources\META-INF\native-image\
echo "Starting Maven Build to single Jar file."
mvn -B -ntp clean install compile package "-Djava.awt.headless=false" "-Dmaven.tests.skip=true" "-Dorg.slf4j.simpleLogger.defaultLogLevel=WARN"
echo "Removing Windows trace files from src dir."
Remove-Item -Path .\src\main\resources\META-INF\native-image\*.json
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
    "-H:ResourceConfigurationFiles=.\graal\native-resources-configuration.json" `
    "-H:ReflectionConfigurationFiles=.\graal\reflection_config.json" `
    -jar .\target\SimpleInternetMonitor.jar `
    .\target-native\SimpleInternetMonitor
echo "Compressing target-native executable into zip."
Compress-Archive -Path .\target-native\* -DestinationPath .\SimpleInternetMonitor.zip
echo "Generated contents in target-native:"
ls .\target-native