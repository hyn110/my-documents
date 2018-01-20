# 22_spring boot 学习笔记

## 1 maven 依赖

```xml
<parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.9.RELEASE</version>
    </parent>
    <dependencies>
        <!--spring boot 基础依赖-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!--thymeleaf 模版 前端-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>

        <!--测试模块，包括JUnit、Hamcrest、Mockito-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
```

## 2 默认静态资源放置位置

​	默认静态资源放置目录 /static , /public , /resurce , /META-INF/resources 下 , /templates 用于存放模版文件

```java
├─src
│  ├─main
│  │  └─resources
│  │      ├─META-INF
│  │      │  └─resources
│  │      ├─public
│  │      ├─resources
          ├─static
          └─templates
```

## 3 引入 swagger2 构建 RESTful API 文档

### 	1  引入swagger2坐标

```xml
<!--Swagger2 依赖-->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.2.2</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.2.2</version>
</dependency>
```

### 	2 添加 swagger 配置类

```java
@Configuration
@EnableSwagger2
public class Swagger2 {

    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(this.apiInfo())
                .select()
          		// 指定扫描的包
                .apis(RequestHandlerSelectors.basePackage("com.fmi110.web"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("使用Swagger2构建RESTful APIs")
                .description("梦之安魂曲~~~")
                .termsOfServiceUrl("http://www.fmi110.com/")
                .contact("蜂蜜110")
                .version("1.0")
                .build();
    }
}
```

### 	3 在controller的方法上添加 swagger2 注解

```java
@RestController
@RequestMapping("/users")
public class UserController {
    static Map<Long, User> users = Collections.synchronizedMap(new HashMap<>());

    // 获取所有
    @ApiOperation(value = "获取用户列表")
    @GetMapping("/")
    public Map<Long, User> getUserList() {
        return users;
    }

    @ApiIgnore  // 忽略该spi
    @GetMapping("xx")
    public String xx() {
        return "xx";
    }

    // 增加
    @ApiOperation(value = "创建用户", notes = "根据 User 对象创建用户")
    @ApiImplicitParam(name = "user", value = "用户详细实体user", required = true, dataType = "User")
    @PostMapping("/")
    public String postUser(@ModelAttribute User user) {
        users.put(user.getId(), user);
        return "success";
    }

    // 根据id 获取
    @ApiOperation(value = "根据id获取用户", notes = "根据url路径中的 id 获取")
    @ApiImplicitParam(name = "id", value = "用户 id", paramType = "path", required = true, dataType = "Long")
    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Long id) {
        return users.get(id);
    }

    // 修改
    @ApiOperation(value = "更新用户详细信息", notes = "根据url中id 更新指定对象")
    @ApiImplicitParams({
                               @ApiImplicitParam(name = "id", value = "用户id", paramType = "path", required = true,
                                                 dataType = "Long"),
                               @ApiImplicitParam(name = "user", value = "用户详细信息实体user", required = true,
                                                 dataType = "User")})
    @PutMapping("/{id}")
    public User putUser(@PathVariable Long id, @ModelAttribute User user) {
        User u = new User();
        BeanUtils.copyProperties(user, u);
        users.put(u.getId(), u);
        return u;
    }

    @ApiOperation("根据id删除用户")
    @ApiImplicitParam(name = "id", value = "用户id", paramType = "path", required = true, dataType = "Long")
    @DeleteMapping("/{id}")
    public User deleteUser(@PathVariable Long id) {
        User remove = users.remove(id);
        return remove;
    }
}
```

> `@ApiOperation` :  说明api的用途 
>
> `@ApiImplicitParam`  : 对方法参数进行
>
> ​	paramType 的合法值 : path  , body  , header , query , form

### 	4 访问api文档

​	http://localhost:8080/swagger-ui.html

## 4 使用 jdbcTemplate 访问数据库

### 	1 添加 maven 坐标

```xml
<!--5 添加jdbc依赖-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.40</version>
</dependency>
```

### 	2 配置数据库连接信息

​	数据库连接信息配置在 `src/main/resources/application.properties` 文件中 ,配置信息会被装配到 `org.springframework.boot.autoconfigure` 包下  (spring-boot-autoconfigure-1.5.9.RELEASE.jar) 对应的类

