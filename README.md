# SimpleInternetMonitor
Measures Internet availibility in a background tray, so you can sue your network provider.

<div>
<img width="62%" alt="grafik" style="display: block;margin: 10px auto 20px;" src="https://github.com/TeaTiMe08/SimpleInternetMonitor/assets/19726327/4a0c79c4-abb0-470c-887d-1bbdfac48143" />
</div>

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

## How to use
- Supported systems are Windows🪟 and Linux🐧.
- Download and execute the latest Windows Installer from [TeaTiMe08/SimpleInternetMonitor/releases](https://github.com/TeaTiMe08/SimpleInternetMonitor/releases).
- Open Provider Selector from the SimpleInternetMonitor in System-Tray.
<img width="318" height="186" alt="grafik" src="https://github.com/user-attachments/assets/1931b3a3-9d59-46d0-b4b5-787d7c501ca8" />

### Note
⚠️ The Protocol and geographical distance to the Provider are impacting the latency of the measurements.

Socket has lower then URL and HTTPS since it only tries to open a TCP channel to an IP address

# Developer Information
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

### Add new netlatency ServiceProvider for measuring other protocols
1. Create class at de/teatime08/netlatency/protocols/impl/ implementing (IRequestCheckerProvider.java)[src/main/java/de/teatime08/netlatency/protocols/IRequestCheckerProvider.java]
2. Add to the (META-INF.services file)[src/main/resources/META-INF/services/de.teatime08.netlatency.protocols.IRequestCheckerProvider]
3. Add to the graal Reflections at (graal/reflection_config.json)[graal/reflection_config.json] [Thanks to @cstancu](https://github.com/cstancu/native-image-service-loader-demo/tree/master)
4. Add to the <code>--enable-url-protocols</code> parameter in the buildNative build files

### Tracing hints
If you want to change some code, especially in UI swing/awt.
Graal must be told to use the right reflections for your UI commands.
So in order to "tell" graal which code to generate, we must 
(trace)[https://www.graalvm.org/latest/reference-manual/native-image/guides/configure-with-tracing-agent/] 
the app by executing the jar with the <code>native-image-agent</code> and writing the traced configuration to file.<br>
The configuration has already been traced, so if you add some features before pushing
execute the graal/trace/merge_tracing.ps1 script and trigger your code-relevant parts,
when finished terminate the app and confirm in the script that your generated config is merged with those in <code>src/main/resources/META-INF/native-image</code>.
