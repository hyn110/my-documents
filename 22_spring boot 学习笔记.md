# 22_spring boot 学习笔记

​	笔记中所有的代码都是以 springboot-1.5.9.RELEASE 版本为基础的 , 参考的教程地址 : [springboot基础教程](http://blog.didispace.com/Spring-Boot%E5%9F%BA%E7%A1%80%E6%95%99%E7%A8%8B/)

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
>
> ​		1 使用 @RequestBody 接收参数时,使用 body
>
> @ApiIgnore 直接作用于方法参数上,用于忽略参数(在页面中不显示)

### 	4 访问api文档

​	http://localhost:8080/swagger-ui.html

### 5 生成静态api文档

​	使用 `swagger2markup` 插件可以将上面生成的api文档转换成静态文件保存到本地 , 可以通过 maven 插件来实现 , 引入插件坐标

```xml
<plugin>
    <groupId>io.github.swagger2markup</groupId>
    <artifactId>swagger2markup-maven-plugin</artifactId>
    <version>1.3.1</version>
    <configuration>
        <!--指定项目中 swagger2 文档的 url-->
        <swaggerInput>
            http://localhost:8080/v2/api-docs
        </swaggerInput>
        <!--生成多个文件,放置在指定目录下-->
        <!--<outputDir>src/docs/asciidoc/generated/all</outputDir>-->
        <!--生成单个文件,文件名为 api-->
        <outputFile>src/docs/asciidoc/generated/api</outputFile>
        <config>
            <!--指定生成的文档类型,可选 : ASCIIDOC、MARKDOWN、CONFLUENCE-->
            <swagger2markup.markupLanguage>MARKDOWN</swagger2markup.markupLanguage>
        </config>
    </configuration>
</plugin>

<!--将swagger2markup生成的 asciidoc 文件转成 html 插件-->
<plugin>
    <groupId>org.asciidoctor</groupId>
    <artifactId>asciidoctor-maven-plugin</artifactId>
    <version>1.5.6</version>
    <configuration>
        <!--源文件目录-->
        <sourceDirectory>src/docs/asciidoc/generated</sourceDirectory>
        <!--输出目录-->
        <outputDirectory>src/docs/asciidoc/html</outputDirectory>
        <backend>html</backend>
        <sourceHighlighter>coderay</sourceHighlighter>
        <attributes>
            <toc>left</toc>
        </attributes>
    </configuration>
</plugin>
```

> `<swaggerInput>` : 指定文档来源 , 需要根据实际项目配置
>
> `<outputFile>` : 指定文档生成后输出到哪个文件 , 也可指定输出到目录(outputDir)
>
> `<swagger2markup.markupLanguage>` : 指定生成文档的类型

​	插件添加好后,先运行 swagger2markup 插件 , 在项目里即可看到生成的文档

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
//        1 通过分数返回有序集合指定区间内的成员，其中有序集成员按分数值递增(从小到大)顺序排列

//        Set<TypedTuple<V>> rangeByScoreWithScores(K key, double min, double max);
//        2 通过分数返回有序集合指定区间内的成员对象，其中有序集成员按分数值递增(从小到大)顺序排列

//        Set<V> rangeByScore(K key, double min, double max, long offset, long count);
//        3 通过分数返回有序集合指定区间内的成员，并在索引范围内，其中有序集成员按分数值递增(从小到大)顺序排列

//        Set<TypedTuple<V>> rangeByScoreWithScores(K key, double min, double max, long offset, long count);
//        4 通过分数返回有序集合指定区间内的成员对象，并在索引范围内，其中有序集成员按分数值递增(从小到大)顺序排列

//        Set<V> reverseRange(K key, long start, long end);
//        5 通过索引区间返回有序集合成指定区间内的成员，其中有序集成员按分数值递减(从大到小)顺序排列

//        Long count(K key, double min, double max);
//        6 通过分数返回有序集合指定区间内的成员个数

//        Long size(K key);
//        7 获取有序集合的成员数，内部调用的就是zCard方法

//        Long zCard(K key);
//        8 获取有序集合的成员数

//        Double score(K key, Object o);
//        9 获取指定成员的score值

//        Long removeRange(K key, long start, long end);
//        10 移除指定索引位置的成员，其中有序集成员按分数值递增(从小到大)顺序排列

//        Long removeRangeByScore(K key, double min, double max);
//        11 根据指定的score值得范围来移除成员

//        Long unionAndStore(K key, K otherKey, K destKey);
//        12 计算给定的一个有序集的并集，并存储在新的 destKey中，key相同的话会把score值相加

//        Long unionAndStore(K key, Collection<K> otherKeys, K destKey);
//        13 计算给定的多个有序集的并集，并存储在新的 destKey中

//        Long intersectAndStore(K key, K otherKey, K destKey);
//        14 计算给定的一个或多个有序集的交集并将结果集存储在新的有序集合 key 中

//        Long intersectAndStore(K key, Collection<K> otherKeys, K destKey);
//        15 计算给定的一个或多个有序集的交集并将结果集存储在新的有序集合 key 中

//        Cursor<TypedTuple<V>> scan(K key, ScanOptions options);
//        16 遍历zset

    }
}
```
## 9 使用 MongoDB 数据库

### 	1 添加依赖

```xml
<!--9 添加mongoDB 依赖-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

