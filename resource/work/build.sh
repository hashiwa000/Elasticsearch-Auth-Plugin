#!/bin/sh

jars="`find ../../.. -name "*.jar" | tr "\\n" ":"`"
jars="jars=${jars}."
# echo ${jars}
rm -rf jp
javac -d . -cp ${jars} src/*
jar cvf test-plugin.jar jp
cp -p test-plugin.jar ..
