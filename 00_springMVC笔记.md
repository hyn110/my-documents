# Spring MVC

​	**web.xml**

```html
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns="http://java.sun.com/xml/ns/javaee"
	xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
	id="WebApp_ID" version="2.5">
	<display-name>springmvc</display-name>
	<welcome-file-list>
		<welcome-file>index.html</welcome-file>
		<welcome-file>index.htm</welcome-file>
		<welcome-file>index.jsp</welcome-file>
	</welcome-file-list>

	<!-- 处理post中文乱码 -->
	<filter>
		<filter-name>characterEncodingFilter</filter-name>
		<filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
		<param-name>encoding</param-name>
		<param-value>utf-8</param-value>
	</filter>
	<filter-mapping>
		<filter-name>characterEncodingFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>

	<servlet>
		<servlet-name>dispatcher</servlet-name>
		<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
		<load-on-startup>1</load-on-startup>
		<!-- 指定映射配置文件的路径 -->
		<init-param>
			<param-name>contextConfigLocation</param-name>
			<param-value>classpath:spring-servlet-config.xml</param-value>
		</init-param>
	</servlet>
	<servlet-mapping>
		<servlet-name>dispatcher</servlet-name>
		<url-pattern>/</url-pattern><!-- "/" 写法不会覆盖默认的servlet -->
	</servlet-mapping>
</web-app>
```

## 1 控制器 Controller

​	在 `POJO` 对象上放置 `@controller` 或 `@RequestMapping` , 即可把一个 `POJO` 类变为处理器

​	需要在控制器 `spring-servlet-config.xml` 配置文件中声明注解支持

​	**spring-servlet-config.xml**

```html
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

	<!-- <bean class="org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping"></bean> 
		<bean class="org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter"></bean> -->

	<bean
		class="org.springframework.web.servlet.view.InternalResourceViewResolver">
		<property name="viewClass"
			value="org.springframework.web.servlet.view.JstlView"></property>
		<property name="prefix" value="/WEB-INF/xxx/"></property>
		<property name="suffix" value=".jsp"></property>
	</bean>
	<!-- 开启注解式处理器支持 -->
	<bean
		class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping"></bean>
	<!-- 开启注解式处理器支持 -->
	<bean
		class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter"></bean>
	<!-- 声明控制器类 -->
	<bean class="com.itheima.springmvc.controller.HelloController2"></bean>
</beans>

```

###  1 路径映射

- 普通URL	:	`@RequestMapping(value={"/pah1","/test/path"})`

- URI 模版模式映射 :      `@RequestMapping(value="users/{userId}")`   或  `@RequestMapping(value="users/{userId}/create")` ...

  > `{userId}` 是占位符 , 可以通过 @PathVariable 提取该值赋给指定变量, 比如这里的 `userId`  , 当 url 为 `/users/123` ,则 `userId` 的将被赋值 `123`

- Ant 风格的 URL 路径映射

  - `@RequestMapping(value="/users/**")` : `**`  代表 任意子路径 , 比如 `/users/create/test`
  - `@RequestMapping(value="/users?")`    : `?`  代表一个字符
  - `@RequestMapping(value="/users*")`    : `*`   代表 0 个或多个字符
  - `@RequestMapping(value="/users/**/{userId}")` : Ant 风格和 URI 模版变量风格混用

- 正则表达式风格的URL路径映射

     从`Spring3.0`开始支持正则表达式风格的URL路径映射，格式为`{变量名:正则表达式}`，这样通过`@PathVariable`提取模式中的`{×××：正则表达式匹配的值}` 中的 `×××` 变量了  `@RequestMapping(value="/products/{categoryCode:\\d+}-{pageNumber:\\d+}")`：可以匹配`“/products/123-1”`，但不能匹配`“/products/abc-1”`，这样可以设计更加严格的规则 

  > URI模板模式映射是{userId}，不能指定模板变量的数据类型，如是数字还是字符串；
  > 正则表达式风格的URL路径映射，可以指定模板变量的数据类型，可以将规则写的相当复杂 



### 2 请求方法映射限定

- `@RequestMapping(value="/methodOr", method = {RequestMethod.POST, RequestMethod.GET})`  : 即请求方法
  可以是 GET 或 POST 
- `@RequestMapping(value="/xxx", method=RequestMethod.GET)` : 即只能进行`GET`请求


### 3 请求参数数据映射限定

