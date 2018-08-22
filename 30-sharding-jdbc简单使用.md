# 30 sharding-jdbc 使用

​	分表分库的组件，官网 [sharding-jdbc](http://shardingjdbc.io/)

## 1 sharding-jdbc2

### 1 maven 依赖

```xml
 		<dependency>
            <groupId>io.shardingjdbc</groupId>
            <artifactId>sharding-jdbc</artifactId>
            <version>2.0.3</version>
        </dependency>
        <dependency>
            <groupId>io.shardingjdbc</groupId>
            <artifactId>sharding-jdbc-core-spring-namespace</artifactId>
            <version>2.0.3</version>
        </dependency>
```

## 2 applicationContext-sharding-jdbc.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:sharding="http://shardingjdbc.io/schema/shardingjdbc/sharding"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                        http://www.springframework.org/schema/beans/spring-beans.xsd
                        http://www.springframework.org/schema/context
                        http://www.springframework.org/schema/context/spring-context.xsd http://shardingjdbc.io/schema/shardingjdbc/sharding http://shardingjdbc.io/schema/shardingjdbc/sharding/sharding.xsd http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd">

    <context:component-scan base-package="com.fmi110.vienna">
        <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
    </context:component-scan>

    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <!-- mapper 接口 -->
        <property name="basePackage" value="com.fmi110.vienna.**.mapper"/>
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory-shardingjdbc"/>
    </bean>

    <!-- 配置sqlSessionFactory -->
    <bean id="sqlSessionFactory-shardingjdbc" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="shardingDataSource"/>
        <!-- mapper.xml 文件 -->
        <property name="mapperLocations" value="classpath*:com/fmi110/vienna/**/*.xml"/>
        <property name="typeAliasesPackage" value="com.fmi110.vienna.common.entity"/>
        <property name="plugins">
            <array>
                <bean class="com.github.pagehelper.PageInterceptor">
                    <!-- 这里的几个配置主要演示如何使用，如果不理解，一定要去掉下面的配置 -->
                    <property name="properties">
                        <value>
                            helperDialect=mysql
                            reasonable=true
                            supportMethodsArguments=true
                            params=count=countSql
                            autoRuntimeDialect=true
                        </value>
                    </property>
                </bean>
            </array>
        </property>
    </bean>

    <!-- 分库算法 -->
    <!--<bean id="orderDatabaseShardingAlgorithm" class="com.fmi110.vienna.shardingjdbc.OrderDatabaseShardingAlgorithm2"/>-->

    <sharding:standard-strategy id="databaseShardingStrategy"
                                sharding-column="ORDER_CHANNEL"
                                precise-algorithm-class="com.fmi110.vienna.shardingjdbc.OrderDatabaseShardingAlgorithm2"/>

    <!-- 订单表 ：分表策略，不横向拆分表 -->
    <sharding:none-strategy id="noShardingStrategy"/>


    <sharding:data-source id="shardingDataSource">
        <sharding:sharding-rule
                data-source-names="dataSource-vienna-channel-main,dataSource-vienna-elong-main,dataSource-vienna-xiecheng-main,dataSource-vienna-web-main">
            <sharding:table-rules>
                <sharding:table-rule logic-table="OR_T_ORDER"
                                     actual-data-nodes="dataSource-vienna-channel-main.OR_T_ORDER,dataSource-vienna-elong-main.OR_T_ORDER,dataSource-vienna-xiecheng-main.OR_T_ORDER,dataSource-vienna-web-main.OR_T_ORDER"
                                     database-strategy-ref="databaseShardingStrategy"
                                     table-strategy-ref="noShardingStrategy"/>

                <sharding:table-rule logic-table="OR_T_ORDER_PRICE"
                                     actual-data-nodes="dataSource-vienna-channel-main.OR_T_ORDER_PRICE,dataSource-vienna-elong-main.OR_T_ORDER_PRICE,dataSource-vienna-xiecheng-main.OR_T_ORDER_PRICE,dataSource-vienna-web-main.OR_T_ORDER_PRICE"
                                     database-strategy-ref="databaseShardingStrategy"
                                     table-strategy-ref="noShardingStrategy"/>

            </sharding:table-rules>
            <sharding:binding-table-rules>
                <sharding:binding-table-rule logic-tables="OR_T_ORDER,OR_T_ORDER_PRICE"/>
            </sharding:binding-table-rules>
        </sharding:sharding-rule>
    </sharding:data-source>

    <!-- 事务 -->
    <bean id="transactionManager-sharding"
          class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="shardingDataSource"/>
    </bean>

    <tx:annotation-driven transaction-manager="transactionManager-sharding"/>
</beans>
```

### 3 分库算法实现类

```java
package com.fmi110.vienna.shardingjdbc;

import io.shardingjdbc.core.api.algorithm.sharding.PreciseShardingValue;
import io.shardingjdbc.core.api.algorithm.sharding.standard.PreciseShardingAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;

/**
 * 订单库分表算法实现
 *
 * @author fmi110
 * @Date 2018/8/1 22:51
 */
public class OrderDatabaseShardingAlgorithm2 implements PreciseShardingAlgorithm<String> {

    private static final Logger logger = LoggerFactory.getLogger(OrderDatabaseShardingAlgorithm2.class);

    /**
     * 数据库配置映射
     */
    private static HashMap<String, String> dataSourceMap = new HashMap<String, String>();

    static {
//        dataSourceMap.put("M", "dataSource-common-main");
        dataSourceMap.put("C", "dataSource-vienna-channel-main");
        dataSourceMap.put("E", "dataSource-vienna-elong-main");
        dataSourceMap.put("X", "dataSource-vienna-xiecheng-main");
        dataSourceMap.put("W", "dataSource-vienna-web-main");
        // 已关闭
//        dataSourceMap.put("T", "dataSource-vienna-tongcheng-main");
    }

    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<String> preciseShardingValue) {
        logger.info("collection = {}, preciseShardingValue = {}");
        String key            = preciseShardingValue.getValue();
        String dataSourceName = dataSourceMap.get(key);
        return dataSourceName;
//        return dataSourceName == null ? dataSourceMap.get("M") : dataSourceName;
    }
}