```properties
###### 数据库连接配置 ######
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/springboot
spring.datasource.username=root
spring.datasource.password=
```

​	sql 建表语句 :

```sql
create table user
(
	id bigint auto_increment primary key,
	name varchar(64) null,
	age int null
)
```

### 	3 使用 jdbcTemplate 操作数据库

​	**springboot 的 jdbcTemplate 时自动配置的 , 不需要显式的声明 bean 对象 , 也就是说可以在代码中直接使用 @autowired 注入 !!!**

```java
package com.fmi110.springboot.service.impl;

import com.fmi110.springboot.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 新增一个用户
     *
     * @param name
     * @param age
     */
    @Override
    public void create(String name, Integer age) {
        jdbcTemplate.update("INSERT INTO user (name,age) VALUES (?,?)", name, age);
    }

    /**
     * 根据name删除一个用户高
     *
     * @param name
     */
    @Override
    public void deleteByName(String name) {
        jdbcTemplate.update("DELETE FROM user WHERE name=?", name);
    }

    /**
     * 获取用户总量
     */
    @Override
    public Integer getAllUsers() {
        return jdbcTemplate.queryForObject("select count(1) from user",Integer.class);
    }

    /**
     * 删除所有用户
     */
    @Override
    public void deleteAllUsers() {
        jdbcTemplate.update("delete from user");
    }
}
```

## 5 使用 spring-data-jpa 操作数据库

### 	1 添加 maven 坐标

```xml
<!--6 添加 spring-data-jpa-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

### 	2 实体类添加 jpa 注解 , 实现表映射

```java
@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String name;
    @Column
    private Integer age;
    ... 省略 set get ...
}
```

### 	3 定义 Reposity 接口

```java
package com.fmi110.springboot.reposity;

import com.fmi110.springboot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserReposity extends JpaRepository<User,Long> {
    /**
     * 通过用户名查找用户
     * @param name
     * @return
     */
    List<User> findUsersByName(String name);

    /**
     * 使用原生sql语句 ,查找所有用户并按年龄排序
     * @return
     */
    @Query(value = "select * from user order by age",nativeQuery = true)
    List<User> findAllUsers();

    @Transactional
    @Modifying
    @Query(value = "delete from user where name=?1",nativeQuery = true)
    void deleteUserByName(String name);
}
```

### 	4 使用 jpa 操作数据库

​	spring data jpa 的 JpaRepository 接口已经定义好了常用的增 , 删 , 改 , 查 , 分页查询 , 排序等操作 , 底层通过代理的方式调用 SimpleJpaRepository 来实现操作 , 所以我们只需要定义好接口,便可直接使用 

```java
package com.fmi110.springboot.test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class JpaTest {
    @Autowired
    private UserReposity userReposity;
    @Test
    public void test(){
        // 保存操作
        userReposity.save(new User("hadoop", 2));
        userReposity.save(new User("storm", 3));

        // 分页并排序查找
        Page<User> page = userReposity.findAll(new PageRequest(1, 4, new Sort(Sort.Direction.ASC, "age")));
        int total = page.getSize();
        List<User> list = page.getContent();
        System.out.println(total+"  ====  "+list);

        List<User> allUsers = userReposity.findAllUsers();
        System.out.println(allUsers);


        // 删除
        userReposity.delete(9l); // 根据id 删除
        User user = new User();
        user.setId(8l);
        userReposity.delete(user);

        // 修改 , 持久态对象修改即可
    }
    
    @Test
    public void test2(){
        userReposity.deleteUserByName("蜂蜜110");
    }
}
```
## 6 Spring Boot多数据源配置与使用

​	[Spring+MyBatis实现读写分离四种实现方案整理](http://blog.csdn.net/wuyongde_0922/article/details/70655185)



## 7 统一异常处理

​	通过注解 @ControllerAdvise 指定 controller 切面 , 并使用 @ErrorHandler 指定发生异常时调用哪个方法 , 如下 :

```java
package com.fmi110.springboot.exception;

