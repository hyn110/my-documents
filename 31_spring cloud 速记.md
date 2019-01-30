# 31 spring cloud 速记

​	工程直接通过 Spring Initializr 创建 , spring boot 和 spring cloud 的版本信息如下

```xml
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.0.5.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>

	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
		<java.version>1.8</java.version>
		<spring-cloud.version>Finchley.SR1</spring-cloud.version>
	</properties>
```

## 1 Eureka

### 1 服务端配置

​	在主应用类上添加下面的注解开启eureka服务端

```java
@EnableEurekaServer  
```

​	单实例的配置:

1. 应用端口

2. 实例hostname

3. 是否将自己注册到服务器上

4. 禁用检索服务

   yml

```yaml
spring:
  application:
    name: eureka
server:
  port: 8762
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8762/eureka/
    register-with-eureka: true
    fetch-registry: false
  server:
    enable-self-preservation: false
```

​	properties

```properties
# yml 文件优先级高于 properties 文件
server.port=8762
spring.application.name=eureka-test
eureka.instance.hostname=localhost
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
eureka.client.service-url.defaultZone=http://${eureka.instance.hostname}:${server.port}/eureka/
```

​	pom 依赖 :

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>com.fmi110</groupId>
	<artifactId>eurekaserver</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>eurekaserver</name>
	<description>Demo project for Spring Boot</description>

	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.0.5.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>

	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
		<java.version>1.8</java.version>
		<spring-cloud.version>Finchley.SR1</spring-cloud.version>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>

	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-dependencies</artifactId>
				<version>${spring-cloud.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>
</project>
```

### 2 客户端配置

​	pom 依赖 :

```xml
	<dependencies>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>
```

​	**这个版本的spring cloud 的eureka的客户端依赖没有 tomcat , 因此必须引入  `spring-boot-starter-web` 依赖,否则报错  **

```properties
Invocation of destroy method failed on bean with name 'scopedTarget.eurekaClient': org.springframework.beans.factory.BeanCreationNotAllowedException: Error creating bean with name 'eurekaInstanceConfigBean': Singleton bean creation not allowed while singletons of this factory are in destruction (Do not request a bean from a BeanFactory in a destroy method implementation!)
```

## 2 Ribbon 客户端负载均衡

### 1 使用 LoadBalancerClient 实现

1. 客户端主类加注解 `@EnableDiscoveryClient`
2. 应用中注入 RestTemplate 和 LoadBalancerClient 对象
3. 通过 LoadBalancerClient 对象获取服务的url *(这一步实现均衡)*
4. 通过RestTemplate对象发起请求

```java
@RestController
@Slf4j
public class DemoController {

    @Autowired
    LoadBalancerClient loadBalancerClient;
    
    @Autowired 
    RestTemplate restTemplate;
    
    @GetMapping("/hello") 
    public String hello(String name) {
        ServiceInstance serviceInstance = loadBalancerClient.choose("eureka-client");
        String url = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/hello?name=" + name;
        log.info("url地址为：{}", url);
        return restTemplate.getForObject(url, String.class);
    } 
}
```

### 2 使用注解 @LoadBalanced 实现

​	在生成 RestTemplate 对象的方法上添加注解 `@LoadBalanced` , 然后正常使用 RestTemplate 进行请求即可

```java
@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
public class EurekaConsumerRibbonApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(EurekaConsumerRibbonApplication.class, args);
        log.info("spring-cloud-eureka-consumer-ribbon启动!");
    }
    
    //添加 @LoadBalanced 使其具备了使用LoadBalancerClient 进行负载均衡的能力
    @Bean
    @LoadBalanced
    public RestTemplate restTemplage() {
        return new RestTemplate();
    }
}
```

### 3 Ribbon 内置的负载均衡规则

| **内置负载均衡规则类**             | **规则描述**                                 |
| ------------------------- | ---------------------------------------- |
| RoundRobinRule            | 简单轮询服务列表来选择服务器。它是Ribbon默认的负载均衡规则。        |
| AvailabilityFilteringRule | 对以下两种服务器进行忽略：（1）在默认情况下，这台服务器如果3次连接失败，这台服务器就会被设置为“短路”状态。短路状态将持续30秒，如果再次连接失败，短路的持续时间就会几何级地增加。注意：可以通过修改配置loadbalancer.<clientName>.connectionFailureCountThreshold来修改连接失败多少次之后被设置为短路状态。默认是3次。（2）并发数过高的服务器。如果一个服务器的并发连接数过高，配置了AvailabilityFilteringRule规则的客户端也会将其忽略。并发连接数的上线，可以由客户端的<clientName>.<clientConfigNameSpace>.ActiveConnectionsLimit属性进行配置。 |
| WeightedResponseTimeRule  | 为每一个服务器赋予一个权重值。服务器响应时间越长，这个服务器的权重就越小。这个规则会随机选择服务器，这个权重值会影响服务器的选择。 |
| ZoneAvoidanceRule         | 以区域可用的服务器为基础进行服务器的选择。使用Zone对服务器进行分类，这个Zone可以理解为一个机房、一个机架等。 |
| BestAvailableRule         | 忽略哪些短路的服务器，并选择并发数较低的服务器。                 |
| RandomRule                | 随机选择一个可用的服务器。                            |
| Retry                     | 重试机制的选择逻辑                                |

```properties
附录：AvailabilityFilteringRule的三个默认配置

