#!/bin/sh

es_home=../..
jdk_bin=${es_home}/jdk/bin/

if [ ! -d "${jdk_bin}" ] ; then
  jdk_bin=""
fi

jars="`find ${es_home} -name "*.jar" | tr "\\n" ":"`"
jars="jars=${jars}."
# echo ${jars}
rm -rf jp
${jdk_bin}javac -d . -cp ${jars} src/*
${jdk_bin}jar cvf auth-plugin.jar jp
#cp -p auth-plugin.jar ..
