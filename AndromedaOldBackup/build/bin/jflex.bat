@echo off
if not "%OS%"=="Windows_NT" goto windows95
@setlocal
set JFLEX_HOME=%~dp0..
goto runJFlex
:windows95
set JFLEX_HOME=C:\JFLEX

:runJFlex
java -Xmx128m -jar %JFLEX_HOME%\lib\JFlex.jar %1 %2 %3 %4 %5 %6 %7 %8 %9
