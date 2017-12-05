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
		<init-param>
			<param-name>encoding</param-name>
			<param-value>utf-8</param-value>
		</init-param>

	</filter>
	<filter-mapping>
		<filter-name>characterEncodingFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>

	<servlet>
		<servlet-name>dispatcher</servlet-name>
		<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
		<!-- 指定映射配置文件的路径 -->
		<init-param>
			<param-name>contextConfigLocation</param-name>
			<param-value>classpath:spring-servlet-config.xml</param-value>
		</init-param>
		<load-on-startup>1</load-on-startup>
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

1. 首先根据@SessionAttributes注解信息查找会话(session)内的对象放入到模型数据中

2. 执行@ModelAttribute注解的方法:如果模型数据中包含同名的数据，则不执行@ModelAttribute注解方法进行准备表单引用数据，而是使用1步骤中的会话数据;如果模型数据中不包含同名的数据，执行@ModelAttribute注解的方法并将返回值添加到模型数据中

3. 执行@RequestMapping方法，绑定@ModelAttribute注解的参数:查找模型数据中是否有@ModelAttribute注解的同名对象，如果有直接使用，否则通过反射创建一个;并将请求参数绑定到该命令对象

   > **此处需要注意:如果使用@SessionAttributes注解控制器类之后，3步骤一定是从模型对象中取得同名的命令对象，如果模型数据中不存在将抛出HttpSessionRequiredException Expected session attribute ‘user’(Spring3.1)**
   >
   > **或HttpSessionRequiredException Session attribute ‘user’ required - not found in session(Spring3.0)异常。**

4. 如果会话可以销毁了，如多步骤提交表单的最后一步，此时可以调用SessionStatus对象的setComplete()标识当前会话的@SessionAttributes指定的数据可以清理了，此时当@RequestMapping功能处理方法执行完毕会进行清理会话数据

#### 6 @Value 绑定SpEL表达式

​	@Value 用于将SpEL表达式结果映射到功能处理方法的参数上

```Java
public String test(@Value("#{systemProperties['java.vm.version']}") String jvmVersion)
```

## 2 数据类型转换系统

​	Spring3引入了更加通用的类型转换系统，其定义了SPI接口(Converter等)和相应的运行时执行类型转换的API(ConversionService等)，在Spring中它和PropertyEditor功能类似，可以替代PropertyEditor来转换外部Bean属性的值到Bean属性需要的类型。

​	该类型转换系统是Spring通用的，其定义在org.springframework.core.convert包中，不仅仅在Spring Web MVC场景下。目标是完全替换PropertyEditor，提供无状态、强类型且可以在任意类型之间转换的类型转换系统，可以用于任何需要的地方，如SpEL、数据绑定。

​	Converter SPI完成通用的类型转换逻辑，如java.util.Date<---->java.lang.Long或java.lang.String---->PhoneNumberModel等。

### 1 架构

#### 1 类型转换器

​	提供类型转换的实现支持,由如下3个接口:

- Converter:类型转换器，用于转换S类型到T类型，此接口的实现必须是线程安全的且可以被共享

  ```Java
  package org.springframework.core.convert.converter;
  public interface Converter<S, T> { //1 S是源类型 T是目标类型
    	T convert(S source); //2 转换S类型的source到T目标类型的转换方法
  }
  ```

- GenericConverter和ConditionalGenericConverter : GenericConverter接口实现能在多种类型之间进行转换，ConditionalGenericConverter是有条件的在多种类型之间进行转换

  ```Java
  package org.springframework.core.convert.converter;
  public interface GenericConverter {
      Set<ConvertiblePair> getConvertibleTypes();
      Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType);
  }
  ```

  > getConvertibleTypes() : 指定了可以转换的类型对
  >
  > convert : 在 sourceType 和 targetType 类型之间进行转换

  ```Java
  package org.springframework.core.convert.converter;
  public interface ConditionalGenericConverter extends GenericConverter {
  	boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType);
  }
  ```

  > matches() : 用于判断是否进行转换		

