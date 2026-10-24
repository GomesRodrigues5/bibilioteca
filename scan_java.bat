@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"
if exist scan_results.txt del scan_results.txt
for /r %%F in (*.java) do (
  set "hasPackage=0"
  set "className="
  for /f "usebackq delims=" %%L in ("%%F") do (
    if "!hasPackage!"=="0" (
      echo %%L | findstr /r "^[ ]*package[ ]" >nul && set "hasPackage=1"
    )
    echo %%L | findstr /r "^[ ]*public[ ]\+class[ ]\+[A-Za-z_][A-Za-z0-9_]*" >nul && (
      for /f "tokens=3" %%C in ('echo %%L') do if not defined className set "className=%%C"
    )
  )
  if defined className (
    set "baseName=%%~nF"
    if not "!className!"=="!baseName!" echo MISMATCH:%%F:!className!:!baseName!>>scan_results.txt
  ) else if "!hasPackage!"=="0" (
    echo NO_PACKAGE:%%F>>scan_results.txt
  )
)
if exist scan_results.txt type scan_results.txt
endlocal
