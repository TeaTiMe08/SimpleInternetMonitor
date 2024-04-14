echo "Setting working dir to current script location: $0"
script_dir="$(dirname "$(readlink -f "$0")")"
cd "$script_dir"
echo "Starting Maven Build to single Jar file."
mvn -B -ntp clean install compile package "-Djava.awt.headless=false" "-Dmaven.tests.skip=true" "-Dorg.slf4j.simpleLogger.defaultLogLevel=WARN"
echo "Generated contents in target:"
ls -alh target/
echo "Creating target-native dir"
mkdir -p target-native
echo "Cleaning target-native directory"
rm -rf target-native/*
echo "Starting Native Build on Linux."
native-image \
    -g -O0 \
    -march=compatibility \
    "-Djava.awt.headless=false" \
    --no-fallback \
    --enable-http \
    --enable-https \
    --enable-url-protocols=ws,wss \
    "-H:+UnlockExperimentalVMOptions" \
    "-H:ResourceConfigurationFiles=native-resources-configuration.json" \
    -jar ./target/SimpleInternetMonitor.jar \
    ./target-native/SimpleInternetMonitor
ls -alh target-native/