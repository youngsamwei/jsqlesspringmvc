@echo off
:: Copyright 2014 The Chromium Authors. All rights reserved.
:: Use of this source code is governed by a BSD-style license that can be
:: found in the LICENSE file.

:: Change HKCU to HKLM if you want to install globally.
:: %~dp0 is the directory containing this bat script and ends with a backslash.
REG ADD "HKCU\Software\Google\Chrome\NativeMessagingHosts\cn.sdkd.ccse.jsqles.chromeapp.host.chromeapphost" /ve /t REG_SZ /d "%~dp0cn.sdkd.ccse.jsqles.chromeapp.host.chromeapphost.json" /f
REG ADD "HKLM\Software\Google\Chrome\NativeMessagingHosts\cn.sdkd.ccse.jsqles.chromeapp.host.chromeapphost" /ve /t REG_SZ /d "%~dp0cn.sdkd.ccse.jsqles.chromeapp.host.chromeapphost.json" /f


::copy sqljdbc_auth.dll

if "%processor_architecture%"=="x86" goto x86
if "%processor_architecture%"=="AMD64" goto x64
:x86
copy /y auth\x86\sqljdbc_auth.dll
goto exit

:x64
copy /y auth\x64\sqljdbc_auth.dll
goto exit

:exit

pause