import com.fmi110.springboot.dto.ErrorInfo;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理类
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    public static final String DEFAULT_ERROR_VIEW = "error";

    /**
     * 返回错误页面
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("exception", e);
        mv.addObject("url", req.getRequestURL());
        mv.setViewName(DEFAULT_ERROR_VIEW);
        return mv;
    }

    /**
     * 返回json格式的错误信息
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = ArithmeticException.class)  // 指定处理的异常类型
    @ResponseBody
    public ErrorInfo<String> defaultAjaxErrorHandler(HttpServletRequest req, Exception e) {
        ErrorInfo<String> dto = new ErrorInfo<>();
        dto.setUrl(req.getRequestURL()
                      .toString());
        dto.setData(e.getMessage());
        dto.setCode(ErrorInfo.ERROR);
        return dto;
    }
}
```



```java
package com.fmi110.springboot.dto;

/**
 * dto : data transfer object 数据传输对象
 */
public class ErrorInfo<T> {

    public static final Integer OK = 0;
    public static final Integer ERROR = 100;

    private Integer code;
    private String  message;
    private String  url;
    private T       data;
    //... 省略 get  set 方法 ...
}
```

## 8 使用 redis 数据库

​	[redisTempalte访问redis数据结构详解](https://www.jianshu.com/p/7bf5dc61ca06)

​	spring data redis 默认采用的序列化策略有两种 : 一种是String的序列化策略，一种是JDK的序列化策略。StringRedisTemplate默认采用的是String的序列化策略，保存的key和value都是采用此策略序列化保存的。RedisTemplate默认采用的是JDK的序列化策略，保存的key和value都是采用此策略序列化保存的

### 	1 添加依赖

```xml
<!--8 添加 redis 依赖-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### 	2 配置redis

​	在 application.properties 中添加 redis 的配置

```properties
# REDIS (RedisProperties)
# Redis数据库索引（默认为0）
spring.redis.database=0
# Redis服务器地址
spring.redis.host=localhost
# Redis服务器连接端口
spring.redis.port=6379
# Redis服务器连接密码（默认为空）
spring.redis.password=
# 连接池最大连接数（使用负值表示没有限制）
spring.redis.pool.max-active=8
# 连接池最大阻塞等待时间（使用负值表示没有限制）
spring.redis.pool.max-wait=-1
# 连接池中的最大空闲连接
spring.redis.pool.max-idle=8
# 连接池中的最小空闲连接
spring.redis.pool.min-idle=0
# 连接超时时间（毫秒）
spring.redis.timeout=0
```

### 	3 使用 StringRedisTemplate

```java
/**
 * springboot redis 测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RedisTest {

    /**
     * StringRedisTemplate 是 springboot 自动装配的 , 不需要显示声明bean
     */
    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate           stringRedisTemplate;
  
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 1 测试存储字符串
     *
     * @throws InterruptedException
     */
    @Test
    public void testString() throws InterruptedException {
        // 5 秒自动过期
        stringRedisTemplate.opsForValue()
                           .set("key1", "value", 5, TimeUnit.SECONDS);
        String v1 = stringRedisTemplate.opsForValue()
                                       .get("key1");
        System.out.println("v1 = " + v1);  // 输出 "value"
        Thread.sleep(6000);
        System.out.println("v1 = " + stringRedisTemplate.opsForValue()
                                                        .get("key1"));  // 输出 null
    }
}
```

### 	4 指定RedisTemplate序列化器,操作redis数据结构

1. 自定义redisTemplate配置

```java
package com.fmi110.springboot.config;

/**
 * redis 配置类
 */
@Configuration
public class RedisConfig {

    /**
     * 注入 application.properties 中的 redis 的配置
     */
    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(){
        System.out.println("===jedisConnectionFactory==");
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // 将配置文件中的配置
        BeanUtils.copyProperties(redisProperties.getPool(),poolConfig);
        return new JedisConnectionFactory(poolConfig);
    }

    /**
     * 构建 redisTemplate 指定使用 Jackson2JsonRedisSerializer 序列化器,对数据进行序列化
     * @param jedisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory jedisConnectionFactory) {

        System.out.println("=====redisTemplate=====jedisConnectionFactory="+jedisConnectionFactory);
        // 1 构建序列化器
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(mapper);

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 2 设置 redisTemplate 连接的redis服务器
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        // 3 设置序列化器
        redisTemplate.setKeySerializer(jackson2JsonRedisSerializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        return redisTemplate;
    }
}

```

