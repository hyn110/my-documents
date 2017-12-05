# Logback 使用

## 1 配置文件 logback.xml

​	配置文件放在 src 目录下 , 下面是一个基础配置:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<!-- For assistance related to logback-translator or configuration  -->
<!-- files in general, please contact the logback user mailing list -->
<!-- at http://www.qos.ch/mailman/listinfo/logback-user             -->
<!--                                                                -->
<!-- For professional support please see                            -->
<!--    http://www.qos.ch/shop/products/professionalSupport         -->
<!--                                                                -->
<configuration>
  <appender name="s" class="ch.qos.logback.core.ConsoleAppender">
    <Target>System.err</Target>
    <encoder>
      <pattern>%d{yyyy-MM-dd HH:mm:ss,SSS} %5p %c{1}:%L - %m%n</pattern>
    </encoder>
  </appender>
  <appender name="file" class="ch.qos.logback.core.FileAppender">
    <File>mylog.log</File>
    <encoder>
      <pattern>%d{yyyy-MM-dd HH:mm:ss,SSS} %5p %c{1}:%L - %m%n</pattern>
    </encoder>
  </appender>
  <root level="info">
    <appender-ref ref="s"/>
    <appender-ref ref="file"/>
  </root>
</configuration>
```

> `logback` 官网提供了 `log4j.properties` 文件到 `logback.xml` 的自动转换 , 网址 :  [转换连接](https://logback.qos.ch/translator/)

​	上面的配置文件对应的 log4j.properties 如下:

```properties
##设置日志记录到控制台的方式
log4j.appender.s=org.apache.log4j.ConsoleAppender
log4j.appender.s.Target=System.err
log4j.appender.s.layout=org.apache.log4j.PatternLayout
log4j.appender.s.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss,SSS} %5p %c{1}:%L - %m%n

##设置日志记录到文件的方式
log4j.appender.file=org.apache.log4j.FileAppender
log4j.appender.file.File=mylog.log
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss,SSS} %5p %c{1}:%L - %m%n

##日志输出的级别，以及配置记录方案
log4j.rootLogger=info, s, file 
```

> 注意 : 这是 log4j 的配置文件 , 并不能给 logback 使用!!!

## 2 依赖的 jar 包

```java
logback-core-1.2.3.jar
logback-classic-1.2.3.jar
logback-examples-1.2.3.jar
slf4j-api-1.7.25.jar
```

## 3 maven 依赖

```xml
<!-- 该依赖会自动关联依赖 slf4j-api.jar 和 logback-core.jar -->
<dependency>
  <groupId>ch.qos.logback</groupId>
  <artifactId>logback-classic</artifactId>
  <version>1.2.3</version>
</dependency>

<dependency>
  <groupId>ch.qos.logback</groupId>
  <artifactId>logback-access</artifactId>
  <version>1.2.3</version>
</dependency>
```

## 4 输出对象

​	`Currently, appenders exist for the console, files, remote socket servers, to MySQL, PostgreSQL, Oracle and other databases, JMS, and remote UNIX Syslog daemons`

