Name "SimpleInternetMonitor Installer"
OutFile "target-native\SimpleInternetMonitor-WindowsInstaller.exe"
InstallDir "$PROGRAMFILES\SimpleInternetMonitor"

Page components
Page directory
Page instfiles

Section "SimpleInternetMonitor" SecApp
    ; Set output path to the installation directory
    SetOutPath $INSTDIR

    ; Copy all files from the target-native archive
    DetailPrint "Extracting files..."
    SetOverwrite on
    File "target-native\*"

    ; Add Uninstaller to Directory
    WriteUninstaller "$INSTDIR\uninstall.exe"
    ; Write Uninstaller Registry Entry
    WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\SimpleInternetMonitor" "Contact" "https://github.com/TeaTiMe08/SimpleInternetMonitor"
    WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\SimpleInternetMonitor" "DisplayIcon" "$INSTDIR\uninstall.exe"
    WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\SimpleInternetMonitor" "DisplayName" "SimpleInternetMonitor"
    WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\SimpleInternetMonitor" "DisplayVersion" "1.0.0"
    WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\SimpleInternetMonitor" "Pulisher" "TeaTiMe08/SimpleInternetMonitor"
    WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\SimpleInternetMonitor" "QuietUninstallString" "$INSTDIR\uninstall.exe"
    WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\SimpleInternetMonitor" "UninstallString" "$INSTDIR\uninstall.exe"
    WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\SimpleInternetMonitor" "URLInfoAbout" "https://github.com/TeaTiMe08/SimpleInternetMonitor"
    ; Approve Autostart automatically
    WriteRegBin HKCU "Software\Microsoft\Windows\CurrentVersion\Explorer\StartupApproved\Run" "SimpleInternetMonitor" 000000000000000000000000
SectionEnd

Section "Start Menu Shortcut" SecShortcut
    ; Create start menu shortcut
    SetShellVarContext all
    CreateDirectory "$SMPROGRAMS\SimpleInternetMonitor"
    CreateShortcut "$SMPROGRAMS\SimpleInternetMonitor\SimpleInternetMonitor.lnk" "$INSTDIR\SimpleInternetMonitor.exe"
    CreateShortCut "$SMPROGRAMS\SimpleInternetMonitor\UninstallSimpleInternetMonitor.lnk" "$INSTDIR\uninstall.exe"
SectionEnd

Section "Autostart with Windows" SecAutostart
    ; Add autostart shortcut
    WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Run" "SimpleInternetMonitor" "$INSTDIR\SimpleInternetMonitor.exe"
SectionEnd

Section Uninstall
    ; Remove autostart registry entry
    DeleteRegValue HKCU "Software\Microsoft\Windows\CurrentVersion\Run" "SimpleInternetMonitor"

    ; Remove files and directories
    Delete "$INSTDIR\SimpleInternetMonitor.exe"
    Delete "$INSTDIR\*.*"
    RMDir "$INSTDIR"

    ; Remove start menu shortcut
    Delete "$SMPROGRAMS\SimpleInternetMonitor\SimpleInternetMonitor.lnk"
    RMDir "$SMPROGRAMS\SimpleInternetMonitor"

    ; Remove autostart entry
    DeleteRegValue HKCU "Software\Microsoft\Windows\CurrentVersion\Run" "SimpleInternetMonitor"

    ; Remove Uninstaller Registry Entries
    DeleteRegValue HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\SimpleInternetMonitor" "Contact"
    DeleteRegValue HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\SimpleInternetMonitor" "DisplayIcon"
    DeleteRegValue HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\SimpleInternetMonitor" "DisplayName"
    DeleteRegValue HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\SimpleInternetMonitor" "DisplayVersion"
    DeleteRegValue HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\SimpleInternetMonitor" "Pulisher"
    DeleteRegValue HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\SimpleInternetMonitor" "QuietUninstallString"
    DeleteRegValue HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\SimpleInternetMonitor" "UninstallString"
    DeleteRegValue HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\SimpleInternetMonitor" "URLInfoAbout"
SectionEnd