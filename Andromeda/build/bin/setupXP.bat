@echo off
if not "%OS%"=="Windows_NT" goto end
@setlocal
set CCK_HOME=%~dp0..
cd %CCK_HOME%
set CCK_HOME=%CD%
cd bin
REM copy setup.inf setup2.inf
REM echo cckhome=%CCK_HOME% >> setup2.inf
REM rundll32 setupapi.dll,InstallHinfSection 132 setup2.inf
REM del setup2.inf
REM reg add HKCU\Environment /v CCK_HOME /t REG_SZ /d %CCK_HOME%
REM reg add HKCU\Environment /v ANT_HOME /t REG_SZ /d %CCK_HOME%
REM REM the next 4 things only work in Windows XP
REM set OLDPATH=%%PATH%%
REM for /F "tokens=3,*" %%i in ('reg query HKCU\Environment /v PATH ^| findstr /r /i /C:"Path.*REG"') do set OLDPATH=%%i
REM reg delete HKCU\Environment /f /v PATH
REM reg add HKCU\Environment /v PATH /t REG_EXPAND_SZ /d %OLDPATH%;%%CCK_HOME%%\bin
REM install some Desktop Links
setup.js
:end5
del classgen
del jflex
del ant
del cup
del setup.unix
del setup.js
del *.desktop
del setupXP.bat