- ConverterFactory :模式的实现，用于选择将一种S源类型转换为R类型的子类型T的转换器的工厂接口

  ```Java
  package org.springframework.core.convert.converter;
  public interface ConverterFactory<S, R> {
    	<T extends R> Converter<S, T> getConverter(Class<T> targetType);
  }
  ```

  > S : 源类型
  >
  > R : 目标类型的父类型
  >
  > T : 目标类型,且是R类型的子类型

  ​	对于我们大部分用户来说一般不需要自定义这些接口，如果需要可以参考内置的实现

  ​	

#### 2 类型转换器注册器 , 类型转换服务

​	提供类型转换器注册支持和运行时类型转换API的支持,一般有两个接口 : 

- ConverterRegistry : 类型转换器注册支持 , 可以注册/删除转换器

  ```Java
  public interface ConverterRegistry {
  	void addConverter(Converter<?, ?> converter);
  	void addConverter(Class<?> sourceType, Class<?> targetType, Converter<?, ?> converter)
  	void addConverter(GenericConverter converter);
  	void addConverterFactory(ConverterFactory<?, ?> converterFactory);
  	void removeConvertible(Class<?> sourceType, Class<?> targetType);
  }
  ```

- ConversionService : 运行时类型转换接口 , 提供运行时类型转换的支持

  ```Java
  public interface ConversionService {
      boolean canConvert(Class<?> sourceType, Class<?> targetType);
      boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType);
      <T> T convert(Object source, Class<T> targetType);
    	Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType);
  }
  ```

  ​	**Spring提供了两个默认实现(其都实现了ConverterRegistry、ConversionService接口):**
  ​		`DefaultConversionService` : 默认的类型转换服务实现;
  ​		`DefaultFormattingConversionService` : 带数据格式化支持的类型转换服务实现，一般使用该服务实现即可

### 2 Spring 内建的类型转换器

1. **标量转换器**

| 类型                             | 说明                                       |
| :----------------------------- | :--------------------------------------- |
| StringToBooleanConverter       | String  >  Booleantrue:true/on/yes/1; false:false/off/no/0 |
| ObjectToStringConverter        | Object >  String调用toString方法转换           |
| StringToNumberConverterFactory | String  >  Number(如Integer、Long等)        |
| NumberToNumberConverterFactory | Number子类型(Integer、Long、Double等)   <>  Number子类型(Integer、Long、Double等) |
| StringToCharacterConverter     | String > java.lang.Character取字符串第一个字符    |
| NumberToCharacterConverter     | Number子类型(Integer、Long、Double等)  > java.lang.Character |
| CharacterToNumberFactory       | java.lang.Character > Number子类型(Integer、Long、Double等) |
| StringToEnumConverterFactory   | String > enum类型                          |
| ….                             | …...                                     |

2. 集合和数组相关的转换器

3. 默认转换器(fallback) : 之前的转换器不能转换时调用

   具体的转换器类型和实现,可以直接参考源码查看.

   转换器实现示例 :

   ```Java
   public class StringToPhoneNumberConverter implements Converter<String, PhoneNumberModel> {
           Pattern pattern = Pattern.compile("^(\\d{3,4})-(\\d{7,8})$");
           @Override
           public PhoneNumberModel convert(String source) {
   			   if(!StringUtils.hasLength(source)) { //1如果source为空 返回null
                           return null;
                   }
                   Matcher matcher = pattern.matcher(source);
                   if(matcher.matches()) {
   					//2如果匹配 进行转换
                     	PhoneNumberModel phoneNumber = new PhoneNumberModel();
                     	phoneNumber.setAreaCode(matcher.group(1));
                       phoneNumber.setPhoneNumber(matcher.group(2));
                       return phoneNumber;
   				} else {
   				  //3如果不匹配 转换失败
   				  throw new IllegalArgumentException(String.format("类型转换失败，需要格 式[010-12345678]，但格式是[%s]", source));
   				} }
   }
   ```

   ### 3 集成转换器到 Spring MVC 环境

   ​	使用xml声明集成转换器的步骤比较麻烦 , 这里不写了 ,百度 …. 后边介绍     `<mvc:annotation-driven>` 和`@EnableWebMvc`，ConversionService会自动注册

   ## 3 数据格式化			

   ​	在如Web /客户端项目中，通常需要将数据转换为具有某种格式的字符串进行展示，因此上节我们学习的数据类型转换系统核心作用不是完成这个需求，因此Spring3引入了格式化转换器(Formatter SPI) 和格式化服务API(FormattingConversionService)从而支持这种需求。在Spring中它和PropertyEditor功能类似，可以替代PropertyEditor来进行对象的解析和格式化，而且支持细粒度的字段级别的格式化/解析.

   ​	Formatter SPI核心是完成解析和格式化转换逻辑，在如Web应用/客户端项目中，需要解析、打印/展示本地化的对象值时使用，如根据Locale信息将java.util.Date---->java.lang.String打印/展示、java.lang.String---->java.util.Date等。

   ​	该格式化转换系统是Spring通用的，其定义在org.springframework.format包中，不仅仅在Spring Web MVC场景下。

