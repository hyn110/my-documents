# 各种框架 maven 坐标

## 1 maven 坐标

### log4j

​	日志框架

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-log4j12</artifactId>
    <version>1.7.24</version>
</dependency>
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency>
```

### logback

​	日志框架 : 

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

> 包含的jar包 :
>
> logback-core-1.2.3.jar
> logback-classic-1.2.3.jar
> logback-examples-1.2.3.jar
> slf4j-api-1.7.25.jar

### shiro

​	权限框架 :  [官网参考文档](http://shiro.apache.org/spring.html)



### spring

#### 0 mvc 全家桶

```xml
<dependency>
    <artifactId>spring-context</artifactId>
    <groupId>org.springframework</groupId>
    <version>4.3.12.RELEASE</version>
</dependency>
<!--javaMail 邮件发送,定时任务等的支持-->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context-support</artifactId>
    <version>4.3.12.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>4.3.12.RELEASE</version>
</dependency>
<!--springMVC 实现文件上传需要的依赖-->
<dependency>
    <groupId>commons-fileupload</groupId>
    <artifactId>commons-fileupload</artifactId>
    <version>1.3.1</version>
</dependency>
<!--springMVC 自动将对象转为 json 需要的依赖-->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.9.0</version>
</dependency>
<!-- spring MVC 数据格式化 @DateTimeFormat @NumberFormat 需要的依赖 -->
<dependency>
    <groupId>joda-time</groupId>
    <artifactId>joda-time</artifactId>
    <version>2.9.9</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-orm</artifactId>
    <version>4.3.12.RELEASE</version>
</dependency>

<!--切面相关-->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aspects</artifactId>
    <version>4.3.12.RELEASE</version>
</dependency>
<!--提供 xml 与对象之间转换的支持-->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-oxm</artifactId>
    <version>4.3.12.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-test</artifactId>
    <version>4.3.12.RELEASE</version>
    <scope>test</scope>
</dependency>
<!-- https://mvnrepository.com/artifact/junit/junit -->
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
    <scope>test</scope>
</dependency>

<!-- base64 编码 -->
<dependency>
    <groupId>commons-codec</groupId>
    <artifactId>commons-codec</artifactId>
    <version>1.11</version>
</dependency>

<!--工具类-->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.5</version>
</dependency>

<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>servlet-api</artifactId>
    <version>2.5</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>jsp-api</artifactId>
    <version>2.0</version>
    <scope>provided</scope>
</dependency>
 <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>jstl</artifactId>
            <version>1.2</version>
</dependency>
```



#### 1 核心库

```xml
<!-- http://mvnrepository.com/artifact/org.springframework/spring-context -->
<dependency>
    <artifactId>spring-context</artifactId>
    <groupId>org.springframework</groupId>
    <version>4.3.12.RELEASE</version>
</dependency>
<!--javaMail 邮件发送,定时任务等的支持-->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context-support</artifactId>
    <version>4.3.12.RELEASE</version>
</dependency>
```

> 会自动依赖 :
>
> spring-aop , spring-beans , spring-core , spring-expression

#### 2 MVC

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>4.3.12.RELEASE</version>
</dependency>
```

> 自动依赖 :
>
> spring 核心库 + spring-web

#### 3 ORM

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-orm</artifactId>
    <version>4.3.12.RELEASE</version>
</dependency>
```

> 自动依赖 :
>
> spring-jdbc , spring-tx , spring-core , spring-beans

#### 4 webmvc 依赖

```xml
     	<!--springMVC 实现文件上传需要的依赖-->
        <dependency>
            <groupId>commons-fileupload</groupId>
            <artifactId>commons-fileupload</artifactId>
            <version>1.3.1</version>
        </dependency>

        <!--springMVC 自动将对象转为 json 需要的依赖-->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.9.0</version>
        </dependency>

        <!-- spring MVC 数据格式化 @DateTimeFormat @NumberFormat 需要的依赖 -->
        <dependency>
            <groupId>joda-time</groupId>
            <artifactId>joda-time</artifactId>
            <version>2.9.9</version>
        </dependency>
```

#### 5 OXM

```xml
<!--提供 xml 与对象之间转换的支持-->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-oxm</artifactId>
    <version>4.3.12.RELEASE</version>
</dependency>
```

#### 6 spring-session

```
<dependency>
  <groupId>org.springframework.session</groupId>
  <artifactId>spring-session</artifactId>
  <version>1.3.3.RELEASE</version>
</dependency>
```



### hibernate

​	经典的 orm 框架,不解释

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.0.7.Final</version>
</dependency>
```

### fastJson

​	阿里出品 , 用于 java 对象和 json 数据之间的互转

```xml
<!-- https://mvnrepository.com/artifact/com.alibaba/fastjson -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.38</version>
</dependency>
```

### c3p0 连接池

```xml
<!--c3p0 连接池-->
<dependency>
    <groupId>c3p0</groupId>
    <artifactId>c3p0</artifactId>
    <version>0.9.1.2</version>
</dependency>
```

### druid 阿里druid连接池

​	阿里开源的线程池技术

```xml
<!--阿里 druid 连接池-->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.1.5</version>
</dependency>
```

