# 各种框架 maven 坐标

## 1 maven 坐标

### log4j

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
    <version>4.2.9.RELEASE</version>
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



### Hibernate

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.0.7.Final</version>
</dependency>
```

### fastJson

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

```xml
<!--阿里 druid 连接池-->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.1.5</version>
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
```

### 2 logback.xml

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