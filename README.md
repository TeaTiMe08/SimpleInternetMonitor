# SimpleInternetMonitor
Measures Internet availibility in a background tray, so you can sue your network provider.

![image](https://github.com/TeaTiMe08/SimpleInternetMonitor/assets/19726327/bd5c703e-4851-4aef-92ea-f2f68fa9efe8)


## Why?
So i am based in Germany and there is no state law for a clean internet cable infractructure.
I am dealing with internet failures reaching from minutes to hours every week.
To be able to compensate my frustration and my money for mobile network data i created this small project
for visualizing the frustration from time to time especially when i am offline... again....
If you also feel that way, lets work on this project together.
I already provided those

## Features
- Continuously measure internet availibilty while your PC is turned on
- Runs in background tray
- Web UI for timeline tracking
- Native Builds and Installer for Windows

## How to build the native executable
1. Get GraalVM [from their Downloads page](https://www.graalvm.org/downloads/)
2. Get [Maven](https://maven.apache.org/download.cgi)
3. Get editbin.exe [from the Visual Studio Installer](https://visualstudio.microsoft.com/downloads/), by installing [these packages](https://stackoverflow.com/questions/57207503/dumpbin-exe-editbin-exe-package-needed-in-visual-studio-2019)
4. Check if you added all of those to your system path: 
   1. native-image
   2. java
   3. mvn
   4. editbin
5. Run the <code>buildNative_from_Windows.ps1</code> script in IntelliJ

## How to build a windows Installation executable
1. Create a native Executable and the .zip file package following the [How to build the native executable](## How to build the native executable)
2. Find the .zip file inside target-native directory
3. Get NSIS from the [NSIS Download Page](https://nsis.sourceforge.io/Download)
4. Open NSIS and open the **Compile NSI script** Feature

![image](https://github.com/TeaTiMe08/SimpleInternetMonitor/assets/19726327/82fece93-adcf-4ad4-8123-bc51d14eaaba)

6. Select the [NSIS_windows.nsi](NSIS_windows.nsi) file
7. Find the SimpleInternetMonitor-WindowsInstaller.exe in the project dir

## Developer Information

### Add new netlatency ServiceProvider for measuring other protocols
1. Create class at de/teatime08/netlatency/protocols/impl/ implementing (IRequestCheckerProvider.java)[src/main/java/de/teatime08/netlatency/protocols/IRequestCheckerProvider.java]
2. Add to the (META-INF.services file)[src/main/resources/META-INF/services/de.teatime08.netlatency.protocols.IRequestCheckerProvider]
3. Add to the graal Reflections at (graal/reflection_config.json)[graal/reflection_config.json] [Thanks to @cstancu](https://github.com/cstancu/native-image-service-loader-demo/tree/master)
4. Add to the <code>--enable-url-protocols</code> parameter in the buildNative build files