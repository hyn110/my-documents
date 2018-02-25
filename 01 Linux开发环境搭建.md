

# Linux开发环境搭建

 **目录**

​	Linux开发环境搭建... 1

​		1 安装Oracle JDK 		…………………………………………………. 1

​		2 安装Tomcat		…………………………………………………. 2

​		3 安装MySQL			…………………………………………………. 5

​	        4 安装Redis数据库	…………………………………………………. 10

​		5 安装Zookeeper 		…………………………………………………. 12

​		6 安装Nginx			…………………………………………………. xxx

​		7 安装FastDFS		…………………………………………………. xxx



​	文档是基于CentOS 6.7环境搭建,所有命令都是以 root 用户操作的

## 0 自定义命令别名

​	如果某个用户想要定义自己的命令别名，可以将命令添加到用户家目录中的文件.bashrc中,然后重启

```sh
vim ~/.bashrc
```

```sh
alias redis-cli='/usr/local/src/redis/bin/redis-cli'
alias redis-start='/usr/local/src/redis/bin/redis-server /usr/local/src/redis/redis.conf'

alias tomcat-start='/usr/local/src/tomcat/bin/startup.sh'
alias tomcat-shutdown='/usr/local/src/tomcat/bin/shutdown.sh'

alias nginx-start='/usr/local/nginx/sbin/ngix'
alias nginx-reload='/usr/local/nginx/sbin/nginx -s reload'
alias nginx-test-conf='/usr/local/nginx/sbin/nginx -t'

alias zookeeper-start='/usr/local/src/zookeeper-3.4.10/bin/zkServer.sh start'
alias zookeeper-stop='/usr/local/src/zookeeper-3.4.10/bin/zkServer.sh stop'
alias zookeeper-status='/usr/local/src/zookeeper-3.4.10/bin/zkServer.sh status'
```

## 1 安装Oracle JDK

​	软件包版本 `jdk-8u144-linux-i586.tar.gz` , 并将文件上传值 linux 的 `/usr/local/src` 目录下

##### 1 查看系统自带的jdk

```Sh
[root@centos-6 src]# rpm -qa | grep -i java
tzdata-java-2015e-1.el6.noarch
java-1.6.0-openjdk-1.6.0.35-1.13.7.1.el6_6.i686
java-1.7.0-openjdk-1.7.0.79-2.5.5.4.el6.i686
java-1.5.0-gcj-1.5.0.0-29.1.el6.i686
java_cup-0.10k-5.el6.i686
```

##### 2 卸载系统自带的jdk

```Sh
[root@centos-6 src]# rpm -e --nodeps java-1.6.0-openjdk-1.6.0.35-1.13.7.1.el6_6.i686 
[root@centos-6 src]# rpm -e --nodeps java-1.7.0-openjdk-1.7.0.79-2.5.5.4.el6.i686
[root@centos-6 src]# rpm -qa | grep -i java
tzdata-java-2015e-1.el6.noarch
java-1.5.0-gcj-1.5.0.0-29.1.el6.i686
java_cup-0.10k-5.el6.i686
```

##### 3 进入jdk压缩包所在目录并解压

```Sh
[root@centos-6 src]# cd /usr/local/src/
[root@centos-6 src]# tar -zxvf jdk-8u144-linux-i586.tar.gz
[root@centos-6 src]# ls
apache-tomcat-8.0.46.tar.gz  jdk-8u144-linux-i586.tar.gz             redis-4.0.1.tar.gz
jdk1.8.0_144                 mysql-5.7.19-1.el6.i686.rpm-bundle.tar  zookeeper-3.4.10.tar.gz
```

##### 4 重命名解压出来的文件夹为java

```Sh
[root@centos-6 src]# mv jdk1.8.0_144/  java
[root@centos-6 src]# ls
apache-tomcat-8.0.46.tar.gz  jdk-8u144-linux-i586.tar.gz             redis-4.0.1.tar.gz
java                         mysql-5.7.19-1.el6.i686.rpm-bundle.tar  zookeeper-3.4.10.tar.gz
```

##### 5 配置JAVA_HOME环境变量

1. 打开 /etc/profile 文件

```Sh
[root@centos-6 src]# vim /etc/profile
```

> 文件打开后,按 `i` 进入编辑模式

2. 在文件末尾添加如下内容,保存并退出

```SH
export JAVA_HOME=/usr/local/src/java
export PATH=$JAVA_HOME/bin:$PATH
```

> 内容添加完成后,按 `esc` 退出编辑模式 , 然后按住 `shift` + 双击 `z` 进行保存并退出!! 

3. 重新载入配置文件并验证

```Sh
[root@centos-6 src]# source /etc/profile     
[root@centos-6 src]# java -version
java version "1.8.0_144"
Java(TM) SE Runtime Environment (build 1.8.0_144-b01)
Java HotSpot(TM) Client VM (build 25.144-b01, mixed mode)
```

## 2 安装Tomcat

​	软件包版本 `apache-tomcat-8.0.46.tar.gz` , 并将文件上传至 linux 的 `/usr/local/src` 目录下

##### 1 进入安装包目录并解压压缩包

```Sh
[root@centos-6 src]# cd /usr/local/src/
[root@centos-6 src]# tar -xzf apache-tomcat-8.0.46.tar.gz 
[root@centos-6 src]# ls
apache-tomcat-8.0.46         jdk-8u144-linux-i586.tar.gz             zookeeper-3.4.10.tar.gz
apache-tomcat-8.0.46.tar.gz  mysql-5.7.19-1.el6.i686.rpm-bundle.tar
java                         redis-4.0.1.tar.gz
```

##### 2 重命名解压包为 tomcat

```sh
[root@centos-6 src]# mv apache-tomcat-8.0.46 tomcat
[root@centos-6 src]# ls
apache-tomcat-8.0.46.tar.gz  jdk-8u144-linux-i586.tar.gz             redis-4.0.1.tar.gz  zookeeper-3.4.10.tar.gz
java                         mysql-5.7.19-1.el6.i686.rpm-bundle.tar  tomcat
```

##### 3 修改防火墙规则,开放8080端口

​	**centos7 下:**

```sh
# 开放端口
firewall-cmd --zone=public --add-port=8080/tcp --permanent
# 重启防火墙
systemctl restart firewalld.service
```

​	**centos 6.5**

1. 添加放行规则并保存,然后重启防火墙

```Sh
[root@centos-6 tomcat]# /sbin/iptables -I INPUT -p tcp --dport 8080 -j ACCEPT 
[root@centos-6 tomcat]# /etc/rc.d/init.d/iptables save 
iptables：将防火墙规则保存到 /etc/sysconfig/iptables：     [确定]
[root@centos-6 tomcat]# /etc/rc.d/init.d/iptables restart 
iptables：将链设置为政策 ACCEPT：filter                    [确定]
iptables：清除防火墙规则：                                 [确定]
iptables：正在卸载模块：                                   [确定]
iptables：应用防火墙规则：                                 [确定]
```

> 查看80端口占用情况的方法:
>
> 1. `lsof -i :80`
> 2. `netstat -anp | grep 80`

2. 查看防火墙的放行状态

```Sh
[root@centos-6 tomcat]# /etc/init.d/iptables status
表格：filter
Chain INPUT (policy ACCEPT)
num  target     prot opt source               destination         
1    ACCEPT     tcp  --  0.0.0.0/0            0.0.0.0/0           tcp dpt:8000 
2    ACCEPT     tcp  --  0.0.0.0/0            0.0.0.0/0           tcp dpt:8080 
3    ACCEPT     all  --  0.0.0.0/0            0.0.0.0/0           state RELATED,ESTABLISHED 
4    ACCEPT     icmp --  0.0.0.0/0            0.0.0.0/0           
5    ACCEPT     all  --  0.0.0.0/0            0.0.0.0/0           
6    ACCEPT     tcp  --  0.0.0.0/0            0.0.0.0/0           state NEW tcp dpt:22 
7    REJECT     all  --  0.0.0.0/0            0.0.0.0/0           reject-with icmp-host-prohibited 

Chain FORWARD (policy ACCEPT)
num  target     prot opt source               destination         
1    REJECT     all  --  0.0.0.0/0            0.0.0.0/0           reject-with icmp-host-prohibited 

Chain OUTPUT (policy ACCEPT)
num  target     prot opt source               destination   
```

> iptables防火墙的配置文件存放于：/etc/sysconfig/iptables 

3. 启动并访问tomcat

```Sh
[root@centos-6 tomcat]# cd /usr/local/src/tomcat/bin  		#进入tomcat执行文件所在的目录
[root@centos-6 bin]# ./startup.sh 						   # 启动tomcat
Using CATALINA_BASE:   /usr/local/src/tomcat
Using CATALINA_HOME:   /usr/local/src/tomcat
Using CATALINA_TMPDIR: /usr/local/src/tomcat/temp
Using JRE_HOME:        /usr/local/src/java
Using CLASSPATH:       /usr/local/src/tomcat/bin/bootstrap.jar:/usr/local/src/tomcat/bin/tomcat-juli.jar
Tomcat started.
[root@centos-6 bin]# ifconfig 							# 查看linux的ip地址
eth0      Link encap:Ethernet  HWaddr 00:1C:42:54:56:C6  
          inet addr:10.211.55.8  Bcast:10.211.55.255  Mask:255.255.255.0
```

> 这个时候在浏览器输入 `http://10.211.55.8:8080` 即可访问到tomcat首页

##### 4 将tomcat配置到环境变量(可选)

​	将tomcat路径配置到环境变量,只是为了方便我们在任意目录下执行 startup.sh  shutdown.sh 命令来启动和关闭tomcat , 如果不配置,直接进入执行文件所在目录在执行也可以,比如前面演示的