# successive connection failures threshold to put the server in circuit tripped state, default 3
niws.loadbalancer.<clientName>.connectionFailureCountThreshold

# Maximal period that an instance can remain in "unusable" state regardless of the exponential increase, default 30
niws.loadbalancer.<clientName>.circuitTripMaxTimeoutSeconds

# threshold of concurrent connections count to skip the server, default is Integer.MAX_INT
<clientName>.<clientConfigNameSpace>.ActiveConnectionsLimit
```

## 3 Feign

### 1 Feign的使用

​	Spring Cloud对Feign进行了增强，使Feign支持了Spring MVC注解，并整合了Ribbon和 Eureka,从而让Feign 的使用更加方便。**只需要通过创建接口并用注解来配置它既可完成对Web服务接口的绑定**

​	使用步骤:

1. 添加依赖
2. 启动类添加注解 `@EnableFeignClients` 
3. 创建接口类 , 添加注解 `FeignClient` , 并通过name属性指定要调用的服务的名称
4. 注入接口,使用接口调用方法即可

```java
@SpringBootApplication
@EnableFeignClients
@Slf4j
public class EurekaConsumerFeignApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(EurekaConsumerFeignApplication.class, args);
        log.info("spring-cloud-eureka-consumer-feign启动");
    }

}
```

```java
@FeignClient(name="eureka-client")
public interface IHelloClient {

    /**
     * 定义接口
     * @param name
     * @return
     */
    @RequestMapping(value="/hello", method=RequestMethod.GET)
    public String hello(@RequestParam("name") String name);
}
```

```java
@RestController
@Slf4j
public class DemoController {
    
    @Autowired
    IHelloClient helloClient;
    
    @GetMapping("/hello") 
    public String hello(String name) {
        log.info("使用feign调用服务，参数name:{}", name);
        return helloClient.hello(name);
    } 
}
```

### 2 Feign 上传文件

​	在Spring Cloud封装的Feign中并不直接支持传文件，但可以通过引入Feign的扩展包来实现

[使用Feign上传文件](https://blog.csdn.net/Dragon_MD/article/details/79745103 )

http://blog.didispace.com/spring-cloud-starter-dalston-2-4/

1. 添加依赖

```xml
<dependency>
   <groupId>io.github.openfeign.form</groupId>
   <artifactId>feign-form</artifactId>
   <version>3.0.3</version>
</dependency>
<dependency>
   <groupId>io.github.openfeign.form</groupId>
   <artifactId>feign-form-spring</artifactId>
   <version>3.0.3</version>
</dependency>
<dependency>
   <groupId>commons-fileupload</groupId>
   <artifactId>commons-fileupload</artifactId>
   <version>1.3.3</version>
</dependency>
```

2. 配置 FeignClient 客户端

```java
@FeignClient(value = "upload-server", configuration = UploadService.MultipartSupportConfig.class)
public interface UploadService {
 
    @PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String handleFileUpload(@RequestPart(value = "file") MultipartFile file);
 
