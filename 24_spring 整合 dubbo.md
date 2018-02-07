# 24_spring 整合 dubbo

​	这里只是为了演示 ,  工程结果如下 :

```sh
├─consumer	# 服务消费
├─parent    # 父工程
└─provider	# 服务提供
```

## 	1 maven 坐标

​	在父工程的pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.fmi110.dubbo</groupId>
    <artifactId>parent</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <modules>
        <module>../provider</module>
        <module>../consumer</module>
    </modules>

    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>4.3.12.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-orm</artifactId>
            <version>4.3.12.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aspects</artifactId>
            <version>4.3.12.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
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
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
            <version>1.7.24</version>
        </dependency>

        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.17</version>
        </dependency>

        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>servlet-api</artifactId>
            <version>2.4</version>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>jsp-api</artifactId>
            <version>2.0</version>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.projectlombok/lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.16.18</version>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>dubbo</artifactId>
            <version>2.6.0</version>
        </dependency>

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
    </dependencies>

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
                    <failOnMissingWebXml>false</failOnMissingWebXml>
                </configuration>
            </plugin>

            <plugin>
                <!--打包时跳过单元测试-->
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <configuration>
                    <skip>true</skip>
                </configuration>
            </plugin>

        </plugins>

    </build>
</project>
```

## 	2 定义服务接口和实现

```java
package com.fmi110.dubbo.service;

public interface IDemoService {
    String sayHello(String name);
}

```

```java
package com.fmi110.dubbo.service.impl;

import com.fmi110.dubbo.service.IDemoService;

public class DemoService implements IDemoService {
    @Override
    public String sayHello(String name) {
        return "hello " + name;
    }
}

```

## 	3 暴露服务

​	在applicationContext.xml中配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
       xsi:schemaLocation="
                    http://www.springframework.org/schema/beans
                    http://www.springframework.org/schema/beans/spring-beans.xsd
                    http://code.alibabatech.com/schema/dubbo http://code.alibabatech.com/schema/dubbo/dubbo.xsd">
    <!--必须配置name属性-->
    <dubbo:application name="provider"/>
    <!--连接注册中心的配置,这里注册中心时 zookeeper 集群-->
    <dubbo:registry address="zookeeper://192.168.80.132:2181?backup=192.168.80.133:2181,192.168.80.134:2181"/>
    <!--远程服务调用的配置(服务发现)-->
    <dubbo:protocol name="dubbo" port="20880"/>
    <!--暴露服务-->
    <dubbo:service interface="com.fmi110.dubbo.service.IDemoService" ref="demoService" retries="2" cluster="failover"/>

    <bean id="demoService" class="com.fmi110.dubbo.service.impl.DemoService" />

</beans>
```

> zookeeper 需要事先配置好

## 	4 启动服务

```java
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:app*.xml");
        context.start();
        // 阻塞进程,防止程序退出
        System.in.read();
    }
}

```

## 	5  客户端引用服务

​	服务引用在消费端的配置文件中 applicationContext.xml 中配置

```java
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
       xsi:schemaLocation="
                    http://www.springframework.org/schema/beans
                    http://www.springframework.org/schema/beans/spring-beans.xsd
                    http://code.alibabatech.com/schema/dubbo http://code.alibabatech.com/schema/dubbo/dubbo.xsd">
    <!--name属性必须配置,名字不要跟服务端相同!!!-->
    <dubbo:application name="consumer"/>
    <dubbo:registry address="zookeeper://192.168.80.132:2181?backup=192.168.80.133:2181,192.168.80.134:2181"/>
    <!-- 生成远程服务代理，可以和本地bean一样使用demoService -->
    <dubbo:reference interface="com.fmi110.dubbo.service.IDemoService" id="demoService" check="false"/>

</beans>
```

## 	6 客户端调用远程服务

```java
package com.fmi110.dubbo.test;

import com.fmi110.dubbo.service.IDemoService;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class DemoTest {
    @Autowired
    IDemoService service;

    @Test
    public void test() throws IOException, InterruptedException {

//        ClassPathXmlApplicationContext context =
//                new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
//        context.start();

//        service = (IDemoService) context.getBean("demoService");

        String str = service.sayHello("dubbo");
        System.out.println(str);
//        System.in.read();
        Thread.sleep(3000);
    }
}
```



