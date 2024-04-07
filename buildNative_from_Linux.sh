echo "Setting working dir to current script location: $0"
script_dir="$(dirname "$(readlink -f "$0")")"
cd "$script_dir"
echo "Starting Maven Build to single Jar file."
mvn clean install compile package "-Dmaven.tests.skip=true"
echo "Creating target-native dir"
mkdir -p target-native
echo "Cleaning target-native directory"
rm -rf target-native/*
echo "Starting Native Build on Linux."
native-image \
    "-Djava.awt.headless=false" \
    --enable-http \
    --enable-https \
    "-H:ResourceConfigurationFiles=native-resources-configuration.json" \
    -march=native \
    -jar ./target/SimpleInternetMonitor.jar \
    ./target-native/SimpleInternetMonitor