@echo off

TITLE Kiwi

set BASEDIR=%~dp0\..

set REPO=%BASEDIR%\lib

set CLASSPATH="%BASEDIR%"\etc;"%REPO%"\*

set JAVA_MAIN_CLASS=fr.aquillet.kiwi.ui.KiwiApplication

set JAVACMD=%JAVA_HOME%/bin/java.exe

set JAVA_OPTIONS=-Dfile.encoding=UTF-8

set MEM_OPTIONS=-Xms256m -Xmx512m

"%JAVACMD%" -classpath  %CLASSPATH% %JAVA_OPTIONS% %MEM_OPTIONS% %JAVA_MAIN_CLASS%
