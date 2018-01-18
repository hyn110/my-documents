# 22_spring boot 学习笔记

# 1 maven 依赖

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

# 2 默认静态资源放置位置

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

# 3 引入 swagger2 构建 RESTful API 文档

## 	1  引入swagger2坐标

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

## 	2 添加 swagger 配置类

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

## 	3 在controller的方法上添加 swagger2 注解

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

## 	4 访问api文档

​	http://localhost:8080/swagger-ui.html

# 4 使用 jdbcTemplate 访问数据库

## 	1 添加 maven 坐标

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

## 	2 配置数据库连接信息

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

## 	3 使用 jdbcTemplate 操作数据库

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

# 5 使用 spring-data-jpa 操作数据库

## 	1 添加 maven 坐标

```xml
<!--6 添加 spring-data-jpa-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

## 	2 实体类添加 jpa 注解 , 实现表映射

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

## 	3 定义 Reposity 接口

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

## 	4 使用 jpa 操作数据库

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