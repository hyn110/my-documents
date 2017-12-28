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



## 2 maven 插件

```xml
<build>
    <plugins>
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
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-war-plugin</artifactId>
            <version>2.6</version>
            <configuration>
                <!-- 配置后项目中没有web.xml文件时,项目不提示错误 -->
                <failOnMissingWebXml>false</failOnMissingWebXml
            </configuration>
        </plugin>
    </plugins>
</build>
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
log4j.logger.org.hibernate.type=TRACE

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