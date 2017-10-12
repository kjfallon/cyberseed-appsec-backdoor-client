BUILDING:

This project is written in Java.  This project contains many java source code files (class files) and includes
dependencies on many open source Java libraries.   Java source code must be compiled with a Java compiler into bytecode and
then executed in in a Java Virtual Machine (JVM).

We have made this very simple to do using Maven which is the most popualar build tool for Java. Maven will download
everything you need, all of the dependencies are automatically downloaded for you.

There are THREE SIMPLE STEPS.

Step 1.  Install Java 8

You cannot compile Java code without a Java compiler to compile it first.  The Java 8 JDK is what the Java 8
compiler is called.  This is a binary application.  There is a specific version for your operating system
and architecture.  We do not know what architecture are using so we can not supply this for you.  Additionally it would
be illegal for us to distribute this ourselves.  By law you must download this from Oracle.  Visit this url
http://www.oracle.com/technetwork/pt/java/javase/downloads/jdk8-downloads-2133151.html and
download and install the "Java SE Development Kit".  You will know you are successful when afterwards from a shell prompt
you can type "javac -version" and a verion number is displayed to you.  Congrats you are now a Java developer and can
compile Java code!

You completed step one!

Step 2. Install Maven.

Maven is the build system that makes compiling this project drop-dead simple.  When you tell this project to compile
Maven will download and configure everything that you need.  All you have to do is install Maven.  You
can install Maven from here: https://maven.apache.org/download.cgi . Maven is written in Java and the download package
is the same for all architectures.  You will know you are successful when afterwards from a shell prompt you can type
"mvn -version" and a version number is displayed to you.   Congrats you can now build complicated Java projects with a
single command!

You completed step two!

Step 3. Run our build.sh shell script.

In this root directory of this project is a shell script named build.sh.  This script contains one line with four words
on it. You may execute this shell script or type these four words "mvn -DskipTests clean package".  This will use Java 8
to execute Maven and build your project.

You completed step three!  There is no stopping you.  You should now attempt to exploit this application.
