@echo off
if not "%OS%"=="Windows_NT" goto windows95
@setlocal
set CUP_HOME=%~dp0..
goto runJFlex
:windows95
set CUP_HOME=C:\CUP

:runJFlex
java -Xmx128m -jar %CUP_HOME%\lib\java-cup-10k-TUM.jar %1 %2 %3 %4 %5 %6 %7 %8 %9
