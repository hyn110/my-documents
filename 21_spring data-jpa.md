# 21_spring data-jpa

​	[spring data-jpa](https://docs.spring.io/spring-data/jpa/docs/2.0.2.RELEASE/reference/html/)

​	[Spring-data-jpa详解，全方位介绍](http://www.cnblogs.com/xuyuanjia/category/858546.html)

​	[jpa动态查询第三方库](https://github.com/wenhao/jpa-spec)

##  1 maven 依赖

​	`spring-framework-bom`  `spring-data-releasetrain元素` 用于解决jar版本不一致问题 , 可以不用再指定版本信息

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>org.springframework.data.examples</groupId>
	<artifactId>spring-data-examples-bom</artifactId>
	<version>1.0.0.BUILD-SNAPSHOT</version>

	<name>Spring Data - Using the BOM for dependency management</name>

	<properties>
		<spring.version>5.0.2.RELEASE</spring.version>
		<spring-data.version>Kay-SR2</spring-data.version>
	</properties>

	<dependencyManagement>
		<dependencies>

			<dependency>
				<groupId>org.springframework</groupId>
				<artifactId>spring-framework-bom</artifactId>
				<version>${spring.version}</version>
				<scope>import</scope>
				<type>pom</type>
			</dependency>

			<dependency>
				<groupId>org.springframework.data</groupId>
				<artifactId>spring-data-releasetrain</artifactId>
				<version>${spring-data.version}</version>
				<scope>import</scope>
				<type>pom</type>
			</dependency>

		</dependencies>

	</dependencyManagement>

	<dependencies>

		<dependency>
			<groupId>org.springframework.data</groupId>
			<artifactId>spring-data-rest-webmvc</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.data</groupId>
			<artifactId>spring-data-jpa</artifactId>
		</dependency>

	</dependencies>

</project>
```

## 2 Spring Data Repositories

​	Spring Data Repositories 提供了 Respositiry 接口 , 该接口极其自接口对常用的数据库操作进行了抽象 

### 1 CrudRepository

​	抽象了常用的增删改查等操作 , 接口定义如下:

```java
public interface CrudRepository<T, ID extends Serializable>
  extends Repository<T, ID> {

  <S extends T> S save(S entity);      	// 保存对象

  Optional<T> findById(ID primaryKey); // 通过id查找

  Iterable<T> findAll();               // 查询所有

  long count();                        // 查询总数

  void delete(T entity);               // 删除对象

  boolean existsById(ID primaryKey);   // 根据id判断数据是否存在

  // … more functionality omitted.
}
```

### 2 PagingAndSortingRepository

​	CurdRepository 的子接口 , 定义了分页和排序功能 :

```java
public interface PagingAndSortingRepository<T, ID extends Serializable>
  extends CrudRepository<T, ID> {

  Iterable<T> findAll(Sort sort);

  Page<T> findAll(Pageable pageable);
}
```

​	访问第二页 , 每页20条数据的参考代码如下 :

```java
PagingAndSortingRepository<User, Long> repository = // … get access to a bean
Page<User> users = repository.findAll(new PageRequest(1, 20));

// 分页并排序查找
Page<User> page = userReposity.findAll(new PageRequest(1, 4, new Sort(Sort.Direction.ASC, "age")));
int total = page.getSize();
List<User> list = page.getContent();
```

### 3 JpaRepository

​	继承 PagingAndSortingRepository

```java
/**
 * JPA specific extension of {@link org.springframework.data.repository.Repository}.
 *
 * @author Oliver Gierke
 * @author Christoph Strobl
 * @author Mark Paluch
 */
@NoRepositoryBean
public interface JpaRepository<T, ID> extends PagingAndSortingRepository<T, ID>, QueryByExampleExecutor<T> {


	List<T> findAll();
	List<T> findAll(Sort sort);
	List<T> findAllById(Iterable<ID> ids);
	<S extends T> List<S> saveAll(Iterable<S> entities);

	/**
	 * Flushes all pending changes to the database.
	 */
	void flush();

	/**
	 * Saves an entity and flushes changes instantly.
	 *
	 * @param entity
	 * @return the saved entity
	 */
	<S extends T> S saveAndFlush(S entity);

	/**
	 * Deletes the given entities in a batch which means it will create a single {@link Query}. Assume that we will clear
	 * the {@link javax.persistence.EntityManager} after the call.
	 *
	 * @param entities
	 */
	void deleteInBatch(Iterable<T> entities);

	/**
	 * Deletes all entities in a batch call.
	 */
	void deleteAllInBatch();

	/**
	 * Returns a reference to the entity with the given identifier.
	 *
	 * @param id must not be {@literal null}.
	 * @return a reference to the entity with the given identifier.
	 * @see EntityManager#getReference(Class, Object)
	 * @throws javax.persistence.EntityNotFoundException if no entity exists for given {@code id}.
	 */
	T getOne(ID id);

	@Override
	<S extends T> List<S> findAll(Example<S> example);

	@Override
	<S extends T> List<S> findAll(Example<S> example, Sort sort);
}

```

### 4 使用步骤

1. 定义接口继承 CurdReposity(或其子接口) , 比如

```java
interface PersonRepository extends Repository<Person, Long> { … }
```

2. 声明查询方法

```java
interface PersonRepository extends Repository<Person, Long> {
  List<Person> findByLastname(String lastname);
}
```

3. 配置spring声明

   javaConfig 配置方式 :

```java
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
class Config {}
```

​	xml 配置方式 :

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xmlns:jpa="http://www.springframework.org/schema/data/jpa"
   xsi:schemaLocation="http://www.springframework.org/schema/beans
     http://www.springframework.org/schema/beans/spring-beans.xsd
     http://www.springframework.org/schema/data/jpa
     http://www.springframework.org/schema/data/jpa/spring-jpa.xsd">

   <jpa:repositories base-package="com.acme.repositories"/>

</beans>
```

4. 注入 reposity 实例,并使用

```java
class SomeClient {
  @autowired
  private final PersonRepository repository;

  SomeClient(PersonRepository repository) {
    this.repository = repository;
  }

  void doSomething() {
    List<Person> persons = repository.findByLastname("Matthews");
  }
}
```

​	**注意 : 可以看到我们并不需要写 PersonRepository 的实现类 , 因为spring 通过代理方式自动创建了 SimpleReposityImpl 类来执行查询 !!!**

## 3 查询策略

​	spring data Repository 内置了一些查询策略 :

1. 根据方法名创建查询语句.该策略只需要用户按照一定的规则书写查询方法名 , spring 会自动生成对应的sql语句
2. 使用用户指定的查询语句(@Query) . 可以是通过注解声明或其他方式
3. 查找声明,不存在则根据方法名创建(默认策略)

### 1 根据方法名创建查询语句

​	The mechanism strips the prefixes `find…By`, `read…By`, `query…By`, `count…By`, and `get…By` from the method and starts parsing the rest of it.

```java
interface PersonRepository extends Repository<User, Long> {

  List<Person> findByEmailAddressAndLastname(EmailAddress emailAddress, String lastname);

  // Enables the distinct flag for the query
  List<Person> findDistinctPeopleByLastnameOrFirstname(String lastname, String firstname);
  List<Person> findPeopleDistinctByLastnameOrFirstname(String lastname, String firstname);

  // Enabling ignoring case for an individual property
  List<Person> findByLastnameIgnoreCase(String lastname);
  // Enabling ignoring case for all suitable properties
  List<Person> findByLastnameAndFirstnameAllIgnoreCase(String lastname, String firstname);

  // Enabling static ORDER BY for a query
  List<Person> findByLastnameOrderByFirstnameAsc(String lastname);
  List<Person> findByLastnameOrderByFirstnameDesc(String lastname);
}
```

​	根据方法名创建sql , 方法名中支持的关键词如下 :

| Keyword             | Sample                                   | JPQL snippet                             |
| ------------------- | ---------------------------------------- | ---------------------------------------- |
| `And`               | `findByLastnameAndFirstname`             | `… where x.lastname = ?1 and x.firstname = ?2` |
| `Or`                | `findByLastnameOrFirstname`              | `… where x.lastname = ?1 or x.firstname = ?2` |
| `Is,Equals`         | `findByFirstname`,`findByFirstnameIs`,`findByFirstnameEquals` | `… where x.firstname = ?1`               |
| `Between`           | `findByStartDateBetween`                 | `… where x.startDate between ?1 and ?2`  |
| `LessThan`          | `findByAgeLessThan`                      | `… where x.age < ?1`                     |
| `LessThanEqual`     | `findByAgeLessThanEqual`                 | `… where x.age <= ?1`                    |
| `GreaterThan`       | `findByAgeGreaterThan`                   | `… where x.age > ?1`                     |
| `GreaterThanEqual`  | `findByAgeGreaterThanEqual`              | `… where x.age >= ?1`                    |
| `After`             | `findByStartDateAfter`                   | `… where x.startDate > ?1`               |
| `Before`            | `findByStartDateBefore`                  | `… where x.startDate < ?1`               |
| `IsNull`            | `findByAgeIsNull`                        | `… where x.age is null`                  |
| `IsNotNull,NotNull` | `findByAge(Is)NotNull`                   | `… where x.age not null`                 |
| `Like`              | `findByFirstnameLike`                    | `… where x.firstname like ?1`            |
| `NotLike`           | `findByFirstnameNotLike`                 | `… where x.firstname not like ?1`        |
| `StartingWith`      | `findByFirstnameStartingWith`            | `… where x.firstname like ?1` (parameter bound with appended `%`) |
| `EndingWith`        | `findByFirstnameEndingWith`              | `… where x.firstname like ?1` (parameter bound with prepended `%`) |
| `Containing`        | `findByFirstnameContaining`              | `… where x.firstname like ?1` (parameter bound wrapped in `%`) |
| `OrderBy`           | `findByAgeOrderByLastnameDesc`           | `… where x.age = ?1 order by x.lastname desc` |
| `Not`               | `findByLastnameNot`                      | `… where x.lastname <> ?1`               |
| `In`                | `findByAgeIn(Collection<Age> ages)`      | `… where x.age in ?1`                    |
| `NotIn`             | `findByAgeNotIn(Collection<Age> ages)`   | `… where x.age not in ?1`                |
| `True`              | `findByActiveTrue()`                     | `… where x.active = true`                |
| `False`             | `findByActiveFalse()`                    | `… where x.active = false`               |
| `IgnoreCase`        | `findByFirstnameIgnoreCase`              | `… where UPPER(x.firstame) = UPPER(?1)`  |

​	spring 根据方法名构建查询时 , 是根据驼峰命名规则从右往左切割确定属性名 , 某些情况下由于方法名的原因可能会导致属性匹配出错,比如 :

```java
List<Person> findByAddressZipCode(ZipCode zipCode);
```

​	spring 将会将属性切割为 addressZip.code , 而用户实际希望切割成 address.zipCode , 为了避免这种错误 , 建议使用 `_` 来分隔属性 , 如 :

```java
List<Person> findByAddress_ZipCode(ZipCode zipCode);
```

​	这样生成的查询方法就是查询 address.zipCode 了

### 2 使用注解指定查询

```java
@Query("select u from User u where u.name like ?1")
List<User> findAllByCustomQueryAndStream(String arg);
```

​	此时使用的就是注解指定的查询语句 , 注意 @Query 默认时 JPQL 语句 , 如果向执行原生 sql 语句 , 可以加上属性 `nativeQuery=true`

```java
@Query("select u from User u where u.name like ?1",nativeQuery=true)
List<User> findAllByCustomQueryAndStream(String arg);

@Query("select u from User u where u.firstname = :firstname or u.lastname = :lastname") 
User findByLastnameOrFirstname(@Param("lastname") String lastname,@Param("firstname") String firstname); 
```

​	如果想执行修改数据 , 需要在方法名上添加注解 @Modifying

## 4 spring data ipa 动态查询实现

​	

​	[Spring Data JPA中的动态查询](http://blog.csdn.net/anxpp/article/details/51996472)





```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:p="http://www.springframework.org/schema/p"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mongo="http://www.springframework.org/schema/data/mongo"
       xmlns:jpa="http://www.springframework.org/schema/data/jpa"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
           http://www.springframework.org/schema/aop
           http://www.springframework.org/schema/aop/spring-aop-3.0.xsd
           http://www.springframework.org/schema/tx
           http://www.springframework.org/schema/tx/spring-tx-3.0.xsd
           http://www.springframework.org/schema/context
           http://www.springframework.org/schema/context/spring-context-3.0.xsd
           http://www.springframework.org/schema/data/mongo
           http://www.springframework.org/schema/data/mongo/spring-mongo-1.0.xsd
           http://www.springframework.org/schema/data/jpa http://www.springframework.org/schema/data/jpa/spring-jpa.xsd">

    <!-- 数据库连接 -->
    <context:property-placeholder location="classpath:*.properties" ignore-unresolvable="true"/>
    <!-- service包 -->
    <context:component-scan base-package="com.fmi110.springjpa.service"/>
    <!-- 使用cglib进行动态代理 -->
    <aop:aspectj-autoproxy proxy-target-class="true"/>
    <!-- 支持注解方式声明式事务 -->
    <tx:annotation-driven transaction-manager="transactionManager" proxy-target-class="true"/>

    <!-- reposity -->
    <!--spring-data-jpa 把Repository和RepositoryImpl 文件放在同一个包下面-->
    <!--RepositoryImpl 不需要实现 Repository接口 , spring-data-jpa自动就能判断二者的关系-->
    <jpa:repositories base-package="com.fmi110.springjpa.reposity" repository-impl-postfix="Impl"
                      entity-manager-factory-ref="entityManagerFactory" transaction-manager-ref="transactionManager"/>
    <!-- 实体管理器 -->
    <bean id="entityManagerFactory" class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <property name="packagesToScan" value="com.fmi110.springjpa.eneity"/>
        <property name="persistenceProvider">
            <bean class="org.hibernate.jpa.HibernatePersistenceProvider"/>
        </property>
        <property name="jpaVendorAdapter">
            <bean class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter">
                <property name="generateDdl" value="false"/>
                <property name="database" value="MYSQL"/>
                <property name="databasePlatform" value="org.hibernate.dialect.MySQL5InnoDBDialect"/>
                <!-- <property name="showSql" value="true" /> -->
            </bean>
        </property>
        <property name="jpaDialect">
            <bean class="org.springframework.orm.jpa.vendor.HibernateJpaDialect"/>
        </property>
        <property name="jpaPropertyMap">
            <map>
                <entry key="hibernate.query.substitutions" value="true 1, false 0"/>
                <entry key="hibernate.default_batch_fetch_size" value="16"/>
                <entry key="hibernate.max_fetch_depth" value="2"/>
                <entry key="hibernate.generate_statistics" value="true"/>
                <entry key="hibernate.bytecode.use_reflection_optimizer" value="true"/>
                <entry key="hibernate.cache.use_second_level_cache" value="false"/>
                <entry key="hibernate.cache.use_query_cache" value="false"/>
            </map>
        </property>
    </bean>

    <!-- 事务管理器 -->
    <bean id="transactionManager" class="org.springframework.orm.jpa.JpaTransactionManager">
        <property name="entityManagerFactory" ref="entityManagerFactory"/>
    </bean>

    <!-- 数据源 -->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource" init-method="init" destroy-method="close">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/shiro"/>
        <property name="username" value="root"/>
        <property name="password" value=""/>

    </bean>

    <!-- 事务 -->
    <tx:advice id="txAdvice" transaction-manager="transactionManager">
        <tx:attributes>
            <tx:method name="*"/>
            <tx:method name="get*" read-only="true"/>
            <tx:method name="find*" read-only="true"/>
            <tx:method name="select*" read-only="true"/>
            <tx:method name="delete*" propagation="REQUIRED"/>
            <tx:method name="update*" propagation="REQUIRED"/>
            <tx:method name="add*" propagation="REQUIRED"/>
            <tx:method name="insert*" propagation="REQUIRED"/>
        </tx:attributes>
    </tx:advice>
    <!-- 事务入口 -->
    <aop:config>
        <aop:pointcut id="allServiceMethod" expression="execution(* com.fmi110.springjpa.service..*.*(..))"/>
        <aop:advisor pointcut-ref="allServiceMethod" advice-ref="txAdvice"/>
    </aop:config>

</beans>
```