#### 1 url 参数限定

- `@RequestMapping(params="create", method=RequestMethod.GET)  `

  > 表示请求中有 “`create`” 的参数名且请求方法为“`GET`”即可匹配，如可匹配的请求URL“`http://×××/parameter1?create`” 

- `@RequestMapping(params="!create", method=RequestMethod.GET)` 

  > 请求中没有“`create`”参数名且请求方法为“`GET`”即可匹配，如可匹配的请求URL“`http://×××/parameter1?abc`” 

- `@RequestMapping(params="submitFlag=create", method=RequestMethod.GET) ` 

  > 表示请求中有“submitFlag=create”请求参数且请求方法为“GET”即可匹配，如请求URL为
  > `http://×××/parameter2?submitFlag=create` ;

- `@RequestMapping(params={"test1", "test2=create"}) ` 

  > 表示请求中的有“`test1`”参数名 且 有
  > “`test2=create`”参数即可匹配，如可匹配的请求URL `http://×××/parameter3?test1&test2=create` 

#### 2 请求头参数设定

- `@RequestMapping(value="/header/test1", headers = "Accept") `

  > 请求的URL必须为“/header/test1”
  >
  > 且 请求头中必须有Accept参数才能匹配 

- `@RequestMapping(value="/header/test1", headers = "abc") `

  > 请求的URL必须为“/header/test1”
  > 且 请求头中必须有abc参数才能匹配 

- `@RequestMapping(value="/header/test3", headers = "Content-Type=application/json") `

  > 请求的URL必须为“`/header/test3`” 
  >
  > 且 请求头中必须有“`Content-Type=application/json`”参数即可匹配。
  >
  > （将Modify `Header的Content-Type参数值改为“applica`tion/json`”即可） 

  > 当你请求的URL为“/header/test3” 但 如果请求头中没有或不是“`Content-Type=application/json`”参数（如“`text/`
  > `html`”其他参数），将返回“HTTP Status 415”状态码【表示不支持的媒体类型(`Media Type`)，也就是`MIME`类型】，即
  > 我们的功能处理方法只能处理`application/json`的媒体类型 

- `@RequestMapping(value="/header/test4", headers = "Accept=application/json") `

  > 表示请求的URL必须为
  > “`/header/test4`” 且 请求头中必须有“`Accept =application/json`”参数即可匹配。（将`Modify Header`的
  > Accept参数值改为“`application/json`”即可） 

  > 当你请求的URL为“`/header/test4`” 但 如果请求头中没有“`Accept=application/json`”参数（如“`text/html`”其他参
  > 数），将返回“`HTTP Status 406`”状态码【不可接受，服务器无法根据Accept头的媒体类型为客户端生成响应】，即客户
  > 只接受“`application/json`”媒体类型的数据，即我们的功能处理方法的响应只能返回“`application/json`”媒体类型的数
  > 据。 

  > `Accept=text/* `：表示主类型为text，子类型任意，如“`text/plain`”、“`text/html`”等都可以匹配 
  >
  > `Accept=*/* `：表示主类型任意，子类型任意，如“`text/plain`”、“`application/xml`”等都可以匹配 

### 4 数据绑定

- @RequestParam 绑定单个请求参数值 
- @PathVariable 绑定URI模板变量值 
- @CookieValue 绑定Cookie数据值 
- @RequestHeader 绑定请求头数据 
- @ModelValue 绑定参数到命令对象 
- @SessionAttributes 绑定命令对象到session 
- @RequestBody 绑定请求的内容区数据并能进行自动类型转换等 
- @RequestPart绑定“multipart/data”数据，除了能绑定@RequestParam能做到的请求参数外，还能绑定上传的
  文件等 

#### 1 @RequestParam 绑定单个请求参数值

```java
public String requestparam1(@RequestParam String username){}
```

> 请求中包含`username`参数（如`/requestparam1?username=zhang`），则自动传入 

```java
public String requestparam2(@RequestParam("username") String name){}
```

> 通过`@RequestParam("username")`明确告诉`Spring Web MVC`使用`username`进行入参 

​	**@RequestParam 注解拥有的参数如下:**

1. **value :  参数名字，即入参的请求参数名字，如username表示请求的参数区中的名字为username的参数的值将传入** 
2. **required :  是否必须，默认是true，表示请求中一定要有相应的参数，否则将报404错误码** 
3. **defaultValue : 默认值，表示如果请求中没有同名参数时的默认值，默认值可以是SpEL表达式，如**
   **`“#{systemProperties['java.vm.version']}”`** 