1. 打开 /etc/profile 文件

```sh
[root@centos-6 src]# vim /etc/profile
```

> 文件打开后,按 `i` 进入编辑模式

2. 在文件末尾添加如下内容,保存并退出

```sh
export JAVA_HOME=/usr/local/src/java
export TOMCAT_HOME=/usr/local/src/tomcat
export PATH=$JAVA_HOME/bin:$TOMCAT_HOME/bin:$PATH
```

> 1. `JAVA_HOME` 是配置jdk环境变量用的
>
>
> 2. 内容添加完成后,按 `esc` 退出编辑模式 , 然后按住 `shift` + 双击 `z` 进行保存并退出!! 

3. 重新载入配置文件并验证

```Sh
[root@centos-6 /]# source /etc/profile
[root@centos-6 /]# startup.sh		# 启动tomcat
Using CATALINA_BASE:   /usr/local/src/tomcat
Using CATALINA_HOME:   /usr/local/src/tomcat
Using CATALINA_TMPDIR: /usr/local/src/tomcat/temp
Using JRE_HOME:        /usr/local/src/java
Using CLASSPATH:       /usr/local/src/tomcat/bin/bootstrap.jar:/usr/local/src/tomcat/bin/tomcat-juli.jar
Tomcat started.
[root@centos-6 /]# shutdown.sh     # 关闭tomcat
Using CATALINA_BASE:   /usr/local/src/tomcat
Using CATALINA_HOME:   /usr/local/src/tomcat
Using CATALINA_TMPDIR: /usr/local/src/tomcat/temp
Using JRE_HOME:        /usr/local/src/java
Using CLASSPATH:       /usr/local/src/tomcat/bin/bootstrap.jar:/usr/local/src/tomcat/bin/tomcat-juli.jar
```

##### 5 配置tomcat应用管理账户(可选)

​	进入tomcat配置文件夹  `/usr/local/src/tomcat/conf` 将 `tomcat-users.xml` 的内容修改如下:

```Html
<?xml version="1.0" encoding="UTF-8"?>
<tomcat-users version="1.0" xmlns="http://tomcat.apache.org/xml" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://tomcat.apache.org/xml tomcat-users.xsd">
	<role rolename="manager-gui"/>
	<user username="admin" password="admin" roles="manager-gui"/>
</tomcat-users>
```

> 此时在浏览器中输入地址 `http://10.211.55.8:8080/manager/html` , 在弹出的对话框中使用用户名 : `admin`  密码 : `admin`  登录,即可查看tomcat上所运行的所有应用,也可以直接通过网页部署war包到tomcat

##### 6 配置开机启动(可选)

​	编辑  `/etc/rc.d/rc.local`  文件 ,在里边添加 tomcat 的启动命令 , 如下:

```properties
### 开机启动 tomcat ####
/usr/local/src/tomcat/bin/startup.sh
```

## 3 安装MySQL

​	软件包版本 `mysql-5.7.19-1.el6.i686.rpm-bundle.tar`  , 并将文件上传值 linux 的 `/usr/local/src` 目录下

##### 1 检查已安装版本并卸载

```Sh
[root@centos-6 src]# rpm -qa | grep -i mysql		# 检查已安装版本
mysql-connector-odbc-5.1.5r1144-7.el6.i686
mysql-libs-5.1.73-5.el6_6.i686
mysql-5.1.73-5.el6_6.i686
MySQL-python-1.2.3-0.3.c1.1.el6.i686
[root@centos-6 src]# rpm -e --nodeps mysql-5.1.73-5.el6_6.i686			#卸载
[root@centos-6 src]# rpm -e --nodeps mysql-libs-5.1.73-5.el6_6.i686		 #卸载
[root@centos-6 src]# rpm -qa | grep -i mysql
mysql-connector-odbc-5.1.5r1144-7.el6.i686
MySQL-python-1.2.3-0.3.c1.1.el6.i686
```

##### 2 进入安装包目录并解压压缩包sr	

```shell
[root@centos-6 src]# cd /usr/local/src
[root@centos-6 src]# tar -xf mysql-5.7.19-1.el6.i686.rpm-bundle.tar 
[root@centos-6 src]# ll
总用量 1108064
-rw-r--r--. 1 root root  450181120 9月   1 2017 mysql-5.7.19-1.el6.i686.rpm-bundle.tar
############### 删除了部分无关的信息 ###############
-rw-r--r--. 1 7155 31415  23109988 6月  24 20:08 mysql-community-client-5.7.19-1.el6.i686.rpm
-rw-r--r--. 1 7155 31415    336296 6月  24 20:08 mysql-community-common-5.7.19-1.el6.i686.rpm

-rw-r--r--. 1 7155 31415   2104540 6月  24 20:09 mysql-community-libs-5.7.19-1.el6.i686.rpm
-rw-r--r--. 1 7155 31415 156964052 6月  24 20:09 mysql-community-server-5.7.19-1.el6.i686.rpm
```

> 1 为了避免依赖冲突,在上一步已经将mysql相关的依赖和包给卸载了
>
> 2 上面显示的4个rpm包分别是两个依赖包 , 客户端 , 服务端

##### 3 安装依赖库

```sh
[root@centos-6 src]# rpm -ivh mysql-community-common-5.7.19-1.el6.i686.rpm 		# 安装依赖
warning: mysql-community-common-5.7.19-1.el6.i686.rpm: Header V3 DSA/SHA1 Signature, key ID 5072e1f5: NOKEY
Preparing...                ########################################### [100%]
   1:mysql-community-common ########################################### [100%]
[root@centos-6 src]# rpm -ivh mysql-community-libs-5.7.19-1.el6.i686.rpm 		# 安装依赖
warning: mysql-community-libs-5.7.19-1.el6.i686.rpm: Header V3 DSA/SHA1 Signature, key ID 5072e1f5: NOKEY
Preparing...                ########################################### [100%]
   1:mysql-community-libs   ########################################### [100%]
```

> 依赖的安装顺序不对会报错!!!

##### 4 安装MySQL客户端

```Sh
[root@centos-6 src]# rpm -ivh mysql-community-client-5.7.19-1.el6.i686.rpm 
warning: mysql-community-client-5.7.19-1.el6.i686.rpm: Header V3 DSA/SHA1 Signature, key ID 5072e1f5: NOKEY
Preparing...                ########################################### [100%]
   1:mysql-community-client ########################################### [100%]
```

> 安装服务端时需要依赖客户端,所以这里先安装mysql客户端  ZZZ

##### 5 安装MySQL服务端

```sh
[root@centos-6 src]# rpm -ivh mysql-community-server-5.7.19-1.el6.i686.rpm 
warning: mysql-community-server-5.7.19-1.el6.i686.rpm: Header V3 DSA/SHA1 Signature, key ID 5072e1f5: NOKEY
Preparing...                ########################################### [100%]
   1:mysql-community-server ########################################### [100%]
```

> 如果出现依赖报错 :  	libsasl2.so.2()(64bit) is needed by mysql-community-server-5.7.19-1.el6.x86_64
>
> 可以强制忽略依赖试试 :
>
> rpm -ivh mysql-community-server-5.7.19-1.el6.i686.rpm  --force --nodeps

##### 6 启动MySQL服务

```Sh
[root@centos-6 src]# service mysqld start
正在启动 mysqld：                                          [确定]
```

> 1. servMySQL 5.6 版本时服务的名称叫 `mysql` , 但是在 5.7 版本时服务改名为 `mysqld` !!!
> 2. MySQL 5.6 版本的启动命令为 `service mysql start`

##### 7 查看MySQL的初始密码

```sh
[root@localhost my_soft]# grep 'password' /var/log/mysqld.log   ## 搜索临时密码
2018-01-24T09:10:39.025038Z 1 [Note] A temporary password is generated for root@localhost: QlJsoqv,!6l-
```

```javascript
[root@centos-6 src]# cat /var/log/mysqld.log 
############### 删除了部分无关的信息 ###############
2017-09-02T07:56:37.832758Z 1 [Note] A temporary password is generated for root@localhost: UQt7,gdR2Q<*
```

> 从输出信息中可以看到 , 临时密码是 : `UQt7,gdR2Q<*`

> 1. MySQL 5.5 版本之前没有默认密码,root用户可以直接登录
> 2. MySQL 5.6 版本初始密码存放在 `/root/.mysql_secret` 文件中
> 3. MySQL 5.7 版本初始密码存放在 `/var/log/mysqld.log` 文件中

##### 8 登录MySQL

```Sh
[root@centos-6 src]# mysql -uroot -p
Enter password: 			###### 在这里输入上一步查到的临时密码 ##### 
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 6
Server version: 5.7.19
```

##### 9 MySQL相关设置

​	所有设置都是登录mysql后进行的!!!

###### 1 修改root用户密码

