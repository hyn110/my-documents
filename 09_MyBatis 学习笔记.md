# MyBatis 学习笔记

## 1 最简单的入门案例

### 1 定义一个 POJO 对象

```Java
package com.fmi110.domain;

public class User {
    private Integer id;
    private String name;
    private String sex;
    private Integer age;
  	...setter getter...
}
```

### 2 引入 maven 坐标

```Xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.fmi110</groupId>
    <artifactId>mybatistest</artifactId>
    <version>1.0-SNAPSHOT</version>

    <packaging>jar</packaging>

     <dependencies>
         <!-- https://mvnrepository.com/artifact/org.mybatis/mybatis -->
         <dependency>
             <groupId>org.mybatis</groupId>
             <artifactId>mybatis</artifactId>
             <version>3.4.1</version>
         </dependency>

         <dependency>
             <groupId>mysql</groupId>
             <artifactId>mysql-connector-java</artifactId>
             <version>5.1.39</version>
         </dependency>

         <dependency>
             <groupId>log4j</groupId>
             <artifactId>log4j</artifactId>
             <version>1.2.17</version>
         </dependency>
         <dependency>
             <groupId>org.slf4j</groupId>
             <artifactId>jcl-over-slf4j</artifactId>
             <version>1.7.25</version>
         </dependency>

         <dependency>
             <groupId>junit</groupId>
             <artifactId>junit</artifactId>
             <version>4.12</version>
         </dependency>
     </dependencies>
</project>
```

> 使用到的 jar 包:
>
> mybatis.3.4.1.jar    
>
> mysql-connector-java.5.1.39.jar   
>
> log4j.1.2.17.jar  
>
>  jcl-over-slf4j.1.7.25

### 3 配置映射文件 UserMapper.xml

​	映射文件习惯跟 bean 对象放在同一个目录下

```Xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!--namespace 习惯命名方式 : 包名 + 映射文件名-->
<mapper namespace="com.fmi110.domain.UserMapper">

    <!--parameterType :     指定调用时传入的参数类型-->
    <!--useGeneratedKeys :  使用数据库底层的子增长策略 , 需要数据库底层的支持 -->
    <insert id="save" parameterType="com.fmi110.domain.User" useGeneratedKeys="true">
        insert INTO tb_user(name,sex,age) VALUES (#{name},#{sex},#{age})
    </insert>
</mapper>
```

### 4 编写配置文件 mybatis-config.xml

​	核心配置文件可直接放在 src 目录下

```Xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

    <settings>
        <!--指定日志的具体实现-->
        <setting name="logImpl" value="LOG4J"/>
        <!--<setting name="logImpl" value="SLF4J"/>-->
        <!--<setting name="logPrefix" value="dao."/>-->
    </settings>
    <!--环境配置,指定使用名为 "mysql" 的 environment-->
    <!--default 的值必须是,某个environment子标签的 id !!-->
    <environments default="mysql">
        <environment id="mysql">
            <!--指定直接使用 JDBC 提交或回滚事务-->
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql:///mybatis"/>
                <property name="username" value="root"/>
                <property name="password" value=""/>
            </dataSource>
        </environment>
    </environments>

    <mappers>
        <mapper resource="com/fmi110/domain/UserMapper.xml"/>
    </mappers>
</configuration>
```

### 5 日志配置文件 log4j.properties

```properties
log4j.rootLogger=ERROR,STDOUT

# 单独配置 MyBatis 的日志级别
log4j.logger.com.fmi110.domain.UserMapper=DEBUG

log4j.appender.STDOUT=org.apache.log4j.ConsoleAppender
log4j.appender.STDOUT.layout=org.apache.log4j.PatternLayout
log4j.appender.STDOUT.layout.ConversionPattern=%5p [%t] - %m%n


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

### 6 编写 java 代码

```Java
@Test
public void testInsert() throws IOException {
    InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
    SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(inputStream);
    SqlSession        session = factory.openSession();
    User user = new User("fmi110"," 男",28);
    int id = session.insert("com.fmi110.domain.UserMapper.save", user);
    System.out.println("id = "+id);
    session.commit();
    session.close();
}
```

## 2 MyBatis 配置文件详解

​	mybatis 的配置根元素是 configuration 对象 , 子元素包含如下节点:

- properties  属性
- settings   设置
- typeAliases   类型命名
- typeHandler  类型处理器
- objectFactory  对象工厂
- plugins   插件
- environments  环境
  - environment  环境变量
  - trasactionManager  事务管理器
  - dataSource  数据源
- databaseIdProvider  数据库厂商识别
- Mappers  映射器

### 1 properties

​	当希望在配置文件用引用外部文件或者典型的 java 属性文件中配置时使用. 比如数据源的具体信息配置在 src 目录下的 jdbc.properties 文件中, 如下:

```properties
driver=com.mysql.jdbc.Driver
url=jdbc:mysql:///mybatis
username=root
password=
```

​	此时在配置文件中使用如下标签进行引用

```Xml
<properties resource="jdbc.properties"/>
```

​	此时数据源的配置可如下通过占位符的形式引用 jdbc.properties 中定义的变量值

```Xml
<dataSource type="POOLED">
    <property name="driver" value="${driver}"/>
    <property name="url" value="${url}"/>
    <property name="username" value="${username}"/>
    <property name="password" value="${password}"/>
</dataSource>
```

### 2 settings 设置

​	mybatis 中即为重要的调整设置,他们会改变 myBatis 的运行时行为,具体参数如下:

| 设置参数                            | 描述                                       | 有效值  | 默认值     |
| ------------------------------- | ---------------------------------------- | ---- | ------- |
| cacheEnable                     | 全局的缓存开关                                  |      | true    |
| lazyLoadingEnable               | 全局的关联对象的延迟加载的开关                          |      | false   |
| aggressiveLazyLoading           | 启动时,任意延时属性的加载都使用带有延迟加载属性的对象完整加载,否则每种属性将会按需加载 |      | true    |
| multipleResultSetsEnable        | 允许单一语句返回多结果集(需要兼容驱动)                     |      | true    |
| userColumnLabel                 | 使用列标签代替列名,不同驱动表现不同,需要自己测试或者查阅资料          |      | true    |
| userGeneratedKey                | 允许 jdbc 自动生成主键,需要驱动兼容. true 时,这个设置强制使用自动生成主键,尽管一些驱动不能兼容但是仍能正常运行(比如 Deby) |      | false   |
| autoMappingBehavior             | 指定 myBatis 如何自动映射到字段或属性. NONE 表示取消自动映射;partial 只会自动映射没有定义嵌套结果集映射的结果集; Full 自动映射全部的结果集 |      | partial |
| autoMappingUnkownColumnBehavior | none , warning , failing 抛异常             |      |         |
| defaultExecutorType             | 配置默认的执行器. simple ; reuse为重用预处理语句(preparedStatments);batch 重用语句并执行批量更新 |      | simple  |
| defaultStatementTimeout         | 超时时间                                     |      | null    |
| defaultFetchSize                | 默认返回结果集的大小                               |      | null    |
| safeRowBoundsEnabled            | 允许在嵌套语句中使用分页(RowBounds)                  |      | false   |
|                                 |                                          |      |         |
|                                 |                                          |      |         |
|                                 |                                          |      |         |
|                                 |                                          |      |         |
|                                 |                                          |      |         |
|                                 |                                          |      |         |
|                                 |                                          |      |         |
|                                 |                                          |      |         |
|                                 |                                          |      |         |

