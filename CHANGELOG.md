# Release [1.4.0] - 25.05.2024
## Added
- More DNS Providers of ISPs in Middle Europe and USA in the standard services list
- Added option to manually add local providers

# Release [1.3.0] - 22.05.2024
## Added
- Socket support for e.g. DNS and IP connecting [#14](https://github.com/TeaTiMe08/SimpleInternetMonitor/issues/14)
- Sorting / Filtering for Providers selection Window [#16](https://github.com/TeaTiMe08/SimpleInternetMonitor/issues/16)

## Fixed
- NSIS: Windows Uninstaller link shortcut not being created
- Protocol String representation mapping to IRequestCheckerProvider

# Release [1.2.0] - 21.04.2024
### Added
- Major UI Update 
- Added statistics for day, previous week for a selected date and total statistic
- Look and feel adjustments for Webpage
- Chart and other partial responsiveness
- Added Logo and custom Background

### Fixed
- Ugly Web UI
- Removed Gimp files and other unnecessary files from target dir.


# Release [1.1.0] - 20.04.2024
### Added
- Add checking, if connected to the local network before measuring [#7](https://github.com/TeaTiMe08/SimpleInternetMonitor/issues/7)
- Fix Tray Icon on Linux/Ubuntu not showing [#5](https://github.com/TeaTiMe08/SimpleInternetMonitor/issues/5)
- Support for wss: WebSocket connections [#3](https://github.com/TeaTiMe08/SimpleInternetMonitor/issues/3) 

### Fixed
- Selecting Provider more then once was not possible without restart
- System Tray now working smoothly on Linux Distros


# Release[1.0.0] - 19.04.2024
### Added
- System Tray Application
- Network Online Measurement
- Select different Providers
- List of German Network Provider Speedtest URLs
- Simple GUI (Website) for viewing the Measurements
