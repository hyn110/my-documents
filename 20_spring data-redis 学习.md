#  20_spring data-redis

[spring data-redis 指南](https://docs.spring.io/spring-data/redis/docs/2.0.2.RELEASE/reference/html/)

[spring data-jpa](https://docs.spring.io/spring-data/jpa/docs/2.0.2.RELEASE/reference/html/)

[Redis 地理位置命令简介](http://blog.csdn.net/opensure/article/details/51375961)



## 1 配置

**1 maven 坐标**

```Xml
<dependencies>
    <dependency>
        <groupId>org.springframework.data</groupId>
        <artifactId>spring-data-redis</artifactId>
        <version>1.8.8.RELEASE</version>
    </dependency>
</dependencies>
```

**2 redis 配置**

 	单机版 jredis 配置

```Xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:p="http://www.springframework.org/schema/p"
  xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

  <bean id="jedisConnectionFactory" class="org.springframework.data.redis.connection.jedis.JedisConnectionFactory" p:host-name="server" p:port="6379" />

</beans>
```

​	jredis 哨兵设置

```Java
/**
 * jedis
 */
@Bean
public RedisConnectionFactory jedisConnectionFactory() {
  RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
  .master("mymaster")
  .sentinel("127.0.0.1", 26379)
  .sentinel("127.0.0.1", 26380);
  return new JedisConnectionFactory(sentinelConfig);
}
```

> `RedisSentinelConfiguration` can also be defined via `PropertySource`.
>
> Configuration Properties
>
> - `spring.redis.sentinel.master`: name of the master node.
> - `spring.redis.sentinel.nodes`: Comma delimited list of host:port pairs.

​	有时想直接访问哨兵主机,此时可以使用 `RedisConnectionFactory.getSentinelConnection()` 或 `RedisConnection.getSentinelCommands()` 可以访问到第一个配置的哨兵

## 2 RedisTemplate

### 1 概述

​	`org.springframework.data.redis.core.RedisTemplate` 负责对象的序列化和链接管理 , `RedisConnection` 访问 redis 服务器并返回二进制数据 . RedisTemplate 是基于 java 实现对象的序列化的 , Redis Module 提供了多种序列化实现 , RedisTemplate 可以通过相应的配置进行选择选择. 序列化器实现位于包 `org.springframework.data.redis.serializer` 下

​	RedisTemplate 实现的通用操作接口与 redis 操作的描述如下表

| 接口                    | 对应的 redis 操作                             |
| --------------------- | ---------------------------------------- |
| *Key 类型 Operations*   |                                          |
| GeoOperations         | Redis 地理位置命令操作 `GEOADD`, `GEORADIUS`,…)  |
| HashOperations        | Redis hash 操作                            |
| HyperLogLogOperations | Redis HyperLogLog operations like (`PFADD`, `PFCOUNT`,…) |
| ListOperations        | 操作 list                                  |
| SetOperations         | 操作 set                                   |
| ValueOperations       | 操作普通字符串                                  |
| ZSetOperations        | Redis zset (or sorted set) 操作            |
| *Key 绑定 Operations*   |                                          |
| BoundGeoOperations    | Redis key bound geospatial operations.   |
| BoundHashOperations   | Redis hash key bound operations          |
| BoundKeyOperations    | Redis key bound operations               |
| BoundListOperations   | Redis list key bound operations          |
| BoundSetOperations    | Redis set key bound operations           |
| BoundValueOperations  | Redis string (or value) key bound operations |
| BoundZSetOperations   | Redis zset (or sorted set) key bound operations |

​	RedisConnectionFactiory 和 RedisTempalte 的可如下配置 :

```Xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:p="http://www.springframework.org/schema/p"
  xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

  <bean id="jedisConnectionFactory" class="org.springframework.data.redis.connection.jedis.JedisConnectionFactory" p:use-pool="true"/>
  
  <!-- RedisTemplate 定义 -->
  <bean id="redisTemplate" class="org.springframework.data.redis.core.RedisTemplate" p:connection-factory-ref="jedisConnectionFactory"/>
  
</beans>
```

​	使用示例如下 :

```Java
public class Example {

  // 注入 RedisTempalte
  @Autowired
  private RedisTemplate<String, String> template;

  // inject the template as ListOperations
  @Resource(name="redisTemplate")
  private ListOperations<String, String> listOps;

  public void addLink(String userId, URL url) {
    listOps.leftPush(userId, url.toExternalForm());
  }
}
```

### 2 StringRedisTemplate

​	因为 redis 大多数情况下 k-v 都是 String 类型的数据 , 所以 spring Redis Module 模块提供了专门用于操作 String 数据的 StringRedisTemplate , 该模板使用 `StringRedisSerializer` 对数据进行序列化 , 配置和使用示例如下:

```Xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:p="http://www.springframework.org/schema/p"
  xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

  <bean id="jedisConnectionFactory" class="org.springframework.data.redis.connection.jedis.JedisConnectionFactory" p:use-pool="true"/>

  <bean id="stringRedisTemplate" class="org.springframework.data.redis.core.StringRedisTemplate" p:connection-factory-ref="jedisConnectionFactory"/>
  
</beans>
```

```Java
public class Example {

  @Autowired
  private StringRedisTemplate redisTemplate;

  public void addLink(String userId, URL url) {
    redisTemplate.opsForList().leftPush(userId, url.toExternalForm());
  }
}
```

​	如果需要获取 connection 直接操作 redis 服务器 , 可通过 RedisCallback 接口 实现 :

```Java
public void useCallback() {

  redisTemplate.execute(new RedisCallback<Object>() {
    public Object doInRedis(RedisConnection connection) throws DataAccessException {
      Long size = connection.dbSize();
      // Can cast to StringRedisConnection if using a StringRedisTemplate
      ((StringRedisConnection)connection).set("key", "value");
    }
   });
}
```

​	注意 : 当使用的模板是 StringRedisTemplate 时 , 返回的连接对象是 StringRedisConnection

## 3 序列化器 Serializers

​	`org.springframework.data.redis.serializer`  包下序列化器有两种类型实现 :

1. 基于 `RedisSerializer` 的双向序列化器 , 该类型主要将数据序列化为 byte[]

2. `RedisElementReader` and `RedisElementWriter` 实现读写对象 , 该类型使用的是 ByteBuffer 实现

   开箱即用的序列化器 , 比如 

   - `StringRedisSerializer`
   - `JdkSerializationRedisSerializer`

常见的序列化器 :

| 序列化器                               | 描述            |
| ---------------------------------- | ------------- |
| OxmSerializer                      | 存储 Object/xml |
| Jackson2JsonRedisSerializer        | 存储 json 对象    |
| GenericJackson2JsonRedisSerializer | 存储 json 对象    |
| StringRedisSerializer              |               |
| JdkSerializationRedisSerializer    |               |

## 4 Hash mapping

Data can be stored using various data structures within Redis. You already learned about `Jackson2JsonRedisSerializer`which can convert objects in [JSON](https://en.wikipedia.org/wiki/JSON) format. JSON can be ideally stored as value using plain keys. A more sophisticated mapping of structured objects can be achieved using Redis Hashes. Spring Data Redis offers various strategies for mapping data to hashes depending on the use case.

1. Direct mapping using `HashOperations` and a [serializer](https://docs.spring.io/spring-data/redis/docs/2.0.2.RELEASE/reference/html/#redis:serializer)
2. Using [Redis Repositories](https://docs.spring.io/spring-data/redis/docs/2.0.2.RELEASE/reference/html/#redis.repositories)
3. Using `HashMapper` and `HashOperations`

### 4.1 Hash mappers

Hash mappers are converters to map objects to a `Map<K, V>` and back. `HashMapper` is intended for using with Redis Hashes.

Multiple implementations are available out of the box:

1. `BeanUtilsHashMapper` using Spring’s [BeanUtils](https://docs.spring.io/spring/docs/5.0.2.RELEASE/javadoc-api/org/springframework/beans/BeanUtils.html).
2. `ObjectHashMapper` using [Object to Hash Mapping](https://docs.spring.io/spring-data/redis/docs/2.0.2.RELEASE/reference/html/#redis.repositories.mapping).
3. [`Jackson2HashMapper`](https://docs.spring.io/spring-data/redis/docs/2.0.2.RELEASE/reference/html/#redis.hashmappers.jackson2) using [FasterXML Jackson](https://github.com/FasterXML/jackson).

```Java
public class Person {
  String firstname;
  String lastname;

  // …
}

public class HashMapping {

  @Autowired
  HashOperations<String, byte[], byte[]> hashOperations;

  HashMapper<Object, byte[], byte[]> mapper = new ObjectHashMapper();

  public void writeHash(String key, Person person) {

    Map<byte[], byte[]> mappedHash = mapper.toHash(person);
    hashOperations.putAll(key, mappedHash);
  }

  public Person loadHash(String key) {

    Map<byte[], byte[]> loadedHash = hashOperations.entries("key");
    return (Person) mapper.fromHash(loadedHash);
  }
}
```

### 4.2 Jackson2HashMapper

`Jackson2HashMapper` provides Redis Hash mapping for domain objects using [FasterXML Jackson](https://github.com/FasterXML/jackson). `Jackson2HashMapper` can map data map top-level properties as Hash field names and optionally flatten the structure. Simple types map to simple values. Complex types (nested objects, collections, maps) are represented as nested JSON.

Flattening creates individual hash entries for all nested properties and resolves complex types into simple types, as far as possible.

```Java
public class Person {
  String firstname;
  String lastname;
  Address address;
}

public class Address {
  String city;
  String country;
}
```

| Hash Field | Value                                    |
| ---------- | ---------------------------------------- |
| firstname  | `Jon`                                    |
| lastname   | `Snow`                                   |
| address    | `{ "city" : "Castle Black", "country" : "The North" }` |

| Hash Field      | Value          |
| --------------- | -------------- |
| firstname       | `Jon`          |
| lastname        | `Snow`         |
| address.city    | `Castle Black` |
| address.country | `The North`    |

​	**注意 : Flattening requires all property names to not interfere with the JSON path. Using dots or brackets in map keys or as property names is not supported using flattening. The resulting hash cannot be mapped back into an Object.**  

## 5 Redis Messaging/PubSub

​	Redis消息机制可以简单的划分为两个方面 : 生产(or发布)消息  和  消费(or订阅)消息 . `RedisTemplate` 主要用于生产消息.

### 1 Sending/Publishing messages

​	RedisTemplate 和 RedisConnection 都可用于发送(发布)消息 . 区别在于 : RedisTemplate 使用可以发送对象 , 而 RedisConnection 必须发送 byte数据 :

```java
// send message through connection RedisConnection con = ...
byte[] msg = ...
byte[] channel = ...
con.publish(msg, channel); // send message through RedisTemplate
RedisTemplate template = ...
template.convertAndSend("hello!", "world");
```

### 2 Receiving/Subscribing for messages

​	RedisConnection 提供 subscribe() 和 psubscribe() 方法用于开启消息订阅 . 注意消息订阅是阻塞的

​	通过实现 `MessageListener`  接口可实现redis消息订阅

​	**Message Listener Containers**



​	**MessageListenerAdapter**

## 6 Redis 事务

### 1 SessionCallback 接口

​	[redis事务简介](https://www.jianshu.com/p/361cb9cd13d5)

​	[redis事务简介2](http://blog.csdn.net/wgh1015398431/article/details/53156027)

​		[MULTI](http://redis.cn/commands/multi.html) 开启事务，总是返回OK，[EXEC](http://redis.cn/commands/exec.html) 提交事务，[DISCARD](http://redis.cn/commands/discard.html)放弃事务（放弃提交执行），[WATCH](http://redis.cn/commands/watch.html)监控

​		redis 也支持事务操作,但是redis事务不支持回滚操作,这是与关系型数据库(如mysql)的明显区别

​		RedisTemplate 支持redis 的 exec , discard , watch 操作, 但是redisTemplate 不保证同一个连接的所有操作都执行 , 为了解决这个问题 , Spring Data Redis 提供了 SessionCallback 接口 , 当需要使用 redis 事务操作时 , 可如下操作:

```java
//execute a transaction
List<Object> txResults = redisTemplate.execute(new SessionCallback<List<Object>>() {
  public List<Object> execute(RedisOperations operations) throws DataAccessException {
    operations.multi();
    operations.opsForSet().add("key", "value1");

    // This will contain the results of all ops in the transaction
    return operations.exec();
  }
});
System.out.println("Number of items added to set: " + txResults.get(0));
```

## 7 管道 Pipelining

​	如果不关注管道命令的返回结果 , 可直接调用 RedisTemplate.execute() 方法,并设置 pipeline 参数为 true 即可 . 如果要获取返回结果可通过 executePiplelined 方法 , 并通过接口 RedisCallback 或 SessionCallback 获取返回值,例如 :

```java
//pop a specified number of items from a queue
List<Object> results = stringRedisTemplate.executePipelined(
  new RedisCallback<Object>() {
    public Object doInRedis(RedisConnection connection) throws DataAccessException {
      StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
      for(int i=0; i< batchSize; i++) {
        stringRedisConn.rPop("myqueue");
      }
    return null;
  }
});
```

## 8 spring cache 支持

​	spring redis 缓存的实现位于包 `org.springframework.data.redis.cache`  , 要使用redis缓存需要在配置中配置 RedisCacheManager :

```java
@Bean
public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
	return RedisCacheManager.create(connectionFactory);
}
```
​	如果想要对 RedisCacheManager 进行配置,可通过 RedisCacheManagerBuilder 来创建:

```java
RedisCacheManager cm = RedisCacheManager.builder(connectionFactory)
	.cacheDefaults(defaultCacheConfig())
	.initialCacheConfigurations(singletonMap("predefined", defaultCacheConfig().disableCachingNullValues()))
	.transactionAware()
	.build();
```

​	RedisCache 对象的特性 , 比如 key的缓存时间 , 序列化器等可通过 RedisCacheConfigure 进行配置 :

```java
RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
    .entryTtl(Duration.ofSeconds(1))
	.disableCachingNullValues();
```

​	RedisCacheManger 的读写操作默认是不加锁的 , 所以并不保证操作的原子性 . 如果要加锁 , 可以如下配置 :

```java
RedisCacheManager cm = RedisCacheManager.build(RedisCacheWriter.lockingRedisCacheWriter())
	.cacheDefaults(defaultCacheConfig())
	...
```

​	RedisCacheManager 的默认配置如下表 :

| Setting             | Value                                    |
| ------------------- | ---------------------------------------- |
| Cache Writer        | non locking                              |
| Cache Configuration | `RedisCacheConfiguration#defaultConfiguration` |
| Initial Caches      | none                                     |
| Trasaction Aware    | no                                       |

​	RedisCacheManagerConfig 的默认配置如下:

| Key Expiration     | none                                     |
| ------------------ | ---------------------------------------- |
| Cache `null`       | yes                                      |
| Prefix Keys        | yes                                      |
| Default Prefix     | the actual cache name                    |
| Key Serializer     | `StringRedisSerializer`                  |
| Value Serializer   | `JdkSerializationRedisSerializer`        |
| Conversion Service | `DefaultFormattingConversionService` with default cache key converters |

## 9 Redis Cluster 集群

​	redis集群需要redis 3.0+ 版本 .

​	spring 提供 RedisClusterConnection 用于连接 redis 集群 , 该对象可通过 RedisClusterConfiguration进行配置,并通过 RedisConnectionFactory 创建 :

```java
@Component
@ConfigurationProperties(prefix = "spring.redis.cluster")
public class ClusterConfigurationProperties {

    /*
     * spring.redis.cluster.nodes[0] = 127.0.0.1:7379
     * spring.redis.cluster.nodes[1] = 127.0.0.1:7380
     * ...
     */
    List<String> nodes;

    /**
     * Get initial collection of known cluster nodes in format {@code host:port}.
     *
     * @return
     */
    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }
}

@Configuration
public class AppConfig {

    /**
     * Type safe representation of application.properties
     */
    @Autowired 
    ClusterConfigurationProperties clusterProperties;

    public @Bean RedisConnectionFactory connectionFactory() {

        return new JedisConnectionFactory(
            new RedisClusterConfiguration(clusterProperties.getNodes()));
    }
}
```

`RedisClusterConfiguration`也可以通过 `PropertySource` 进行配置

配置的属性如下 : 

- `spring.redis.cluster.nodes`: Comma delimited list of host:port pairs.

- `spring.redis.cluster.max-redirects`: Number of allowed cluster redirections.

  使用示例 :

```java
RedisClusterConnection connection = connectionFactory.getClusterConnnection();

connection.set("foo", value);         // slot: 12182
connection.set("{foo}.bar", value);   // slot: 12182
connection.set("bar", value);         // slot:  5461

connection.mGet("foo", "{foo}.bar");                                           

connection.mGet("foo", "bar");                 
```

```java
ClusterOperations clusterOps = redisTemplate.opsForCluster();
clusterOps.shutdown(NODE_7379);  
```

## 10 spring 整合 redis 集群配置

​	applicationContext-rediscluster.xml

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
        http://www.springframework.org/schema/aop 
        http://www.springframework.org/schema/aop/spring-aop-3.0.xsd
        http://www.springframework.org/schema/context 
        http://www.springframework.org/schema/context/spring-context-3.0.xsd">
	
	<context:component-scan base-package="com"/>

    <aop:aspectj-autoproxy proxy-target-class="true"/>
    
	<context:property-placeholder location="classpath:*.properties"/>

	
	<bean id="poolConfig" class="redis.clients.jedis.JedisPoolConfig">  
        <property name="maxIdle" value="1" /> 
        <property name="maxTotal" value="5" /> 
        <property name="blockWhenExhausted" value="true" /> 
        <property name="maxWaitMillis" value="30000" /> 
        <property name="testOnBorrow" value="true" />  
    </bean> 
	
	<bean id="jedisConnFactory" class="org.springframework.data.redis.connection.jedis.JedisConnectionFactory">
	    <!--配置集群-->
            <constructor-arg name="clusterConfig" ref="redisClusterConfig" />
        </bean> 
	     
	<!-- redis template definition -->  
	<bean id="redisTemplate" class="org.springframework.data.redis.core.RedisTemplate">  
	    <property name="connectionFactory" ref="jedisConnFactory" />
	    <property name="keySerializer">  
	        <bean class="org.springframework.data.redis.serializer.StringRedisSerializer" />  
	    </property>     
	    <property name="valueSerializer">  
	        <bean class="org.springframework.data.redis.serializer.JdkSerializationRedisSerializer" />  
	    </property>  
	    <property name="hashKeySerializer">    
	       <bean class="org.springframework.data.redis.serializer.StringRedisSerializer"/>    
	    </property>  
	    <property name="hashValueSerializer">  
	       <bean class="org.springframework.data.redis.serializer.JdkSerializationRedisSerializer"/>    
	    </property>  
	</bean> 
	
	<bean id="redisClusterConfig" class="org.springframework.data.redis.connection.RedisClusterConfiguration">  
            <property name="maxRedirects" value="3" />  
            <property name="clusterNodes">  
                <set>  
	           <bean class="org.springframework.data.redis.connection.RedisNode">  
	               <constructor-arg name="host" value="192.168.*.*"></constructor-arg>   
	               <constructor-arg name="port" value="6379"></constructor-arg>  
	           </bean>  
                   <bean class="org.springframework.data.redis.connection.RedisNode">  
                       <constructor-arg name="host" value="192.168.*.*"></constructor-arg>   
                       <constructor-arg name="port" value="6380"></constructor-arg>  
                   </bean>  
                   <bean class="org.springframework.data.redis.connection.RedisNode">  
                       <constructor-arg name="host" value="192.168.*.*"></constructor-arg>   
                       <constructor-arg name="port" value="6381"></constructor-arg>  
                   </bean>  
               </set>  
           </property>  
       </bean>  
</beans>
```

## 11 RedisTemplate 操作示例

```java
public static void main(String[] args) {
        ClassPathXmlApplicationContext appCtx = new ClassPathXmlApplicationContext("spring-redis.xml");
        final RedisTemplate<String, Object> redisTemplate = appCtx.getBean("redisTemplate",RedisTemplate.class);
        
  		//添加一个 key 
        ValueOperations<String, Object> value = redisTemplate.opsForValue();
        value.set("lp", "hello word");
        //获取 这个 key 的值
        System.out.println(value.get("lp"));
       
  
  	    //添加 一个 hash集合
        HashOperations<String, Object, Object>  hash = redisTemplate.opsForHash();
  	    Map<String,Object> map = new HashMap<String,Object>();
        map.put("name", "lp");
        map.put("age", "26");
        hash.putAll("lpMap", map);
        //获取 map
        System.out.println(hash.entries("lpMap"));
  
  
        //添加 一个 list 列表
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.rightPush("lpList", "lp");
        list.rightPush("lpList", "26");
        //输出 list
        System.out.println(list.range("lpList", 0, 1));
  
  
        //添加 一个 set 集合
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.add("lpSet", "lp");
        set.add("lpSet", "26");
        set.add("lpSet", "178cm");
        //输出 set 集合
        System.out.println(set.members("lpSet"));
  
  
        //添加有序的 set 集合
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        zset.add("lpZset", "lp", 0);
        zset.add("lpZset", "26", 1);
        zset.add("lpZset", "178cm", 2);
        //输出有序 set 集合
        System.out.println(zset.rangeByScore("lpZset", 0, 2));
    }
```





