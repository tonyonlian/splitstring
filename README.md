### 一、提交任务

1.安装jstorm

2.pom.xml配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.tunyl.jsex</groupId>
    <artifactId>split-string</artifactId>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
        <dependency>
            <groupId>com.alibaba.jstorm</groupId>
            <artifactId>jstorm-core</artifactId>
            <version>2.1.1</version>
            <!--本地运行的时候注释下行，提交时记得要放开注释-->
            <scope>provided</scope>
            <!--<exclusions>-->
                <!--<exclusion>-->
                    <!--<groupId>org.slf4j</groupId>-->
                    <!--<artifactId>slf4j-api</artifactId>-->
                <!--</exclusion>-->
                <!--<exclusion> &lt;!&ndash; we prefer our explicit version, though it should be the same &ndash;&gt;-->
                    <!--<groupId>org.slf4j</groupId>-->
                    <!--<artifactId>slf4j-nop</artifactId>-->
                <!--</exclusion>-->
                <!--<exclusion> &lt;!&ndash; we prefer our explicit version, though it should be the same &ndash;&gt;-->
                    <!--<groupId>org.slf4j</groupId>-->
                    <!--<artifactId>slf4j-jdk14</artifactId>-->
                <!--</exclusion>-->

            <!--</exclusions>-->
        </dependency>

        <!--<dependency>-->
            <!--<groupId>com.twitter</groupId>-->
            <!--<artifactId>chill-java</artifactId>-->
            <!--<version>0.9.2</version>-->
        <!--</dependency>-->

        <!--<dependency>-->
            <!--<groupId>org.slf4j</groupId>-->
            <!--<artifactId>log4j-over-slf4j</artifactId>-->
            <!--<version>1.7.10</version>-->
            <!--<scope>provided</scope>-->
        <!--</dependency>-->
        <!--<dependency>-->
            <!--<groupId>ch.qos.logback</groupId>-->
            <!--<artifactId>logback-classic</artifactId>-->
            <!--<version>1.0.13</version>-->
            <!--<scope>provided</scope>-->
        <!--</dependency>-->
        <!--<dependency>-->
            <!--<groupId>org.slf4j</groupId>-->
            <!--<artifactId>slf4j-log4j12</artifactId>-->
            <!--<version>1.7.5</version>-->
        <!--</dependency>-->
        <!--<dependency>-->
            <!--<groupId>log4j</groupId>-->
            <!--<artifactId>log4j</artifactId>-->
            <!--<version>1.2.17</version>-->
        <!--</dependency>-->



    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <configuration>
                    <!--<forkCount>1</forkCount>-->
                    <forkMode>pertest</forkMode>
                    <argLine>-Xms1024m -Xmx4096m</argLine>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>1.7.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <transformers>
                                <transformer
                                        implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                                    <resource>META-INF/spring.handlers</resource>
                                </transformer>
                                <transformer
                                        implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                                    <resource>META-INF/spring.schemas</resource>
                                </transformer>
                                <transformer
                                        implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>com.tunyl.senence.WordCountTopology</mainClass>
                                </transformer>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>1.6</source>
                    <target>1.6</target>
                </configuration>
            </plugin>

        </plugins>
    </build>


</project>
```
3.修改simple.yaml

设置strom.cluster.mode 的值为distributed

```yml
#storm.cluster.mode: "local"
storm.cluster.mode: "distributed"

topology.spout.parallel: 1
topology.bolt.parallel: 1
#topology.message.timeout.secs: 300
topology.workers: 2
topology.name: "test123"


```
4.打jar包
```bash
mvn install

```
5.运行命令
将simple.yaml拷贝jar包所在的目录里，然后执行这个命令
```bahs
jstorm jar split-string-1.0-SNAPSHOT.jar com.tunyl.senence.WordCountTopology simple.yaml

```
### 二、本地运行

1.修改pom.xml配置

```xml

<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.tunyl.jsex</groupId>
    <artifactId>split-string</artifactId>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
        <dependency>
            <groupId>com.alibaba.jstorm</groupId>
            <artifactId>jstorm-core</artifactId>
            <version>2.1.1</version>
            <!--本地运行的时候注释下行，提交时记得要放开注释-->
            <!--<scope>provided</scope>-->
             <exclusions>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-api</artifactId>
                </exclusion>
                <exclusion> <!-- we prefer our explicit version, though it should be the same -->
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-nop</artifactId>
                </exclusion>
                <exclusion> <!-- we prefer our explicit version, though it should be the same -->
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-jdk14</artifactId>
                </exclusion>
            </exclusions>
           
        </dependency>
        
        
         <!-- 日志-->
        <dependency>
            <groupId>com.twitter</groupId>
            <artifactId>chill-java</artifactId>
            <version>0.9.2</version>
        </dependency>

        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>log4j-over-slf4j</artifactId>
            <version>1.7.10</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.0.13</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
            <version>1.7.5</version>
        </dependency>
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.17</version>
        </dependency>


    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <configuration>
                    <!--<forkCount>1</forkCount>-->
                    <forkMode>pertest</forkMode>
                    <argLine>-Xms1024m -Xmx4096m</argLine>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>1.7.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <transformers>
                                <transformer
                                        implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                                    <resource>META-INF/spring.handlers</resource>
                                </transformer>
                                <transformer
                                        implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                                    <resource>META-INF/spring.schemas</resource>
                                </transformer>
                                <transformer
                                        implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>com.tunyl.senence.WordCountTopology</mainClass>
                                </transformer>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>1.6</source>
                    <target>1.6</target>
                </configuration>
            </plugin>

        </plugins>
    </build>

</project>

```

2.修改simple.yaml

设置strom.cluster.mode 的值为local

```yml
storm.cluster.mode: "local"
#storm.cluster.mode: "distributed"

topology.spout.parallel: 1
topology.bolt.parallel: 1
#topology.message.timeout.secs: 300
topology.workers: 2
topology.name: "test123"


```
3.运行main函数