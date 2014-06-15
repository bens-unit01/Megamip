@echo off
echo.
echo Installing the app ...
cd dist
adb install -r MipVideoPlayer-captive-runtime.apk
::adb install -r MipVideoPlayer-debug.apk
pause

