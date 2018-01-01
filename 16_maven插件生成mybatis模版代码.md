# 16_maven插件生成mybatis模版代码

​	这里介绍的是使用maven 插件生成 MyBatis 映射文件和接口 , 测试代码地址 : https://github.com/hyn110/mybatis-code-generator

## 1 maven工程pom文件导入插件

​	pom.xml :

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.fmi110.mybatis</groupId>
    <artifactId>code-generator</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <build>
        <plugins>
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
        </plugins>
    </build>

</project>
```

> `<configurationFile>src/main/resources/mybatis-generator-config.xml</configurationFile>`   标签指定了配置文件名为 `mybatis-generator-config.xml`   ,  放置在 src/main/resources 目录下

## 2 配置 mybatis-generator-config.xml

​	文件放置目录由 pom.xml 里声明插件时进行配置,放置在  src/main/resources 目录下

​	mybatis-generator-config.xml :

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

> 要自己配置的选项 :
>
> 1. `<jdbcConnection>`  标签配置数据库连接信息
> 2. `<javaModelGenerator>`   指定生成java文件所处的包和路径
> 3. ` <sqlMapGenerator>` 指定生成的 xml 文件所在的路径
> 4. `<javaClientGenerator>`  指定生成的 mapper 接口所在的包和文件路径
> 5. `<table>`  指定表 , 一个表对应一个 <table> 标签

## 3 运行插件即可生成模版文件