### javamail 邮件发送

​	通过 java 代码实现邮件的发送

```xml
 <!-- https://mvnrepository.com/artifact/com.sun.mail/javax.mail -->
 <dependency>
     <groupId>com.sun.mail</groupId>
     <artifactId>javax.mail</artifactId>
     <version>1.4.4</version>
 </dependency>
```

### freemarker 模版

​	一个模板技术

```xml
<!-- https://mvnrepository.com/artifact/freemarker/freemarker -->
<dependency>
    <groupId>freemarker</groupId>
    <artifactId>freemarker</artifactId>
    <version>2.3.9</version>
</dependency>
```

### cxf 

​	apache 下的一个 WebService 框架

```Xml
<!-- https://mvnrepository.com/artifact/org.apache.cxf/cxf-rt-frontend-jaxws -->
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-rt-frontend-jaxws</artifactId>
    <version>3.0.1</version>
</dependency>

<!-- https://mvnrepository.com/artifact/org.apache.cxf/cxf-rt-transports-http -->
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-rt-transports-http</artifactId>
    <version>3.0.1</version>
</dependency>
```

### swagger2

​	用于 spring mvc 生成文档和测试

```xml
<!--Swagger2 依赖-->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.2.2</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.2.2</version>
</dependency>
<!-- https://mvnrepository.com/artifact/io.github.swagger2markup/swagger2markup -->
<dependency>
    <groupId>io.github.swagger2markup</groupId>
    <artifactId>swagger2markup</artifactId>
    <version>1.3.1</version>
</dependency>
```

### lombok

​	用于简化bean对象的书写

```xml
<!-- https://mvnrepository.com/artifact/org.projectlombok/lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.16.18</version>
    <scope>provided</scope>
</dependency>
```

### dubbo

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>dubbo</artifactId>
    <version>2.6.0</version>
</dependency>
```

### zookeeper

```xml
<!-- ZK -->
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.4.9</version>
    <exclusions>
        <exclusion>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>com.101tec</groupId>
    <artifactId>zkclient</artifactId>
    <version>0.2</version>
    <exclusions>
        <exclusion>
            <artifactId>slf4j-api</artifactId>
            <groupId>org.slf4j</groupId>
        </exclusion>
        <exclusion>
            <artifactId>log4j</artifactId>
            <groupId>log4j</groupId>
        </exclusion>
        <exclusion>
            <artifactId>slf4j-log4j12</artifactId>
            <groupId>org.slf4j</groupId>
        </exclusion>
    </exclusions>
</dependency>
```

### jedis

```xml
<!-- https://mvnrepository.com/artifact/redis.clients/jedis -->
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>2.9.0</version>
</dependency>

<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-redis</artifactId>
    <version>1.8.8.RELEASE</version>
</dependency>
```

### mysql-connector-java

```xml
 <dependency>
     <groupId>mysql</groupId>
     <artifactId>mysql-connector-java</artifactId>
     <version>5.1.40</version>
 </dependency>
```

### mybatis

```xml
<!-- https://mvnrepository.com/artifact/org.mybatis/mybatis -->
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.4.5</version>
</dependency>
<!--mybatis 和 spring 整合-->
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis-spring</artifactId>
    <version>1.3.1</version>
</dependency>

 <dependency>
            <groupId>org.mybatis.generator</groupId>
            <artifactId>mybatis-generator-core</artifactId>
            <version>1.3.5</version>
</dependency>
```

### mybatis-plus

```xml
<!-- http://baomidou.oschina.io/mybatis-plus-doc/#/quick-start -->
<dependency>
	<groupId>com.baomidou</groupId>
	<artifactId>mybatis-plus</artifactId>
	<version>2.1.8</version>
</dependency>
```

### mybatis 通用 mapper

```xml
<!-- https://mvnrepository.com/artifact/tk.mybatis/mapper -->
<dependency>
    <groupId>tk.mybatis</groupId>
    <artifactId>mapper</artifactId>
    <version>4.0.2</version>
</dependency>
```

[文档](https://github.com/abel533/Mybatis-Spring)

### mybatis 分页插件

```xml
<!--mybatis 分页插件-->
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper</artifactId>
    <version>5.1.2</version>
</dependency>
<dependency>
    <groupId>com.github.miemiedev</groupId>
    <artifactId>mybatis-paginator</artifactId>
    <version>1.2.17</version>
</dependency>
```

### zxing 二维码工具

```xml
     <!-- https://mvnrepository.com/artifact/com.google.zxing/core -->
        <!-- 二维码 -->
        <dependency>
            <groupId>com.google.zxing</groupId>
            <artifactId>core</artifactId>
            <version>3.3.0</version>
        </dependency>
        <dependency>
            <groupId>com.google.zxing</groupId>
            <artifactId>javase</artifactId>
            <version>3.3.0</version>
            <!--<version>${zxing.version}</version>-->
            <!--<scope>provided</scope>-->
        </dependency>
