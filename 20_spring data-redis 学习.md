# 20_spring data-redis

[spring data-redis 指南](https://docs.spring.io/spring-data/redis/docs/2.0.2.RELEASE/reference/html/)

[spring data-jpa](https://docs.spring.io/spring-data/jpa/docs/2.0.2.RELEASE/reference/html/)

## 1 配置

**1 maven 坐标**

```Xml
<dependencies>
    <dependency>
        <groupId>org.springframework.data</groupId>
        <artifactId>spring-data-redis</artifactId>
        <version>2.0.2.RELEASE</version>
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
| GeoOperations         | Redis geospatial operations like `GEOADD`, `GEORADIUS`,…) |
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

### 5.8.1. Hash mappers

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

### 5.8.2. Jackson2HashMapper

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