```java
public String requestparam4(@RequestParam(value="username",required=false) String username){}
```

> 表示请求中可以没有名字为username的参数，如果没有默认为null，此处需要注意如下几点： 
>
> 1. **原子类型：必须有值，否则抛出异常，如果允许空值请使用包装类代替** 
> 2. **Boolean包装类型类型：默认Boolean.FALSE，其他引用类型默认为null** 

```java
public String requestparam5(
  			@RequestParam(value="username", required=true, defaultValue="zhang") String username){}
```

> 表示如果请求中没有名字为username的参数，默认值为“zhang” 

​	**请求中有多个同名的应该如何接收呢？如给用户授权时，可能授予多个权限，首先看下如下代码** 

```java
public String requestparam7(@RequestParam(value="role") String roleList){}
```

> 如果请求参数类似于url?role=admin&rule=user，则实际roleList参数入参的数据为“admin,user”，即多个数据之间
> 使用“，”分割；

​	**我们应该使用如下方式来接收多个请求参数 :**

```java
public String requestparam7(@RequestParam(value="role") String[] roleList){} 
或
public String requestparam8(@RequestParam(value="list") List<String> list){}
```

#### 2 @PathVariable 绑定URI模板变量值

```java
@RequestMapping(value="/users/{userId}/topics/{topicId")
public String test(@PathVariable(value="userId")  int userId ,
                   @PathVariable(value="topicId") int topicId)
```

> 如请求的URL为“`控制器URL/users/123/topics/456`”，则自动将URL中模板变量`{userId}`和`{topicId}`绑定到通过
> `@PathVariable`注解的同名参数上，即入参后`userId=123`、`topicId=456` 

#### 3 @CookieValue 绑定Cookie数据

```java
public String test(@CookieValue(value="JESSIONID",defaultValue="") String sessionId)	
```

> 如上配置将自动将JSESSIONID值入参到sessionId参数上，defaultValue表示Cookie中没有JSESSIONID时默认为空 

```java
public String test(@CookieValue(value="JESSIONID",defaultValue="" Cookie sessinId))
```

> 传入参数类型也可以是javax.servlet.http.Cookie类型 

#### 4 @RequestHeader 绑定请求头数据

```java
@RequestMapping(value="/header")
public String test(@RequestHeader("User-Agent") String userAgent ,
                   @RequestHeader(value="Accept") String[] accepts)
```

> 如上配置将自动将请求头“User-Agent”值入参到userAgent参数上，并将“Accept”请求头值入参到accepts参数
> 上 

​	@RequestHeader也拥有和@RequestParam相同的三个参数，含义一样 

#### 5 @ModelAttribute 绑定请求参数到命令对象

​	@ModelAttribute 一般拥有如下三个作用:

1. **绑定请求参数到命令对象**：放在功能处理方法的入参上时，用于将多个请求参数绑定到一个命令对象，从而简化绑定
   流程，而且自动暴露为模型数据用于视图页面展示时使用 
2. **暴露表单引用对象为模型数据**：放在处理器的一般方法（非功能处理方法）上时，是为表单准备要展示的表单引用对
   象，如注册时需要选择的所在城市等，而且在执行功能处理方法（@RequestMapping注解的方法）之前，自动添加到
   模型对象中，用于视图页面展示时使用 
3. **暴露@RequestMapping方法返回值为模型数据**：放在功能处理方法的返回值上时，是暴露功能处理方法的返回值为
   模型数据，用于视图页面展示时使用 

##### 1 绑定请求参数到命令对象 

```java
public String test1(@RequestAttribute("user") UserModel user)
```

> 当请求地址为 ...?username=123&password=123 时 , 请求参数 username ,password 的值会被绑定到 user 对象中 ; 同时我们可以在视图页面中使用 ${user.username} 来获取绑定的命令对象的属性

>  绑定请求参数到命令对象**支持对象图导航式的绑定**，如请求参数包含`“?username=zhang&password=123&workInfo.city=bj”`自动绑定到user中的workInfo属性的city属性中.

```java
@RequestMapping(value="/model2/{username}")
public String test2(@ModelAttribute("model") DataBinderTestModel model) {}
```

