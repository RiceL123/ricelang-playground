#!/bin/bash

arg=$1

base=${arg%.vc}

export CLASSPATH=$(cd ..; pwd) &&
javac lang/System.java &&
javac CodeGen/Emitter.java &&
javac CodeGen/JVM.java
javac vc.java &&
java VC.vc $base.vc &&
./jasmin.sh $base.j

java -cp .:.. $base