### 	2 在application.properties中配置链接信息

```
占坑
```

### 	3 定义接口,继承 MongoRepository



## 10 整合mybatis

### 	1 添加依赖

```xml
<!--10 整合mybatis-->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>1.3.0</version>
</dependency>
<!--还需要数据库驱动等依赖...-->
```

### 	2 配置数据库连接信息

​	在 application.properties 中配置

```properties
###### 数据库连接配置 ######
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/springboot?useUnicode=true&characterEncoding=utf8 
spring.datasource.username=root
spring.datasource.password=
```

### 	3 创建 user 表和User对象

​	建表语句 :

```sql
create table user
(
	id bigint auto_increment primary key,
	name varchar(64) null,
	age int null
)
```

```java
public class User {

    private Long id;
    private String name;
    private Integer age;

    // 省略getter和setter

}
```

### 	4 创建 Mapper 接口

```java
package com.fmi110.springboot.mapper;

import com.fmi110.springboot.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * User对象
 */
@Mapper
public interface UserMapper {

    @Select("SELECT * from user where name=#{name}")
    User findByName(@Param("name") String name);

    @Insert("INSERT INTO user (name,age) VALUES (#{name},#{age})")
    int insert(User user);
}
```

### 	5 测试

```java
package com.fmi110.springboot.test;

import com.fmi110.springboot.domain.User;
import com.fmi110.springboot.mapper.UserMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MybatisTest {
    @Autowired
    UserMapper userMapper;

    @Test
    public void testMybatis(){
        User user = new User("spring", 5);
        userMapper.insert(user);
    }

    @Test
    public void testselect(){
        User user = userMapper.findByName("黑马程序员");
        System.out.println("====="+user);
    }
}

```

### 	6 mybatis 设置

​	在项目中引入 mybatis-spring-boot-starter 依赖后 , 启动项目时 , springboot 会自动装载 `org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration` 类 , 实现mybatis的初始化 , mybatis的配置通过配置类`org.mybatis.spring.boot.autoconfigure.MybatisProperties` 进行配置 , 在 application.properties 文件中以 mybatis 开头的配置项会被注入到 MybatisProperties.java 中对应的属性 , 所以mybatis的配置可以在 application.properties中如下配置:

```properties
###### 5 mybatis 配置 #####
mybatis.config-location=
mybatis.mapper-locations=
```

## 11 配置缓存支持 

### 0 缓存相关知识

**1 @Cacheable**

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

**2 @CachePut**

​	**常用于方法注解,相当于 update 操作 , 并将返回值存入缓存 . 与 @Cacheable 不同 , 该注解每次都会触发真实方法(被注解的方法)的调用**