> URI模板变量也能自动绑定到命令对象中，当你请求的URL中包含
> “bool=yes&schooInfo.specialty=computer&hobbyList[0]=program&hobbyList[1]=music&map[key1]=value1
> 会自动绑定到命令对象上 

​	**当URI模板变量和请求参数同名时，URI模板变量具有高优先权** 

##### 2 暴露表单引用对象为模型数据

```java
@ModelAttribute("cityList")
public List<String> cityList(){
   	return Arrays.asList("广西","广东");
}
```

> 如上代码会在执行功能处理方法之前执行，并将其自动添加到模型对象中，在功能处理方法中调用Model 入参的
> containsAttribute("cityList")将会返回true 

```java
@ModelAttribute("user") //①
public UserModel getUser(@RequestParam(value="username", defaultValue="") String username) {
      //TODO 去数据库根据用户名查找用户对象
      UserModel user = new UserModel();
      user.setRealname("zhang");
      return user;
}
```

> 如你要修改用户资料时一般需要根据用户的编号/用户名查找用户来进行编辑，此时可以通过如上代码查找要编辑的用户。
> 也可以进行一些默认值的处理 (执行数据准备操作)

```java
@RequestMapping(value="/model1") //②
public String test1(@ModelAttribute("user") UserModel user, Model model)
```

​	**此处我们看到①和②有同名的命令对象，那Spring Web MVC内部如何处理的呢 :** 

1. 首先执行@ModelAttribute注解的方法，准备视图展示时所需要的模型数据；@ModelAttribute注解方法形式参数规
   则和@RequestMapping规则一样，如可以有@RequestParam等； 
2. 执行@RequestMapping注解方法，进行模型绑定时首先查找模型数据中是否含有同名对象，如果有直接使用，如果没
   有通过反射创建一个，因此②处的user将使用①处返回的命令对象。即②处的user等于①处的user 

##### 3 暴露@RequestMapping方法返回值为模型数据 

```java
public @ModelAttribute("user2") UserModel test3(@ModelAttribute("user2") UserModel user)
```

> @ModelAttribute注解的返回值会覆盖@RequestMapping注解方法中的@ModelAttribute注解的同名命令对象 

##### 4 匿名绑定命令参数 

```java
public String test4(@ModelAttribute UserModel user, Model model)
或
public String test5(UserModel user, Model model)
```

> 此时我们没有为命令对象提供暴露到模型数据中的名字，此时的名字是什么呢？Spring Web MVC自动将简单类名（首
> 字母小写）作为名字暴露，如“cn.javass.chapter6.model.UserModel”暴露的名字为“userModel” 

```java
public @ModelAttribute List<String> test6()
或
public @ModelAttribute List<UserModel> test7()
```

> 对于集合类型（Collection接口的实现者们，包括数组），生成的模型对象属性名为“简单类名（首字母小
> 写）”+“List”，如List<String>生成的模型对象属性名为“stringList”，List<UserModel>生成的模型对象属性名
> 为“userModelList” 

> 其他情况一律都是使用简单类名（首字母小写）作为模型对象属性名，如Map<String, UserModel>类型的模型对象属
> 性名为“map 

##### 

##### 5 @SessionAttributes绑定命令对象到session 

```java
//1、在控制器类头上添加@SessionAttributes注解
@SessionAttributes(value = {"user"}) //①
public class SessionAttributeController
  
//2、@ModelAttribute注解的方法进行表单引用对象的创建
@ModelAttribute("user") //②
public UserModel initUser()
  
//3、@RequestMapping注解方法的@ModelAttribute注解的参数进行命令对象的绑定
@RequestMapping("/session1") //③
public String session1(@ModelAttribute("user") UserModel user)
  
//4、通过SessionStatus的setComplete()方法清除@SessionAttributes指定的会话数据
@RequestMapping("/session2") //③
public String session(@ModelAttribute("user") UserModel user, SessionStatus status) {
 	if(true) { //④
  		status.setComplete(); // 清楚session数据
	}
	return "success";
}
```

> @SessionAttributes(value = {"user"}) 标识将模型数据中的名字为“user” 的对象存储到会话中（默认HttpSession），
> 此处value指定将模型数据中的哪些数据（名字进行匹配）存储到会话中，此外还有一个types属性表示模型数据中的哪
> 些类型的对象存储到会话范围内，如果同时指定value和types属性则那些名字和类型都匹配的对象才能存储到会话范围
> 内 

​	**包含@SessionAttributes的执行流程如下所示：** 