#### 1 格式化器

​	提供格式化转换的支持.共有如下几个接口:

- Printer接口:格式化显示接口，将T类型的对象根据Locale信息以某种格式进行打印显示(即返回字符串形
  式)

  - Parser接口:解析接口，根据Locale信息解析字符串到T类型的对象;

- Formatter接口:格式化SPI接口，继承Printer和Parser接口，完成T类型对象的格式化和解析功能;

- AnnotationFormatterFactory接口:注解驱动的字段格式化工厂，用于创建带注解的对象字段的Printer和
  Parser，即用于格式化和解析带注解的对象字段

- FormatterRegistry:格式化转换器注册器，用于注册格式化转换器(Formatter、Printer和Parser、

  AnnotationFormatterFactory)

- FormattingConversionService:继承自ConversionService，运行时类型转换和格式化服务接口，提供运行期

  类型转换和格式化的支持。

#### 2 内建的格式化转换器

| 类型                                       | 说明                                       |
| ---------------------------------------- | ---------------------------------------- |
| DateFormatter                            | java.util.Date<---->String实现日期的格式化/解析    |
| NumberFormatter                          | java.lang.Number<---->String实现通用样式的格式化/解析 |
| CurrencyFormatter                        | java.lang.BigDecimal<---->String实现货币样式的格式化/解析 |
| PercentFormatter                         | java.lang.Number<---->String实现百分数样式的格式化/解析 |
| NumberFormatAnnotationFormatterFactory   | @NumberFormat注解类型的数字字段类型<---->String     1 通过@NumberFormat指定格式化/解析格式                     2 可以格式化/解析的数字类型:Short、Integer、Long、Float、Double、BigDecimal、BigInteger |
| JodaDateTimeFormatAnnotationFormatterFactory | @DateTimeFormat注解类型的日期字段类型<---->String               1 通过@DateTimeFormat指定格式化/解析格式                               2 可以格式化/解析的日期类型:joda中的日期类型(org.joda.time包的):LocalDateJodaDateTimeFormatAnnotationFormatterFactory LocalDateTime、LocalTime、ReadableInstantjava内置的日期类型:Date、Calendar、Long                                                                **注意:**classpath中必须有Joda-Time类库，否则无法格式化日期类型 |

​	*`NumberFormatAnnotationFormatterFactory`和`JodaDateTimeFormatAnnotationFormatterFactory`(如果classpath提供了Joda-Time类库)在使用格式化服务实现`DefaultFormattingConversionService`时会自动注册

​		

#### 3 数据校验

​	Spring 2.x 提供编程式验证支持,太麻烦...

​	Spring3开始支持JSR-303验证框架，JSR-303支持XML风格的和注解风格的验证

##### 1 XML风格的数据验证集成

1. 添加jar包 `hibernate-validator-4.3.0.Final.jar`
2. 在配置文件中添加对JSR-303框架的支持

```Html
<!-- 以下 validator ConversionService 在使用 mvc:annotation-driven 会 自动注册-->
<bean id="validator" class="org.springframework.validation.beanvalidation.LocalValidatorFactoryBean">
	<property name="providerClass" value="org.hibernate.validator.HibernateValidator"/> 
	<!-- 如果不加默认 使用classpath下的 ValidationMessages.properties -->
	<property name="validationMessageSource" ref="messageSource"/>
</bean>
```

> validationMessageSource属性:指定国际化错误消息从哪里取，此处使用之前定义的messageSource来获取国际化消息;如果此处不指定该属性，则默认到classpath下的ValidationMessages.properties取国际化错误消息			

通过ConfigurableWebBindingInitializer注册validator:

```Html
<bean id="webBindingInitializer"
class="org.springframework.web.bind.support.ConfigurableWebBindingInitializer">
  <property name="conversionService" ref="conversionService"/>
  <property name="validator" ref="validator"/>
</bean>
```

​	如上集成过程看起来比较麻烦，使用 `<mvc:annotation-driven>` 和@`EnableWebMvc`，
ConversionService会自动注册

3. JSR-303验证框架注解为模型对象指定验证信息

```Java
public class UserModel {
	@NotNull(message="{username.not.empty}")
	private String username;
}
```

> @NotNull指定此username字段不允许为空，当验证失败时将从之前指定的messageSource中获取
> “username.not.empty”对应的错误信息，此处只有通过“{错误消息键值}”格式指定的才能从messageSource获取。				

4. 控制器

```Java
@Controller
public class HelloWorldController {
        @RequestMapping("/validate/hello")
        public String validate(@Valid @ModelAttribute("user") UserModel user, Errors errors) {
          	if(errors.hasErrors()) {
        		return "validate/error";
			}
			return "redirect:/success";
		} 
}
```

> 在命令对象上注解@Valid来告诉Spring MVC此命令对象在绑定完毕后需要进行JSR-303验证，如果验证失败会将错误信息添加到errors错误对象中

5. 提供验证失败后要展示的页面

  ​				



## 3 Spring MVC 项目快速搭建

​	在Spring MVC 里实现WebApplicationInitializer 接口便可实现等同于 web.xml 的配置!!!

1. 创建maven项目,同时添加依赖,pom.xml如下:

```Html
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<groupId>com.fmi110</groupId>
	<artifactId>hello-spring-mvc</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>war</packaging>

	<properties>

		<java.version>1.8</java.version>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>

		<jsp.version>2.2</jsp.version>
		<jstl.version>1.2</jstl.version>
		<servlet.version>3.1.0</servlet.version>

		<springframework.version>4.3.10.RELEASE</springframework.version>

		<logback.version>1.2.3</logback.version>
		<slf4j.version>1.7.25</slf4j.version>
		<log4j.version>1.2.16</log4j.version>

	</properties>

	<build>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<version>3.5.1</version>
				<configuration>
					<target>${java.version}</target>
					<source>${java.version}</source>
				</configuration>
			</plugin>
			<plugin>
				<groupId>org.apache.tomcat.maven</groupId>
				<artifactId>tomcat7-maven-plugin</artifactId>
				<version>2.2</version>
			</plugin>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-war-plugin</artifactId>
				<version>2.6</version>
				<configuration>
					<!-- 配置后项目中没有web.xml文件时不报错 -->
					<failOnMissingWebXml>false</failOnMissingWebXml>
				</configuration>
			</plugin>
		</plugins>
	</build>
	<dependencies>

		<!-- https://mvnrepository.com/artifact/javax.servlet/javax.servlet-api -->
		<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>javax.servlet-api</artifactId>
			<version>${servlet.version}</version>
			<scope>provided</scope>
		</dependency>

		<dependency>
			<groupId>javax.servlet.jsp</groupId>
			<artifactId>jsp-api</artifactId>
			<version>${jsp.version}</version>
			<scope>provided</scope>
		</dependency>

		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-webmvc</artifactId>
			<version>${springframework.version}</version>
		</dependency>

		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-tx</artifactId>
			<version>${springframework.version}</version>
		</dependency>


		<dependency>
			<groupId>jstl</groupId>
			<artifactId>jstl</artifactId>
			<version>${jstl.version}</version>
		</dependency>


		<!-- start 使用SLF4J 和 LogBack 作为日志 -->
		<dependency>
			<groupId>ch.qos.logback</groupId>
			<artifactId>logback-classic</artifactId>
			<version>${logback.version}</version>
		</dependency>
		<dependency>
			<groupId>ch.qos.logback</groupId>
			<artifactId>logback-core</artifactId>
			<version>${logback.version}</version>
		</dependency>
		<dependency>
			<groupId>ch.qos.logback</groupId>
			<artifactId>logback-access</artifactId>
			<version>${logback.version}</version>
		</dependency>
		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-api</artifactId>
			<version>${slf4j.version}</version>
		</dependency>
		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>jcl-over-slf4j</artifactId>
			<version>${slf4j.version}</version>
		</dependency>
		<dependency>
			<groupId>log4j</groupId>
			<artifactId>log4j</artifactId>
			<version>${log4j.version}</version>
		</dependency>
		<!-- end 使用SLF4J 和 LogBack 作为日志 -->

	</dependencies>
</project>
```

