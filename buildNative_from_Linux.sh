echo "Setting working dir to current script location: $0"
script_dir="$(dirname "$(readlink -f "$0")")"
cd "$script_dir"
echo "Copying over Linux traces"
cp ./graal/trace/linux/*.json ./src/main/resources/META-INF/native-image/
echo "Starting Maven Build to single Jar file."
mvn -B -ntp clean install compile package "-Djava.awt.headless=false" "-Dmaven.tests.skip=true" "-Dorg.slf4j.simpleLogger.defaultLogLevel=WARN"
echo "Removing Windows trace files from src dir."
rm ./src/main/resources/META-INF/native-image/*.json
echo "Generated contents in target:"
ls -alh target/
echo "Creating target-native dir"
mkdir -p target-native
echo "Cleaning target-native directory"
rm -rf target-native/*
echo "Starting Native Build on Linux."
native-image \
    -g -O0 \
    "-Djava.awt.headless=false" \
    --no-fallback \
    --enable-http \
    --enable-https \
    --enable-url-protocols=ws,wss \
    "-H:+UnlockExperimentalVMOptions" \
    "-H:ResourceConfigurationFiles=./graal/native-resources-configuration.json" \
    -jar ./target/SimpleInternetMonitor.jar \
    ./target-native/SimpleInternetMonitor
ls -alh target-native/