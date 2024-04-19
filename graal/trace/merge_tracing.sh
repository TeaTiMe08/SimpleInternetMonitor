echo "Setting working dir to current script location: $0"
script_dir="$(dirname "$(readlink -f "$0")")"
cd "$script_dir"
# Merge new config to already existing META-INF
java "-Dfile.encoding=UTF-8" -agentlib:native-image-agent=config-merge-dir=./linux/. -jar ../../target/SimpleInternetMonitor.jar
