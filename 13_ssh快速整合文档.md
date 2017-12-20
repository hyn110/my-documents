# SSH快速整合

​	使用的框架 : `struts 2.3.32`   +  `spring 4.2.9`  + `hibernate 5.0.7`  + `mysql-connector-java-5.1.39`

## 1 相关jar包

​	**struts 相关包 :** 

​		![](struts.jpg)

​		![](struts2.png)

​	**hibernate 核心包 :** 

​		![](hibernate.jpg)

​	**c3p0 连接池 :** 

​		![](c3p0.png)

​	**spring 相关 :**

​		![](spring1.png)

​		![](spring2.png)

​	**log 日志相关 :** 

​		![](log4j.png)

​	**jdbc 驱动 :** 

​		![](jdbc.png)

## 2 配置文件

### 1 web.xml

```html
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns="http://java.sun.com/xml/ns/javaee"
	xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
	id="WebApp_ID" version="2.5">
	<display-name>crm</display-name>
	<welcome-file-list>
		<welcome-file>index.html</welcome-file>
		<welcome-file>index.htm</welcome-file>
		<welcome-file>index.jsp</welcome-file>
	</welcome-file-list>

	<!-- spring ioc启动 -->
	<listener>
		<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
	</listener>
	<context-param>
		<param-name>contextConfigLocation</param-name>
		<param-value>classpath:applicationContext.xml</param-value>
	</context-param>

	<!-- 解决懒加载 -->
	<filter>
		<filter-name>openSession</filter-name>
		<filter-class>org.springframework.orm.hibernate5.support.OpenSessionInViewFilter</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>openSession</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>

	<!-- 前端控制器 -->
	<filter>
		<filter-name>struts2</filter-name>
		<filter-class>org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>struts2</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
</web-app>
```

### 2 log4j.properties

```js
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

### 打印传递给sql的参数  ###
log4j.logger.org.hibernate.type=TRACE
```

### 3 struts.xml

```html
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE struts PUBLIC
	"-//Apache Software Foundation//DTD Struts Configuration 2.3//EN"
	"http://struts.apache.org/dtds/struts-2.3.dtd">
<struts>
	<constant name="struts.devMode" value="true"></constant>

	<package name="normal-demo" namespace="/" extends="struts-default">
		<action name="*" method="do_{1}" class="XXXX">
			<result name="addSuccess">/index.jsp</result>
			
			<!-- 以流的形式做出响应,并告知浏览器输出文件类型是图片 -->
			<result name="streamDemo" type="stream">
				<param name="contentType">image/jpeg</param>
				<param name="inputName">imageStream</param>
				<param name="bufferSize">1024</param>
			</result>
		</action>
	</package>

	<package name="json-demo" namespace="/" extends="json-default">

		<action name="*" method="do_{1}" class="OOOO">
			<!-- 返回json字符串 -->
			<result name="jsonDemo" type="json">
				<param name="root">list</param>
				<param name="defaultEncoding">utf-8</param>
			</result>
		</action>
	</package>
</struts>
```
> 需要修改的地方 : 
>
> 1. 每个 `action` 标签的 `name`  ,  `method` ,  `class`  需要根据实际项目进行修改
> 2. 每个 `result` 标签的 `name` ,  `type` 
> 3. 如果要使用 `struts2` 的插件包输出 `json` 字符串 , 则 `package` 需要继承 `json-default` , 并且 `<result>` 的 `type="json"`

### 4 applicationContext.xml

```html
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
	xmlns:tx="http://www.springframework.org/schema/tx"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.2.xsd
		http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.2.xsd
		http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-4.2.xsd">

	<!-- 声明使用占位符 , 并指定占位符文件位置 -->
	<context:property-placeholder location="classpath:jdbc.properties" />

	<!-- 开启注解扫描 -->
	<context:annotation-config />

	<!-- 数据库连接池的配置信息 -->
	<bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
		<!-- 配置：驱动，url，用户名，密码 -->
		<property name="driverClass" value="${jdbc.driver}" />
		<property name="jdbcUrl" value="${jdbc.url}" />
		<property name="user" value="${jdbc.user}" />
		<property name="password" value="${jdbc.password}" />
	</bean>

	<!-- 整合hibernate -->
	<bean id="sessionFactory"
		class="org.springframework.orm.hibernate5.LocalSessionFactoryBean">

		<property name="dataSource" ref="dataSource" />
		<!-- 设置hibernate的相关属性 -->
		<property name="hibernateProperties">
			<props>
				<prop key="hibernate.dialect">${hibernate.dialect}</prop>
				<prop key="hibernate.show_sql">${hibernate.show_sql}</prop>
				<prop key="hibernate.format_sql">${hibernate.format_sql}</prop>
				<prop key="hibernate.hbm2ddl.auto">${hibernate.hbm2ddl.auto}</prop>
			</props>
		</property>
		<!-- 指定hibernate映射文件所在的位置 -->
		<property name="mappingDirectoryLocations">
			<array>
				<value>${hibernate.mapping.dir}</value>
			</array>
		</property>
	</bean>

	<!-- 声明事务管理器 -->
	<bean id="transactionManager"
		class="org.springframework.orm.hibernate5.HibernateTransactionManager">
		<property name="sessionFactory" ref="sessionFactory" />
	</bean>

	<!-- 注解方式的事务 -->
	<tx:annotation-driven transaction-manager="transactionManager" />


	<!-- 引入action service dao 声明 -->
	<!-- <import resource="applicationContext-action.xml" /> -->
	<!-- <import resource="applicationContext-service.xml" /> -->
	<!-- <import resource="applicationContext-dao.xml" /> -->

</beans>
```

> `applicationContext-xxx.xml` 是以 `import` 的形式导入 , 这样可使主配置文件 `applicationContext.xml` 看起来更加简洁

### 5 jdbc.properties

```js
## jdbc 数据库相关的设置 ##
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql:///crm
jdbc.user=root
jdbc.password=

## hibernate 相关设置
hibernate.dialect=org.hibernate.dialect.MySQL5Dialect
hibernate.show_sql=true
hibernate.format_sql=true
hibernate.hbm2ddl.auto=update

hibernate.mapping.dir=classpath:

```

> 数据库的信息,必须根据自己的数据库修改 `url` , `user`  , `password`
>
> `hibernate.mapping.dir` 是`hibernate` 映射文件所属的目录 ,  比如 `classpath: com/itheima/domian` **注意 : 是路径,所以用 `/` 而不是 `.`**







### 