2. 添加日志配置文件

   这里使用了 LogBack 和 SLF4J , logback 支持 xml 格式的配置文件,文件放在 src/main/resources 目录下,logback.xml 如下:

```Html
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="false">
	

	<!--定义日志文件的存储地址 勿在 LogBack 的配置中使用相对路径 -->
	<property name="LOG_HOME" value="/home" />
	<property name="appName" value="fmi110-hello-springmvc" />
	
	
	<!-- 设置应用的名字 -->
	<contextName>${appName}</contextName>
	
	<!-- 控制台输出 -->
	<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
		<encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
			<!--格式化输出：	%d表示日期，
							%thread表示线程名，
							%-5level：级别从左显示5个字符宽度
							%msg：日志消息，%n是换行符 
							-->
			<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} -%msg%n</pattern>
		</encoder>
	</appender>
	<!-- 按照每天生成日志文件 -->
	<appender name="FILE"
		class="ch.qos.logback.core.rolling.RollingFileAppender">
		<rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
			<!--日志文件输出的文件名 -->
			<FileNamePattern>${LOG_HOME}/TestWeb.log.%d{yyyy-MM-dd}.log
			</FileNamePattern>
			<!--日志文件保留天数 -->
			<MaxHistory>30</MaxHistory>
		</rollingPolicy>
		<encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
			<!--格式化输出：%d表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度%msg：日志消息，%n是换行符 -->
			<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} -
				%msg%n</pattern>
		</encoder>
		<!--日志文件最大的大小 -->
		<triggeringPolicy
			class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
			<MaxFileSize>10MB</MaxFileSize>
		</triggeringPolicy>
	</appender>
	<!-- show parameters for hibernate sql 专为 Hibernate 定制 -->
	<logger name="org.hibernate.type.descriptor.sql.BasicBinder"
		level="TRACE" />
	<logger name="org.hibernate.type.descriptor.sql.BasicExtractor"
		level="DEBUG" />
	<logger name="org.hibernate.SQL" level="DEBUG" />
	<logger name="org.hibernate.engine.QueryParameters" level="DEBUG" />
	<logger name="org.hibernate.engine.query.HQLQueryPlan" level="DEBUG" />

	<!--myibatis log configure -->
	<logger name="com.apache.ibatis" level="TRACE" />
	<logger name="java.sql.Connection" level="DEBUG" />
	<logger name="java.sql.Statement" level="DEBUG" />
	<logger name="java.sql.PreparedStatement" level="DEBUG" />

	<!-- 日志输出级别 -->
	<root level="INFO">
		<appender-ref ref="STDOUT" />
		<!-- <appender-ref ref="FILE" /> --> <!-- 如果要输入日志到文件,则解开此注解 -->
	</root>
	<!--日志异步到数据库 -->
	<!-- 
		<appender name="DB" class="ch.qos.logback.classic.db.DBAppender">
			日志异步到数据库
			<connectionSource
				class="ch.qos.logback.core.db.DriverManagerConnectionSource">
				连接池
				<dataSource class="com.mchange.v2.c3p0.ComboPooledDataSource">
					<driverClass>com.mysql.jdbc.Driver</driverClass>
					<url>jdbc:mysql://127.0.0.1:3306/databaseName</url>
					<user>root</user>
					<password>root</password>
				</dataSource>
			</connectionSource>
		</appender> 
	-->
</configuration>
```

3. 添加jsp页面

```Html
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h1>Hello , Spring MVC</h1>
	<h1>${name }</h1>
</body>
</html>
```

> 页面放置在 `src/main/resources/webapp/WEB-INF/jsp/` 目录下

4. Spring MVC 配置

   创建一个配置类,进行spring mvc 的相关设置

