# 19_spring 整合 ehcache 缓存

​	[spring 缓存注解](http://blog.csdn.net/wangshfa/article/details/50678534)

​	[ehcache官方文档](http://www.ehcache.org/documentation/) 

​	[Spring整合Ehcache管理缓存](https://www.jianshu.com/p/94860934d6d2?utm_campaign=haruki&utm_content=note&utm_medium=reader_share&utm_source=qq)

​	[缓存算法lru,lfu,fifo简介](http://www.cnblogs.com/dolphin0520/p/3749259.html)

​	[LinkedHashMap实现LRU缓存算法](http://blog.csdn.net/exceptional_derek/article/details/11713255)

## 1 maven 坐标

​	ehcache 的坐标如下, spring坐标这里就不给出了

```xml
<dependency>
  <groupId>net.sf.ehcache</groupId>
  <artifactId>ehcache</artifactId>
  <version>2.10.2</version>
</dependency>
```

## 2 ehcache.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<ehcache updateCheck="false" dynamicConfig="false">
    <diskStore path="java.io.tmpdir"/>

    <!--缓存授权信息用-->
    <cache name="authorizationCache"
           maxEntriesLocalHeap="2000"
           eternal="false"
           timeToIdleSeconds="1800"
           timeToLiveSeconds="1800"
           overflowToDisk="false"
           statistics="true">
    </cache>
    <!--缓存认证信息用-->
    <cache name="authenticationCache"
           maxEntriesLocalHeap="2000"
           eternal="false"
           timeToIdleSeconds="1800"
           timeToLiveSeconds="1800"
           overflowToDisk="false"
           statistics="true">
    </cache>
    <!--缓存会话用-->
    <cache name="activeSessionCache"
           maxEntriesLocalHeap="2000"
           eternal="false"
           timeToIdleSeconds="1800"
           timeToLiveSeconds="1800"
           overflowToDisk="false"
           statistics="true">
    </cache>

    <!-- 缓存1分钟 -->
    <cache name="oneMinute"
           maxElementsInMemory="10000"
           maxElementsOnDisk="100000"
           eternal="false"
           timeToIdleSeconds="60"
           timeToLiveSeconds="60"
           overflowToDisk="false"
           diskPersistent="false" />

    <!-- 缓存半小时 -->
    <cache name="halfHour"
           maxElementsInMemory="10000"
           maxElementsOnDisk="100000"
           eternal="false"
           timeToIdleSeconds="1800"
           timeToLiveSeconds="1800"
           overflowToDisk="false"
           diskPersistent="false" />

    <!-- 缓存一小时 -->
    <cache name="hour"
           maxElementsInMemory="10000"
           maxElementsOnDisk="100000"
           eternal="false"
           timeToIdleSeconds="3600"
           timeToLiveSeconds="3600"
           overflowToDisk="false"
           diskPersistent="false" />

    <!-- 缓存一天 -->
    <cache name="oneDay"
           maxElementsInMemory="10000"
           maxElementsOnDisk="100000"
           eternal="false"
           timeToIdleSeconds="86400"
           timeToLiveSeconds="86400"
           overflowToDisk="false"
           diskPersistent="false" />

    <!--
        name:缓存名称。
        maxElementsInMemory：缓存最大个数。
        eternal:对象是否永久有效，一但设置了，timeout将不起作用。
        timeToIdleSeconds：设置对象在失效前的允许闲置时间（单位：秒）。仅当eternal=false对象不是永久有效时使用，可选属性，默认值是0，也就是可闲置时间无穷大。
        timeToLiveSeconds：设置对象在失效前允许存活时间（单位：秒）。最大时间介于创建时间和失效时间之间。仅当eternal=false对象不是永久有效时使用，默认是0.，也就是对象存活时间无穷大。
        overflowToDisk：当内存中对象数量达到maxElementsInMemory时，Ehcache将会对象写到磁盘中。
        diskSpoolBufferSizeMB：这个参数设置DiskStore（磁盘缓存）的缓存区大小。默认是30MB。每个Cache都应该有自己的一个缓冲区。
        maxElementsOnDisk：硬盘最大缓存个数。
        diskPersistent：是否缓存虚拟机重启期数据 Whether the disk store persists between restarts of the Virtual Machine. The default value is false.
        diskExpiryThreadIntervalSeconds：磁盘失效线程运行时间间隔，默认是120秒。
        memoryStoreEvictionPolicy：当达到maxElementsInMemory限制时，Ehcache将会根据指定的策略去清理内存。默认策略是LRU（最近最少使用,基于时间）。你可以设置为FIFO（先进先出）或是LFU（较少使用,基于访问频率）。
        clearOnFlush：内存数量最大时是否清除。
    -->
    <defaultCache name="defaultCache"
                  maxElementsInMemory="10000"
                  eternal="false"
                  timeToIdleSeconds="120"
                  timeToLiveSeconds="120"
                  overflowToDisk="false"
                  maxElementsOnDisk="100000"
                  diskPersistent="false"
                  diskExpiryThreadIntervalSeconds="120"
                  memoryStoreEvictionPolicy="LRU"/>

</ehcache>
```

## 3 applicationContext-ehcahe.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	   xmlns:cache="http://www.springframework.org/schema/cache"
	   xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/cache
        http://www.springframework.org/schema/cache/spring-cache.xsd ">

	<!--启用缓存注解-->
	<cache:annotation-driven cache-manager="cacheManager"/>

	<bean id="ehcacheManager" class="org.springframework.cache.ehcache.EhCacheManagerFactoryBean">
		<!--指定 ehcache 配置文件路径-->
		<property name="configLocation" value="classpath:ehcache.xml"/>
	</bean>

	<bean id="cacheManager" class="org.springframework.cache.ehcache.EhCacheCacheManager">
		<property name="cacheManager" ref="ehcacheManager"/>
		<property name="transactionAware" value="true"/>
	</bean>
</beans>
```

​	配置中 `<property name="configLocation" value="classpath:ehcache.xml"/>`  指定了ehcache的配置文件所在的目录!!!

## 4. spring 缓存注解

​	[spring cache 注解介绍](https://www.ibm.com/developerworks/cn/opensource/os-cn-spring-cache/)

### 1 @Cacheable

​	**常用于方法注解 , 相当于  save 操作 !!! 先检查缓存是否有数据,没有数据则调用被注解的方法**

| 参数        | 说明                                       | 示例                                       |
| --------- | ---------------------------------------- | ---------------------------------------- |
| value     | 缓存的名称，在 spring 配置文件中定义，必须指定至少一个          | 例如：@Cacheable(value=”mycache”) 或者 @Cacheable(value={”cache1”,”cache2”} |
| key       | 缓存的 key，可以为空，如果指定要按照 SpEL 表达式编写，如果不指定，则缺省按照方法的所有参数进行组合 | 例如：@Cacheable(value=”testcache”,key=”#userName”) |
| condition | 缓存的条件，可以为空，使用 SpEL 编写，返回 true 或者 false，只有为 true 才进行缓存 | 例如：@Cacheable(value=”testcache”,condition=”#userName.length()>2”) |

​	**注意 :** @Cacheable 的作用相当于 save 操作 , 在调用方法前会检查缓存中是否有数据 , **如果数据已经存在 , 则直接从缓存中取 , 不再调用方法 !!! 这种行为对于执行数据库更新的方法上要慎用!!!可能会导致更新语句不执行!!!此时 ,应该使用 @CachePut 注解**

```java
@Cacheable(value="accountCache")// 使用了一个缓存名叫 accountCache , key的值默认为方法的参数 -- username 的值
public Account getAccountByName(String userName) { 
  return getFromDB(userName); 
} 
```

### 2 @CachePut  

​	**常用于方法注解,相当于 update 操作 , 并将返回值存入缓存 . 与 @Cacheable 不同 , 该注解每次都会触发真实方法(被注解的方法)的调用**

| 参数        | 说明                                       | 示例                                       |
| --------- | ---------------------------------------- | ---------------------------------------- |
| value     | 缓存的名称，在 spring 配置文件中定义，必须指定至少一个          | 例如：@Cacheable(value=”mycache”) 或者 @Cacheable(value={”cache1”,”cache2”} |
| key       | 缓存的 key，可以为空，如果指定要按照 SpEL 表达式编写，如果不指定，则缺省按照方法的所有参数进行组合 | 例如：@Cacheable(value=”testcache”,key=”#userName”) |
| condition | 缓存的条件，可以为空，使用 SpEL 编写，返回 true 或者 false，只有为 true 才进行缓存 | 例如：@Cacheable(value=”testcache”,condition=”#userName.length()>2”) |

### 3 @CacheEvict

​	**常用于方法注解 , 根据一定的条件对缓存进行清空**

​	**注意: @CacheEvict 注释有一个属性 beforeInvocation，缺省为 false，即缺省情况下，都是在实际的方法执行完成后，才对缓存进行清空操作。期间如果执行方法出现异常，则会导致缓存清空不被执行**

| 参数               | 说明                                       | 示例                                       |
| ---------------- | ---------------------------------------- | ---------------------------------------- |
| value            | 缓存的名称，在 spring 配置文件中定义，必须指定至少一个          | 例如：@CachEvict(value=”mycache”) 或者 @CachEvict(value={”cache1”,”cache2”} |
| key              | 缓存的 key，可以为空，如果指定要按照 SpEL 表达式编写，如果不指定，则缺省按照方法的所有参数进行组合 | 例如：@CachEvict(value=”testcache”,key=”#userName”) |
| condition        | 缓存的条件，可以为空，使用 SpEL 编写，返回 true 或者 false，只有为 true 才清空缓存 | 例如：@CachEvict(value=”testcache”,condition=”#userName.length()>2”) |
| allEntries       | 是否清空所有缓存内容，缺省为 false，如果指定为 true，则方法调用后将立即清空所有缓存 | 例如：@CachEvict(value=”testcache”,allEntries=true) |
| beforeInvocation | 是否在方法执行前就清空，缺省为 false，如果指定为 true，则在方法还没有执行的时候就清空缓存，缺省情况下，如果方法执行抛出异常，则不会清空缓存 | 例如：@CachEvict(value=”testcache”，beforeInvocation=true) |

## 5 spring cache自定义 key 的生成策略

​	[Spring-Cache key设置注意事项](http://blog.csdn.net/exceptional_derek/article/details/11713255)

### 1 spring cache 默认 key 生成策略

​	Since caches are essentially key-value stores, each invocation of a cached method needs to be translated into a suitable key for cache access. Out of the box, the caching abstraction uses a simple `KeyGenerator` based on the following algorithm:

- If no params are given, return `SimpleKey.EMPTY`.
- If only one param is given, return that instance.
- If more the one param is given, return a `SimpleKey` containing all parameters.

​	This approach works well for most use-cases; As long as parameters have *natural keys* and implement valid `hashCode()` and `equals()` methods. If that is not the case then the strategy needs to be changed.

​	也就是说 , 当方法参数具备自然主键,并且重写了 `hashCode()` 和 `equal()` 方法时 , spring cache 默认的可以生成策略可以满足要求 , 否则就需要自己实现key的生成策略

### 2 使用 spring EL 表达式生成 key

​	[官方文档](https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#cache)

```java
@Cacheable(cacheNames="books", key="#isbn")
public Book findBook(ISBN isbn, boolean checkWarehouse, boolean includeUsed)

@Cacheable(cacheNames="books", key="#isbn.rawNumber")
public Book findBook(ISBN isbn, boolean checkWarehouse, boolean includeUsed)

@Cacheable(cacheNames="books", key="T(someType).hash(#isbn)")
public Book findBook(ISBN isbn, boolean checkWarehouse, boolean includeUsed)
```

​	以下内容出处 : [@Cacheable 的key生成](http://blog.csdn.net/fireofjava/article/details/48913335)

​	Spring还为我们提供了一个root对象可以用来生成key。通过该root对象我们可以获取到以下信息。

| **属性名称**    | **描述**           | **示例**               |
| ----------- | ---------------- | -------------------- |
| methodName  | 当前方法名            | #root.methodName     |
| method      | 当前方法             | #root.method.name    |
| target      | 当前被调用的对象         | #root.target         |
| targetClass | 当前被调用的对象的class   | #root.targetClass    |
| args        | 当前方法参数组成的数组      | #root.args[0]        |
| caches      | 当前被调用的方法使用的Cache | #root.caches[0].name |

​       当我们要使用root对象的属性作为key时我们也可以将“#root”省略，因为Spring默认使用的就是root对象的属性。如：

```java
 @Cacheable(value={"users", "xxx"}, key="caches[1].name")
 public User find(User user) {
      return null;
 }
@Cacheable(value={"TeacherAnalysis_public_chart"}, key="#root.target.getDictTableName() + '_' + #root.target.getFieldName()")
public List<Map<String, Object>> getChartList(Map<String, Object> paramMap)
```

> key 要使用字符串需要使用单引号 `'`  包裹字符串!!!

### 3 自定义 key 生成器

​	参考该文章 : [Spring cache 自定义Key生成策略](https://my.oschina.net/lis1314/blog/708711)