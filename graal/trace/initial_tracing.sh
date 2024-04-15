echo "Setting working dir to current script location: $0"
script_dir="$(dirname "$(readlink -f "$0")")"
cd "$script_dir"
echo "Creating tracing dir"
mkdir ../linux
java  -Dfile.encoding=UTF-8 -agentlib:native-image-agent=config-output-dir=../linux. -jar ../../target/SimpleInternetMonitor.jar