```Java
package com.fmi110.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 * @author huangyunning java配置类 , 取代 xml 配置 WebMvcConfigurerAdapter
 *         类中提供了很多对web设置的方法,重写这些方法对应用进行设定 比如,添加拦截器 , 添加格式转换器
 *         ,消息转换器等,通常可以通过方法名推测或看文档
 */
@Configuration
@EnableWebMvc
@ComponentScan("com.fmi110")
public class MyMVCConfig extends WebMvcConfigurerAdapter {
	/**
	 * 添加路劲映射,通常设置直接的页面跳转用的的
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/toupload").setViewName("upload"); // 当请求为 /upload 时,返回 upload.jsp 页面
		super.addViewControllers(registry);
	}

	/**
	 * 配置视图解析对象
	 * 
	 * @return
	 */
	@Bean
	public InternalResourceViewResolver internalResourceViewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/classes/webapp/jsp/");
		viewResolver.setSuffix(".jsp");
		viewResolver.setViewClass(JstlView.class);
		return viewResolver;
	}

	/**
	 * 注入文件解析器,处理文件上传时需要注入该对象
	 * 
	 * @return
	 */
	@Bean
	public MultipartResolver multipartResolver() {
		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
		commonsMultipartResolver.setDefaultEncoding("utf-8"); // 默认 iso-8859-1
		commonsMultipartResolver.setMaxUploadSizePerFile(5 * 1024 * 1024); // 设置单个文件最大大小
		commonsMultipartResolver.setMaxUploadSize(50 * 1024 * 1024); // 设置最大上传
		return commonsMultipartResolver;
	}
}
```

> 1. @EnableWebMvc 注解会开启一些默认的配置,如一些ViewResources 或 MessageConverter 等

5. 进行Web配置

   进行web的配置,等同于配置web.xml,只是这里我们使用java配置

```Java
package com.fmi110.config;

public class WebConfigure implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {

		// 1 创建上下文
		// 2 注册配置类
		// 3 关联servlet容器上下文
		// 4 servlet容器上下文添加spring前端控制器servlet
		// 5 设置url-pattern 和 启动顺序
		AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
		appContext.register(MyMVCConfig.class);
		appContext.setServletContext(servletContext);

		Dynamic dispatcherServlet = servletContext.addServlet("dispatcher", new DispatcherServlet(appContext)); // 需要传递spring的上下文
		dispatcherServlet.addMapping("/");
		dispatcherServlet.setLoadOnStartup(1);

	}

}
```

> 1. `WebApplicationInitializer` 是spring提供用来配置servlet 3.0+ 的接口,从而取代了 web.xml 的位置. 实现此接口将会自动被 `SpringServletContainerInitializer(用来启动servlet3.0容器)` 读取

6. 创建控制器,并访问


```Java
package com.fmi110.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

//@Scope("prototype")
@Controller
public class HelloController {

	private final static Logger log = LoggerFactory.getLogger(HelloController.class);

	// @RequestMapping("/hello")
	@RequestMapping(value = "/hello", produces = "text/html;charset=gbk")
	public @ResponseBody ModelAndView hello(HttpServletRequest request) throws InterruptedException {

		log.info("======hello request = " + request + "===== controller = " + this);
		// log.info("当前线程 : " + Thread.currentThread().getName());
		// log.info("sleeping.....start.");
		// Thread.sleep(10000);
		// log.info("sleeping.....end.");
		ModelAndView mv = new ModelAndView();
		mv.setViewName("index");
		mv.addObject("msg", "这时从controller传递回来的信息");
		return mv;
	}

	/**
	 * produces 属性相当于调用了 response.setContentType("text/html;charset=utf-8")
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/hello1", produces = "text/html;charset=utf-8")
	public @ResponseBody String hello1(HttpServletRequest request) {

		return "你好 , spring mvc";
	}

}
```

```Java
package com.fmi110.controller;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author huangyunning 处理文件上传
 */
@Controller
public class UploadController {

	private static Logger log = LoggerFactory.getLogger(UploadController.class);

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody String upload(MultipartFile file) throws Exception {

		String fileName = file.getOriginalFilename();

		log.info("=== 接受文件 : " + fileName);

		File desFile = new File("/Users/huangyunning/Desktop/", "copy_" + fileName);
		FileUtils.writeByteArrayToFile(desFile, file.getBytes()); // 复制文件
		return "successed...";
	}

}
```

页面

```Html
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<form method="post" enctype="multipart/form-data" action="${pageContext.request.contextPath }/upload">
		文件上传: <input type="file" name="file"> <input type="submit">
	</form>
</body>
</html>
```