2. 测试操作 List , Set , ZSet 等数据

```java
package com.fmi110.springboot.test;

/**
 * springboot redis 测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 2 测试存储 List
     */
    @Test
    public void testList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("java");
        list.add("c++");
        list.add("php");
        list.add("kotlin");
        list.add("java script");

        Long res = redisTemplate.opsForList()
                                .leftPushAll("list", list.toArray());

        List<Object> range = redisTemplate.opsForList()
                                          .range("list", 0, -1);

        System.out.println(range); // range = [java script, kotlin, php, c++, java]


        redisTemplate.opsForList()
                     .getOperations()
                     .delete("list");  // 删除指定 key 的数据

        range = redisTemplate.opsForList()
                             .range("list", 0, -1);
        System.out.println(range);  // range = []

        // 1 设置指定 key 数据的过期时间
        Boolean expire = redisTemplate.opsForList()
                                      .getOperations()
                                      .expire("list", 2, TimeUnit.SECONDS);
        // 2 判断指定 key 是否存在
        Boolean hasKey = redisTemplate.opsForList()
                                      .getOperations()
                                      .hasKey("list");
    }

    /**
     * 3 测试存储 Object 对象
     */
    @Test
    public void testObject() {
        User user = new User("fmi110", 18);

        redisTemplate.opsForValue()
                     .set("user", user);
        User u = (User) redisTemplate.opsForValue()
                                     .get("user");
        // 输出 : User{id=null, name='fmi110', age=18}
        System.out.println(u);


        User            user1 = new User("蜂蜜", 12);
        User            user2 = new User("itcast", 18);
        ArrayList<User> list  = new ArrayList<>();
        list.add(user);
        list.add(user1);
        list.add(user2);
        redisTemplate.opsForList()
                     .leftPushAll("userList", list.toArray());
        // 输出 : [User{id=null, name='itcast', age=18}, User{id=null, name='蜂蜜', age=12}]
        System.out.println(redisTemplate.opsForList()
                                        .range("userList", 0, 1));
    }

    /**
     * 存储 hash 结构数据
     */
    @Test
    public void testHash() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "fmi110");
        map.put("class", "三年级一班");
        map.put("age", 18);

        redisTemplate.opsForHash()
                     .putAll("user1", map);

        Boolean hasKey = redisTemplate.opsForHash()
                                      .hasKey("user1", "name");
        // 删除 user1 中的 age
        Long delete = redisTemplate.opsForHash()
                                   .delete("user1", "age");
        // 获取 user1 中的 name 值
        Object name = redisTemplate.opsForHash()
                                   .get("user1", "name");

        // age值 +2 (不存在时添加)
        Long age = redisTemplate.opsForHash()
                                      .increment("user1", "age", 2);
        // 获取 user1 对应的散列表的所有key
        Set<Object> keys = redisTemplate.opsForHash()
                                          .keys("user1");
        // 获取 user1 对应的散列表对应的所有值
        List<Object> values = redisTemplate.opsForHash()
                                           .values("user1");
        // 获取 user1 中的所有数据,返回 map
        Map<Object, Object> user1 = redisTemplate.opsForHash()
                                                 .entries("user1");
        System.out.println(user1); // {name=fmi110, class=三年级一班}
        // 获取 user1 对应的游标,用于迭代获取数据 , redis 2.8.0 后可用
        Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash()
                                                                .scan("user1", ScanOptions.NONE);
        while (cursor.hasNext()) {
            Map.Entry<Object, Object> entry = cursor.next();
            System.out.println(entry.getKey()+" ---> "+entry.getValue());
        }
    }

    /**
     * 存储 ZSet 数据,有序集合的成员是唯一的,但分数(score)却可以重复。
     */
    @Test
    public void testZSet(){

        Boolean zset1 = redisTemplate.opsForZSet()
                                     .add("zset", "zset-1", 1.0); // 添加成功返回true

        DefaultTypedTuple<Object> tuple2 = new DefaultTypedTuple<>("zset-2", 2.0);
        DefaultTypedTuple<Object> tuple3 = new DefaultTypedTuple<>("zset-3", 3.0);
        DefaultTypedTuple<Object> tuple4 = new DefaultTypedTuple<>("zset-4", 4.0);

        HashSet<ZSetOperations.TypedTuple<Object>> typedTuples = new HashSet<>();
        typedTuples.add(tuple2);
        typedTuples.add(tuple3);
        typedTuples.add(tuple4);

        Long size = redisTemplate.opsForZSet()
                                 .add("zset", typedTuples); // 添加或更新

        // 获取元素
        Set<Object> zset = redisTemplate.opsForZSet()
                                        .range("zset", 0, -1);

        // 输出 : [zset-1, zset-2, zset-3, zset-4]
        System.out.println(zset);
        // 移除 zset 中的 "zset-3"
        Long remove = redisTemplate.opsForZSet()
                                  .remove("zset", "zset-3");
        // 增加指定值的分数 + 2.0
        redisTemplate.opsForZSet()
                     .incrementScore("zset", "zset-2", 2.0d);
        // 返回指定值的排名 , 0 为排名第一 (分数有小到大排序)
        Long rank = redisTemplate.opsForZSet()
                                  .rank("zset", "zset-2");
        // 获取指定元素降序的排名
        Long reverseRank = redisTemplate.opsForZSet()
                                  .reverseRank("zset", "zset-1");

        // 通过索引区间返回有序集合成指定区间内的成员对象，其中有序集成员按分数值递增(从小到大)顺序排列
        Set<ZSetOperations.TypedTuple<Object>> set = redisTemplate.opsForZSet()
                                                                    .rangeWithScores("zset", 0, -1);
        for (ZSetOperations.TypedTuple<Object> tuple : set) {
            System.out.println(tuple.getValue()+"  --->  "+tuple.getScore());
        }
// 其他方法 :
//        Set<V> rangeByScore(K key, double min, double max);
//        通过分数返回有序集合指定区间内的成员，其中有序集成员按分数值递增(从小到大)顺序排列

//        Set<TypedTuple<V>> rangeByScoreWithScores(K key, double min, double max);
//        通过分数返回有序集合指定区间内的成员对象，其中有序集成员按分数值递增(从小到大)顺序排列

//        Set<V> rangeByScore(K key, double min, double max, long offset, long count);
//        通过分数返回有序集合指定区间内的成员，并在索引范围内，其中有序集成员按分数值递增(从小到大)顺序排列

//        Set<TypedTuple<V>> rangeByScoreWithScores(K key, double min, double max, long offset, long count);
//        通过分数返回有序集合指定区间内的成员对象，并在索引范围内，其中有序集成员按分数值递增(从小到大)顺序排列

//        Set<V> reverseRange(K key, long start, long end);
//        通过索引区间返回有序集合成指定区间内的成员，其中有序集成员按分数值递减(从大到小)顺序排列

//        Long count(K key, double min, double max);
//        通过分数返回有序集合指定区间内的成员个数

//        Long size(K key);
//        获取有序集合的成员数，内部调用的就是zCard方法

//        Long zCard(K key);
//        获取有序集合的成员数

//        Double score(K key, Object o);
//        获取指定成员的score值

//        Long removeRange(K key, long start, long end);
//        移除指定索引位置的成员，其中有序集成员按分数值递增(从小到大)顺序排列

//        Long removeRangeByScore(K key, double min, double max);
//        根据指定的score值得范围来移除成员

//        Long unionAndStore(K key, K otherKey, K destKey);
//        计算给定的一个有序集的并集，并存储在新的 destKey中，key相同的话会把score值相加

//        Long unionAndStore(K key, Collection<K> otherKeys, K destKey);
//        计算给定的多个有序集的并集，并存储在新的 destKey中

//        Long intersectAndStore(K key, K otherKey, K destKey);
//        计算给定的一个或多个有序集的交集并将结果集存储在新的有序集合 key 中

//        Long intersectAndStore(K key, Collection<K> otherKeys, K destKey);
//        计算给定的一个或多个有序集的交集并将结果集存储在新的有序集合 key 中

//        Cursor<TypedTuple<V>> scan(K key, ScanOptions options);
//        遍历zset

    }
}

```