    @Configuration
    class MultipartSupportConfig {
        @Bean
        public Encoder feignFormEncoder() {
            return new SpringFormEncoder();
        }
    }
 
}
```

3. 上传文件

```java
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class UploadTester {

    @Autowired
    private UploadService uploadService;

    @Test
    @SneakyThrows
    public void testHandleFileUpload() {

        File file = new File("upload.txt");
        DiskFileItem fileItem = (DiskFileItem) new DiskFileItemFactory().createItem("file",
                MediaType.TEXT_PLAIN_VALUE, true, file.getName());

        try (InputStream input = new FileInputStream(file); 
             OutputStream os = fileItem.getOutputStream()) {
            IOUtils.copy(input, os);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid file: " + e, e);
        }

        MultipartFile multi = new CommonsMultipartFile(fileItem);

        log.info(uploadService.handleFileUpload(multi));
    }

}
```

4. 服务接收方法

```java
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class Application {

    @RestController
    public class UploadController {

        @PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public String handleFileUpload(@RequestPart(value = "file") MultipartFile file) {
            return file.getName();
        }

    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).web(true).run(args);
    }

}
```

### 3 文件下载

https://blog.csdn.net/AaronSimon/article/details/82710979 

```java
@RequestMapping(value = "/downloadFile",
                method = RequestMethod.GET,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
Response downloadFile();
```

```java
@RequestMapping(value = "/download",method = RequestMethod.GET)
  public ResponseEntity<byte[]> downFile(){
    ResponseEntity<byte[]> result=null ;
    InputStream inputStream = null;
    try {
      // feign文件下载
      Response response = uploadService.downloadFile();
      Response.Body body = response.body();
      inputStream = body.asInputStream();
      byte[] b = new byte[inputStream.available()];
      inputStream.read(b);
      HttpHeaders heads = new HttpHeaders();
      heads.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=123.txt");
      heads.add(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON_VALUE);
      
      // String fileName = new String(name.getBytes("UTF-8"), "iso-8859-1");// 为了解决中文名称乱码问题
      // headers.setContentDispositionFormData("attachment", fileName);
      // headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);


      result = new ResponseEntity <byte[]>(b,heads, HttpStatus.OK);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if(inputStream != null){
        try {
          inputStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return result;
  }
```

## 4 Hystrix 服务容错保护

### 1 Feign Hystrix

1 依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```

2 在 application.properties 中添加属性

```properties
feign.hystrix.enabled=true
```

3 在启动类上添加注解 `EnableFeignClients`

4 创建服务接口类 , 同时定义 fallback 属性

```java
@FeignClient(name= "spring-cloud-producer",fallback = HelloRemoteHystrix.class)
public interface HelloRemote {

    @RequestMapping(value = "/hello")
    public String hello(@RequestParam(value = "name") String name);

}
```

5 创建回调类

```java
@Component
public class HelloRemoteHystrix implements HelloRemote{

    @Override
    public String hello(@RequestParam(value = "name") String name) {
        return "hello" +name+", this messge send failed ";
    }
}
```

### 2 Hystrix 监控面板

​	`Hystrix-dashboard(仪表盘)`是一款针对Hystrix进行实时监控的工具，通过`Hystrix Dashboard`我们可以在直观地看到各`Hystrix Command`的请求响应时间, 请求成功率等数据

1 依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

2 在启动类上添加注解 `@EnableHystrixDashboard`

3 application.properties 中开启端点监控

```properties
#开启监控端点
management.endpoints.web.exposure.include=hystrix.stream
```

> 1 这里需要注意,`2.0`之后，默认只开启了端点`info`、`health`。其他的需要通过`management.endpoints.web.exposure.include`进行额外配置
>
> 2 **注意：2.0之后，默认的监控端点地址加了上下文路径actuator。可通过management.endpoints.web.base-path属性进行修改，默认是：actuator**



现在我们启动`spring-cloud-hystrix`。然后添加:[http://127.0.0.1:8038/actuator/hystrix.stream](http://127.0.0.1:8038/actuator/hystrix.stream) 到仪表盘中

### 3 Turbine 数据聚合

​	`hystrix`只能实现单个微服务的监控，可是一般项目中是微服务是以集群的形式搭建，一个一个的监控不现实。而`Turbine`的原理是，**建立一个turbine服务，并注册到eureka中，并发现eureka上的hystrix服务。通过配置turbine会自动收集所需hystrix的监控信息，最后通过dashboard展现，以达到集群监控的效果**

1 添加依赖

```xml
<!-- turbine依赖 -->
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-netflix-turbine</artifactId>
	</dependency>
	<!-- eureka client依赖 -->
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
	</dependency>
```

2 启动类加入注解`@EnableTurbine`和`@EnableDiscoveryClient`

3 配置文件加入注册中心及`turbine`相关配置信息

```properties
#应用名称
spring.application.name=hystrix-tuibine

#端口号
server.port=9698

#指定注册中心地址
eureka.client.service-url.defaultZone=http://127.0.0.1:1000/eureka
# 启用ip配置 这样在注册中心列表中看见的是以ip+端口呈现的
eureka.instance.prefer-ip-address=true
# 实例名称  最后呈现地址：ip:2000
eureka.instance.instance-id=${spring.cloud.client.ip-address}:${server.port}

#turbine配置
# 需要监控的应用名称，默认逗号隔开，内部使用Stringutils.commaDelimitedListToStringArray分割
turbine.app-config=hystrix-example
# 集群名称
turbine.cluster-name-expression="default"
# true 同一主机上的服务通过host和port的组合来进行区分，默认为true
# false 时 在本机测试时 监控中host集群数会为1了 因为本地host是一样的
turbine.combine-host-port=true
```



## 5 Spring-Cloud-Config 分布式配置中心

http://blog.didispace.com/spring-cloud-starter-dalston-3-2/

### 1 基于git

#### 1 创建配置文件

1 在github中创建一个目录：`spring-cloud-config-repo`，来存放配置文件信息

2 创建配置文件

my-config-client-dev.properties

```properties
config=this is dev!
```

my-config-client-test.properties

```properties
config=this is test!
```

> **注意：因为存在多个项目都是用配置中心问题，而每个项目的应用名称是不尽相同的，所以配置文件的命名方式即为：应用名+环境变量(profile)的命名方式。因此，每个应用理应设置应用名称，是个好习惯。具体的映射规则，在service端会进行说明的**

#### 2 Service 端

1 加入依赖

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

2 启动类添加注解 `@EnableConfigServer` , 声明是 ConfigServer

3 在配置文件中添加 git 仓库信息

```properties
spring.application.name=spring-cloud-config-server
server.port=5678

#配置文件git配置
spring.cloud.config.server.git.uri=https://github.com/xxx/spring-cloud-learning.git
# 搜索路径，即配置文件的目录，可配置多个，逗号分隔。默认为根目录。
spring.cloud.config.server.git.searchPaths=spring-cloud-config-repo
# git用户名和密码 针对私有仓库而言需要填写
spring.cloud.config.server.git.username=
spring.cloud.config.server.git.password=
```

4 启动应用

​	访问[http://127.0.0.1:5678/my-config-client-dev.properties](http://127.0.0.1:5678/my-config-client-dev.properties) ，返回了配置文件的信息，说明已经读取到远程仓库信息了

我们可以通过访问配置信息的URL与配置文件的映射关系，获取相应的配置信息。

```properties
/{application}/{profile}[/{label}]
/{application}-{profile}.yml
/{label}/{application}-{profile}.yml
/{application}-{profile}.properties
/{label}/{application}-{profile}.properties
```

> url会映射 {application}-{profile}.properties 对应的配置文件，
> 其中{label}对应Git上不同的分支，默认为master。我们可以尝试构造不同的url来访问不同的配置内容，
> 比如：
> 要访问`master`分支，`my-config-client`应用的dev环境:
>
> http://127.0.0.1:5678/my-config-client/dev/master

#### 3 Client 端

1 添加依赖

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-config</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

2 创建一个启动类

3 添加配置文件 bootstrap.properties 和 常规的 application.properties

`bootstrap.properties`

```properties
# 设置分支
spring.cloud.config.label=master
# 环境变量
spring.cloud.config.profile=dev
# 是否使用注册中心方式进行获取 后续会进行讲解
#spring.cloud.config.discovery.enabled=false
# 服务端地址 
# 在不使用注册中心模式下 直接填写实际地址
spring.cloud.config.uri=http://127.0.0.1:5678
# 注册中心应用id 下一章节会进行讲解
#spring.cloud.config.discovery.service-id=
```

`application.properties`

```properties
# 设置应用名称，需要和配置文件匹配
spring.application.name=my-config-client
server.port=5666
```

**PS:**

​	`spring-cloud-config`相关的属性**必须配置在bootstrap.properties中**，config部分内容才能被正确加载。因为config的相关配置会先于`application.properties`，而`bootstrap.properties`的加载也是先于`application.properties`

4 编写一个控制层，利用`@Value`进行参数测试

```java
@RestController
public class DemoController {

	@Value("${config}")
	String config;
	
	@GetMapping("/")
	public String demo() {
		return "返回的config参数值为:" + config;
	}
}
```

5 启动应用，访问：[http://127.0.0.1:5666/](http://127.0.0.1:5666/) ，可以看见配置信息已经被正确返回了

#### 4 动态刷新

​	在config-client的`pom.xml`中新增`spring-boot-starter-actuator`监控模块，其中包含了`/refresh`刷新API。

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

​	通过POST请求发送到`http://localhost:7002/refresh` 即可刷新配置信息

​	该功能还可以同Git仓库的Web Hook功能进行关联，当有Git提交变化时，就给对应的配置主机发送`/refresh`请求来实现配置信息的实时更新。但是，当我们的系统发展壮大之后，维护这样的刷新清单也将成为一个非常大的负担，而且很容易犯错，那么有什么办法可以解决这个复杂度呢？后续我们将继续介绍如何通过Spring Cloud Bus来实现以消息总线的方式进行通知配置信息的变化，完成集群上的自动化更新

### 2 基于数据库

​	基于数据库存储配置参考 : http://blog.didispace.com/spring-cloud-starter-edgware-3-1/

### 3 基于 SVN



