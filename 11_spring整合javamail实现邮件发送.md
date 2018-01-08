# 11 spring整合javamail实现邮件发送

## 1 maven坐标依赖

```xml
	<dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>4.3.12.RELEASE</version>
        </dependency>
        <!--javaMail 邮件发送等的支持-->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context-support</artifactId>
            <version>4.3.12.RELEASE</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/com.sun.mail/javax.mail -->
        <dependency>
            <groupId>com.sun.mail</groupId>
            <artifactId>javax.mail</artifactId>
            <version>1.4.4</version>
        </dependency>
    </dependencies>
```

## 2 applicationContext.xml 配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.2.xsd
		http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.2.xsd">

    <!-- 声明使用占位符 , 并指定占位符文件位置 -->
    <context:property-placeholder location="classpath:javamail.properties" />
    <!-- 开启注解扫描 -->
    <context:component-scan base-package="com.fmi110"/>

    <!-- 使用qq邮箱 -->
    <bean id="javaMailSender" class="org.springframework.mail.javamail.JavaMailSenderImpl">
        <!--发送邮件的邮箱地址-->
        <property name="host" value="${mail.host}"/>
        <property name="port" value="${mail.port}"/>
        <property name="username" value="${mail.username}"/>
        <!-- qq邮箱的授权码，如果是企业邮箱，则使用登录密码 -->
        <property name="password" value="${mail.password}"/>
        <property name="javaMailProperties">
            <props >
                <prop key="mail.smtp.auth">true</prop>
            </props>
        </property>
    </bean>

    <!--FreeMarker模板-->
    <!--<bean id="freeMarkerConfigurer" class="org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer">-->
        <!--<property name="templateLoaderPath" value="classpath:freemarker" />-->
        <!--<property name="freemarkerSettings">-->
            <!--<props>-->
                <!--<prop key="template_update_delay">1800</prop>-->
                <!--<prop key="locale">zh_CN</prop>-->
                <!--<prop key="default_encoding">UTF-8</prop>-->
            <!--</props>-->
        <!--</property>-->
    <!--</bean>-->

</beans>
```

## 3 javamail.properties 配置文件

​	如果是maven工程构建,则该文件放置在 `src\main\resources`  目录下(非maven构建的工程直接放置在 src 目录下) , 这里我使用的qq邮箱作为发件邮箱.

​	**通过qq 或 网易等邮箱使用 javamail 发送邮件时, 密码栏需要使用授权码,qq邮箱授权码获取方式请参考 : [qq邮箱开启POP3服务并获取授权码](http://service.mail.qq.com/cgi-bin/help?subtype=1&&id=28&&no=1001256)**

```properties
mail.host=smtp.qq.com
# 465或587  如果使用 ssl 发送的话,端口号为 465 !!
mail.port=587
mail.username=100xxx5458@qq.com
# 授权码(使用第三方客户端发送邮件需要使用授权码而不是登录密码
# 授权码的获取查看邮箱官方说明)
mail.password=qnuwnpgmrvkxbfid
```

> 如果出现异常 : 
>
> org.springframework.mail.MailAuthenticationException: Authentication failed; nested exception is javax.mail.AuthenticationFailedException: 535 Error: ÇëÊ¹ÓÃÊÚÈ¨ÂëµÇÂ¼¡£ÏêÇéÇë¿´....
>
> 那么就是授权码输入错误导致的!!!!

## 4 编写 MailUtil.java 工具类

```java
package com.fmi110.erp;

import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 发送邮件的工具类,使用邮件内容使用了 freemark 模版
 */
@Component
public class MailUtil {
   
  @Autowired
    private JavaMailSender       javaMailSender;

//    @Autowired
//    private FreeMarkerConfigurer freeMarkerConfigurer;
   
  	/**
     * 邮箱发件人
     */
    @Value(value = "${mail.username}")
    private String               mailHost;

    /**
     * 从freemarker模版中构建邮件内容
     * @param to      收件人
     * @param subject 邮件主题
     * @param content 邮件内容
     */
//    public void sendMailUseTemplate(String to,String subject,String content) throws Exception {
//        String              text = "";
//        Map<String, String> map  = new HashMap<String, String>(1);
//        map.put("content", content);
//
//        // 根据模板内容，动态把map中的数据填充进去，生成HTML
//        Template template = freeMarkerConfigurer.getConfiguration()
//                                                .getTemplate("mail.ftl");
//        // map中的key，对应模板中的${key}表达式
//        text = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
//
//        sendMail(to, subject, text);
//    }

    /**
     * 发送邮件,手动指定主题和内容
     *
     * @param to      收件人
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    public void sendMail(String to, String subject, String content) throws MessagingException {
        MimeMessage       message       = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
        messageHelper.setFrom(mailHost);  // 设置发件人
        messageHelper.setSubject(subject); // 设置邮件主题

        messageHelper.setTo(to);  // 设置收件人
        messageHelper.setText(content, true); // 设置文本内容
        javaMailSender.send(message);
    }

    /**
     * 带附件发送邮件
     * @param to        收件人
     * @param subject   主题
     * @param content   正文
     * @param filePaths 文件的路径
     * @throws MessagingException
     */
    public void sendMail(String to, String subject, String content, String... filePaths) throws MessagingException {
        //获取JavaMailSender bean
        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        //设置utf-8或GBK编码，否则邮件会有乱码
        MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true, "utf-8");
        try {
            messageHelper.setFrom(mailHost);  // 设置发件人
            messageHelper.setSubject(subject); // 设置邮件主题

            messageHelper.setTo(to);  // 设置收件人
            messageHelper.setText(content, true);

            //附件内容
//          messageHelper.addInline("b", new File("E:/logo.png"));
            if(filePaths!=null&&filePaths.length>0){
              	for (String path : filePaths) {
                	File file = new File(path);
                	// 这里的方法调用和插入图片是不同的，使用MimeUtility.encodeWord()来解决附件名称的中文问题
                	messageHelper.addAttachment(MimeUtility.encodeWord(file.getName()), file);
            	}
            }
            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## 5 测试

```java
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by huangyunning on 2017/12/15.
 */
public class TestMail {
    public static void main(String[] args) throws Exception {
        ApplicationContext context  = new ClassPathXmlApplicationContext("applicationContext-javamail.xml");
        MailUtil           mailUtil = (MailUtil) context.getBean("mailUtil");
        mailUtil.sendMail("huangyunning@itcast.cn","测试邮件","hello,fmi110");
    }
}
```