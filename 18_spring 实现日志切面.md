# 18_spring 实现日志切面

​	日志框架使用 log4j  

## 1 spring 配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
	xmlns:aop="http://www.springframework.org/schema/aop" xmlns:tx="http://www.springframework.org/schema/tx"
	xsi:schemaLocation="
		http://www.springframework.org/schema/beans 
		http://www.springframework.org/schema/beans/spring-beans.xsd
		http://www.springframework.org/schema/context 
		http://www.springframework.org/schema/context/spring-context.xsd
		http://www.springframework.org/schema/tx 
		http://www.springframework.org/schema/tx/spring-tx.xsd
		http://www.springframework.org/schema/aop 
		http://www.springframework.org/schema/aop/spring-aop.xsd
		">

	<context:component-scan base-package="cn.itcast.erp.aspect"/>
	<!-- 设置 expose-proxy="true" 后代理类中调用同类中的方法,也能被拦截-->
	<aop:aspectj-autoproxy proxy-target-class="true" expose-proxy="true"/>
</beans>
```

## 2 提供切面类

```java
package cn.itcast.erp.aspect;

import cn.itcast.erp.entity.Emp;
import com.opensymphony.xwork2.ActionContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 日志切面,用于记录系统操作日志
 */
@Component
@Aspect
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

//    String actionMessage = "{userid:userid1,username:username1,action:action1,method:method1,args:args1}";
    /**日志信息模版*/
//    String msgTemplate = "{" +
//                         "\"useId\": \"userId1\"," +
//                         "\"username\": \"username1\"," +
//                         "\"action\": \"action1\"," +
//                         "\"method\": \"method1\"," +
//                         "\"args\": \"args1\"" +
//                         "}";
    String msgTemplate = "{\n" +
                         "  \"useId\": \"userId1\",\n" +
                         "  \"username\": \"username1\",\n" +
                         "  \"action\": \"action1\",\n" +
                         "  \"method\": \"method1\",\n" +
                         "  \"args\": \"args1\"\n" +
                         "}";

    @Pointcut("execution(* cn.itcast.erp.action.*.*(..))")
    public void actionPointcut() {
    }

    @Pointcut("execution(* cn.itcast.erp.biz..*.*(..))")
    public void servicePointcut() {
    }

    @Pointcut("execution(* cn.itcast.erp.dao..*.*(..))")
    public void daoPointcut() {
    }

    @Around("actionPointcut() ||daoPointcut() ||servicePointcut()")
//    @Around("servicePointcut()")
    public Object recordLog(ProceedingJoinPoint point) throws Throwable {
        // 获取当前系统登录用户
        Emp currentUser = (Emp)ActionContext.getContext().getSession().get("loginUser");

        String methodName = point.getSignature()
                                 .getName(); // 获取方法名

        if(methodName.contains("set")){
            // set  方法不记录
            return point.proceed(); // 放行
        }

        // 获取拦截到的对象
        Object target = point.getTarget();

        String name = target.getClass()
                            .getName();
        // 获取方法的参数
        Object[] args = point.getArgs();


        String msg = msgTemplate.replace("userId1", (currentUser==null)?"":currentUser.getUuid() + "")
                                    .replace("username1",(currentUser==null)?"": currentUser.getName())
                                    .replace("action1", target.getClass()
                                                              .getCanonicalName())
                                    .replace("method1", methodName)
                                    .replace("args1", Arrays.toString(args));
        // 记录日志
        logger.info(msg);
        return point.proceed(); // 放行
    }
}

```

> 这里时直接复制的项目里的代码,实际使用时按照需求自己修改日志信息模版

## 3 配置 log4j 日志

​	下面的配置实现了,只将 `cn.itcast.erp.aspect.LogAspect`  包下输出的日志记录到数据库中(使用的mysql数据库) , 日志配置文件中关于 数据库连接信息部分的配置需要根据自己的实际环境进行修改,需要修改的配置项如下 :

> log4j.appender.database.URL=jdbc:mysql://localhost:3306/erp_log 	数据库连接 url
> log4j.appender.database.user=root									数据库用户名
> log4j.appender.database.password=								用户密码
>
> log4j.appender.database.sql										执行的sql语句

```properties
##设置日志记录到控制台的方式
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target=System.err
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %5p %c{1}:%L - %m%n

##设置日志记录到文件的方式
log4j.appender.file=org.apache.log4j.FileAppender
log4j.appender.file.File=d:\\erp.log
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %5p %c{1}:%L - %m%n


## 设置数据库连接信息
log4j.appender.database=org.apache.log4j.jdbc.JDBCAppender
log4j.appender.database.Threshold=info
# 产生3条记录时才输出到数据库
log4j.appender.database.BufferSize=3
log4j.appender.database.URL=jdbc:mysql://localhost:3306/erp_log
log4j.appender.database.driver=com.mysql.jdbc.Driver
log4j.appender.database.user=root
log4j.appender.database.password=
log4j.appender.database.layout=org.apache.log4j.PatternLayout
# Set the SQL statement to be executed.
log4j.appender.database.sql=INSERT INTO LOGS (oper_time,logger,level,message) VALUES ("%d{yyyy-MM-dd HH:mm:ss}","%c{1}","%p",'%m');
## 切面类的日志输出到数据库
log4j.logger.cn.itcast.erp.aspect.LogAspect=INFO,database

##日志输出的级别，以及配置记录方案
log4j.rootLogger=info,stdout
## 指定包下的日志通过 file 输出
#log4j.logger.com.fmi110=INFO,file
# 指定 LogAspect 类的日志输出到数据库
# log4j.logger.cn.itcast.erp.aspect.LogAspect=INFO,database


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

## 4 创建数据库

```sql
create database erp_log;
create table logs
(
	id bigint auto_increment
		primary key,
	oper_time timestamp default CURRENT_TIMESTAMP not null,
	LOGGER varchar(64) null,
	LEVEL varchar(10) null,
	MESSAGE varchar(2048) null
);
```