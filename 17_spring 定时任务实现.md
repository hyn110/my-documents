# 17_spring 定时任务实现

​	[定时任务常见框架](https://www.jianshu.com/p/780235132d81)

​	[定时任务的几种实现](http://gong1208.iteye.com/blog/1773177)

## 1 Spring-task 实现

​	spring-task 是 spring3.0以后自主开发的定时任务工具，可以将它比作一个轻量级的Quartz，而且使用起来很简单，除spring相关的包外不需要额外的包，而且支持注解和配置文件两种 . 下面演示最简单的继承案例:

### 0 maven 依赖

```xml
<!--spring 定时任务依赖-->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context-support</artifactId>
    <version>4.3.12.RELEASE</version>
</dependency>
```

### 1 配置 applicationContext_task.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:task="http://www.springframework.org/schema/task"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/task
       http://www.springframework.org/schema/task/spring-task.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd">

    <!-- 扫描包 -->
    <context:component-scan base-package="cn.itcast.erp.job"/>

    <bean id="taskExecutor" class="org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor">
        <!-- 核心线程数 -->
        <property name="corePoolSize" value="2" />
        <!-- 最大线程数 -->
        <property name="maxPoolSize" value="4" />
        <!-- 队列最大长度 -->
        <property name="queueCapacity" value="10" />
        <!-- 线程池维护线程所允许的空闲时间，默认为60s -->
        <property name="keepAliveSeconds" value="60" />
    </bean>

    <!-- 注解式,使用 cglib 代理 -->
    <task:annotation-driven executor="taskExecutor" proxy-target-class="true"/>
</beans>

```

### 2 提供任务类 , 并指定任务周期

​	这里使用注解 @Componnent 将任务类声明为 spring 组件 , 并使用 @Scheduled  指定任务周期

```java
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MailJob {

	@Scheduled(cron = "0/30 * * * * ?")
	public void doJob(){
		System.out.println("=========doJob========"+new Date());
		// 具体的任务逻辑
	}
}
```

### 3 将配置文件和任务类整合到项目中

​	配置项目加载对应的配置文件即可.

## 2 Quartz 实现

### 0 maven 依赖

```xml
<dependency>
    <groupId>org.quartz-scheduler</groupId>
    <artifactId>quartz</artifactId>
    <version>2.2.1</version>
</dependency>
<dependency>
    <groupId>org.quartz-scheduler</groupId>
    <artifactId>quartz-jobs</artifactId>
    <version>2.2.1</version>
</dependency>

<!--spring 定时任务依赖-->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context-support</artifactId>
    <version>4.3.12.RELEASE</version>
</dependency>
```

### 1 提供任务类

```java
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

public class MailJob {
	public void doJob(){
		System.out.println("=========doJob========"+new Date());
		// 具体的任务逻辑
	}
}
```

### 2 配置 applicationContext_quartz.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xsi:schemaLocation="
		http://www.springframework.org/schema/beans 
		http://www.springframework.org/schema/beans/spring-beans.xsd">  
	
	<!-- 声明任务类 , 也可以用注解 -->
	<bean id="mailJob" class="cn.itcast.erp.job.MailJob"></bean>
	<!-- 任务描述 -->
	<bean id="jobDetail" class="org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean">
       <!--指定定时任务的对象-->
        <property name="targetObject" ref="mailJob"/>
		<!-- 调用 的方法 -->
		<property name="targetMethod" value="doJob"></property>
		<!-- 禁止并发(多线程), 使用单线程 -->
		<property name="concurrent" value="false"></property>
	</bean>
  
	<!-- 指定触发器  -->
	<bean id="trigger" class="org.springframework.scheduling.quartz.CronTriggerFactoryBean">
		<property name="jobDetail" ref="jobDetail"></property>
		<!-- 七子表达式,定义任务的周期 -->
		<property name="cronExpression" value="0/30 * * * * ?"></property>
	</bean>
  
	<!-- 任务调度管理容器 -->
	<bean id="scheduler" class="org.springframework.scheduling.quartz.SchedulerFactoryBean" >
		<property name="triggers">
			<list>
				<ref bean="trigger"/>
			</list>
		</property>
	</bean>
	
</beans>
	
```

### 3 将配置文件和任务类整合到项目中

​	配置项目加载对应的配置文件即可.