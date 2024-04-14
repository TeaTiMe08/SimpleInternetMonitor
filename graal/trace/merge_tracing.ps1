echo "Setting working dir to current script location: " $PSScriptRoot
cd $PSScriptRoot
# Merge new config to already existing META-INF
java "-Dfile.encoding=UTF-8" -agentlib:native-image-agent=config-merge-dir=..\..\target\classes\META-INF\native-image\. -jar ..\..\target\SimpleInternetMonitor.jar

# Prompt the user with a yes/no question
$confirmation = Read-Host "Do you want to move the new merged Trace files to sources dir? (Y/N) [Default: Y]"
# If the user presses Enter without input, default to 'Y'
if ($confirmation -eq "" -or $confirmation -eq "Y" -or $confirmation -eq "y") {
    # Specify the source and destination directories
    $sourceDirectory = "..\..\target\classes\META-INF\native-image"
    $destinationDirectory = "..\..\src\main\resources\META-INF\native-image"

    echo $sourceDirectory
    # Check if the source directory exists
    if (Test-Path $sourceDirectory) {
        # Copy files from the source to the destination directory
        Copy-Item -Path $sourceDirectory\* -Destination $destinationDirectory -Recurse -Force
        Write-Host "New trace configuration has been added to the sources."
    } else {
        Write-Host "Source directory does not exist."
    }
}
else {
    Write-Host "No action taken. The new merged trace configuration was not moved to 'Sources'."
    Write-Host "Can still be moved manually by loading files from target/classes/META-INF/native-image into src/main/resources/META-INF/native-image"
}