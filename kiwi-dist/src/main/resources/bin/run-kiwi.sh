#!/bin/sh

BASEDIR=./..

REPO=$BASEDIR/lib

CLASSPATH=$BASEDIR/etc:$REPO/*

JAVA_MAIN_CLASS=fr.aquillet.kiwi.ui.KiwiApplication

JAVACMD=java

JAVA_OPTIONS=-Dfile.encoding=UTF-8

MEM_OPTIONS=-Xms256m -Xmx512m

LOGBACK_CONFIG_FILE="$BASEDIR/etc/"logback.xml

"$JAVACMD" -classpath $CLASSPATH $JAVA_OPTIONS $MEM_OPTIONS $JAVA_MAIN_CLASS -Dlogging.config=$LOGBACK_CONFIG_FILE