| 参数        | 说明                                       | 示例                                       |
| --------- | ---------------------------------------- | ---------------------------------------- |
| value     | 缓存的名称，在 spring 配置文件中定义，必须指定至少一个          | 例如：@Cacheable(value=”mycache”) 或者 @Cacheable(value={”cache1”,”cache2”} |
| key       | 缓存的 key，可以为空，如果指定要按照 SpEL 表达式编写，如果不指定，则缺省按照方法的所有参数进行组合 | 例如：@Cacheable(value=”testcache”,key=”#userName”) |
| condition | 缓存的条件，可以为空，使用 SpEL 编写，返回 true 或者 false，只有为 true 才进行缓存 | 例如：@Cacheable(value=”testcache”,condition=”#userName.length()>2”) |

**3 @CacheEvict**

​	**常用于方法注解 , 根据一定的条件对缓存进行清空**

​	**注意: @CacheEvict 注释有一个属性 beforeInvocation，缺省为 false，即缺省情况下，都是在实际的方法执行完成后，才对缓存进行清空操作。期间如果执行方法出现异常，则会导致缓存清空不被执行**

| 参数               | 说明                                       | 示例                                       |
| ---------------- | ---------------------------------------- | ---------------------------------------- |
| value            | 缓存的名称，在 spring 配置文件中定义，必须指定至少一个          | 例如：@CachEvict(value=”mycache”) 或者 @CachEvict(value={”cache1”,”cache2”} |
| key              | 缓存的 key，可以为空，如果指定要按照 SpEL 表达式编写，如果不指定，则缺省按照方法的所有参数进行组合 | 例如：@CachEvict(value=”testcache”,key=”#userName”) |
| condition        | 缓存的条件，可以为空，使用 SpEL 编写，返回 true 或者 false，只有为 true 才清空缓存 | 例如：@CachEvict(value=”testcache”,condition=”#userName.length()>2”) |
| allEntries       | 是否清空所有缓存内容，缺省为 false，如果指定为 true，则方法调用后将立即清空所有缓存 | 例如：@CachEvict(value=”testcache”,allEntries=true) |
| beforeInvocation | 是否在方法执行前就清空，缺省为 false，如果指定为 true，则在方法还没有执行的时候就清空缓存，缺省情况下，如果方法执行抛出异常，则不会清空缓存 | 例如：@CachEvict(value=”testcache”，beforeInvocation=true) |

**4 spring cache自定义 key 的生成策略**

​	[Spring-Cache key设置注意事项](http://blog.csdn.net/exceptional_derek/article/details/11713255)

1 spring cache 默认 key 生成策略

​	Since caches are essentially key-value stores, each invocation of a cached method needs to be translated into a suitable key for cache access. Out of the box, the caching abstraction uses a simple `KeyGenerator` based on the following algorithm:

- If no params are given, return `SimpleKey.EMPTY`.

- If only one param is given, return that instance.

- If more the one param is given, return a `SimpleKey` containing all parameters.

  ​This approach works well for most use-cases; As long as parameters have *natural keys* and implement valid `hashCode()` and `equals()` methods. If that is not the case then the strategy needs to be changed.

  ​也就是说 , 当方法参数具备自然主键,并且重写了 `hashCode()` 和 `equal()` 方法时 , spring cache 默认的可以生成策略可以满足要求 , 否则就需要自己实现key的生成策略

2 使用 spring EL 表达式生成 key

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

### 	1 添加依赖

​	[参考博客](http://blog.didispace.com/springbootcache1/)

```xml
<!--11 配置缓存支持-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

### 	2 配置缓存

​	在Spring Boot中通过 `@EnableCaching` 注解自动化配置合适的缓存管理器（CacheManager），Spring Boot根据下面的顺序去侦测缓存提供者：

- Generic

- JCache (JSR-107)

- EhCache 2.x

- Hazelcast

- Infinispan

- Redis

- Guava

- Simple

  **除了按顺序侦测外，我们也可以通过配置属性 `spring.cache.type` 来强制指定。我们可以通过debug调试查看cacheManager对象的实例来判断当前使用了什么缓存**

#### 1 使用 Ehcahce 缓存

​	为了防止springboot 自动加载其他的缓存 , 这里我们在 application.properties 中显示的指定缓存使用 ehcache , 如下 :

```properties
###### 6 指定使用的缓存类型 #####
spring.cache.type=ehcache
#spring.cache.ehcache.config=ehcache.xml
```

1. 引入 ehcache 依赖

```xml
<dependency>
    <groupId>net.sf.ehcache</groupId>
    <artifactId>ehcache</artifactId>
</dependency>
```

2. 添加ehcache配置文件	

   在`src/main/resources`目录下创建：`ehcache.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<ehcache updateCheck="false" dynamicConfig="false">
    <diskStore path="java.io.tmpdir"/>
    
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

​	对于EhCache的配置文件也可以通过 `application.properties` 文件中使用 `spring.cache.ehcache.config` 属性来指定 , 如下 :

```properties
spring.cache.ehcache.config=classpath:config/another-config.xml
```

       	3. 在程序入口类使能缓存 @EnableCaching

```java
@EnableCaching
@SpringBootApplication
public class HelloApplication {
    public static void main(String[] arg){
        SpringApplication.run(HelloApplication.class, arg);
    }
}
```

4. 使用缓存

   在需要使用缓存的方法上添加对应的注解

```java
    @Cacheable(value = "oneMinute")  // 指定使用的缓存对象为 onMinute (在 ehcache.xml 中配置的)
    @Override
    public Integer getAllUsersCount() {
        System.out.println("===========getAllUsersCount==========");
        return jdbcTemplate.queryForObject("select count(1) from user",Integer.class);
    }
```

#### 	2 使用 redis 缓存

​	只需要在 application.properties 中指定缓存的类型为 redis , 并添加redis依赖 , 以及配置redis 的连接信息 , springboot 就会自动装配对应的bean对象 , 剩下的就是使用注解即可 , redis 的相关配置参照前面的内容配置即可...

## 12 日志管理

​	参考原资料地址 : http://blog.didispace.com/springbootlog/

​	Spring Boot在所有内部日志中使用[Commons Logging](http://commons.apache.org/proper/commons-logging/)，但是默认配置也提供了对常用日志的支持，如：[Java Util Logging](http://docs.oracle.com/javase/7/docs/api/java/util/logging/package-summary.html)，[Log4J](http://logging.apache.org/log4j/), [Log4J2](http://logging.apache.org/log4j/)和[Logback](http://logback.qos.ch/)。每种Logger都可以通过配置使用控制台或者文件输出日志内容,Spring Boot中默认配置了`ERROR`、`WARN`和`INFO`级别的日志输出到控制台,但不会记录到文件中

### 	1 设置日志输出到文件

在`application.properties`中配置`logging.file`或`logging.path`属性。

- logging.file，设置文件，可以是绝对路径，也可以是相对路径。如：`logging.file=my.log`
- logging.path，设置目录，会在该目录下创建spring.log文件，并写入日志内容，如：`logging.path=/var/log`

**日志文件会在10Mb大小的时候被截断，产生新的日志文件，默认级别为：ERROR、WARN、INFO**

### 	2 设置日志输出级别

​	在`application.properties`中进行配置完成日志记录的级别控制。

​	配置格式：`logging.level.*=LEVEL`

```properties
logging.level：日志级别控制前缀，*为包名或Logger名

LEVEL：选项TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF

举例：

logging.level.com.didispace=DEBUG：com.didispace包下所有class以DEBUG级别输出

logging.level.root=WARN：root日志以WARN级别输出

```

### 	3 指定日志框架

根据不同的日志系统，你可以按如下规则组织配置文件名，就能被正确加载：

```properties
Logback：logback-spring.xml, logback-spring.groovy, logback.xml, logback.groovy

Log4j：log4j-spring.properties, log4j-spring.xml, log4j.properties, log4j.xml

Log4j2：log4j2-spring.xml, log4j2.xml

JDK (Java Util Logging)：logging.properties
```

​	**Spring Boot官方推荐优先使用带有-spring的文件名作为你的日志配置（如使用logback-spring.xml，而不是logback.xml）**

### 	4 设置日志输出格式

​	在`application.properties`配置如下参数控制输出格式 :

- logging.pattern.console：定义输出到控制台的样式（不支持JDK Logger）

  - logging.pattern.file：定义输出到文件的样式（不支持JDK Logger）

    ​

## 13 打war包部署到tomcat

​	springboot 默认是以 main() 方法的方式(jar包) 运行的 , 如果向部署到web容器运行 , 可如下处理 :

1. 新建类继承 SpringBootServletInitializer , 并复写configure() 方法	

```java
public class SpringbootServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(SpringbootApplication.class);
    }

}
```

​	注意 : `SpringbootApplication.class`  是 springboot 程序的入口类 , 即 main() 函数所在的类

2. 在pom.xml 中指定打包类型为 war

```xml
<packaging>war</packaging>
```

3. 打包并部署到web容器


## 14 使用dubbo

​	[dubbo-spring-boot-project](https://github.com/dubbo/dubbo-spring-boot-project)

​	[Springboot 整合 Dubbo/ZooKeeper 详解 SOA 案例](https://www.bysocket.com/?p=1681)

### 	1 依赖

```xml
<properties>
        <zkclient.version>0.2</zkclient.version>
        <zookeeper.version>3.4.9</zookeeper.version>
</properties>

<!--13 引入dubbo-->
<dependency>
    <groupId>com.alibaba.boot</groupId>
    <artifactId>dubbo-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
<!-- ZK -->
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>${zookeeper.version}</version>
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
    <version>${zkclient.version}</version>
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
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-actuator</artifactId>
</dependency>
```

​	当坐标不能使用时,添加下列仓库

```xml
<!--dubbo 不识别时,指定仓库-->
<repositories>
    <repository>
        <id>sonatype-nexus-snapshots</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
        <releases>
            <enabled>false</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

### 	2 配置服务暴露

​	在 application.properties 中配置 dubbo

```properties
###### 13 dubbo 服务端配置 ######
# Base packages to scan Dubbo Components (e.g @Service , @Reference)
dubbo.scan.basePackages  =com.fmi110.springboot.dubbo.service

# Dubbo Config properties
## ApplicationConfig Bean 
dubbo.application.id = dubbo-provider-demo
dubbo.application.name = dubbo-provider-demo

## ProtocolConfig Bean 远程服务调用的配置
dubbo.protocol.id = dubbo
dubbo.protocol.port = 20880
## 注册中心的配置
dubbo.registry.id = my-registry
dubbo.registry.address = zookeeper://192.168.80.132:2181?backup=192.168.80.133:2181,192.168.80.134:2181
```

> 这里注册中心使用了 zookeeper 集群,需要自行搭建

### 	3 定义接口和实现类,并暴露服务

​	接口 :

```java
public interface IDubboService {
    String sayHello(String msg);
}
```

​	实现 :

```java
@Service(
    //    version = "1.0.0", // 添加版本信息的话,则消费端也必须指定对应的版本信息,否则发现不了服务
        application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}"
)
public class DubboService implements IDubboService {
    @Override
    public String sayHello(String msg) {
        return "hello " + msg;
    }
}
```

> 这里通过注解的方式暴露服务

### 	4 消费端配置

```properties
dubbo.application.id = dubbo-consumer-demo
dubbo.application.name = dubbo-consumer-demo

## ProtocolConfig Bean 远程服务调用的配置
dubbo.protocol.id = dubbo
dubbo.protocol.port = 20880
## 注册中心的配置
dubbo.registry.id = my-registry
dubbo.registry.address = zookeeper://192.168.80.132:2181?backup=192.168.80.133:2181,192.168.80.134:2181
```

### 	5 注入服务接口

```java
@Reference(
        // version = "1.0.0",
        application = "${dubbo.application.id}",
        url = "dubbo://localhost:12345")
private DemoService demoService;
```