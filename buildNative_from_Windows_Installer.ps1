# Use the 64 bit version of editbin to avoid opening a console when executing the .exe
# Install Instructions: https://stackoverflow.com/questions/57207503/dumpbin-exe-editbin-exe-package-needed-in-visual-studio-2019
# editbin must be called manually, because adding the editbin folder to the path can cause trouble with
# GraalVMs make getting overwritten, causes ClassNotFoundException in the Executable.
editbin.exe /SUBSYSTEM:WINDOWS .\target-native\SimpleInternetMonitor.exe
makensis.exe .\NSIS_windows.nsi