​	[mysql密码校验策略](https://www.cnblogs.com/ivictor/p/5142809.html)

```mysql
mysql> set password=password('123456');
Query OK, 0 rows affected, 1 warning (0.00 sec)
```

> 1 将root用户密码修改为 `123456`
>
> 2 也可用此命令修改 : `alter user 'root'@'localhost' identified by '123456'` root用户的密码为`123456`
>
> PS : 在 centOS7 上,默认不允许修改简单的密码 , 必须先修改密码的策略为 0 , 方可修改,密令如下:
>
> `set global validate_password_policy=0;`
>
> `set global validate_password_length=4;`   

​	查看mysql的字符编码集

```Mysql
mysql> show variables like '%char%';
+--------------------------+----------------------------+
| Variable_name            | Value                      |
+--------------------------+----------------------------+
| character_set_client     | utf8                       |
| character_set_connection | utf8                       |
| character_set_database   | latin1                     |
| character_set_filesystem | binary                     |
| character_set_results    | utf8                       |
| character_set_server     | latin1                     |
| character_set_system     | utf8                       |
| character_sets_dir       | /usr/share/mysql/charsets/ |
+--------------------------+----------------------------+
8 rows in set (0.01 sec)
```

> `character_set_server     | latin1  `   说明服务端的编码集为 latin1 , 等下我们将其修改为 utf-8

###### 2 允许root用户远程登录数据库

​	[无法远程登录解决办法](http://www.jb51.net/article/124228.htm)

1. 登录数据库,授权root用户远程登录

```Mysql
mysql> GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '123456' WITH GRANT OPTION;
mysql> flush privileges;   # 刷入权限
Query OK, 0 rows affected (0.00 sec)
```

> 1. `*.*`  第一个 `*` 代表所有的数据库 , 第二个 `*` 代表对应数据库下的所有表 , 即授权访问MySQL数据库上所有数据库所有的表
> 2. 'root'@'%'     `root` 是用户名, `%` 是主机名或IP地址,这里的 `%` 代表任意主机或IP地址,也可以将 `%` 替换成指定的ip地址(如 192.168.88.88) , 这样的话 root 用户就只能在指定的主机上进行远程访问了 
> 3. 移除权限 `REVOKE all on *.* from root;`

2. 退出数据库,设置防火墙拦截规则 , 放行 3306 端口

   **centos 6.5 使用 iptables 的放行方式如下 :**

```Mysql
mysql> exit   # 退出数据库
Bye
```

```Sh
[root@centos-6 src]# /sbin/iptables -I INPUT -p tcp --dport 3306 -j ACCEPT   # 放行3306端口
[root@centos-6 src]# /etc/init.d/iptables save								# 保存规则到数据库
iptables：将防火墙规则保存到 /etc/sysconfig/iptables：     [确定]
[root@centos-6 src]# service iptables restart								# 重启防火墙
iptables：将链设置为政策 ACCEPT：filter                    [确定]
iptables：清除防火墙规则：                                 [确定]
iptables：正在卸载模块：                                   [确定]
iptables：应用防火墙规则：                                 [确定]
```

```sh
[root@centos-6 src]# /etc/init.d/iptables status      # 查看防火墙的状态
表格：filter
Chain INPUT (policy ACCEPT)
num  target     prot opt source               destination         
1    ACCEPT     tcp  --  0.0.0.0/0            0.0.0.0/0           tcp dpt:3306    # 放行3306
2    ACCEPT     tcp  --  0.0.0.0/0            0.0.0.0/0           tcp dpt:8000 
3    ACCEPT     tcp  --  0.0.0.0/0            0.0.0.0/0           tcp dpt:8080 
```

> iptables防火墙的配置文件存放于：/etc/sysconfig/iptables 

​	**centos 7 的放行方式如下 :**

​	默认情况下 centOS 7 使用的时 FirewallD 防火墙 , 放行方式如下 :

```sh
# 开放端口
firewall-cmd --zone=public --add-port=3306/tcp --permanent
# 重启防火墙
systemctl restart firewalld.service
```

> --zone     作用域
>
> --add-port=3306/tcp     添加端口 , 格式为 : 端口/通讯协议
>
> --permanent    永久生效,没有此参数重启后失效

###### 3 修改字符编码集

1. 查找mysql的配置文件

```sh
[root@centos-6 src]# find / -name *my.cnf*
/etc/my.cnf.d
/etc/my.cnf
```

> 1. 5.6版本配置文件路径为 `/usr/my.cnf`
> 2. 5.7版本配置文件路径为 `/etc/my.cnf`

2. 编辑配置文件 , 添加如下内容

```ini
[client]
default-character-set=utf8
[mysqld]
character-set-server=utf8
```

> 1. `[client]`  表明是对客户端进行设置
> 2. `[mysqld]`  表明是对服务端进行设置
> 3. 如果配置文件中已经存在 `[client]`  `[mysqld]` ,则只添加对应的设置内容即可,不存在就按照上面的进行设置

3. 重启MySQL服务即可 `service mysqld restart`

###### 4 设置MySQL服务开机自启动

```sh
[root@centos-6 src]# chkconfig mysqld on			# 设置自启动
[root@centos-6 src]# chkconfig | grep mysqld		# 查看开机启动设置
mysqld          0:关闭  1:关闭  2:启用  3:启用  4:启用  5:启用  6:关闭
```

> 1 如果要指定启动级别则使用 `chkconfig --level 135 mysqld on` 指定在运行级别为1或3或5时自启动

## 4 安装Redis数据库

​	软件包版本  `redis-4.0.1.tar.gz`  , 并将文件上传值 linux 的 `/usr/local/src` 目录下

##### 1 安装gcc编译器

```sh
[root@centos-6 src]# yum install gcc-c++
已加载插件：fastestmirror, security
设置安装进程
######..... 省略一堆信息 .....######
事务概要
=====================================================================================================================
Install       7 Package(s)
Upgrade       3 Package(s)

总下载量：19 M
确定吗？[y/N]：y      # 在这里输入 y 确认下载并安装gcc
下载软件包：
(1/10): cloog-ppl-0.15.7-1.2.el6.i686.rpm                                                     |  
######..... 省略一堆信息 .....######
 Userid : CentOS-6 Key (CentOS 6 Official Signing Key) <centos-6-key@centos.org>
 Package: centos-release-6-7.el6.centos.12.3.i686 (@anaconda-CentOS-201508042139.i386/6.7)
 From   : /etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-6
确定吗？[y/N]：y      # 在这里输入 y 确认
```

##### 2 进入软件所在目录并解压

```Sh
[root@centos-6 src]# cd /usr/local/src
[root@centos-6 src]# tar -zxf redis-4.0.1.tar.gz 
[root@centos-6 src]# ls
apache-tomcat-8.0.46.tar.gz  jdk-8u144-linux-i586.tar.gz  redis-4.0.1.tar.gz  zookeeper-3.4.10.tar.gz
java                         redis-4.0.1                  tomcat
```

##### 3 进入解压目录,并编译源码

```Sh
[root@centos-6 src]# cd /usr/local/src/redis-4.0.1
[root@centos-6 redis-4.0.1]# make
```

##### 4 安装redis

```sh
[root@centos-6 redis-4.0.1]# make PREFIX=/usr/local/src/redis install
cd src && make install
make[1]: Entering directory `/usr/local/src/redis-4.0.1/src'
    CC Makefile.dep
make[1]: Leaving directory `/usr/local/src/redis-4.0.1/src'
make[1]: Entering directory `/usr/local/src/redis-4.0.1/src'

Hint: It's a good idea to run 'make test' ;)

    INSTALL install
    INSTALL install
    INSTALL install
    INSTALL install
    INSTALL install
make[1]: Leaving directory `/usr/local/src/redis-4.0.1/src'
```

> `PREFIX=/usr/local/src/redis` 指定安装目录为  `/usr/local/src/redis` , 目录不存在会自动创建

##### 5 复制配置文件到redis安装目录下

```sh
[root@centos-6 bin]# cp /usr/local/src/redis-4.0.1/redis.conf /usr/local/src/redis
[root@centos-6 bin]# ls
redis-benchmark  redis-check-aof  redis-check-rdb  redis-cli  redis-sentinel  redis-server
[root@centos-6 bin]# cd /usr/local/src/redis
[root@centos-6 redis]# ls
bin  redis.conf
```

##### 6 修改配置文件,设置redis后台运行

```sh
[root@centos-6 redis]# vim /usr/local/src/redis/redis.conf 
```

> 1 打开vim后,在非编辑模式下 输入 `/daem` 然后回车,可以搜索 `daem` 开头的字符串,可以快速找到配置项

```properties
daemonize yes
```

> 默认 `daemonize` 的只是 `no` , 这里改为 `yes` 然后保存退出即可

##### 7 运行redis

```sh
[root@centos-6 redis]# ./bin/redis-server ./redis.conf 
9108:C 02 Sep 18:40:30.284 # oO0OoO0OoO0Oo Redis is starting oO0OoO0OoO0Oo
9108:C 02 Sep 18:40:30.284 # Redis version=4.0.1, bits=32, commit=00000000, modified=0, pid=9108, just started
9108:C 02 Sep 18:40:30.284 # Configuration loaded
```

​	查看redis的运行状态

```sh
[root@centos-6 redis]# ps -ef |grep -i redis
root      9109     1  0 18:40 ?        00:00:00 ./bin/redis-server 127.0.0.1:6379
root      9116  1935  0 18:41 pts/0    00:00:00 grep -i redis
```

> `-i`  忽略大小写

##### 8 修改防火墙拦截规则,放行6379端口

​	**centos7 下:**

```sh
# 开放端口
firewall-cmd --zone=public --add-port=6379/tcp --permanent
# 重启防火墙
systemctl restart firewalld.service
```

​	**centos 6.5**

```sh
[root@centos-6 redis]# /sbin/iptables -I INPUT -p tcp --dport 6379 -j ACCEPT
[root@centos-6 redis]# /etc/rc.d/init.d/iptables save
[root@centos-6 redis]# service iptables restart
iptables：将链设置为政策 ACCEPT：filter                    [确定]
iptables：清除防火墙规则：                                 [确定]
iptables：正在卸载模块：                                   [确定]
iptables：应用防火墙规则：                                 [确定]
```

> iptables防火墙的配置文件存放于：/etc/sysconfig/iptables 

##### 9 客户端连接并测试

```mysql
[root@centos-6 redis]# ./bin/redis-cli 
127.0.0.1:6379> ping
PONG
```

> 输入 `ping` 返回 `pong` ,即测试成功

##### 10 修改配置文件,允许外部计算机登录

​	redis默认配置绑定的是本地主机的ip,也就是说只能在安装redis的主机上连接,如果想要通过远程连接(比如通过jedis连接),需要修改配置文件 .  步骤如下 :

> 本人的redis的配置文件所在路径为 /usr/local/src/redis/redis.conf

1. 注释掉配置文件中有关绑定本地主机ip地址的配置项   `bind 127.0.0.1`

> 该配置项位于配置文件的第69行

2. 关闭保护模式 即配置为  `protected-mode no`

> 该配置项位于配置文件的第88行

3. 重启 redis 服务



## 5 安装Zookeeper

​	版本         `zookeeper-3.4.10.tar.gz` , 文件上传至 `/usr/local/src` 目录下

##### 1 进入目录解压文件

```Sh
[root@centos-6 src]# cd /usr/local/src/
[root@centos-6 src]# tar -zxf zookeeper-3.4.10.tar.gz 
[root@centos-6 src]# ls
apache-tomcat-8.0.46.tar.gz  redis               tomcat
java                         redis-4.0.1         zookeeper-3.4.10
jdk-8u144-linux-i586.tar.gz  redis-4.0.1.tar.gz  zookeeper-3.4.10.tar.gz
```

##### 2 进入解压目录,创建文件夹 data

```Sh
[root@centos-6 src]# cd zookeeper-3.4.10
[root@centos-6 zookeeper-3.4.10]# mkdir data
```

##### 3 进入解压目录下的conf文件夹,复制 zoo_sample.cfg 并重命名为 zoo.cfg

```Sh
[root@centos-6 zookeeper-3.4.10]# cd conf
[root@centos-6 conf]# ls
configuration.xsl  log4j.properties  zoo_sample.cfg
[root@centos-6 conf]# cp zoo_sample.cfg zoo.cfg
```

##### 4 编辑zoo.cfg文件,修改zookeeper的工作目录

```properties
dataDir=/usr/local/src/zookeeper-3.4.10/data
```

##### 5 设置防火墙拦截规则,放行2181端口

​	**centos7 下:**

```sh
# 开放端口
firewall-cmd --zone=public --add-port=2181/tcp --permanent
# 重启防火墙
systemctl restart firewalld.service
```
​	**centos 6.5**

```Sh
[root@centos-6 redis]# /sbin/iptables -I INPUT -p tcp --dport 2181 -j ACCEPT
[root@centos-6 redis]# /etc/rc.d/init.d/iptables save
iptabels: 未被识别的服务
[root@centos-6 redis]# service iptables restart
iptables：将链设置为政策 ACCEPT：filter                    [确定]
iptables：清除防火墙规则：                                 [确定]
iptables：正在卸载模块：                                   [确定]
iptables：应用防火墙规则：                                 [确定]
```

> 1. `/etc/init.d/iptables status` 可以查看拦截规则状态
> 2. iptables防火墙的配置文件存放于：/etc/sysconfig/iptables 

##### 6 进入解压目录下的bin文件夹,运行zookeeper

```Sh
[root@centos-6 bin]# cd /usr/local/src/zookeeper-3.4.10/bin
[root@centos-6 bin]# ls
README.txt  zkCleanup.sh  zkCli.cmd  zkCli.sh  zkEnv.cmd  zkEnv.sh  zkServer.cmd  zkServer.sh
[root@centos-6 bin]# ./zkServer.sh start		# 启动
ZooKeeper JMX enabled by default
Using config: /usr/local/src/zookeeper-3.4.10/bin/../conf/zoo.cfg
Starting zookeeper ... STARTED
[root@centos-6 bin]# ./zkServer.sh status		# 查看运行状态
ZooKeeper JMX enabled by default
Using config: /usr/local/src/zookeeper-3.4.10/bin/../conf/zoo.cfg
Mode: standalone
[root@centos-6 bin]# ./zkServer.sh stop			# 停止
ZooKeeper JMX enabled by default
Using config: /usr/local/src/zookeeper-3.4.10/bin/../conf/zoo.cfg
Stopping zookeeper ... STOPPED
```

##### 7 设置开机启动

​	编辑    `/etc/rc.d/rc.local`  文件 ,在里边添加 tomcat 的启动命令 , 如下:

```properties
/usr/local/src/zookeeper-3.4.10/bin/zkServer.sh start
```

## 6 安装nginx

[centos7 安装nginx的两种方式](http://www.jb51.net/article/107966.htm)

#### 1 yum 在线安装

1. 添加 Nginx 仓库的yum 配置

   1. 编辑配置文件

      ```sh
      [root@centos-6 src]# vim /etc/yum.repos.d/nginx.repo
      ```

   2. 在配置文件中添加如下内容

      ```Properties
      [nginx]
      name=nginx repo
      baseurl=http://nginx.org/packages/centos/6/$basearch/
      gpgcheck=0
      enabled=1
      ```

2. 安装

```sh
yum  install --installroot=/usr/local/src/ngix  ngix  # 指定安装路径
```

```sh
[root@centos-6 src]# yum install nginx
已加载插件：fastestmirror, security
设置安装进程
解决依赖关系
--> 执行事务检查
---> Package nginx.i386 0:1.12.1-1.el6.ngx will be 安装
--> 完成依赖关系计算
############ 省略一吨信息 ##############
Install       1 Package(s)
总下载量：966 k
Installed size: 2.4 M
确定吗？[y/N]：      								# 在这里输入y确定
############ 省略一吨信息 ##############
已安装:
  nginx.i386 0:1.12.1-1.el6.ngx   
```

#### 2 通过源码包安装

​	版本           ` nginx-1.12.1.tar.gz`  , 文件上传至 `/usr/local/src` 目录下

##### 1 安装gcc编译器

```Sh
[root@centos-6 src]# yum install gcc-c++
已加载插件：fastestmirror, security
设置安装进程
######..... 省略一堆信息 .....######
事务概要
=====================================================================================================================
Install       7 Package(s)
Upgrade       3 Package(s)

总下载量：19 M
确定吗？[y/N]：y      # 在这里输入 y 确认下载并安装gcc
下载软件包：
(1/10): cloog-ppl-0.15.7-1.2.el6.i686.rpm                                                     |  
######..... 省略一堆信息 .....######
 Userid : CentOS-6 Key (CentOS 6 Official Signing Key) <centos-6-key@centos.org>
 Package: centos-release-6-7.el6.centos.12.3.i686 (@anaconda-CentOS-201508042139.i386/6.7)
 From   : /etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-6
确定吗？[y/N]：y      # 在这里输入 y 确认
```

##### 2 进入软件所在目录并解压

```Sh
[root@centos-6 src]# tar -zxf nginx-1.12.1.tar.gz 
[root@centos-6 src]# ls
apache-tomcat-8.0.46.tar.gz  nginx-1.12.1         redis-4.0.1         zookeeper-3.4.10
java                         nginx-1.12.1.tar.gz  redis-4.0.1.tar.gz  zookeeper-3.4.10.tar.gz
```

##### 3 安装OpenSSL和PCRE依赖

```sh
[root@centos-6 nginx-1.12.1]# yum -y install pcre-devel
[root@centos-6 nginx-1.12.1]# yum -y install openssl openssl-devel
```

> OpenSSL 是启用SSL支持所需的依赖
>
> PCRE 是使用 nginx 的 rewrite 模块所需要的依赖

##### 4 进入解压目录,执行配置命令

```Sh
[root@centos-6 nginx-1.12.1]# ./configure 
checking for OS
 + Linux 2.6.32-573.el6.i686 i686
checking for C compiler ... found
 + using GNU C compiler
 + gcc version: 4.4.7 20120313 (Red Hat 4.4.7-18) (GCC) 
checking for gcc -pipe switch ... found
############### 省略一吨信息 ############
Configuration summary
  + using system PCRE library
  + OpenSSL library is not used
  + using system zlib library

  nginx path prefix: "/usr/local/nginx"
  nginx binary file: "/usr/local/nginx/sbin/nginx"
  nginx modules path: "/usr/local/nginx/modules"
  nginx configuration prefix: "/usr/local/nginx/conf"
  nginx configuration file: "/usr/local/nginx/conf/nginx.conf"
  nginx pid file: "/usr/local/nginx/logs/nginx.pid"
  nginx error log file: "/usr/local/nginx/logs/error.log"
  nginx http access log file: "/usr/local/nginx/logs/access.log"
  nginx http client request body temporary files: "client_body_temp"
  nginx http proxy temporary files: "proxy_temp"
  nginx http fastcgi temporary files: "fastcgi_temp"
  nginx http uwsgi temporary files: "uwsgi_temp"
  nginx http scgi temporary files: "scgi_temp"
```

> 1. 从日志 `nginx path prefix: "/usr/local/nginx"` 可看出 nginx 默认会被安装到 /usr/local/nginx 目录下
> 2. 如果要指定安装路径 , 则要使用如下命令 : `./configure --prefix=/usr/local/src/nginx` ,则会安装在 /usr/local/src/nginx 目录下
> 3. `/usr/local/nginx/sbin/nginx -V`   命令可查看添加的哪些第三方模块

##### 4 编译源码

```sh
[root@centos-6 nginx-1.12.1]# make
```

##### 5 安装nginx

```sh
[root@centos-6 nginx-1.12.1]# make install
```

​	进入安装目录查看,文件

```sh
[root@centos-6 nginx]# cd /usr/local/nginx && ll
总用量 16
drwxr-xr-x. 2 root root 4096 9月   3 06:29 conf
drwxr-xr-x. 2 root root 4096 9月   3 06:29 html
drwxr-xr-x. 2 root root 4096 9月   3 06:29 logs
drwxr-xr-x. 2 root root 4096 9月   3 06:30 sbin
```

##### 6 修改防火墙规则放行80端口

​	**centos7 下:**

```sh
# 开放端口
firewall-cmd --zone=public --add-port=80/tcp --permanent
# 重启防火墙
systemctl restart firewalld.service
```

​	**centos 6.5**

```Sh
[root@centos-6 tomcat]# /sbin/iptables -I INPUT -p tcp --dport 80 -j ACCEPT 
[root@centos-6 tomcat]# /etc/rc.d/init.d/iptables save 
iptables：将防火墙规则保存到 /etc/sysconfig/iptables：     [确定]
[root@centos-6 tomcat]# /etc/rc.d/init.d/iptables restart 
```

> iptables防火墙的配置文件存放于：/etc/sysconfig/iptables 
>
> 查看80端口占用情况的方法:
>
> 1. `lsof -i :80`
> 2. `netstat -anp | grep 80`

##### 7 设置开机启动nginx

​	编辑    `/etc/rc.d/rc.local`  文件 ,在里边添加 tomcat 的启动命令 , 如下:

```
### 开机启动 nginx ####
/usr/local/nginx/sbin/nginx
```

##### 8 nginx 常用命令

1. 启动						`/usr/local/nginx/sbin/ngix`

  2. 查看nginx进程		`ps -ef | grep ngnix`

2. 测试配置文件是否正确

    ```sh
    [root@centos-6 sbin]# pwd			# 查看当前目录
    /usr/local/nginx/sbin
    [root@centos-6 sbin]# /usr/local/nginx/sbin/nginx -t
    nginx: the configuration file /usr/local/nginx/conf/nginx.conf syntax is ok
    nginx: configuration file /usr/local/nginx/conf/nginx.conf test is successful
    ```

    > 1. 当修改配置文件后,最好执行检查命令,确认无误后再重启ngnix
    > 2. 也可使用该命令  `./nginx -t -c  /usr/local/nginx/conf/nginx.conf`      



4. 重启

```
 [root@centos-6 sbin]# /usr/local/nginx/sbin/nginx -s reload
```




## 7 安装FastDFS

​	FastDFS是一个开源的轻量级[分布式文件系统](https://baike.so.com/doc/6591749-6805528.html)，它对[文件](https://baike.so.com/doc/3623678-3809447.html)进行管理，[功能](https://baike.so.com/doc/5380785-5617067.html)包括:文件存储、文件同步、文件访问(文件上传、文件下载)等，解决了大容量存储和负载均衡的问题。特别适合以文件为载体的在线服务，如相册网站、视频网站等等。

​	[github地址](https://github.com/happyfish100/)

​	[论坛地址](http://bbs.chinaunix.net/forum-240-1.html)

​	[学习资料](http://blog.csdn.net/poechant/article/details/6996047)

​	使用的安装包已上传至  `/usr/local/src`  目录下,具体版本 如下 : 

​	  `fastdfs-5.11.tar.gz`  

##### 1 安装编译器gcc

```Sh
[root@centos-6 ~]# yum install gcc-c++
```

##### 2 安装依赖库libevent

```Sh
[root@centos-6 ~]# yum -y install libevent
```

> V5.0以前的版本依赖libevent；V5.0以后，不再依赖libevent

##### 3 安装libfastcommon

​	 v5.04开始依赖libfastcommon，github地址：<https://github.com/happyfish100/libfastcommon>

​	源码托管在github上,我们可以先将源码下载到本地再上传至linux,也可以直接通过 git 克隆到 linux 指定位置 , 这里我们克隆到 /usr/local/src

       	1. 安装 git 客户端

```sh
[root@centos-6 libfastcommon]# yum -y install git
```

2. 克隆 libfastcommon 源码工程

```sh
[root@centos-6 src]# cd /usr/local/src/						# 切换目录
[root@centos-6 src]# git clone https://github.com/happyfish100/libfastcommon
Initialized empty Git repository in /usr/local/src/libfastcommon/.git/
remote: Counting objects: 2083, done.
remote: Total 2083 (delta 0), reused 0 (delta 0), pack-reused 2083
Receiving objects: 100% (2083/2083), 990.95 KiB | 573 KiB/s, done.
Resolving deltas: 100% (1488/1488), done.
```

3. 进入源码目录,编译并安装

```Sh
[root@centos-6 src]# cd libfastcommon/			
[root@centos-6 libfastcommon]# ls
doc  HISTORY  INSTALL  libfastcommon.spec  make.sh  php-fastcommon  README  src
[root@centos-6 libfastcommon]# ./make.sh 				# 编译
######............省略部分信息............######
[root@centos-6 libfastcommon]# ./make.sh install 		# 安装 
mkdir -p /usr/lib
mkdir -p /usr/lib
install -m 755 libfastcommon.so /usr/lib
install -m 755 libfastcommon.so /usr/lib
mkdir -p /usr/include/fastcommon
######............省略部分信息............######
[root@centos-6 libfastcommon]# ll /usr/lib/libfastcommon.so 	# 确定库文件被安装到 /usr/lib 下
-rwxr-xr-x. 1 root root 553855 9月   3 18:18 /usr/lib/libfastcommon.so
```

> 有些教程上说安装后 , libfastcommon.so  文件默认复制到 /usr/lib64 目录下 , 此时需要收到复制到 /usr/lib 目录下 , 跟我演示的由差异 , 具体原因不知,可能是版本差异,不纠结了...

##### 4 安装 FastDFS

1. 克隆项目到 /usr/local/src 目录下

```Sh
[root@centos-6 src]# git clone https://github.com/happyfish100/fastdfs.git

Initialized empty Git repository in /usr/local/src/fastdfs/.git/
remote: Counting objects: 915, done.
remote: Total 915 (delta 0), reused 0 (delta 0), pack-reused 915
Receiving objects: 100% (915/915), 680.84 KiB | 60 KiB/s, done.
Resolving deltas: 100% (603/603), done.

[root@centos-6 src]# ll fastdfs				# 查看项目结构
总用量 136
drwxr-xr-x. 3 root root  4096 9月   3 18:28 client
drwxr-xr-x. 2 root root  4096 9月   3 18:28 common
drwxr-xr-x. 2 root root  4096 9月   3 18:28 conf
-rw-r--r--. 1 root root 35067 9月   3 18:28 COPYING-3_0.txt
-rw-r--r--. 1 root root  3171 9月   3 18:28 fastdfs.spec
-rw-r--r--. 1 root root 33207 9月   3 18:28 HISTORY
drwxr-xr-x. 2 root root  4096 9月   3 18:28 init.d
-rw-r--r--. 1 root root  7755 9月   3 18:28 INSTALL
-rwxr-xr-x. 1 root root  5548 9月   3 18:28 make.sh
drwxr-xr-x. 2 root root  4096 9月   3 18:28 php_client
-rw-r--r--. 1 root root  2380 9月   3 18:28 README.md
-rwxr-xr-x. 1 root root  1768 9月   3 18:28 restart.sh
-rwxr-xr-x. 1 root root  1680 9月   3 18:28 stop.sh
drwxr-xr-x. 4 root root  4096 9月   3 18:28 storage					# storage
drwxr-xr-x. 2 root root  4096 9月   3 18:28 test
drwxr-xr-x. 2 root root  4096 9月   3 18:28 tracker					# tracker
```

2. 进入工程目录编译并安装

```sh
[root@centos-6 src]# cd fastdfs/
[root@centos-6 fastdfs]# ls
client  conf             fastdfs.spec  init.d   make.sh     README.md   stop.sh  test
common  COPYING-3_0.txt  HISTORY       INSTALL  php_client  restart.sh  storage  tracker
[root@centos-6 fastdfs]# ./make.sh 					# 编译

[root@centos-6 fastdfs]# ./make.sh install			# 安装
mkdir -p /usr/bin								# 
mkdir -p /etc/fdfs								# 配置文件存放目录
cp -f fdfs_trackerd /usr/bin
if [ ! -f /etc/fdfs/tracker.conf.sample ]; then cp -f ../conf/tracker.conf /etc/fdfs/tracker.conf.sample; fi
if [ ! -f /etc/fdfs/storage_ids.conf.sample ]; then cp -f ../conf/storage_ids.conf /etc/fdfs/storage_ids.conf.sample; fi
mkdir -p /usr/bin
mkdir -p /etc/fdfs
cp -f fdfs_storaged  /usr/bin
if [ ! -f /etc/fdfs/storage.conf.sample ]; then cp -f ../conf/storage.conf /etc/fdfs/storage.conf.sample; fi
mkdir -p /usr/bin
mkdir -p /etc/fdfs
mkdir -p /usr/lib64
mkdir -p /usr/lib
```

> 1. 这里执行安装命令后 , tracker服务 和 storage 服务都会被安装到当前系统上
> 2. 如果想让tracker服务 和 storage 服务 在不同的主机上运行,只需在另外一台主机上执行上述指令即可

##### 5 配置 track 服务

1. 复制源码工程目录下的conf 目录的文件到 /etc/fdfs

```sh
[root@centos-6 fastdfs]# cp /usr/local/src/fastdfs/conf/* /etc/fdfs
[root@centos-6 fastdfs]# ll /etc/fdfs/
总用量 108
-rw-r--r--. 1 root root 23981 9月   3 18:49 anti-steal.jpg
-rw-r--r--. 1 root root  1461 9月   3 18:49 client.conf
-rw-r--r--. 1 root root  1461 9月   3 18:37 client.conf.sample
-rw-r--r--. 1 root root   955 9月   3 18:49 http.conf
-rw-r--r--. 1 root root 31172 9月   3 18:49 mime.types
-rw-r--r--. 1 root root  7927 9月   3 18:49 storage.conf
-rw-r--r--. 1 root root  7927 9月   3 18:37 storage.conf.sample
-rw-r--r--. 1 root root   105 9月   3 18:49 storage_ids.conf
-rw-r--r--. 1 root root   105 9月   3 18:37 storage_ids.conf.sample
-rw-r--r--. 1 root root  7389 9月   3 18:49 tracker.conf
-rw-r--r--. 1 root root  7389 9月   3 18:37 tracker.conf.sample
```

2. 配置 tracker.conf

```Sh
[root@centos-6 fastdfs]# vim /etc/fdfs/tracker.conf
```

```properties
把
base_path=/home/yuqing/fastdfs
改为：
base_path=/home/fastdfs
-------------------------------------
修改 tracker_server 的ip地址(运行tracker服务主机的ip地址+端口号)
tracker_server=10.211.55.8:22122
```

> vim操作提示 : 
>
> 1. 在非插入状态下输入 `/base` 然后按 `回车` , 可搜索当前文档中包含  `base`  的字符串
> 2. 按回车后 , 按 `n` 可多次执行上一次的搜索命令

```Sh
[root@centos-6 fastdfs]# mkdir /home/fastdfs	# 创建目录
```

3. 启动 tracker 服务

```Sh
[root@centos-6 fastdfs]# /usr/bin/fdfs_trackerd /etc/fdfs/tracker.conf start
[root@centos-6 fastdfs]# ps -ef | grep fdfs		# 查看 tracker 的运行状态
root     20657     1  0 19:00 ?        00:00:00 /usr/bin/fdfs_trackerd /etc/fdfs/tracker.conf start			# 可以看到 tracker 服务正在运行
root     20665 19682  0 19:00 pts/1    00:00:00 grep track
```

> 1. 启动 : /usr/bin/fdfs_trackerd /etc/fdfs/tracker.conf 	start
> 2. 重启 : /usr/bin/fdfs_trackerd /etc/fdfs/tracker.conf       restart
> 3. 停止 : /usr/bin/fdfs_trackerd /etc/fdfs/tracker.conf start       stop
> 4. 停止 : killall fdfs_trackerd 

4. 配置 tracker 开机启动

   这里通过在 /etc/rc.d/rc.local 文件中 添加启动命令实现开机启动

```Sh
[root@centos-6 fastdfs]# vim /etc/rc.d/rc.local
```

​	在文件中添加如下内容,然后保存退出

```Js
#!/bin/sh
#
# This script will be executed *after* all the other init scripts.
# You can put your own initialization stuff in here if you don't
# want to do the full Sys V style init stuff.

touch /var/lock/subsys/local
/usr/bin/fdfs_trackerd /etc/fdfs/tracker.conf start     # 这行是添加的启动命令
```

##### 6 配置 storage 服务

1. 编辑 storage.conf

```sh
[root@centos-6 ~]# vim /etc/fdfs/storage.conf
```

> /etc/fdfs 目录下的所有文件都是从源码工程下的 conf 目录复制过来,具体操作参考前面的配置 tracker 的内容

​	修改内容如下:

```properties
base_path=/home/yuqing/fastdfs 
改为：
base_path=/home/fastdfs
----------------------------------------------
store_path0=/home/yuqing/fastdfs
改为：
store_path0=/home/fastdfs
#如果有多个挂载磁盘则定义多个store_path，如下
#store_path1=.....
#store_path2=......
----------------------------------------------
#配置tracker服务器:IP
tracker_server=10.221.55.8:22122   
```

> 1. tracker服务的IP地址是 安装 tracker 服务的主机的ip地址 , 端口号可以从 tracker.conf 中查看到 , 这里我安装 tracker服务的主机的ip地址是 : 10.221.55.8:22122 
> 2. base_path  和 store_path0 指定的路径可能不存在,需要手动创建 , 命令如下

```Sh
[root@centos-6 ~]# mkdir -p /home/fastdfs_storage/
```

2. 启动storage服务并查看进程

```sh
[root@centos-6 ~]# /usr/bin/fdfs_storaged /etc/fdfs/storage.conf start
[root@centos-6 ~]# ps -ef | grep fdfs
root      2247     1  0 12:16 ?        00:00:00 /usr/bin/fdfs_storaged /etc/fdfs/storage.conf start			# 可以看到 storage 服务正在运行
root      2249  2170  0 12:16 pts/0    00:00:00 grep storage
```

> 1. `/usr/bin/fdfs_storaged /etc/fdfs/storage.conf restart`   命令并不会终止 storage 服务 ,而是有新开一个进程 , 具体不知道是哪里设置问题还是就这样
> 2. 没有  `/usr/bin/fdfs_storaged /etc/fdfs/storage.conf stop`  命令 , 会提示找不到进程文件
> 3. 停止 storage 服务命令可用 : killall fdfs_storaged

3. 配置开机启动

```Sh
[root@centos-6 fastdfs]# vim /etc/rc.d/rc.local
```

​	在文件中添加如下内容,然后保存退出

```Sh
/usr/bin/fdfs_storaged /etc/fdfs/storage.conf start
```

##### 7 修改防火墙规则

​	**centos7 下:**

```sh
# 开放端口
firewall-cmd --zone=public --add-port=22122 /tcp --permanent
firewall-cmd --zone=public --add-port=23000/tcp --permanent
# 重启防火墙
systemctl restart firewalld.service
```

​	**centos 6.5**

```Sh
[root@centos-6 tomcat]# /sbin/iptables -I INPUT -p tcp --dport 22122 -j ACCEPT 
[root@centos-6 tomcat]# /sbin/iptables -I INPUT -p tcp --dport 23000 -j ACCEPT 
[root@centos-6 tomcat]# /etc/rc.d/init.d/iptables save 
iptables：将防火墙规则保存到 /etc/sysconfig/iptables：     [确定]
[root@centos-6 tomcat]# /etc/rc.d/init.d/iptables restart 
```

> 1. 这里需要放行两个端口 22122 和 23000
> 2. 22122 是fastdfs的默认端口
> 3. 23000 是fastdfs提供的[fastdfs_client]模块(java客户端) 使用的 socket 接口 , 如果不放行此端口,通过java代码连接会提示连接被拒绝!!!

##### 8 测试上传文件

​	FastDFS安装成功后通过 /usr/bin/fdfs_test 测试上传、下载等操作.

1. 修改客户端配置文件 client.conf

```sh
[root@centos-6 fdfs]# vim /etc/fdfs/client.conf
```

​	修改 `base_path`  和  `tracker_server `  两个变量的值

```properties
# the base path to store log files
base_path=/home/fastdfs

# tracker_server can ocur more than once, and tracker_server format is
#  "host:port", host can be hostname or ip address
tracker_server=10.211.55.8:22122
```

> `10.211.55.8`  为运行 tracker 服务的主机的ip地址

2. 测试文件上传

   这里测试上传 /root/install.log 文件 , 测试结果如下

```Sh
[root@centos-6 fdfs]# /usr/bin/fdfs_test /etc/fdfs/client.conf upload /root/install.log
This is FastDFS client test program v5.12

Copyright (C) 2008, Happy Fish / YuQing

FastDFS may be copied only under the terms of the GNU General
Public License V3, which may be found in the FastDFS source kit.
Please visit the FastDFS Home Page http://www.csource.org/ 
for more detail.

[2017-09-12 12:20:15] DEBUG - base_path=/home/fastdfs, connect_timeout=30, network_timeout=60, tracker_server_count=1, anti_steal_token=0, anti_steal_secret_key length=0, use_connection_pool=0, g_connection_pool_max_idle_time=3600s, use_storage_id=0, storage server id count: 0

tracker_query_storage_store_list_without_group: 
        server 1. group_name=, ip_addr=10.211.55.8, port=23000

group_name=group1, ip_addr=10.211.55.8, port=23000
storage_upload_by_filename			#### 文件保存在服务器的信息 ####
group_name=group1, remote_filename=M00/00/00/CtM3CFm3YH-AXy_CAAB4RZQzVgU274.log
source ip address: 10.211.55.8
file timestamp=2017-09-12 12:20:15
file size=30789
file crc32=2486392325
example file url: http://10.211.55.8/group1/M00/00/00/CtM3CFm3YH-AXy_CAAB4RZQzVgU274.log
storage_upload_slave_by_filename
group_name=group1, remote_filename=M00/00/00/CtM3CFm3YH-AXy_CAAB4RZQzVgU274_big.log
source ip address: 10.211.55.8
file timestamp=2017-09-12 12:20:15
file size=30789
file crc32=2486392325
example file url: http://10.211.55.8/group1/M00/00/00/CtM3CFm3YH-AXy_CAAB4RZQzVgU274_big.log
```

> 1. 文件在磁盘上的路径为 : /home/fastdfs/data/M00/00/00/CtM3CFm3YH-AXy_CAAB4RZQzVgU274.log
>
> 路径的拼接规则是 `$base_path` + `/data` + `/$remote_filename`   , `$xxx` 为变量 xxx 的取值 ,从上面的日志文件中可以知道具体值 

##### 9 安装 nginx , 并添加 fastdfs-nginx-module 模块

​	官方提供了对应的 nginx 模块 . 这里因为需要添加模块 ,所以需要重新编译 nginx , 安装 nginx 需要安装一些依赖 ,具体参考前面的内容 , 这里直接从下载和编译开始演示

​	**在 storage 中安装nginx 主要是为了提供 http 服务,同时解决group中 storage服务器的同步延时的问题.而tracker中安装nginx主要是为了提供http访问的反向代理 , 负载均衡等服务**	

​	[nginx-1.12.1](http://nginx.org/download/nginx-1.12.1.tar.gz)

​	[fastdfs-nginx-module](https://github.com/happyfish100/fastdfs-nginx-module)

1. git 克隆 fastdfs-nginx-module 模块源码到本地

```Sh
[root@centos-6 src]# git clone https://github.com/happyfish100/fastdfs-nginx-module
```

2. 复制 fastdfs-nginx-module 的配置文件到FastDFS 的配置文件所在目录,并修改配置信息

```Sh
[root@centos-6 src]# cp /usr/local/src/fastdfs-nginx-module/src/mod_fastdfs.conf /etc/fdfs/
[root@centos-6 src]# vim /etc/fdfs/mod_fastdfs.conf 
```

​	修改如下变量为对应的值

```properties
# the base path to store log files
base_path=/home/fastdfs
---------------------------------------
tracker_server=10.211.55.8:22122
---------------------------------------
# if the url / uri including the group name
# set to false when uri like /M00/00/00/xxx
# set to true when uri like ${group_name}/M00/00/00/xxx, such as group1/M00/xxx
# default value is false
url_have_group_name = true
---------------------------------------
# store_path#, based 0, if store_path0 not exists, it's value is base_path
# the paths must be exist
# must same as storage.conf
store_path0=/home/fastdfs
```

> 注意 : store_path0 的值必须和 storage.conf 文件的配置的值相同!!!

3. 下载nginx到 /usr/local/src/

```Sh
[root@centos-6 ~]# cd /usr/local/src/		# 进入 /usr/local/src/ 目录
[root@centos-6 src]# wget http://nginx.org/download/nginx-1.12.1.tar.gz
--2017-09-12 13:33:44--  http://nginx.org/download/nginx-1.12.1.tar.gz
正在解析主机 nginx.org... 206.251.255.63, 95.211.80.227, 2606:7100:1:69::3f, ...
正在连接 nginx.org|206.251.255.63|:80... 已连接。
已发出 HTTP 请求，正在等待回应... 200 OK
长度：981093 (958K) [application/octet-stream]
正在保存至: “nginx-1.12.1.tar.gz”

100%[===========================================================================>] 981,093      496K/s   in 1.9s    

2017-09-12 13:33:46 (496 KB/s) - 已保存 “nginx-1.12.1.tar.gz” [981093/981093])
```

4. 解压nginx

```sh
[root@centos-6 src]# tar -zxf nginx-1.12.1.tar.gz 
[root@centos-6 src]# cd nginx-1.12.1			# 进入解压目录
```

5. 执行配置命令,并添加 fastdfs-nginx-module 模块

```Sh
[root@centos-6 nginx-1.12.1]# ./configure --add-module=/usr/local/src/fastdfs-nginx-module/src/
  nginx path prefix: "/usr/local/nginx"
  nginx binary file: "/usr/local/nginx/sbin/nginx"
  nginx modules path: "/usr/local/nginx/modules"
  nginx configuration prefix: "/usr/local/nginx/conf"
  nginx configuration file: "/usr/local/nginx/conf/nginx.conf"
  nginx pid file: "/usr/local/nginx/logs/nginx.pid"
  nginx error log file: "/usr/local/nginx/logs/error.log"
  nginx http access log file: "/usr/local/nginx/logs/access.log"
  nginx http client request body temporary files: "client_body_temp"
  nginx http proxy temporary files: "proxy_temp"
  nginx http fastcgi temporary files: "fastcgi_temp"
  nginx http uwsgi temporary files: "uwsgi_temp"
  nginx http scgi temporary files: "scgi_temp"
```

6. 编译并安装

```Sh
[root@centos-6 nginx-1.12.1]# make && make install
```

​	通过如下命令可以查看nginx编译安装时的命令，安装了哪些模块

```Sh
[root@centos-6 ~]# /usr/local/nginx/sbin/nginx -V
nginx version: nginx/1.12.1
built by gcc 4.4.7 20120313 (Red Hat 4.4.7-18) (GCC) 
configure arguments: --add-module=/usr/local/src/fastdfs-nginx-module/src/
```



7. 配置nginx.conf

```Sh
[root@centos-6 nginx-1.12.1]# vim /usr/local/nginx/conf/nginx.conf
```

​	在http{}块内添加一个 server{} ,添加如下内容

```properties
    #### 添加处理请求fdfsdfs 资源的虚拟主机 ####
    server {
        listen       80;
        server_name  10.211.55.8;

        location /group1/M00/ {
                ngx_fastdfs_module;
        }
    }
```

> 1. server_name指定本机ip
> 2. location /group1/M00/：group1为nginx 服务FastDFS的分组名称，M00是FastDFS自动生成编号，对应store_path0=/home/fastdfs/fdfs_storage，如果FastDFS定义store_path1，这里就是M01

8. 启动nginx

```Sh
[root@centos-6 nginx-1.12.1]# /usr/local/nginx/sbin/nginx
```

> 查看80端口占用情况的方法:
>
> 1. `lsof -i :80`
> 2. `netstat -anp | grep 80`

9.  修改防火墙规则放行80端口

```sh
[root@centos-6 tomcat]# /sbin/iptables -I INPUT -p tcp --dport 80 -j ACCEPT 
[root@centos-6 tomcat]# /etc/rc.d/init.d/iptables save 
iptables：将防火墙规则保存到 /etc/sysconfig/iptables：     [确定]
[root@centos-6 tomcat]# /etc/rc.d/init.d/iptables restart 
```

10. 测试上传图片,并通过浏览器访问

  预先在/root目录下放置一张图片,通过FastDFS安装包中，自带了客户端程序，进行文件上传 . 上传之前需要按照第7步配置好 client.cnf

```Sh
[root@centos-6 ~]# /usr/bin/fdfs_test /etc/fdfs/client.conf upload /root/girl.jpg
This is FastDFS client test program v5.12

Copyright (C) 2008, Happy Fish / YuQing
##### ....省略部分信息.... ######

group_name=group1, ip_addr=10.211.55.8, port=23000
storage_upload_by_filename
group_name=group1, remote_filename=M00/00/00/CtM3CFm3gyqAHr6XAABMKJ3eYow231.jpg
source ip address: 10.211.55.8
file timestamp=2017-09-12 14:48:10
file size=19496
file crc32=2648597132
example file url: http://10.211.55.8/group1/M00/00/00/CtM3CFm3gyqAHr6XAABMKJ3eYow231.jpg
storage_upload_slave_by_filename
group_name=group1, remote_filename=M00/00/00/CtM3CFm3gyqAHr6XAABMKJ3eYow231_big.jpg
source ip address: 10.211.55.8
file timestamp=2017-09-12 14:48:10
file size=19496
file crc32=2648597132
example file url: http://10.211.55.8/group1/M00/00/00/CtM3CFm3gyqAHr6XAABMKJ3eYow231_big.jpg
```

> 在浏览器中输入 : http://10.211.55.8/group1/M00/00/00/CtM3CFm3gyqAHr6XAABMKJ3eYow231.jpg 即可访问到刚刚上传的文件



## 8 安装 solr

​	Elasticsearch 与 Solr 的比较总结*

- *二者安装都很简单；*
- *Solr 利用 Zookeeper 进行分布式管理，而 Elasticsearch 自身带有分布式协调管理功能;*
- *Solr 支持更多格式的数据，而 Elasticsearch 仅支持json文件格式；*
- *Solr 官方提供的功能更多，而 Elasticsearch 本身更注重于核心功能，高级功能多有第三方插件提供；*
- *Solr 在传统的搜索应用中表现好于 Elasticsearch，但在处理实时搜索应用时效率明显低于 Elasticsearch。*

*Solr 是传统搜索应用的有力解决方案，但 Elasticsearch 更适用于新兴的实时搜索应用*

​	**这里安装的是 solr 最新的7.0.0版本,环境要求 jdk 不低于 1.8 !!! jdk 的安装这里不做介绍了..**

### 1  jetty 容器运行solr

​	从官网下载回来的 solr 压缩包里边,自带了一个 jetty 服务,可将 solr 直接运行在 jetty 服务器上.

##### 1 下载solr 压缩包

```sh
[root@centos-6 src]# cd /usr/local/src    # 切换目录到 /usr/local/src
[root@centos-6 src]# wget http://mirrors.hust.edu.cn/apache/lucene/solr/7.0.0/solr-7.0.0.tgz
[root@centos-6 src]# ls -lh solr-7.0.0.tgz    # 查看文件的信息
-rw-r--r--. 1 root root 143M 10月  6 2017 solr-7.0.0.tgz
```

##### 2 解压

```Sh
[root@centos-6 src]# tar -xzf solr-7.0.0.tgz 
[root@centos-6 solr-7.0.0]# ll
总用量 1464
drwxr-xr-x.  3 root root   4096 10月  6 19:56 bin
-rw-r--r--.  1 root root 722808 9月   9 03:36 CHANGES.txt
drwxr-xr-x. 11 root root   4096 9月   9 04:21 contrib
drwxr-xr-x.  4 root root   4096 10月  6 01:08 dist
drwxr-xr-x.  3 root root   4096 10月  6 01:08 docs
drwxr-xr-x.  7 root root   4096 10月  6 01:08 example
drwxr-xr-x.  2 root root  36864 10月  6 01:08 licenses
-rw-r--r--.  1 root root  12646 9月   9 03:34 LICENSE.txt
-rw-r--r--.  1 root root 655812 9月   9 03:36 LUCENE_CHANGES.txt
-rw-r--r--.  1 root root  24831 9月   9 03:34 NOTICE.txt
-rw-r--r--.  1 root root   7271 9月   9 03:34 README.txt
drwxr-xr-x. 11 root root   4096 10月  6 05:43 server						
```

##### 3 运行 solr

​	解压后就能运行了,运行命令文件位于解压包下的 bin 目录. *ps:以下出现 `./` 的命令说明该命令行是在解压出来的目录下执行的*

1. 启动 solr 			`./bin/solr start`
2. 停止 solr                      `./bin/solr stop`
3. 查看运行状态              `./bin/solr status`  

> solr 出于安全因素的考虑,默认情况下以 root 用户运行时会报错 , 此时可以通过添加参数 `-force` 强行运行,比如 `./bin/solr -force start`

​	solr 7.0.0的压缩包默认提供techproducts, dih, schemaless,  cloud 4个示例工程,如果想要,运行对应的示例工程通过 `-e` 参数指定对应的工程即可,例如运行 techproducts 工程的命令如下 : `./bin/solr -e techproducts`

### 2 tomcat 容器运行 solr

​	下面演示如何将 solr 运行在 tomcat 容器上.

​	这里事先准备好一个 tomcat 包 , 可以直接解压一个 tomcat 的压缩包出来用 , 我这里解压一个并将文件夹重命名为 solr-tomcat , 放在 /usr/local/src 目录下 , 同时将 solr-tomcat/webapps/ 内的文全部删除 . 

##### 1 在 webapps 目录中新建 solr 文件夹

```Sh
[root@centos-6 solr-tomcat]# mkdir /usr/local/src/solr-tomcat/webapps/solr
```

##### 2 将 solr 的网页应用工程复制到 tomcat 中

```sh
[root@centos-6 src]# cp -r /usr/local/src/solr-7.0.0/server/solr-webapp/webapp/. /usr/local/src/solr-tomcat/webapps/solr/    # 复制工程内容
[root@centos-6 src]# ll /usr/local/src/solr-tomcat/webapps/solr/      # 查看文件
总用量 44
drwxr-xr-x. 3 root root  4096 10月  7 17:23 css
-rw-r--r--. 1 root root  3262 10月  7 17:23 favicon.ico
drwxr-xr-x. 4 root root  4096 10月  7 17:23 img
-rw-r--r--. 1 root root 13253 10月  7 17:23 index.html
drwxr-xr-x. 3 root root  4096 10月  7 17:23 js
drwxr-xr-x. 2 root root  4096 10月  7 17:23 libs
drwxr-xr-x. 2 root root  4096 10月  7 17:23 partials
drwxr-xr-x. 3 root root  4096 10月  7 17:23 WEB-INF
```

##### 3 复制日志的 jar包和配置文件tomcat 的 solr 工程

```Sh
[root@centos-6 solr]# cp -r /usr/local/src/solr-7.0.0/server/lib/ext/* /usr/local/src/solr-tomcat/webapps/solr/WEB-INF/lib/     # 复制日志 jar 包

[root@centos-6 solr]# mkdir -p /usr/local/src/solr-tomcat/webapps/solr/WEB-INF/classes  # 建目录

[root@centos-6 solr]# cp -r /usr/local/src/solr-7.0.0/server/resources/* /usr/local/src/solr-tomcat/webapps/solr/WEB-INF/classes   # 复制日志配置文件
```

##### 4 创建solrhome

```Sh
[root@centos-6 src]# mkdir /usr/local/src/solrhome/    # 创建 solrhome 目录

[root@centos-6 src]# cp -r  /usr/local/src/solr-7.0.0/server/solr/. /usr/local/src/solrhome/

[root@centos-6 solrhome]# ll /usr/local/src/solrhome/
总用量 16
drwxr-xr-x. 4 root root 4096 10月  7 18:09 configsets
-rw-r--r--. 1 root root 3004 10月  7 18:09 README.txt
-rw-r--r--. 1 root root 2117 10月  7 18:09 solr.xml
-rw-r--r--. 1 root root  975 10月  7 18:09 zoo.cfg
```

> solr-7.0.0/server/solr  是一个标准的 solrhome 目录 , 所以这里这几复制过来用 , 在此基础上修改

##### 5 修改tomcat 中solr 工程的 solrhome 路径

​	为 web 应用设置 sorlhome路径需要修改 web.xml 文件

```Sh
[root@centos-6 WEB-INF]# vim /usr/local/src/solr-tomcat/webapps/solr/WEB-INF/web.xml 
```

​	在原配置文件的第40-46行注释了一对 <env-entry> 标签 , 该标签用于配置 solrhome 路径,可以复制该标签内容粘贴到第48行 , 并修改 <env-entry-value> 的内容为上步创建是 solrhome 的路径,然后保存退出.如下:

```Xml
 48     <env-entry>
 49          <env-entry-name>solr/home</env-entry-name>
 50          <env-entry-value>/usr/local/src/solrhome</env-entry-value>
 51          <env-entry-type>java.lang.String</env-entry-type>
 52     </env-entry>
```

##### 6 启动 tomcat , 并在浏览器访问 solr



## 9 tomcat 集群 

### 1 单台机器上部署多个tomcat实例

​	这里在原来机器上增加部署 tomcat_2 和 tomcat_3 实例 ,

1. 复制tomcat文件夹

   将之前部署好的tomcat文件夹,复制两份分别命名为 tomcat_2 和 tomcat_3 , 其父级目录均为 `/usr/local/src`

2. 为 tomcat2 和 tomcat_3 添加环境变量

   编辑 `/etc/profile` 文件 , 添加如下内容 , 保存退出后执行 `source /etc/profile`  , 使其生效

```sh
export TOMCAT_2_HOME=/usr/local/src/tomcat_2
export CATALINA_2_HOME=/usr/local/src/tomcat_2
export CATALINA_2_BASE=/usr/local/src/tomcat_2

export TOMCAT_3_HOME=/usr/local/src/tomcat_3
export CATALINA_3_HOME=/usr/local/src/tomcat_3
export CATALINA_3_BASE=/usr/local/src/tomcat_3
```

3. 修改 catalina.sh 文件

   编辑 `${TOMCAT_X_HOME}/bin/catalina.sh`  文件 ('X' 代表 2 和 3 , 因为这里要配置两个实例) , 在  `# OS specific support.  $var _must_ be set to either true or false.`  的下一行添加如下内容

```sh
# tomcat_2 的配置
export CATALINA_BASE=$CATALINA_2_BASE
export CATALINA_HOME=$CATALINA_2_HOME
```

​	同理, 在tomcat_3 实例的 catalina.sh 上添加内容 : 

```sh
export CATALINA_BASE=$CATALINA_3_BASE
export CATALINA_HOME=$CATALINA_3_HOME
```

4. 修改端口

   修改 `${TOMCAT_X_HOME}/conf/server.xml` 文件中的 `<Server port=8005 ../>`  `<Connector port=8080 ../>`  `<Connector port=8009 ../>`  三个端口号为未使用的端口 , 这里修改如下 :

   tomcat_2 :

   > `8005  --> 8006`
   >
   > `8080  --> 8081`
   >
   > `8009  --> 8010`

   tomcat_3 :

   > `8005  --> 8007`
   >
   > `8080  --> 8082`
   >
   > `8009  --> 8011`

5. 开放端口 8081 和 8082

   centos 7 下的命令 :

```sh
firewall-cmd --zone=public --add-port=8081/tcp --permanent
firewall-cmd --zone=public --add-port=8082/tcp --permanent

# 重启防火墙
systemctl restart firewalld.service
```

​	上述操作完后 , 即可启动 tomcat_2 和 tomcat_3 并访问

### 2 nginx 实现负载均衡

​	1. 配置nginx

```sh
[root@localhost local]# mkdir -p /usr/local/nginx/conf/vhost
[root@localhost local]# vim /usr/local/nginx/conf/nginx.conf
```

> /usr/local/nginx/conf/vhost  目录用于存放自定义配置的文件

​	在 nginx.conf 的文件中, server 节点后添加如下内容

```sh
include vhost/*.conf
```

​	配置后的文件结构大致如下:

```sh
http
{
 server{}
 include vhost/*.conf   # 增加的内容
}
```

2. 在 vhost目录下新建文件 tomcat.loadbalance.conf

   文件内容如下 , 配置了当访问 `www.mmall.com` 时 , 将请求分别转发到 `127.0.0.1:8081`  和 `127.0.0.1:8082`  

```sh
upstream www.mmall.com{
        server 127.0.0.1:8081 weight=2;
        server 127.0.0.1:8082 weight=1;
}

server {
    listen 80;
    autoindex on;
    server_name www.mmall.com;
    access_log /usr/local/nginx/logs/access.log combined;
    index index.html index.htm index.jsp index.php;
	
	if ( $query_string ~* ".*[\;'\<\>].*" ){
        return 404;
    }
    location / {
            proxy_pass http://www.mmall.com;
            add_header Access-Control-Allow-Origin *;
     }
}
```

​	内容添加并保存后 , 执行配置检查命令 `$NGINX_HOME/sbin/nginx -t` , 如果配置没有问题 ,即可执行 `$NGINX_HOME/sbin/nginx -s reload` 命令 , 重新加载配置文件

3. 修改域名配置 /etc/hosts

   添加如下内容 :

```sh
127.0.0.1  www.mmall.com	
```

​	执行完上述操作后 , 访问 `www.mmall.com` 时 , 即可访问服务器的tomcat , 并且请求会按照权重比例分发到不同的tomcat上



如果觉得笔记不错,扫码鼓励下吧,两毛也是爱,O(∩_∩)O~~~~*

![2毛也是爱~~~](pay.jpg )





