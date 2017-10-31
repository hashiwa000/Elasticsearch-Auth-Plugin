#!/bin/sh

jars="`find ../../.. -name "*.jar" | tr "\\n" ":"`"
jars="jars=${jars}."
# echo ${jars}
rm -rf jp
javac -d . -cp ${jars} src/*
jar cvf auth-plugin.jar jp
cp -p auth-plugin.jar ..
