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

#### 1 核心库

```xml
<!-- http://mvnrepository.com/artifact/org.springframework/spring-context -->
<dependency>
    <artifactId>spring-context</artifactId>
    <groupId>org.springframework</groupId>
    <version>4.3.12.RELEASE</version>
</dependency>
<!--javaMail 邮件发送等的支持-->
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

## 2 maven 插件

### 1 maven-compiler-plugin 编译插件

```xml
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.5.1</version>
            <configuration>
                <!-- 配置使用的 jdk 版本 -->
                <target>1.8</target>
                <source>1.8</source>
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
                <failOnMissingWebXml>false</failOnMissingWebXml
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
            <version>5.1.39</version>
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

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="false">
	

	<!--定义日志文件的存储地址 勿在 LogBack 的配置中使用相对路径 -->
	<property name="LOG_HOME" value="/home" />
	<property name="appName" value="fmi110-hello-springmvc" />
	
	
	<!-- 设置应用的名字 -->
	<contextName>${appName}</contextName>
	
	<!-- 控制台输出 -->
	<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
		<encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
			<!--格式化输出：	%d表示日期，
							%thread表示线程名，
							%-5level：级别从左显示5个字符宽度
							%msg：日志消息，%n是换行符 
							-->
			<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} -%msg%n</pattern>
		</encoder>
	</appender>
	
	<!-- 
	<appender name="FILE"
		class="ch.qos.logback.core.rolling.RollingFileAppender">
		<rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
			<FileNamePattern>${LOG_HOME}/TestWeb.log.%d{yyyy-MM-dd}.log
			</FileNamePattern>
			<MaxHistory>30</MaxHistory>
		</rollingPolicy>
		<encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
			<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} -
				%msg%n</pattern>
		</encoder>
		<triggeringPolicy
			class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
			<MaxFileSize>10MB</MaxFileSize>
		</triggeringPolicy>
	</appender>
	 -->
	
	<!-- show parameters for hibernate sql 专为 Hibernate 定制 -->
	<logger name="org.hibernate.type.descriptor.sql.BasicBinder"
		level="TRACE" />
	<logger name="org.hibernate.type.descriptor.sql.BasicExtractor"
		level="DEBUG" />
	<logger name="org.hibernate.SQL" level="DEBUG" />
	<logger name="org.hibernate.engine.QueryParameters" level="DEBUG" />
	<logger name="org.hibernate.engine.query.HQLQueryPlan" level="DEBUG" />

	<!--myibatis log configure -->
	<logger name="com.apache.ibatis" level="TRACE" />
	<logger name="java.sql.Connection" level="DEBUG" />
	<logger name="java.sql.Statement" level="DEBUG" />
	<logger name="java.sql.PreparedStatement" level="DEBUG" />

	<!-- 日志输出级别 -->
	<root level="INFO">
		<appender-ref ref="STDOUT" />
		<!-- <appender-ref ref="FILE" /> --> <!-- 如果要输入日志到文件,则解开此注解 -->
	</root>
	<!--日志异步到数据库 -->
	<!-- 
		<appender name="DB" class="ch.qos.logback.classic.db.DBAppender">
			日志异步到数据库
			<connectionSource
				class="ch.qos.logback.core.db.DriverManagerConnectionSource">
				连接池
				<dataSource class="com.mchange.v2.c3p0.ComboPooledDataSource">
					<driverClass>com.mysql.jdbc.Driver</driverClass>
					<url>jdbc:mysql://127.0.0.1:3306/databaseName</url>
					<user>root</user>
					<password>root</password>
				</dataSource>
			</connectionSource>
		</appender> 
	-->
</configuration>
```


```xml
<?xml version="1.0" encoding="UTF-8" ?>

<configuration>

    <appender name="consoleLog" class="ch.qos.logback.core.ConsoleAppender">
        <layout class="ch.qos.logback.classic.PatternLayout">
            <pattern>
                %d{yyyy-MM-dd HH:mm:ss} %5p %c{1}:%L - %m%n
            </pattern>
        </layout>
    </appender>

    <!--只输出 info , warn 级别日志(error 级别被过滤掉了)-->
    <appender name="fileInfoLog" class="ch.qos.logback.core.rolling.RollingFileAppender">
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
        <!--滚动策略-->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!--路径-->
            <fileNamePattern>dinnermall_log/info.%d.log</fileNamePattern>
        </rollingPolicy>
    </appender>

    <!--只输出 error 级别日志-->
    <appender name="fileErrorLog" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>ERROR</level>
        </filter>
        <encoder>
            <pattern>
                %d{yyyy-MM-dd HH:mm:ss} %5p %c{1}:%L - %m%n
            </pattern>
        </encoder>
        <!--滚动策略-->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!--路径-->
            <fileNamePattern>dinnermall_log/error.%d.log</fileNamePattern>
        </rollingPolicy>
    </appender>

    <root level="info">
        <appender-ref ref="consoleLog" />
        <appender-ref ref="fileInfoLog" />
        <appender-ref ref="fileErrorLog" />
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



## 4 java对象配置类