```

### redission

​	https://github.com/redisson/redisson/wiki/1.-%E6%A6%82%E8%BF%B0

​	Redisson是一个在Redis的基础上实现的Java驻内存数据网格（In-Memory Data Grid）。它不仅提供了一系列的分布式的Java常用对象，还提供了许多分布式服务。其中包括(`BitSet`, `Set`, `Multimap`, `SortedSet`, `Map`, `List`, `Queue`, `BlockingQueue`, `Deque`, `BlockingDeque`, `Semaphore`, `Lock`, `AtomicLong`, `CountDownLatch`, `Publish / Subscribe`, `Bloom filter`, `Remote service`, `Spring cache`, `Executor service`, `Live Object service`, `Scheduler service`) Redisson提供了使用Redis的最简单和最便捷的方法。Redisson的宗旨是促进使用者对Redis的关注分离（Separation of Concern），从而让使用者能够将精力更集中地放在处理业务逻辑上。

```xml
<!-- JDK 1.8+ compatible -->
<dependency>
   <groupId>org.redisson</groupId>
   <artifactId>redisson</artifactId>
   <version>3.6.1</version>
</dependency>  

<!-- JDK 1.6+ compatible -->
<dependency>
   <groupId>org.redisson</groupId>
   <artifactId>redisson</artifactId>
   <version>2.11.1</version>
</dependency>
```

### Ehcache

```
 <!-- 缓存 -->
        <dependency>
            <groupId>net.sf.ehcache</groupId>
            <artifactId>ehcache</artifactId>
            <version>2.10.0</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis-ehcache</artifactId>
            <version>1.0.0</version>
            <exclusions>
                <exclusion>
                    <groupId>net.sf.ehcache</groupId>
                    <artifactId>ehcache-core</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
```



## 2 maven 插件

### 1 maven-compiler-plugin 编译插件

```xml
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.6.2</version>
            <configuration>
                <!-- 配置使用的 jdk 版本 -->
                <target>1.8</target>
                <source>1.8</source>
              	<encoding>UTF-8</encoding>
              	<!--项目中在 lib 目录下放置 jar 时需声明,否则打包时jar丢失-->
              	<compilerArguments>
                	<extdirs>${project.basedir}/src/main/webapp/WEB-INF/lib</extdirs>
              	</compilerArguments>
            </configuration>
        </plugin>
```

### 2 tomcat7-maven-plugin 

```xml
        <plugin>
            <groupId>org.apache.tomcat.maven</groupId>
            <artifactId>tomcat7-maven-plugin</artifactId>
            <version>2.2</version>
            <configuration>
                <!-- 配置上下文路径和端口号 -->
                <path>/</path>
                <port>8083</port>
            </configuration>
        </plugin>
```

### 3 maven-war-plugin

​	web 应用打包插件

```xml
	<plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-war-plugin</artifactId>
            <version>2.6</version>
            <configuration>
                <!-- 配置后项目中没有web.xml文件时,项目不提示错误 -->
                <failOnMissingWebXml>false</failOnMissingWebXml>
            </configuration>
        </plugin>
```

### 4 mybatis-generator-maven-plugin

​	MyBatis 模版代码生成插件 , 需要提供配置文件

```xml
<plugin>
    <groupId>org.mybatis.generator</groupId>
    <artifactId>mybatis-generator-maven-plugin</artifactId>
    <version>1.3.2</version>
    <configuration>
        <!--配置文件的位置-->
        <configurationFile>src/main/java/generatorConfig.xml</configurationFile>
        <verbose>true</verbose>
        <overwrite>true</overwrite>
    </configuration>
    <dependencies>
        <dependency>
            <!--插件单独指定依赖驱动jar,否则报找不到驱动异常-->
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.40</version>
        </dependency>
    </dependencies>
</plugin>
```

### 5 swagger2markup-maven-plugin

​	通过maven 插件将 swagger 文档生成静态文件保存到磁盘

```xml
<plugin>
    <groupId>io.github.swagger2markup</groupId>
    <artifactId>swagger2markup-maven-plugin</artifactId>
    <version>1.3.1</version>
    <configuration>
        <!--指定项目中 swagger2 文档的 url-->
        <swaggerInput>
            http://localhost:8080/v2/api-docs
        </swaggerInput>
        <!--生成多个文件,放置在指定目录下-->
        <!--<outputDir>docs/asciidoc/generated/all</outputDir>-->
        <!--生成单个文件,文件名为 api-->
        <outputFile>docs/asciidoc/generated/api</outputFile>
        <config>
            <!--指定生成的文档类型,可选 : ASCIIDOC、MARKDOWN、CONFLUENCE-->
            <swagger2markup.markupLanguage>MARKDOWN</swagger2markup.markupLanguage>
        </config>
    </configuration>
</plugin>
<!--将swagger2markup生成的 asciidoc 文件转成 html 插件-->
<plugin>
    <groupId>org.asciidoctor</groupId>
    <artifactId>asciidoctor-maven-plugin</artifactId>
    <version>1.5.6</version>
    <configuration>
        <!--源文件目录-->
        <sourceDirectory>docs/asciidoc/generated</sourceDirectory>
        <!--输出目录-->
        <outputDirectory>docs/asciidoc/html</outputDirectory>
        <backend>html</backend>
        <sourceHighlighter>coderay</sourceHighlighter>
        <attributes>
            <toc>left</toc>
        </attributes>
    </configuration>