//dataSource-vienna-channel-main,dataSource-vienna-elong-main,dataSource-vienna-xiecheng-main,dataSource-vienna-web-main
```



## 2 sharding-jdbc3

### 1 maven 依赖

```xml
<properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <spring-version>3.2.10.RELEASE</spring-version>
        <spring-orm.version>3.2.10.RELEASE</spring-orm.version>
        <mybatis.version>3.4.5</mybatis.version>
        <mybatis-spring.version>1.3.1</mybatis-spring.version>
        <mybatis-pagehelper.version>5.1.2</mybatis-pagehelper.version>
        <mybatis-paginator.version>1.2.17</mybatis-paginator.version>
        <!--<mapper.version>4.0.2</mapper.version>-->
        <sharding-sphere.version>3.0.0.M1</sharding-sphere.version>
 </properties>

		<dependency>
            <groupId>io.shardingsphere</groupId>
            <artifactId>sharding-jdbc-spring-namespace</artifactId>
            <version>${sharding-sphere.version}</version>
        </dependency>
        <dependency>
            <groupId>io.shardingsphere</groupId>
            <artifactId>sharding-jdbc-orchestration-spring-namespace</artifactId>
            <version>${sharding-sphere.version}</version>
        </dependency>

<dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>${spring-version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-beans</artifactId>
            <version>${spring-version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aop</artifactId>
            <version>${spring-version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>${spring-version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context-support</artifactId>
            <version>${spring-version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-tx</artifactId>
            <version>${spring-version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>${spring-version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-orm</artifactId>
            <version>${spring-orm.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-web</artifactId>
            <version>${spring-version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>${spring-version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>${spring-version}</version>
        </dependency>

        <!--<dependency>-->
            <!--<groupId>tk.mybatis</groupId>-->
            <!--<artifactId>mapper</artifactId>-->
            <!--<version>${mapper.version}</version>-->
        <!--</dependency>-->

        <dependency>
            <groupId>com.github.pagehelper</groupId>
            <artifactId>pagehelper</artifactId>
            <version>${mybatis-pagehelper.version}</version>
        </dependency>
        <dependency>
            <groupId>com.github.miemiedev</groupId>
            <artifactId>mybatis-paginator</artifactId>
            <version>${mybatis-paginator.version}</version>
        </dependency>
 <!-- mybatis -->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>${mybatis.version}</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis-spring</artifactId>
            <version>${mybatis-spring.version}</version>
        </dependency>
        <dependency>
            <groupId>com.github.pagehelper</groupId>
            <artifactId>pagehelper</artifactId>
            <version>${mybatis-pagehelper.version}</version>
        </dependency>
```

### 2 applicationContext-sharding-jdbc.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	   xmlns:context="http://www.springframework.org/schema/context"
	   xmlns:tx="http://www.springframework.org/schema/tx"
	   xmlns:sharding="http://shardingsphere.io/schema/shardingsphere/sharding"
	   xsi:schemaLocation="http://www.springframework.org/schema/beans
                        http://www.springframework.org/schema/beans/spring-beans.xsd
                        http://shardingsphere.io/schema/shardingsphere/sharding
                        http://shardingsphere.io/schema/shardingsphere/sharding/sharding.xsd
                        http://www.springframework.org/schema/context
                        http://www.springframework.org/schema/context/spring-context.xsd
                        http://www.springframework.org/schema/tx
                        http://www.springframework.org/schema/tx/spring-tx.xsd">

	<context:component-scan base-package="com.fmi110.vienna">
		<context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller" />
	</context:component-scan>

	<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
		<!-- mapper 接口 -->
		<property name="basePackage" value="com.fmi110.vienna.**.mapper"/>
		<property name="sqlSessionFactoryBeanName" value="sqlSessionFactory-shardingjdbc"/>
	</bean>

	<!-- 配置sqlSessionFactory -->
	<bean id="sqlSessionFactory-shardingjdbc" class="org.mybatis.spring.SqlSessionFactoryBean">
		<property name="dataSource" ref="shardingDataSource"/>
		<!-- mapper.xml 文件 -->
		<property name="mapperLocations" value="classpath*:com/fmi110/vienna/**/*.xml"/>
	</bean>

	<!-- 分库算法 -->
	<bean id="orderDatabaseShardingAlgorithm" class="com.fmi110.vienna.shardingjdbc.OrderDatabaseShardingAlgorithm" />
	<!-- 分库策略，策略依赖于算法 -->
	<sharding:standard-strategy id="databaseShardingStrategy" sharding-column="ORDER_CHANNEL" precise-algorithm-ref="orderDatabaseShardingAlgorithm" />
	<!-- 订单表 ：分表策略，不横向拆分表 -->
	<sharding:none-strategy id="noShardingStrategy" />


	<sharding:data-source id="shardingDataSource" >
		<sharding:sharding-rule data-source-names="dataSource-vienna-channel-main,dataSource-vienna-elong-main,dataSource-vienna-xiecheng-main,dataSource-vienna-web-main">
			<sharding:table-rules>
				<!--<sharding:table-rule logic-table="OR_T_ORDER" actual-data-nodes="dataSource-vienna-channel-main.OR_T_ORDER,dataSource-vienna-elong-main.OR_T_ORDER,dataSource-vienna-xiecheng-main.OR_T_ORDER,dataSource-vienna-web-main.OR_T_ORDER" database-strategy-ref="databaseShardingStrategy" table-strategy-ref="noShardingStrategy"  />-->

				<sharding:table-rule logic-table="OR_T_ORDER_PRICE" actual-data-nodes="dataSource-vienna-channel-main.OR_T_ORDER_PRICE,dataSource-vienna-elong-main.OR_T_ORDER_PRICE,dataSource-vienna-xiecheng-main.OR_T_ORDER_PRICE,dataSource-vienna-web-main.OR_T_ORDER_PRICE" database-strategy-ref="databaseShardingStrategy" table-strategy-ref="noShardingStrategy"  />

			</sharding:table-rules>
			<sharding:binding-table-rules>
				<sharding:binding-table-rule logic-tables="OR_T_ORDER_PRICE"/>
			</sharding:binding-table-rules>
		</sharding:sharding-rule>
	</sharding:data-source>

	<!-- 事务 -->
	<bean id="transactionManager-sharding"
		  class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
		<property name="dataSource" ref="shardingDataSource" />
	</bean>

	<tx:annotation-driven transaction-manager="transactionManager-sharding" />
</beans>
```

###  3 分库算法类

```java
package com.fmi110.vienna.shardingjdbc;

import io.shardingsphere.core.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.core.api.algorithm.sharding.standard.PreciseShardingAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;

/**
 * 订单库分表算法实现
 *
 * @author fmi110
 * @Date 2018/8/1 22:51
 */
public class OrderDatabaseShardingAlgorithm implements PreciseShardingAlgorithm<String> {

    private static final Logger logger = LoggerFactory.getLogger(OrderDatabaseShardingAlgorithm.class);

    /**
     * 数据库配置映射
     */
    private static HashMap<String, String> dataSourceMap = new HashMap<String, String>();

    static {
//        dataSourceMap.put("M", "dataSource-common-main");
        dataSourceMap.put("C", "dataSource-vienna-channel-main");
        dataSourceMap.put("E", "dataSource-vienna-elong-main");
        dataSourceMap.put("X", "dataSource-vienna-xiecheng-main");
        dataSourceMap.put("W", "dataSource-vienna-web-main");
        // 已关闭
//        dataSourceMap.put("T", "dataSource-vienna-tongcheng-main");
    }

    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<String> preciseShardingValue) {
        logger.info("collection = {}, preciseShardingValue = {}");
        String key            = preciseShardingValue.getValue();
        String dataSourceName = dataSourceMap.get(key);
        return dataSourceName;
//        return dataSourceName == null ? dataSourceMap.get("M") : dataSourceName;
    }
}


//dataSource-vienna-channel-main,dataSource-vienna-elong-main,dataSource-vienna-xiecheng-main,dataSource-vienna-web-main
```

```java
public final class PreciseShardingValue<T extends Comparable<?>> implements ShardingValue {
    
    private final String logicTableName;  // 逻辑表明
    
    private final String columnName;	// 分表分库依赖的列名
    
    private final T value;	// 列的值
}
```