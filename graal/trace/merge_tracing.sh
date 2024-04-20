echo "Setting working dir to current script location: $0"
script_dir="$(dirname "$(readlink -f "$0")")"
cd "$script_dir"
echo "Copying over Linux traces"
cp ./linux/*.json ../../src/main/resources/META-INF/native-image/
echo "Starting Maven Build to single Jar file."
cd ../../
mvn -B -ntp clean install compile package "-Djava.awt.headless=false" "-Dmaven.tests.skip=true" "-Dorg.slf4j.simpleLogger.defaultLogLevel=WARN"
cd "$script_dir"
# Merge new config to already existing META-INF
java "-Dfile.encoding=UTF-8" -agentlib:native-image-agent=config-merge-dir=./linux/. -jar ../../target/SimpleInternetMonitor.jar