</plugin>
```

### 6 build-helper-maven-plugin 

```xml
<!-- 指定多个源代码目录、多个资源文件目录 -->
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>build-helper-maven-plugin</artifactId>
        <version>1.8</version>
        <executions>
          <execution>
            <id>add-source</id>
            <phase>generate-sources</phase>
            <goals>
              <goal>add-source</goal>
            </goals>
            <configuration>
              <sources>
                <source>src/java/main</source>
                <source>src/java/generated</source>
              </sources>
            </configuration>
          </execution>
        </executions>
      </plugin>
```

### 7 打包多个资源目录

```xml
 <build>  
        <finalName>test</finalName>  
        <!--  
        这样也可以把所有的xml文件，打包到相应位置。  -->
        <resources>  
            <resource>  
                <directory>src/main/resources</directory>  
                <includes>  
                    <include>**/*.properties</include>  
                    <include>**/*.xml</include>  
                    <include>**/*.tld</include>  
                </includes>  
                <filtering>false</filtering>  
            </resource>  
            <resource>  
                <directory>src/main/java</directory>  
                <includes>  
                    <include>**/*.properties</include>  
                    <include>**/*.xml</include>  
                    <include>**/*.tld</include>  
                </includes>  
                <filtering>false</filtering>  
            </resource>  
        </resources>  
    </build>  
