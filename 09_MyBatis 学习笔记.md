# MyBatis 学习笔记

​	[MyBatis 中文文档链接](http://www.mybatis.org/mybatis-3/zh/index.html)

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

​	该部分内容参考文档 :    [myBatis3中文文档](http://www.mybatis.org/mybatis-3/zh/configuration.html)

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

| 设置参数                            | 描述                                       | 有效值      |
| ------------------------------- | ---------------------------------------- | -------- |
| cacheEnable                     | 全局的缓存开关                                  | True     |
| lazyLoadingEnable               | 全局的关联对象的延迟加载的开关                          | false    |
| aggressiveLazyLoading           | 启动时,任意延时属性的加载都使用带有延迟加载属性的对象完整加载,否则每种属性将会按需加载 | true     |
| multipleResultSetsEnable        | 允许单一语句返回多结果集(需要兼容驱动)                     | true     |
| userColumnLabel                 | 使用列标签代替列名,不同驱动表现不同,需要自己测试或者查阅资料          | true     |
| userGeneratedKey                | 允许 jdbc 自动生成主键,需要驱动兼容. true 时,这个设置强制使用自动生成主键,尽管一些驱动不能兼容但是仍能正常运行(比如 Deby) | false    |
| autoMappingBehavior             | 指定 myBatis 如何自动映射到字段或属性. NONE 表示取消自动映射;partial 只会自动映射没有定义嵌套结果集映射的结果集; Full 自动映射全部的结果集 | partial  |
| autoMappingUnkownColumnBehavior | none , warning , failing 抛异常             |          |
| defaultExecutorType             | 配置默认的执行器. simple ; reuse为重用预处理语句(preparedStatments);batch 重用语句并执行批量更新 | simple   |
| defaultStatementTimeout         | 超时时间                                     | null     |
| defaultFetchSize                | 默认返回结果集的大小                               | null     |
| safeRowBoundsEnabled            | 允许在嵌套语句中使用分页(RowBounds)                  | false    |
| mapUnderscoreToCamelCase        | 开启下划线自动映射驼峰命名规则                          | false    |
| localCacheScope                 | MyBatis 利用本地缓存机制防止循环引用和加速重复嵌套查询.默认是 SESSION,会缓存一个会话中执行的所有查询,若设置为 STATEMENT,则本地会话仅用在语句执行上,对相同的 sqlSession 的不同调用将不会共享数据 | session  |
| jdbcTypeForNull                 | 当没有为参数提供特定的 JDBC 类型时,为空值指定 JDBC类型.可选 null\|varchar\|other | other    |
| lazyLoadTriggerMethods          | 指定哪个对象的方法触发一次延迟加载                        |          |
| callSettersOnNulls              | 指定当结果集中为 null 时,是否调用映射对象的 setter(map 对象时为 put )方法,这对于 Map.keySet() 依赖或 null 值初始化时有用 | false    |
| logPrefix                       | 指定 MyBatis 增加到日志名称的前缀                    | null     |
| logImpl                         | 指定 MyBatis 所有日志的具体实现,,可选 SLF4J\|LOG4J\|LOG4J2\|JDK_LOGGING\|COMMONS_LOGGING\|... |          |
| proxyFactory                    | 指定 MyBatis 创建具有延迟加载功能对象时所使用的代理工具         | Javasist |

​	下面是默认的配置:

```Xml
<settings>
    <setting name="cacheEnabled" value="true"/>
    <setting name="lazyLoadingEnabled" value="false"/>
    <setting name="multipleResultSetsEnabled" value="true"/>
    <setting name="useColumnLabel" value="true"/>
    <setting name="useGeneratedKeys" value="false "/>
    <setting name="autoMappingBehavior" value="PARTIAL"/>
    <setting name="autoMappingUnknownColumnBehavior" value="NONE"/>
    <setting name="defaultExecutorType" value="SIMPLE"/>
    <!--<setting name="defaultStatementTimeout" value="25"/>-->
    <!--<setting name="defaultFetchSize" value="100"/>-->
    <setting name="safeRowBoundsEnabled" value="false"/>
    <setting name="mapUnderscoreToCamelCase" value="false"/>
    <setting name="localCacheScope" value="SESSION"/>
    <setting name="jdbcTypeForNull" value="OTHER"/>
    <setting name="lazyLoadTriggerMethods" value="equals,clone,hashCode,toString"/>
    <setting name="callSettersOnNulls" value="false"/>
    <setting name="logImpl" value="LOG4J"/>
</settings>
```

> value 值区分大小写!!!! 枚举类型数据的值!!!

### 3 typeAliases 类型别名

​	typealiases 是为类型设置一个短的名字.它只和 xml 配置有关,用来减少类完全限定名的冗余的.例如:

```Xml
<typeAliases>
    <typeAlias type="com.fmi110.domain.User" alias="user"/>
    <!--<package name="com.fmi110.domain"/>-->
</typeAliases>
```

​	这样配置后, "user" 可以用在任何使用 "com.fmi110.domain.User" 的地方,比如映射器里可以如下写:

```Xml
<insert id="save" parameterType="USER" useGeneratedKeys="true">
        insert INTO tb_user(name,sex,age) VALUES (#{name},#{sex},#{age})
</insert>
```

> 事实上 , parameterType="USER" , 用 "user" "User" "USER" 此时都能识别,貌似这里是忽略大小写的!!!

​	也可以指定包名,Mybatis 会自动到包下面搜索需要的 javaBean 对象

```Xml
<typeAliases>
    <!--<typeAlias type="com.fmi110.domain.User" alias="USER"/>-->
    <package name="com.fmi110.domain"/>
</typeAliases>
```

​	此时默认的别名为类名的小写,比如 com.fmi110.domain.User 的别名为 user , 如果要指定别名,可以在 Bean 类上使用注解 @Alias 指定,如:

```Java
@Alias("User")
public class User {
    private Integer id;
    private String name;
	.....
}
```

> 我自己测试时映射文件 mapper 中使用别名的地方是不区分大小写的!!!

​	MyBatis 内置了一些默认的别名,如下,注意 它们都是大小写不敏感的!!!:

| 别名         | 映射的类型      |
| ---------- | ---------- |
| _byte      | byte       |
| _long      | long       |
| _short     | short      |
| _int       | int        |
| _integer   | int        |
| _double    | double     |
| _float     | float      |
| _boolean   | boolean    |
| string     | String     |
| byte       | Byte       |
| long       | Long       |
| short      | Short      |
| int        | Integer    |
| integer    | Integer    |
| double     | Double     |
| float      | Float      |
| boolean    | Boolean    |
| date       | Date       |
| decimal    | BigDecimal |
| bigdecimal | BigDecimal |
| object     | Object     |
| map        | Map        |
| hashmap    | HashMap    |
| list       | List       |
| arraylist  | ArrayList  |
| collection | Collection |
| iterator   | Iterator   |

### 4 typeHandlers 类型处理器

​	MyBatis 在预处理语句(PreparedStatement)中设置一个参数或从结果集中取出一个值时,都会用类型处理器将获取的值以合适的方式转换成 Java 类型 . MyBatis 提供了一些默认的类型处理器,这里不介绍了,参考官方文档 : [typeHandlers](http://www.mybatis.org/mybatis-3/zh/configuration.html#typeHandlers)

### 5 ObjectFactory 对象工厂

​	MyBatis 每次创建结果对象的新实例时，它都会使用一个对象工厂（ObjectFactory）实例来完成。 默认的对象工厂需要做的仅仅是实例化目标类，要么通过默认构造方法，要么在参数映射存在的时候通过参数构造方法来实例化。 如果想覆盖对象工厂的默认行为，则可以通过创建自己的对象工厂来实现。比如：

```Java
// ExampleObjectFactory.java
public class ExampleObjectFactory extends DefaultObjectFactory {
  public Object create(Class type) {
    return super.create(type);
  }
  public Object create(Class type, List<Class> constructorArgTypes, List<Object> constructorArgs) {
    return super.create(type, constructorArgTypes, constructorArgs);
  }
  public void setProperties(Properties properties) {
    super.setProperties(properties);
  }
  public <T> boolean isCollection(Class<T> type) {
    return Collection.class.isAssignableFrom(type);
  }}
```

```Xml
<!-- mybatis-config.xml -->
<objectFactory type="org.mybatis.example.ExampleObjectFactory">
  <property name="someProperty" value="100"/>
</objectFactory>
```

​	ObjectFactory 接口很简单，它包含两个创建用的方法，一个是处理默认构造方法的，另外一个是处理带参数的构造方法的。 最后，setProperties 方法可以被用来配置 ObjectFactory，在初始化你的 ObjectFactory 实例后， objectFactory 元素体中定义的属性会被传递给 setProperties 方法。

### 6 environments 配置环境

​	MyBatis 可以配置成适应多种环境，这种机制有助于将 SQL 映射应用于多种数据库之中， 现实情况下有多种理由需要这么做。例如，开发、测试和生产环境需要有不同的配置；或者共享相同 Schema 的多个生产数据库， 想使用相同的 SQL 映射。许多类似的用例。

​	**不过要记住：尽管可以配置多个环境，每个 SqlSessionFactory 实例只能选择其一。**

所以，如果你想连接两个数据库，就需要创建两个 SqlSessionFactory 实例，每个数据库对应一个。而如果是三个数据库，就需要三个实例，依此类推，记起来很简单：

- **每个数据库对应一个 SqlSessionFactory 实例**

为了指定创建哪种环境，只要将它作为可选的参数传递给 SqlSessionFactoryBuilder 即可。可以接受环境配置的两个方法签名是：

```Java
SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, environment);
SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, environment,properties);
```

​	如果忽略了环境参数，那么默认环境将会被加载，如下所示

```Java
SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader);
SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader,properties);
```

​	环境元素定义了如何配置环境:

```Xml
<!--环境配置,指定连接 mysql 数据库-->
<environments default="mysql">
    <environment id="mysql">
        <!--指定直接使用 JDBC 提交或回滚事务-->
        <transactionManager type="JDBC"/>
        <dataSource type="POOLED">
            <property name="driver" value="${driver}"/>
            <property name="url" value="${url}"/>
            <property name="username" value="${username}"/>
            <property name="password" value="${password}"/>
        </dataSource>
    </environment>
  
    <environment id="test"><!--测试环境用-->
        <transactionManager type="JDBC"/>
        <dataSource type="POOLED">
            <property name="driver" value="${driver}"/>
            <property name="url" value="${test.url}"/>
            <property name="username" value="${test.username}"/>
            <property name="password" value="${test.password}"/>
        </dataSource>
    </environment>
</environments>
```

​	这里配置了id 分别为 "mysql" 和 "test" 两个环境, 默认使用的是 "mysql" 环境!!!

#### 1 transactionManager 事务管理器

​	myBatis 提供了两种事务管理器 `JDBC`  和  `MANAGED`

- JDBC : 直接使用 jdbc 的提交和回滚设置 , 它依赖于从数据源得到连接来管理事务范围
- MANAGERD : 这个配置几乎不做什么 . 它从不提交或回滚一个连接,而是让容器来管理事务的整个生命周期(比如 JEE应用服务器 的上下文).默认情况下它会关闭连接,然而一些容器并不希望这样,可以将 closeConnection 属性设置为 false 来组织该行为

```Xml
<transactionManager type="MANAGED">
  <property name="closeConnection" value="false"/>
</transactionManager>
```

**注意 : 如果你正在使用 Spring + MyBatis，则没有必要配置事务管理器， 因为 Spring 模块会使用自带的管理器来覆盖前面的配置。**

#### 2 dataSource 数据源

​	MyBatis 提供了 `UNPOOLED` , `POOLED` , `JNDI` 三种数据源类型	

- UNPOOLED : 不适用连接池 , 每次请求都打开和关闭连接 , 比较少使用.
- POOLED : 使用连接池
- JNDI : 这个数据源的实现是为了能在如 EJB 或应用服务器这类容器中使用，容器可以集中或在外部配置数据源，然后放置一个 JNDI 上下文的引用。这种数据源配置只需要两个属性：
  - `initial_context` – 这个属性用来在 InitialContext 中寻找上下文（即，initialContext.lookup(initial_context)）。这是个可选属性，如果忽略，那么 data_source 属性将会直接从 InitialContext 中寻找。
  - `data_source` – 这是引用数据源实例位置的上下文的路径。提供了 initial_context 配置时会在其返回的上下文中进行查找，没有提供时则直接在 InitialContext 中查找。

### 7 mapper 映射器

​	MyBatis 需要开发者自己些 sql 语句,mapper 映射器的功能是告诉框架去哪里获取映射文件,配置方式由如下几种:

``` Xml
<!-- 使用类相对路径查找 -->
<mappers>
  <mapper resource="org/mybatis/builder/AuthorMapper.xml"/>
  <mapper resource="org/mybatis/builder/BlogMapper.xml"/>
  <mapper resource="org/mybatis/builder/PostMapper.xml"/>
</mappers>
```

``` Xml
<!-- 使用本地文件 -->
<mappers>
  <mapper url="file:///var/mappers/AuthorMapper.xml"/>
  <mapper url="file:///var/mappers/BlogMapper.xml"/>
  <mapper url="file:///var/mappers/PostMapper.xml"/>
</mappers>
```

```Xml
<!-- 使用接口类 -->
<mappers>
  <mapper class="org.mybatis.builder.AuthorMapper"/>
  <mapper class="org.mybatis.builder.BlogMapper"/>
  <mapper class="org.mybatis.builder.PostMapper"/>
</mappers>
```

```Xml
<!-- 指定包名,mybaits 会扫描包下的所有接口 -->
<mappers>
  <package name="org.mybatis.builder"/>
</mappers>
```

## 3 Mapper XML 映射文件

​	MyBatis 的 sql 语句是通过 SQL 映射文件编辑的,SQL 映射文件有很少的几个顶级元素（按照它们应该被定义的顺序）：

- `cache` – 给定命名空间的缓存配置。
- `cache-ref` – 其他命名空间缓存配置的引用。
- `resultMap` – 是最复杂也是最强大的元素，用来描述如何从数据库结果集中来加载对象。
- `parameterMap` – 已废弃！老式风格的参数映射。内联参数是首选,这个元素可能在将来被移除，这里不会记录。
- `sql` – 可被其他语句引用的可重用语句块。
- `insert` – 映射插入语句
- `update` – 映射更新语句
- `delete` – 映射删除语句
- `select` – 映射查询语句

### 1 select 标签

​	select 标签用于映射查询语句 , 如:

```Xml
<select id="selectPerson" parameterType="int" resultType="hashmap">
  SELECT * FROM PERSON WHERE ID = #{id}
</select>
```

> 注意 : 这里使用了 MyBatis 内置的别名 hashmap !!! 映射 java.util.HashMap

​	这个语句被称作 selectPerson，接受一个 int（或 Integer）类型的参数，并返回一个 HashMap 类型的对象，其中的键是列名，值便是结果行中的对应值。

​	这里 `#{id}` 是占位符 , 这里如果用 jdbc 来实现的话 , 大致如下:

```Java
// Similar JDBC code, NOT MyBatis…
String selectPerson = "SELECT * FROM PERSON WHERE ID=?";
PreparedStatement ps = conn.prepareStatement(selectPerson);
ps.setInt(1,id);
```

​	select 允许的配置如下:

```Xml
<select
  id="selectPerson"
  parameterType="int"
  resultType="hashmap"
  resultMap="personResultMap"
  flushCache="false"
  useCache="true"
  timeout="10000"
  fetchSize="256"
  statementType="PREPARED"
  resultSetType="FORWARD_ONLY">
```

| 属性              | 描述                                       |
| --------------- | ---------------------------------------- |
| `id`            | 唯一表示符 , 用来引用该语句                          |
| `parameterType` | 入参类的完全限定名或别名。可选的，因为 MyBatis 可以通过 TypeHandler 推断出具体传入语句的参数，默认值为 unset。 |
| parameterMap    | 废弃...                                    |
| `resultType`    | 返回数据类型的完全限定名或别名。如果返回集合,类型应该是集合里的元素的类型!!!使用 resultType 或 resultMap，但不能同时使用。 |
| `resultMap`     | 外部 resultMap 的命名引用。结果集的映射是 MyBatis 最强大的特性，对其有一个很好的理解的话，许多复杂映射的情形都能迎刃而解。使用 resultMap 或 resultType，但不能同时使用。 |
| `flushCache`    | 是否清空缓存,默认false                           |
| `useCache`      | 将其设置为 true，将会导致本条语句的结果被二级缓存，默认值：对 select 元素为 true。 |
| `timeout`       | 连接超时时间 , 默认 null                         |
| `fetchSize`     | 这是尝试影响驱动程序每次批量返回的结果行数和这个设置值相等。默认值为 unset（依赖驱动）。 |
| `statementType` | STATEMENT，PREPARED 或 CALLABLE 的一个。这会让 MyBatis 分别使用 Statement，PreparedStatement 或 CallableStatement，默认值：PREPARED。 |
| `resultSetType` | FORWARD_ONLY，SCROLL_SENSITIVE 或 SCROLL_INSENSITIVE 中的一个，默认值为 unset （依赖驱动）。 |
| `databaseId`    |                                          |
| `resultOrdered` | 这个设置仅针对嵌套结果 select 语句适用：如果为 true，就是假设包含了嵌套结果集或是分组了，这样的话当返回一个主结果行的时候，就不会发生有对前面结果集的引用的情况。这就使得在获取嵌套的结果集的时候不至于导致内存不够用。默认值：`false`。 |
| `resultSets`    | 这个设置仅对多结果集的情况适用，它将列出语句执行后返回的结果集并给每个结果集一个名称，名称是逗号分隔的。 |

### 2 insert  update delete

​	修改数据的sql 映射配置非常相近:

```Xml
<insert
  id="insertAuthor"
  parameterType="domain.blog.Author"
  flushCache="true"
  statementType="PREPARED"
  keyProperty=""
  keyColumn=""
  useGeneratedKeys=""
  timeout="20">
```

```Xml
<update
  id="updateAuthor"
  parameterType="domain.blog.Author"
  flushCache="true"
  statementType="PREPARED"
  timeout="20">
```

```xml
<delete
  id="deleteAuthor"
  parameterType="domain.blog.Author"
  flushCache="true"
  statementType="PREPARED"
  timeout="20">
```

| 属性                 | 描述                                       |
| ------------------ | ---------------------------------------- |
| `id`               | 命名空间中的唯一标识符，可被用来代表这条语句。                  |
| `parameterType`    | 入参类型,可选. MyBatis 可自动推测                   |
| `parameterMap`     |                                          |
| `flushCache`       | 是否清空缓存,默认 false                          |
| `timeout`          | 连接超时时间 , 默认 null                         |
| `statementType`    | STATEMENT，PREPARED 或 CALLABLE 的一个。这会让 MyBatis 分别使用 Statement，PreparedStatement 或 CallableStatement，默认值：PREPARED。 |
| `useGeneratedKeys` | （仅对 insert 和 update 有用）这会令 MyBatis 使用 JDBC 的 getGeneratedKeys 方法来取出由数据库内部生成的主键（比如：像 MySQL 和 SQL Server 这样的关系数据库管理系统的自动递增字段），默认值：false。 |
| `keyProperty`      | （仅对 insert 和 update 有用）唯一标记一个属性，MyBatis 会通过 getGeneratedKeys 的返回值或者通过 insert 语句的 selectKey 子元素设置它的键值，默认：`unset`。如果希望得到多个生成的列，也可以是逗号分隔的属性名称列表。 |
| `keyColumn`        | （仅对 insert 和 update 有用）通过生成的键值设置表中的列名，这个设置仅在某些数据库（像 PostgreSQL）是必须的，当主键列不是表中的第一列的时候需要设置。如果希望得到多个生成的列，也可以是逗号分隔的属性名称列表。 |
| `databaseId`       | 如果配置了 databaseIdProvider，MyBatis 会加载所有的不带 databaseId 或匹配当前 databaseId 的语句；如果带或者不带的语句都有，则不带的会被忽略。 |

​	示例语句如下:

```Xml
<insert id="insertAuthor">
  insert into Author (id,username,password,email,bio)
  values (#{id},#{username},#{password},#{email},#{bio})
</insert>

<update id="updateAuthor">
  update Author set
    username = #{username},
    password = #{password},
    email = #{email},
    bio = #{bio}
  where id = #{id}
</update>

<delete id="deleteAuthor">
  delete from Author where id = #{id}
</delete>
```

​	如果数据库支持自动生成主键 , 则可以设置 `useGeneratedKeys=true` 获取生成的主键值,然后通过 `keyProperty` 属性指定将值赋给哪个字段,如:

```Xml
<insert id="insertAuthor" useGeneratedKeys="true"
    keyProperty="id">
  insert into Author (username,password,email,bio)
  values (#{username},#{password},#{email},#{bio})
</insert>
```

​	则返回的 Author对象的 id 字段将获取到插入数据的主键值.

​	对于不支持自动生成类型的数据库或可能不支持自动生成主键 JDBC 驱动来说，MyBatis 有另外一种方法来生成主键。

​	这里有一个简单（甚至很傻）的示例，它可以生成一个随机 ID（你最好不要这么做，但这里展示了 MyBatis 处理问题的灵活性及其所关心的广度）： 

```Xml
<insert id="insertAuthor">
  <selectKey keyProperty="id" resultType="int" order="BEFORE">
    select CAST(RANDOM()*1000000 as INTEGER) a from SYSIBM.SYSDUMMY1
  </selectKey>
  insert into Author
    (id, username, password, email,bio, favourite_section)
  values
    (#{id}, #{username}, #{password}, #{email}, #{bio}, #{favouriteSection,jdbcType=VARCHAR})
</insert>
```

​	在上面的示例中，selectKey 元素将会首先运行，Author 的 id 会被设置，然后插入语句会被调用。这给你了一个和数据库中来处理自动生成的主键类似的行为，避免了使 Java 代码变得复杂。selectKey 描述如下:

```Xml
<selectKey
  keyProperty="id"
  resultType="int"
  order="BEFORE"
  statementType="PREPARED">
```

| 属性              | 描述                                       |
| --------------- | ---------------------------------------- |
| `keyProperty`   | selectKey 语句结果应该被设置的目标属性。如果希望得到多个生成的列，也可以是逗号分隔的属性名称列表。 |
| `keyColumn`     | 匹配属性的返回结果集中的列名称。如果希望得到多个生成的列，也可以是逗号分隔的属性名称列表。 |
| `resultType`    | 结果的类型。MyBatis 通常可以推算出来，但是为了更加确定写上也不会有什么问题。MyBatis 允许任何简单类型用作主键的类型，包括字符串。如果希望作用于多个生成的列，则可以使用一个包含期望属性的 Object 或一个 Map。 |
| `order`         | 这可以被设置为 BEFORE 或 AFTER。如果设置为 BEFORE，那么它会首先选择主键，设置 keyProperty 然后执行插入语句。如果设置为 AFTER，那么先执行插入语句，然后是 selectKey 元素 - 这和像 Oracle 的数据库相似，在插入语句内部可能有嵌入索引调用。 |
| `statementType` | 与前面相同，MyBatis 支持 STATEMENT，PREPARED 和 CALLABLE 语句的映射类型，分别代表 PreparedStatement 和 CallableStatement 类型。 |

## 4 结果映射 ResultMap

​	将结果集映射到 map 中, 默认情况下 key 为表的列名 , value 为对应列的值

```xml
    <select id="selectUser" parameterType="int" resultType="map">
        SELECT * FROM USER
    </select>
```

```java
List<Map<String,Object>> list = session.selectList("com.fmi110.domain.UserMapper.selectUser");
```

​	默认情况下 , MyBatis 会将查询到的数据的列和需要返回的对象的属性逐一进行匹配赋值,但是如果对象的属性和列名不一致 , 则不会赋值 , 这时就需要使用 resultMap进行处理.

​	定义映射关系 :

```xml
<resultMap id="userMap" type="com.fmi110.domain.User">
    <id property="id" column="id"/>
    <result property="age" column="age"/>
    <result property="name" column="name"/>
    <result property="sex" column="sex"/>
</resultMap>

<select id="selectUser" parameterType="int" resultMap="userMap">
    SELECT * FROM USER
</select>
```

> 1. `resultMap --> id`   唯一标识符
> 2. `resultMap --> type`  返回数据的实际类型  
> 3. `<id>`  表示数据库表的主键 , property 指定java对象的属性 , column 指定表的列名

## 5 映射关系

​	当java对象的内部存在关联对象时,此时结果集就需要进行关联关系映射 . 

### 1 一对一映射

​	通过  `<resultMap>`   标签的子标签 `<association>`  标签

### 2 一对多映射



### 3 多对多映射