# cxf 入门

## 1 WebService 简单描述

​	一言以蔽之：**WebService是一种跨编程语言和跨操作系统平台的远程调用技术。**常用的webservice框架 : cfx、jwx、XFire与Axis2，jwx也就是jax-ws，是java6提供的对webservice的一种实现。cxf框架则简化了服务发布过程。Axis与XFire已随着技术不断的更替慢慢落幕，而目前也只有axis2和cxf官方有更新，Axis与XFire都已不再更新。

​		`XML+XSD` , `SOAP` 和 `WSDL` 就是构成WebService平台的三大技术。

### 1 XML+XSD

​	XML 封装数据 , 解决数据表示问题 , 因为与平台和厂商无关 , 保证了通用性 ; XSD 解决数据类型问题 , 定义了 webService 所使用的数据的类型 , 保证了不同系统下数据通用的问题

### 2 SOAP

​	soap协议 . WebService 是通过 http 协议发送请求和接收数据结果 , http 消息内容都有特定的消息头和消息内容 . 而 SOAP 协议限定了这些消息头和内容的格式. 简单理解就是 , SOAP 约束了 WebService 的请求信息和响应信息应该长什么样 .

### 3 WSDL

​	WSDL(Web Services Description Language) 服务描述语言 , WebService 作为一个服务 , 必须告诉调用者服务地址在哪?提供什么服务?调用服务需要传什么参数?等 这些信息 , 就是通过 WSDL文件来描述的 .

​	WSDL文件保存在Web服务器上，通过一个url地址就可以访问到它。客户端要调用一个WebService服务之前，要知道该服务的WSDL文件的地址。WebService服务提供商可以通过两种方式来暴露它的WSDL文件地址：**1.注册到UDDI服务器，以便被人查找；2.直接告诉给客户端调用者。**

### 4 UDDI

​	UDDI (Universal Description, Discovery, and Integration) 是一个主要针对Web服务供应商和使用者的新项目。在用户能够调用Web服务之前，必须确定这个服务内包含哪些商务方法，找到被调用的接口定义，还要在服务端来编制软件，UDDI是一种根据描述文档来引导系统查找相应服务的机制。UDDI利用SOAP消息机制（标准的XML/HTTP）来发布，编辑，浏览以及查找注册信息。它采用XML格式来封装各种不同类型的数据，并且发送到注册中心或者由注册中心来返回需要的数据。

## 2 WebService服务工作流程

![](img/webservice.png)

​	注册到 UUID服务器的 webservice 工作流程大致如下 :

1. 服务提供者实现服务 , 并在服务注册中心(UDDI)进行注册
2. 服务请求者向服务注册中心请求特定服务(其实就是请求对应的 wsdl 文件)
3. 服务注册中心找到对应的服务后 , 将该服务的描述(wsdl)文件返回为请求者
4. 请求者根据返回的 wsdl 文件 , 生成相应的 soap消息 , 发送给服务提供者
5. 服务提供者根据 soap消息执行对应的服务 , 并将结果返回给请求者