```

### 8 maven-resources-plugin

```xml
  <!--    
            利用此plugin，把源代码中的xml文件，打包到相应位置，  
            这里主要是为了打包Mybatis的mapper.xml文件   
            -->  
            <plugin>  
                <artifactId>maven-resources-plugin</artifactId>  
                <version>2.5</version>  
                <executions>  
                    <execution>  
                        <id>copy-xmls</id>  
                        <phase>process-sources</phase>  
                        <goals>  
                            <goal>copy-resources</goal>  
                        </goals>  
                        <configuration>  
                            <outputDirectory>${basedir}/target/classes</outputDirectory>  
                            <resources>  
                                <resource>  
                                    <directory>${basedir}/src/main/java</directory>  
                                    <includes>  
                                        <include>**/*.xml</include>  
                                    </includes>  
                                </resource>  
                            </resources>  
                        </configuration>  
                    </execution>  
                </executions>  
            </plugin>     
```

### 9 mybatis-generator-maven-plugin

```xml
 <plugin>
                <groupId>org.mybatis.generator</groupId>
                <artifactId>mybatis-generator-maven-plugin</artifactId>
                <version>1.3.2</version>
                <configuration>
                    <!--配置文件的位置-->
                    <configurationFile>src/main/resources/mybatis-generator-config.xml</configurationFile>
                    <verbose>true</verbose>
                    <overwrite>true</overwrite>
                </configuration>
                <dependencies>
                    <dependency>
                        <!--插件单独指定依赖驱动jar,否则报找不到驱动异常-->
                        <groupId>mysql</groupId>
                        <artifactId>mysql-connector-java</artifactId>
                        <version>5.1.39</version>
                    </dependency>
                </dependencies>
            </plugin>
```



## 3 常用的配置文件模版

### 1 log4j.properties

```properties
##设置日志记录到控制台的方式
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target=System.err
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss,SSS} %5p %c{1}:%L - %m%n

##设置日志记录到文件的方式
log4j.appender.file=org.apache.log4j.FileAppender
log4j.appender.file.File=mylog.log
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss,SSS} %5p %c{1}:%L - %m%n

##日志输出的级别，以及配置记录方案
log4j.rootLogger=info, stdout 

## 单独设置 hibernate sql 日志的级别（可看到绑定参数值）
# log4j.logger.org.hibernate.type=TRACE

#------------------------------------------------------------------------  
#    %m 输出代码中指定的消息   
#    %p 输出优先级，即DEBUG，INFO，WARN，ERROR，FATAL   
#    %r 输出自应用启动到输出该log信息耗费的毫秒数   
#    %c 输出所属的类目，通常就是所在类的全名   
#    %t 输出产生该日志事件的线程名   
#    %n 输出一个回车换行符，Windows平台为“rn”，Unix平台为“n”   
#    %d 输出日志时间点的日期或时间，默认格式为ISO8601，也可以在其后指定格式，比如：%d{yyyy MMM dd HH:mm:ss,SSS}，输出类似：2002年10月18日 ：10：28，921  
#    %l 输出日志事件的发生位置，包括类目名、发生的线程，以及在代码中的行数。  
#    %x Used to output the NDC (nested diagnostic context) associated with the thread that generated the logging event  
#    %X Used to output the MDC (mapped diagnostic context) associated with the thread that generated the logging event for specified key  
#------------------------------------------------------------------------  
```

### 2 logback.xml

​	下面这个配置将 info 信息和 error 信息分别输出到两个文件中 !!!

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<configuration>
    <!--定义日志文件的存储地址 勿在 LogBack 的配置中使用相对路径 -->
    <property name="LOG_HOME" value="c:/00_mmall_log"/>
    <property name="appName" value="mmall"/>

    <!-- 设置应用的名字 -->
    <contextName>${appName}</contextName>

    <appender name="consoleLog" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>
                %d{yyyy-MM-dd HH:mm:ss} %5p %c{1}:%L - %m%n
            </pattern>
        </encoder>
    </appender>

    <appender name="fileInfoLog" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!--只输出 info , warn 级别日志(error 级别被过滤掉了)-->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>DENY</onMatch>
            <onMismatch>ACCEPT</onMismatch>
        </filter>

        <encoder>
            <pattern>
                %d{yyyy-MM-dd HH:mm:ss} %5p %c{1}:%L - %m%n
            </pattern>
        </encoder>

        <!--&lt;!&ndash;基于时间的滚动策略&ndash;&gt;-->
        <!--<rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">-->
            <!--&lt;!&ndash;路径&ndash;&gt;-->
            <!--<fileNamePattern>${LOG_HOME}/error.%d.log</fileNamePattern>-->
            <!--&lt;!&ndash;超过30天的日志将被删除&ndash;&gt;-->
            <!--<MaxHistory>30</MaxHistory>-->
        <!--</rollingPolicy>-->
        <!--&lt;!&ndash; 基于文件大小的滚动策略&ndash;&gt;-->
        <!--<triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">-->
            <!--<MaxFileSize>10MB</MaxFileSize>-->
        <!--</triggeringPolicy>-->

        <!--同时基于文件大小和时间的滚动策略-->
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!--路径-->
            <fileNamePattern>${LOG_HOME}/info.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <!-- 单文件最大 50 MB,最多保存 30 天的日志内容 , 日志文件总大小不超过 20 GB -->
            <maxFileSize>50MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>20GB</totalSizeCap>
        </rollingPolicy>

    </appender>

    <appender name="fileErrorLog" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!--只输出 error 级别日志-->
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>ERROR</level>
        </filter>

        <!--同时基于文件大小和时间的滚动策略-->
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!--路径-->
            <fileNamePattern>${LOG_HOME}/error.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <!-- 单文件最大 50 MB,最多保存 60 天的日志内容 , 日志文件总大小不超过 20 GB -->
            <maxFileSize>50MB</maxFileSize>
            <maxHistory>60</maxHistory>
            <totalSizeCap>20GB</totalSizeCap>
        </rollingPolicy>

        <encoder>
            <pattern>
                %d{yyyy-MM-dd HH:mm:ss} %5p %c{1}:%L - %m%n
            </pattern>
        </encoder>

    </appender>

    <!--日志异步到数据库 -->
    <!--<appender name="DB" class="ch.qos.logback.classic.db.DBAppender">-->
        <!--&lt;!&ndash;日志异步到数据库&ndash;&gt;-->
        <!--<connectionSource class="ch.qos.logback.core.db.DriverManagerConnectionSource">-->
            <!--&lt;!&ndash;连接池&ndash;&gt;-->
            <!--<dataSource class="com.mchange.v2.c3p0.ComboPooledDataSource">-->
                <!--<driverClass>com.mysql.jdbc.Driver</driverClass>-->
                <!--<url>jdbc:mysql://127.0.0.1:3306/databaseName</url>-->
                <!--<user>root</user>-->
                <!--<password>root</password>-->
            <!--</dataSource>-->
        <!--</connectionSource>-->
    <!--</appender>-->


    <!-- 定制 Hibernate 日志 -->
    <logger name="org.hibernate.type.descriptor.sql.BasicBinder" level="TRACE"/>
    <logger name="org.hibernate.type.descriptor.sql.BasicExtractor" level="DEBUG"/>
    <logger name="org.hibernate.SQL" level="DEBUG"/>
    <logger name="org.hibernate.engine.QueryParameters" level="DEBUG"/>
    <logger name="org.hibernate.engine.query.HQLQueryPlan" level="DEBUG"/>

    <!-- 定制 mybatis 日志-->
    <logger name="com.apache.ibatis" level="TRACE"/>
    <logger name="java.sql.Connection" level="DEBUG"/>
    <logger name="java.sql.Statement" level="DEBUG"/>
    <logger name="java.sql.PreparedStatement" level="DEBUG"/>

    <root level="info">
        <appender-ref ref="consoleLog"/>
        <appender-ref ref="fileInfoLog"/>
        <appender-ref ref="fileErrorLog"/>
    </root>

</configuration>
```



### 3 c3p0连接池

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <!-- 一、使用c3p0连接池注册数据源 -->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <!-- 基础配置 -->
        <property name="jdbcUrl" value="${jdbc.url}"></property>
        <property name="driverClass" value="${jdbc.driver}"></property>
        <property name="user" value="${jdbc.user}"></property>
        <property name="password" value="${jdbc.password}"></property>

        <!-- 关键配置 -->
        <!--初始化时获取三个连接，取值应在minPoolSize与maxPoolSize之间。Default: 3 -->
        <property name="initialPoolSize" value="3"></property>
        <!--连接池中保留的最小连接数。Default: 2 -->
        <property name="minPoolSize" value="2"></property>
        <!--连接池中保留的最大连接数。Default: 15 -->
        <property name="maxPoolSize" value="15"></property>
        <!--当连接池中的连接耗尽的时候c3p0一次同时获取的连接数。Default: 3 -->
        <property name="acquireIncrement" value="3"></property>

        <!-- 性能配置 -->
        <!-- 控制数据源内加载的PreparedStatements数量。如果maxStatements与maxStatementsPerConnection均为0，则缓存被关闭。Default: 0 -->
        <property name="maxStatements" value="8"></property>
        <!-- maxStatementsPerConnection定义了连接池内单个连接所拥有的最大缓存statements数。Default: 0 -->
        <property name="maxStatementsPerConnection" value="5"></property>
        <!--最大空闲时间,1800秒内未使用则连接被丢弃。若为0则永不丢弃。Default: 0 -->
        <property name="maxIdleTime" value="1800"></property>
    </bean>

</beans>
```

### 4 druid 连接池

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <!-- 一、使用druid数据库连接池注册数据源 -->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <!-- 基础配置 -->
        <property name="url" value="${jdbc.url}"></property>
        <property name="driverClassName" value="${jdbc.driver}"></property>
        <property name="username" value="${jdbc.user}"></property>
        <property name="password" value="${jdbc.password}"></property>

        <!-- 关键配置 -->
        <!-- 初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时 -->
        <property name="initialSize" value="3" />
        <!-- 最小连接池数量 -->
        <property name="minIdle" value="2" />
        <!-- 最大连接池数量 -->
        <property name="maxActive" value="15" />
        <!-- 配置获取连接等待超时的时间 -->
        <property name="maxWait" value="10000" />

        <!-- 性能配置 -->
        <!-- 打开PSCache，并且指定每个连接上PSCache的大小 -->
        <property name="poolPreparedStatements" value="true" />
        <property name="maxPoolPreparedStatementPerConnectionSize" value="20" />

        <!-- 其他配置 -->
        <!-- 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 -->
        <property name="timeBetweenEvictionRunsMillis" value="60000" />
        <!-- 配置一个连接在池中最小生存的时间，单位是毫秒 -->
        <property name="minEvictableIdleTimeMillis" value="300000" />
        <!--   建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，
                  执行validationQuery检测连接是否有效。 -->
        <property name="testWhileIdle" value="true" />
        <!-- 这里建议配置为TRUE，防止取到的连接不可用 ,申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。-->
        <property name="testOnBorrow" value="true" />
        <!-- 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能 -->
        <property name="testOnReturn" value="false" />
    </bean>

</beans>
```

> 占位符文件 jdbc.properties 的内容如下 :
>
> `jdbc.url=jdbc:mysql://127.0.0.1:3306/mmall?autoReconnect=true&useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull`
>
> `jdbc.driver=com.mysql.jdbc.Driver`
>
> `jdbc.user=root`
>
> `jdbc.password=`

### 5 druid 连接池监控servlet

```xml
<!--阿里 druid 连接池监控-->
<servlet>
    <servlet-name>DruidStatView</servlet-name>
    <servlet-class>com.alibaba.druid.support.http.StatViewServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>DruidStatView</servlet-name>
    <url-pattern>/druid/*</url-pattern>
</servlet-mapping>
<filter>
    <filter-name>druidWebStatFilter</filter-name>
    <filter-class>com.alibaba.druid.support.http.WebStatFilter</filter-class>
    <init-param>
        <!--排除一些不必要的url-->
        <param-name>exclusions</param-name>
        <param-value>/public/*,*.js,*.css,/druid*,*.jsp,*.swf</param-value>
    </init-param>
    <init-param>
        <!--让 druid 从session 中读取 user 的信息 , key 为 user-->
        <param-name>principalSessionName</param-name>
        <param-value>user</param-value>
    </init-param>
    <init-param>
        <!--配置profileEnable能够监控单个url调用的sql列表-->
        <param-name>profileEnable</param-name>
        <param-value>true</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>druidWebStatFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```
### 6 mybatis-generator-config

​	Mybatis 生成mapper文件需要使用的配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
        PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">

<generatorConfiguration>
    <!--指定数据库驱动的路径 , 使用maven 插件时可以不用,
        使用 Mybatis Plugin 插件生成代码时一定要指定,否则报找不到驱动    
    -->
    <!--<classPathEntry-->
            <!--location="D:\01_MAVEN_RESPOTY\mysql\mysql-connector-java\5.1.39\mysql-connector-java-5.1.39.jar"/>-->

    <context id="sqlserverTables" targetRuntime="MyBatis3">
        <!-- 生成的pojo，将implements Serializable -->
        <plugin type="org.mybatis.generator.plugins.SerializablePlugin"></plugin>
        <commentGenerator>
            <!-- 是否去除自动生成的注释 true：是 ： false:否 -->
            <property name="suppressAllComments" value="true"/>
        </commentGenerator>

        <!-- 数据库链接URL、用户名、密码 -->
        <jdbcConnection driverClass="com.mysql.jdbc.Driver"
                        connectionURL="jdbc:mysql://localhost:3306/mybatis"
                        userId="root"
                        password="123456">
        </jdbcConnection>

        <!-- 默认false，把JDBC DECIMAL 和 NUMERIC 类型解析为 Integer true，把JDBC DECIMAL
            和 NUMERIC 类型解析为java.math.BigDecimal -->
        <javaTypeResolver>
            <property name="forceBigDecimals" value="false"/>
        </javaTypeResolver>

        <!-- 生成model模型，对应的包路径，以及文件存放路径(targetProject)，
            targetProject可以指定具体的路径,如./src/main/java，
            也可以使用“MAVEN”来自动生成，这样生成的代码会在target/generatord-source目录下 -->
        <!--<javaModelGenerator targetPackage="com.joey.mybaties.test.pojo" targetProject="MAVEN"> -->
        <javaModelGenerator targetPackage="com.fmi110.mybatis.entity"
                            targetProject="./src/main/java">

            <property name="enableSubPackages" value="true"/>
            <!-- 从数据库返回的值被清理前后的空格 -->
            <property name="trimStrings" value="true"/>
        </javaModelGenerator>

        <!--对应的mapper.xml文件 -->
        <sqlMapGenerator targetPackage="com.fmi110.mybatis.mappers"
                         targetProject="./src/main/resources">
            <property name="enableSubPackages" value="true"/>
        </sqlMapGenerator>

        <!-- 对应的Mapper接口类文件 -->
        <javaClientGenerator type="XMLMAPPER"
                             targetPackage="com.fmi110.mybatis.mappers"
                             targetProject="./src/main/java">
            <property name="enableSubPackages" value="true"/>
        </javaClientGenerator>


        <!-- 列出要生成代码的所有表，这里配置的是不生成Example文件 -->

        <table tableName="student" domainObjectName="Student"
               enableCountByExample="false" enableUpdateByExample="false"
               enableDeleteByExample="false" enableSelectByExample="false"
               selectByExampleQueryId="false">
            <property name="useActualColumnNames" value="false"/>
        </table>
    </context>
</generatorConfiguration>
```

### 7 git 忽略文件清单 .gitignore

```
*.class

# package ignore
*.war
*.ear

# kdiff3 ignore
*.orig

#eclipse ignore
.settings/
.project
.classpath

#idea ignore
.idea/
/idea/
*.iml
*.ipr
*.iws

# temp file ignore
*.log
*.cahce
*.diff
*.patch
*.tmp

# system ignore
.DS_Store
Thumbs.db

# maven output ignore
target/
```

### 8 springmvc-config.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                        http://www.springframework.org/schema/beans/spring-beans-4.0.xsd
                        http://www.springframework.org/schema/mvc
                        http://www.springframework.org/schema/mvc/spring-mvc-4.0.xsd
                        http://www.springframework.org/schema/context
                        http://www.springframework.org/schema/context/spring-context-4.0.xsd">
    <!-- 关闭默认扫描策略,只扫描controller -->
    <context:component-scan base-package="com.fmi110" annotation-config="true" use-default-filters="false">
        <context:include-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
    </context:component-scan>
    <mvc:annotation-driven/>
    <!--静态资源交给 defaultServlet 处理,否则访问静态资源会 404-->
    <mvc:default-servlet-handler/>

    <!--spring 整合 swagger2-->
    <bean id="swagger2Config"
          class="springfox.documentation.swagger2.configuration.Swagger2DocumentationConfiguration"/>
    <mvc:resources location="classpath*:/META-INF/resources/" mapping="swagger-ui.html"/>
    <mvc:resources location="classpath*:/META-INF/resources/webjars/" mapping="/webjars/**"/>
</beans>
```

### 9 applicationContext.xml  (ssm)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans-4.2.xsd
		http://www.springframework.org/schema/context
		http://www.springframework.org/schema/context/spring-context-4.2.xsd
		http://www.springframework.org/schema/tx
		http://www.springframework.org/schema/tx/spring-tx-4.2.xsd">

    <!-- 声明使用占位符 , 并指定占位符文件位置 -->
    <context:property-placeholder location="classpath:jdbc.properties"/>
    <!-- 开启注解扫描 -->
    <!--
    <context:component-scan base-package="com.fmi110.mmall.service,com.fmi110.mmall.dao"/>
 	-->
    <context:component-scan base-package="com.fmi110.mmall">
        <!--排除 controller 扫描 , controller 有 DispatcherServlet 扫描-->
        <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
    <!-- 1 使用druid数据库连接池注册数据源 -->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <!-- 基础配置 -->
        <property name="url" value="${jdbc.url}"></property>
        <property name="driverClassName" value="${jdbc.driver}"></property>
        <property name="username" value="${jdbc.user}"></property>
        <property name="password" value="${jdbc.password}"></property>

        <!-- 关键配置 -->
        <!-- 初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时 -->
        <property name="initialSize" value="3"/>
        <!-- 最小连接池数量 -->
        <property name="minIdle" value="2"/>
        <!-- 最大连接池数量 -->
        <property name="maxActive" value="15"/>
        <!-- 配置获取连接等待超时的时间 -->
        <property name="maxWait" value="10000"/>

        <!-- 性能配置 -->
        <!-- 打开PSCache，并且指定每个连接上PSCache的大小 -->
        <property name="poolPreparedStatements" value="true"/>
        <property name="maxPoolPreparedStatementPerConnectionSize" value="20"/>

        <!-- 其他配置 -->
        <!-- 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 -->
        <property name="timeBetweenEvictionRunsMillis" value="60000"/>
        <!-- 配置一个连接在池中最小生存的时间，单位是毫秒 -->
        <property name="minEvictableIdleTimeMillis" value="300000"/>
        <!--   建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，
                  执行validationQuery检测连接是否有效。 -->
        <property name="testWhileIdle" value="true"/>
        <!-- 这里建议配置为TRUE，防止取到的连接不可用 ,申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。-->
        <property name="testOnBorrow" value="true"/>
        <!-- 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能 -->
        <property name="testOnReturn" value="false"/>
    </bean>

    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <!--指定映射文件路径-->
        <property name="mapperLocations" value="classpath*:mappers/*Mapper.xml"/>
        <!--分页插件-->
        <property name="plugins">
            <array>
                <bean class="com.github.pagehelper.PageInterceptor">
                    <!-- pagehelper 5.x 的配置跟 4.x 已经不同,具体参考文档-->
                    <!-- https://pagehelper.github.io/docs/howtouse/ -->
                    <property name="properties">
                        <value>
                            helperDialect=mysql
                            reasonable=true
                            supportMethodsArguments=true
                            params=count=countSql
                            autoRuntimeDialect=true
                        </value>
                    </property>
                </bean>
            </array>
        </property>
    </bean>
    <!--配置mapper接口的扫描-->
    <bean name="mapperScannerConfiguerer" class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <property name="basePackage" value="com.fmi110.mmall.dao"/>
    </bean>

    <tx:annotation-driven transaction-manager="transactionManager" proxy-target-class="true"/>

    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
        <property name="rollbackOnCommitFailure" value="true"/>
    </bean>
</beans>
```

### 10 web.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://java.sun.com/xml/ns/javaee"
         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
         id="WebApp_ID" version="2.5">
    <display-name>mmall</display-name>
    <welcome-file-list>
        <welcome-file>index.html</welcome-file>
        <welcome-file>index.htm</welcome-file>
        <welcome-file>index.jsp</welcome-file>
    </welcome-file-list>

    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath*:applicationContext*.xml</param-value>
    </context-param>

    <filter>
        <filter-name>characterEncodingFilter</filter-name>
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>characterEncodingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

    <servlet>
        <servlet-name>dispatcher</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <!--springmvc 配置文件 默认是 WEB-INF 目录下的 [servlet-name]-servlet.xml 文件-->
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:springmvc-config.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>dispatcher</servlet-name>
        <!-- "/" 写法不会覆盖默认的servlet , "/*" 会导致静态资源无法访问 , 需要另外配置 -->
        <url-pattern>/</url-pattern>
    </servlet-mapping>

    <!--########### druid 数据库连接池监测配置 ################-->
    <!--阿里 druid 连接池监控-->
    <servlet>
        <servlet-name>DruidStatView</servlet-name>
        <servlet-class>com.alibaba.druid.support.http.StatViewServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>DruidStatView</servlet-name>
        <url-pattern>/druid/*</url-pattern>
    </servlet-mapping>
    <filter>
        <filter-name>druidWebStatFilter</filter-name>
        <filter-class>com.alibaba.druid.support.http.WebStatFilter</filter-class>
        <init-param>
            <!--排除一些不必要的url-->
            <param-name>exclusions</param-name>
            <param-value>/public/*,*.js,*.css,/druid*,*.jsp,*.swf</param-value>
        </init-param>
        <init-param>
            <!--让 druid 从session 中读取 user 的信息 , key 为 user-->
            <param-name>principalSessionName</param-name>
            <param-value>user</param-value>
        </init-param>
        <init-param>
            <!--配置profileEnable能够监控单个url调用的sql列表-->
            <param-name>profileEnable</param-name>
            <param-value>true</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>druidWebStatFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
</web-app>
```



## 4 java对象配置类

### 1 springmvc 全局异常处理器

```java
package com.fmi110.commons;

import com.fmi110.utils.WebUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fmi110
 * @Description: 全局异常统一处理类
 * @Date 2018/2/27 11:08
 */
@Component
public class ExceptionResolver implements HandlerExceptionResolver{

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
                                         Exception ex) {

        /**
         * 在这里可以定制自己的异常处理策略
         */
//        if (ex instanceof MyException) {
//            return new ModelAndView("error-my",map);
//        } else {
//            return new ModelAndView("error",map);
//        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code",500);
        map.put("message","系统内部异常...");
        map.put("data", "这里填充需要返回的数据的对象");

        // 判断是否时ajax 请求
        boolean isAjax = WebUtils.isAjax((HandlerMethod) handler);
        if (isAjax) {
            // 视图内容将会转为 json 返回
            ModelAndView mv = new ModelAndView(new MappingJackson2JsonView());
            mv.addObject(map);
            return mv;
        }else{
            // 普通请求返回页面资源
            return new ModelAndView("error.jsp",map);
        }
    }
}

```

