<p align="center">
    <a href="https://docs.oracle.com/en/java/javase/17/"><img src="https://img.shields.io/badge/Java-Release%2017-green"/></a>
    <span>&nbsp;</span>
    <a href="https://jitpack.io/#teletha/typewriter"><img src="https://img.shields.io/jitpack/v/github/teletha/typewriter?label=Repository&color=green"></a>
    <span>&nbsp;</span>
    <a href="https://teletha.github.io/typewriter"><img src="https://img.shields.io/website.svg?down_color=red&down_message=CLOSE&label=Official%20Site&up_color=green&up_message=OPEN&url=https%3A%2F%2Fteletha.github.io%2Ftypewriter"></a>
</p>


## About The Project

<p align="right"><a href="#top">back to top</a></p>


## Prerequisites
Typewriter runs on all major operating systems and requires only [Java version 17](https://docs.oracle.com/en/java/javase/17/) or later to run.
To check, please run `java -version` from the command line interface. You should see something like this:
```
> java -version
openjdk version "16" 2021-03-16
OpenJDK Runtime Environment (build 16+36-2231)
OpenJDK 64-Bit Server VM (build 16+36-2231, mixed mode, sharing)
```
<p align="right"><a href="#top">back to top</a></p>

## Using in your build
For any code snippet below, please substitute the version given with the version of Typewriter you wish to use.
#### [Maven](https://maven.apache.org/)
Add JitPack repository at the end of repositories element in your build.xml:
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
Add it into in the dependencies element like so:
```xml
<dependency>
    <groupId>com.github.teletha</groupId>
    <artifactId>typewriter</artifactId>
    <version>0.1</version>
</dependency>
```
#### [Gradle](https://gradle.org/)
Add JitPack repository at the end of repositories in your build.gradle:
```gradle
repositories {
    maven { url "https://jitpack.io" }
}
```
Add it into the dependencies section like so:
```gradle
dependencies {
    implementation 'com.github.teletha:typewriter:0.1'
}
```
#### [SBT](https://www.scala-sbt.org/)
Add JitPack repository at the end of resolvers in your build.sbt:
```scala
resolvers += "jitpack" at "https://jitpack.io"
```
Add it into the libraryDependencies section like so:
```scala
libraryDependencies += "com.github.teletha" % "typewriter" % "0.1"
```
#### [Leiningen](https://leiningen.org/)
Add JitPack repository at the end of repositories in your project.clj:
```clj
:repositories [["jitpack" "https://jitpack.io"]]
```
Add it into the dependencies section like so:
```clj
:dependencies [[com.github.teletha/typewriter "0.1"]]
```
#### [Bee](https://teletha.github.io/bee)
Add it into your project definition class like so:
```java
require("com.github.teletha", "typewriter", "0.1");
```
<p align="right"><a href="#top">back to top</a></p>


## Contributing
Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.
If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

The overwhelming majority of changes to this project don't add new features at all. Optimizations, tests, documentation, refactorings -- these are all part of making this product meet the highest standards of code quality and usability.
Contributing improvements in these areas is much easier, and much less of a hassle, than contributing code for new features.

### Bug Reports
If you come across a bug, please file a bug report. Warning us of a bug is possibly the most valuable contribution you can make to Typewriter.
If you encounter a bug that hasn't already been filed, [please file a report](https://github.com/teletha/typewriter/issues/new) with an [SSCCE](http://sscce.org/) demonstrating the bug.
If you think something might be a bug, but you're not sure, ask on StackOverflow or on [typewriter-discuss](https://github.com/teletha/typewriter/discussions).
<p align="right"><a href="#top">back to top</a></p>


## Built with
Typewriter depends on the following products on runtime.
* [bson-3.11.0](https://mvnrepository.com/artifact/org.mongodb/bson/3.11.0)
* [bson-codecs-jsr310-3.5.4](https://mvnrepository.com/artifact/io.github.cbartosiak/bson-codecs-jsr310/3.5.4)
* [mongo-java-driver-3.12.11](https://mvnrepository.com/artifact/org.mongodb/mongo-java-driver/3.12.11)
* [sinobu-2.16.0](https://mvnrepository.com/artifact/com.github.teletha/sinobu/2.16.0)
* [sqlite-jdbc-3.36.0.3](https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc/3.36.0.3)

Typewriter depends on the following products on test.
* [antibug-1.3.0](https://mvnrepository.com/artifact/com.github.teletha/antibug/1.3.0)
* [apiguardian-api-1.1.2](https://mvnrepository.com/artifact/org.apiguardian/apiguardian-api/1.1.2)
* [byte-buddy-1.12.12](https://mvnrepository.com/artifact/net.bytebuddy/byte-buddy/1.12.12)
* [byte-buddy-agent-1.12.12](https://mvnrepository.com/artifact/net.bytebuddy/byte-buddy-agent/1.12.12)
* [commons-lang3-3.12.0](https://mvnrepository.com/artifact/org.apache.commons/commons-lang3/3.12.0)
* [junit-jupiter-api-5.9.0-M1](https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api/5.9.0-M1)
* [junit-jupiter-engine-5.9.0-M1](https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-engine/5.9.0-M1)
* [junit-jupiter-params-5.9.0-M1](https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-params/5.9.0-M1)
* [junit-platform-commons-1.9.0-M1](https://mvnrepository.com/artifact/org.junit.platform/junit-platform-commons/1.9.0-M1)
* [junit-platform-engine-1.9.0-M1](https://mvnrepository.com/artifact/org.junit.platform/junit-platform-engine/1.9.0-M1)
* [junit-platform-launcher-1.9.0](https://mvnrepository.com/artifact/org.junit.platform/junit-platform-launcher/1.9.0)
* [mongo-java-server-1.40.0](https://mvnrepository.com/artifact/de.bwaldvogel/mongo-java-server/1.40.0)
* [mongo-java-server-core-1.40.0](https://mvnrepository.com/artifact/de.bwaldvogel/mongo-java-server-core/1.40.0)
* [mongo-java-server-memory-backend-1.40.0](https://mvnrepository.com/artifact/de.bwaldvogel/mongo-java-server-memory-backend/1.40.0)
* [netty-buffer-4.1.77.Final](https://mvnrepository.com/artifact/io.netty/netty-buffer/4.1.77.Final)
* [netty-codec-4.1.77.Final](https://mvnrepository.com/artifact/io.netty/netty-codec/4.1.77.Final)
* [netty-common-4.1.77.Final](https://mvnrepository.com/artifact/io.netty/netty-common/4.1.77.Final)
* [netty-handler-4.1.77.Final](https://mvnrepository.com/artifact/io.netty/netty-handler/4.1.77.Final)
* [netty-resolver-4.1.77.Final](https://mvnrepository.com/artifact/io.netty/netty-resolver/4.1.77.Final)
* [netty-transport-4.1.77.Final](https://mvnrepository.com/artifact/io.netty/netty-transport/4.1.77.Final)
* [opentest4j-1.2.0](https://mvnrepository.com/artifact/org.opentest4j/opentest4j/1.2.0)
* [slf4j-api-1.7.36](https://mvnrepository.com/artifact/org.slf4j/slf4j-api/1.7.36)
<p align="right"><a href="#top">back to top</a></p>


## License
Copyright (C) 2022 The TYPEWRITER Development Team

MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
<p align="right"><a href="#top">back to top</a></p>