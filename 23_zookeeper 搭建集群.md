# 23_zookeeper 搭建集群

​	搭建环境 : `centOS 7 64位` + `zookeeper-3.4.10.tar.gz` 

​	准备 :  三台主机

| 主机名      | ip             |
| -------- | -------------- |
| centos_1 | 192.168.80.132 |
| centos_2 | 192.168.80.133 |
| centos_3 | 192.168.80.134 |

​	将  `zookeeper-3.4.10.tar.gz` 文件上传至 `/usr/local/src` 目录下

## 1 单机安装

​	**注意 : 需要在三台主机上都安装 zookeeper !!!** 步骤如下 :

### 1 进入目录解压文件

```Sh
[root@centos-6 src]# cd /usr/local/src/
[root@centos-6 src]# tar -zxf zookeeper-3.4.10.tar.gz 
[root@centos-6 src]# ls
apache-tomcat-8.0.46.tar.gz  redis               tomcat
java                         redis-4.0.1         zookeeper-3.4.10
jdk-8u144-linux-i586.tar.gz  redis-4.0.1.tar.gz  zookeeper-3.4.10.tar.gz
```

### 2 进入解压目录,创建文件夹 data

```Sh
[root@centos-6 src]# cd zookeeper-3.4.10
[root@centos-6 zookeeper-3.4.10]# mkdir data
```

### 3 进入解压目录下的conf文件夹,复制 zoo_sample.cfg 并重命名为 zoo.cfg

```Sh
[root@centos-6 zookeeper-3.4.10]# cd conf
[root@centos-6 conf]# ls
configuration.xsl  log4j.properties  zoo_sample.cfg
[root@centos-6 conf]# cp zoo_sample.cfg zoo.cfg
```

### 4 编辑zoo.cfg文件,修改zookeeper的工作目录

```properties
dataDir=/usr/local/src/zookeeper-3.4.10/data
```

### 5 设置防火墙拦截规则,放行2181端口

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

### 6 进入解压目录下的bin文件夹,运行zookeeper

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
### 7 设置命令别名(可选)

​	设置命令别名只是为了方便操作 , 设置方法如下 , 打开用户目录下的 .bashrc 文件,

```sh
vim  ~/bashrc
```

​	添加如下内容 :

```sh
alias zookeeper-start='/usr/local/src/zookeeper-3.4.10/bin/zkServer.sh start'
alias zookeeper-stop='/usr/local/src/zookeeper-3.4.10/bin/zkServer.sh stop'
alias zookeeper-status='/usr/local/src/zookeeper-3.4.10/bin/zkServer.sh status'
```

​	然后重启系统 , 这样在任何目录下就都可以通过 `zookeeper-start`  启动zookeeper

## 2 集群配置

​	接下来需要在三台主机上进行配置 , 实现集群

### 1 配置集群各个主机的ip地址

​	**这一步在三台主机上的设置一样!!!在三台主机上都需要这么做!!!**

​	这步在 `/usr/local/src/zookeeper-3.4.10/conf/zoo.cfg` 配置文件中配置 , 

```sh
vim /usr/local/src/zookeeper-3.4.10/conf/zoo.cfg
```

​	添加如下内容 :

```properties
server.1=192.168.80.132:2888:3888
server.2=192.168.80.133:2888:3888
server.3=192.168.80.134:2888:3888
```

> 1. ip 地址分别为三台主机的ip地址
> 2. 2888端口是zookeeper 连接 leader 主机的端口
> 3. 3888端口是zookeeper 用于投票选举 leader 的端口

​	配置后的 zoo.cfg 内容如下 :

```properties
# The number of milliseconds of each tick
tickTime=2000
initLimit=10
# The number of ticks that can pass between 
# sending a request and getting an acknowledgement
syncLimit=5
# the directory where the snapshot is stored.
# do not use /tmp for storage, /tmp here is just 
# example sakes.
dataDir=/usr/local/src/zookeeper-3.4.10/data
# the port at which the clients will connect
clientPort=2181
# the maximum number of client connections.
# increase this if you need to handle more clients
#maxClientCnxns=60
#
# Be sure to read the maintenance section of the 
# administrator guide before turning on autopurge.
#
# http://zookeeper.apache.org/doc/current/zookeeperAdmin.html#sc_maintenance
#
# The number of snapshots to retain in dataDir
#autopurge.snapRetainCount=3
# Purge task interval in hours
# Set to "0" to disable auto purge feature
#autopurge.purgeInterval=1

server.1=192.168.80.132:2888:3888
server.2=192.168.80.133:2888:3888
server.3=192.168.80.134:2888:3888

server.1=192.168.204.129:2888:3888
server.2=192.168.204.130:2888:3888
server.3=192.168.204.131:2888:3888
```

### 2 配置 ServerID 标识

​	**在三台主机上都要配置,并且需要跟 zoo.cfg 中的配置对应上!!!** 设置方法如下 : 在 `/usr/local/src/zookeeper-3.4.10/data` 目录下创建 myid 文件 , 并在文件中输入对应的数字

​	在 192.168.80.132 主机上 :

```sh
echo '1' >> /usr/local/src/zookeeper-3.4.10/data/myid
```

> zoo.cfg 中配置了 `server.1=192.168.80.132:2888:3888`  所以在 192.168.80.132 的 mypid 文件中需要输入 1 , 这个数字由 `server.x` 的 x 的值决定!!!

​	在 192.168.80.133 主机上 :
```sh
echo '2' >> /usr/local/src/zookeeper-3.4.10/data/myid
```


​	在 192.168.80.134 主机上 :
```sh
echo '3' >> /usr/local/src/zookeeper-3.4.10/data/myid
```
### 3 放行 2888 和 3888 端口

​	**三台主机上都要放行 !!!**

```sh
# 开放端口
firewall-cmd --zone=public --add-port=2888/tcp --permanent &&
firewall-cmd --zone=public --add-port=3888/tcp --permanent
# 重启防火墙
systemctl restart firewalld.service
```

### 4 启动集群

​	分别在三台主机上执行启动命令

```sh
[root@localhost conf]# zookeeper-start 
ZooKeeper JMX enabled by default
Using config: /usr/local/src/zookeeper-3.4.10/bin/../conf/zoo.cfg
Starting zookeeper ... STARTED
```

> `zookeeper-start`  是之前自己设置的命令别名 !!!

​	在 192.168.80.132 上查看zookeeper的运行状态

```sh
[root@localhost conf]# zookeeper-status
ZooKeeper JMX enabled by default
Using config: /usr/local/src/zookeeper-3.4.10/bin/../conf/zoo.cfg
Mode: follower
```

​	可以看到运行模式 follower



### 3 zookeeper 相关概念

#### 1 ZooKeeper 四字命令

​	Zookeeper支持某些特定的四字命令字母与其的交互。他们大多数是查询命令，用来获取Zookeeper服务的当前状态及相关信息。用户在客户端可以通过**telnet**或**nc**向Zookeeper提交相应的命令

| 命令   | 功能描述                                     |
| ---- | ---------------------------------------- |
| conf | 输出服务配置信息                                 |
| cons | 列出连接的客户端的会话信息 , 包括 : 接收/发送的包数量 , 会话id , 操作延迟 , 最后的操作 等 |
| dump | 列出未处理的会话和临时节点                            |
| envi | 输出服务环境的信息                                |
| reqs | 列出未经处理的请求                                |
| ruok | 测试服务的状态是否正确 . 如果正确返回 imok , 否则无回应        |
| stat | 输出有关性能和连接的客户端的列表                         |
| wchs | 列出服务器 watch 的详细信息                        |
| wchc | 通过 session 列出服务器 watch 的详细信息 , 它的输出时一个与 watch 相关的会话的列表 |
| wchp | 通过 路径 列出服务器的 watch 的详细信息 , 它输出一个与 session 相关的路径 |

​	安装 nc 命令

```sh
yum install -y nc
```

​	**测试四字命令 :**

```sh
[root@localhost ~]# echo conf | nc localhost 2181
clientPort=2181
dataDir=/usr/local/src/zookeeper-3.4.10/data/version-2
dataLogDir=/usr/local/src/zookeeper-3.4.10/data/version-2
tickTime=2000
maxClientCnxns=60
minSessionTimeout=4000
maxSessionTimeout=40000
serverId=2
initLimit=10
syncLimit=5
electionAlg=3
electionPort=3888
quorumPort=2888
peerType=0
```

#### 2 客户端连接 zookeeper 服务

​	zookeeper 提供了 `zkCli.sh`  客户端用于连接 zooService , 并执行操作 , zkCli.sh 路径 : `/usr/local/src/zookeeper-3.4.10/bin/zkCli.sh` , 连接命令 :

```sh
zkCli.sh  -server  host:port
zkCli.sh  -server  localhost:2181  # 连接到本地的zookeeper 服务
```

```sh
[root@localhost bin]# ./zkCli.sh -server  localhost:2181
Connecting to localhost:2181
2018-01-30 16:08:22,344 [myid:] - INFO  [main:Environment@100] - Client environment:zookeeper.version=3.4.10-39d3a4f269333c922ed3db283be479f9deacaa0f, built on 03/23/2017 10:13 GMT
2018-01-30 16:08:22,351 [myid:] - INFO  [main:Environment@100] - Client environment:host.name=localhost
2018-01-30 16:08:22,351 [myid:] - INFO  [main:Environment@100] - Client environment:java.version=1.8.0_144
2018-01-30 16:08:22,353 [myid:] - INFO  [main:Environment@100] - Client environment:java.vendor=Oracle Corporation
2018-01-30 16:08:22,353 [myid:] - INFO  [main:Environment@100] - Client environment:java.home=/usr/local/src/java/jre
2018-01-30 16:08:22,353 [myid:] - INFO  [main:Environment@100] - Client environment:java.class.path=/usr/local/src/zookeeper-3.4.10/bin/../build/classes:/usr/local/src/zookeeper-3.4.10/bin/../build/lib/*.jar:/usr/local/src/zookeeper-3.4.10/bin/../lib/slf4j-log4j12-1.6.1.jar:/usr/local/src/zookeeper-3.4.10/bin/../lib/slf4j-api-1.6.1.jar:/usr/local/src/zookeeper-3.4.10/bin/../lib/netty-3.10.5.Final.jar:/usr/local/src/zookeeper-3.4.10/bin/../lib/log4j-1.2.16.jar:/usr/local/src/zookeeper-3.4.10/bin/../lib/jline-0.9.94.jar:/usr/local/src/zookeeper-3.4.10/bin/../zookeeper-3.4.10.jar:/usr/local/src/zookeeper-3.4.10/bin/../src/java/lib/*.jar:/usr/local/src/zookeeper-3.4.10/bin/../conf:
2018-01-30 16:08:22,353 [myid:] - INFO  [main:Environment@100] - Client environment:java.library.path=/usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib
2018-01-30 16:08:22,354 [myid:] - INFO  [main:Environment@100] - Client environment:java.io.tmpdir=/tmp
2018-01-30 16:08:22,354 [myid:] - INFO  [main:Environment@100] - Client environment:java.compiler=<NA>
2018-01-30 16:08:22,354 [myid:] - INFO  [main:Environment@100] - Client environment:os.name=Linux
2018-01-30 16:08:22,354 [myid:] - INFO  [main:Environment@100] - Client environment:os.arch=amd64
2018-01-30 16:08:22,354 [myid:] - INFO  [main:Environment@100] - Client environment:os.version=3.10.0-327.10.1.el7.x86_64
2018-01-30 16:08:22,354 [myid:] - INFO  [main:Environment@100] - Client environment:user.name=root
2018-01-30 16:08:22,354 [myid:] - INFO  [main:Environment@100] - Client environment:user.home=/root
2018-01-30 16:08:22,354 [myid:] - INFO  [main:Environment@100] - Client environment:user.dir=/usr/local/src/zookeeper-3.4.10/bin
2018-01-30 16:08:22,355 [myid:] - INFO  [main:ZooKeeper@438] - Initiating client connection, connectString=localhost:2181 sessionTimeout=30000 watcher=org.apache.zookeeper.ZooKeeperMain$MyWatcher@5c29bfd
2018-01-30 16:08:22,376 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@1032] - Opening socket connection to server localhost/127.0.0.1:2181. Will not attempt to authenticate using SASL (unknown error)
Welcome to ZooKeeper!
JLine support is enabled
2018-01-30 16:08:22,477 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@876] - Socket connection established to localhost/127.0.0.1:2181, initiating session
2018-01-30 16:08:22,490 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@1299] - Session establishment complete on server localhost/127.0.0.1:2181, sessionid = 0x26145dfb2cb0001, negotiated timeout = 30000

WATCHER::

WatchedEvent state:SyncConnected type:None path:null
[zk: localhost:2181(CONNECTED) 0] 

```

​	zkCli.sh 可用的命令如下 : 

```sh
create [-s] [-e] path data acl	# 创建节点 , 并存储数据  -s 顺序节点 , -e  临时节点
delete path [version]			# 删除节点,如果有子节点要先删除子节点
	
stat path [watch] 			# 查看某个节点下的所有子节点信息
set path data [version]		# 设置指定节点存储的数据,如果指定版本,需要和当前节点的数据版本一致
get path [watch]			# 获取指定节点的数据内容
rmr path				# 删除当前路径节点及其所有子节点(递归)
sync path

ls path [watch]			# 显示指定节点的信息
ls2 path [watch]		# 显示指定节点的详细信息
	
setquota -n|-b val path	# 设置指定节点的配额 , -n 限制子节点个数 , -b 限制节点的数据长度,超出配置zookeeper不抱错,而是在日志信息中记录
delquota [-n|-b] path	# 删除指定节点的配额
listquota path			# 显示指定节点的配额
	
setAcl path acl
getAcl path

history 	# 查看客户端这次会话所执行的所有命令
redo cmdno  # 执行指定历史命令
printwatches on|off
	
addauth scheme auth

quit  # 退出客户端
close 	# 关闭 zookeeper 服务
connect host:port  # 连接到指定的 zookeeper 服务
```

#### 3 Zookeeper Stat 结构

​	[内容出处](http://www.majunwei.com/view/201707121303386351.html)

Zookeeper中的每个znode的stat机构都由下面的字段组成：

- czxid - 引起这个znode创建的zxid
- mzxid - znode最后更新的zxid
- ctime - znode被创建的毫秒数(从1970年开始)
- mtime - znode最后修改的毫秒数(从1970年开始)
- version - znode数据变化号
- cversion - znode子节点变化号
- aversion - znode访问控制列表的变化号
- ephemeralOwner - 如果是临时节点这个是znode拥有者的session id。如果不是临时节点则是0。
- dataLength - znode的数据长度
- numChildren - znode子节点数量

#### 4 ZooKeeper Watches

​	Zookeeper里的所有读取操作  `getData()` , `getChildren()` 和  `exists()`   都有设置watch的选项。这是Zookeeper watch的定义：watch事件是one-time触发，向客户端发送设置watch，当设置watch的数据变化时发生。在watch定义里有三个关键点：

- **一次触发** - 当数据有了变化时将向客户端发送一个watch事件。例如，如果一个客户端用 `getData("/znode1",true)` 并且过一会之后 `/znode1` 的数据改变或删除了，客户端将获得一个 `/znode1` 的watch事件。如果 `/znode1` 再次改变，将不会发送watch事件除非设置了新watch。
- **发往客户端** - 这意味着事件发往客户端，但是可能在成功之前没到客户端。Watches是异步发往watchers。Zookeeper提供一个顺序保证：在看到watch事件之前绝不会看到变化。网络延迟或其他因素可能引起客户端看到watches并在不同时间返回code。关键点是不同客户端看到的是一致性的顺序。
- **为数据设置watch** - 一个节点可以有不同方式改变。它帮助 Zookeeper 维护两个 watches：`data watches` 和 `child watches` 。getData() 和 exists() 设置data watches。`getChildren()` 设置 `child watches` 。两者任选其一，它可以帮助watches根据类型返回。**getData() 和 exists() 返回关于节点数据的信息，然而 getChildren() 返回children列表**。因此，setData()将会触发znode设置的data watches。一个成功的create()将会触发一个datawatches和一个父节点的child watch。一个成功的delete()将触发一个data watch和一个child watch。

​       Watches是在 client 连接到 Zookeeper 服务端的本地维护。这可让watches成为轻量的，可维护的和派发的。当一个client连接到新server，watch将会触发任何session事件。断开连接后不能接收到。当客户端重连，先前注册的watches将会被重新注册并触发如果需要。

##### 1 Zookeeper保证watches的什么？

关于watches，Zookeeper维护这些保证：

- Watches和其他事件、watches和异步恢复都是有序的。Zookeeper客户端保证每件事都是有序派发。
- 客户端在看到新数据之前先看到watch事件。
- 对应更新顺序的Zookeeper watches事件顺序由Zookeeper服务所见。

##### 2 关于Watches要记住的事情

- Watches是一次触发；**如果你得到一个watch事件且想在将来的变化得到通知，必须设置另一个watch。**
- 因为watches是一次触发且在获得事件和发送请求得到wathes之间有延迟你不能可靠的看到发生在Zookeeper节点的每一个变化。准备好处理这个案例在获得事件和再次设置watch之间变化多次。(你可能不在意，但是至少认识到它可能发生)
- 一个watch对象，或function/context对，对于指定的通知只能触发一次。例如，如果相同的文件通过exists和getData注册了相同的watch对象并且文件稍后删除了，watch将只会触发文件的删除通知。
- 从服务端断开连接时(比如服务器故障)，将不会得到任何watches直到重新建立连接。因为这个原因session事件被发送到所有watch处理器。使用session事件进入安全模式：断开连接时不接收事件，所以在这个模式里你的程序应该采取保守。

#### 5 Zookeeper使用ACLs控制访问

​	Zookeeper使用ACLs控制访问它的znodes(Zookeeper的数据节点)。ACL实现非常类似于UNIX文件访问权限：它使用权限位允许/不允许一个简单和范围的各种操作。不像标准的UNIX权限，Zookeeper节点不由三个标准范围(用户，组 和 world)限制。Zookeeper没有znode所有者的概念。而是一个ACLs指定一组ids和与这些ids相关联的权限。

​	还要注意ACL只适用于特定的znode。尤其不适用于children。例如，如果/app对ip:192.168.1.56是只读的并且/app/status是全都可读的，任何人可以读取/app/status；ACLs不是递归控制的。

Zookeeper支持可插拔的权限认证方案。使用scheme:id的形式指定Ids,scheme是id对应的权限认证scheme。例如，ip:172.16.16.1是一个地址为172.16.16.1的id.

客户端连接的Zookeeper并授权自己时，Zookeeper联合所有对应客户端的ids。这些ids在尝试访问节点时核查znodes的ACLs。ACLs由对组成(scheme:expression,permissions)。expression的格式是针对scheme。例如，(ip:19.22.0.0/16, READ)对开头19.22的IP地址的任意客户端提供读取权限。

### ACL权限

Zookeeper支持下面的permissions：

- CREATE：可以创建子节点
- READ：可以从节点获取数据并列出它的子节点
- WRITE：可以向节点设置数据
- DELETE：可以删除一个子节点
- ADMIN：可以设置权限
- CREATE和DELELTE权限已经更细粒度的划分了WRITE权限。CREATE和DELETE的案例是：

你想要A在Zookeeper节点上能够set，但是不能CREATE或DELETE子节点。

CREATE而没有DELETE：客户端通过在父节点创建的Zookeeper节点创建请求。你想要所有客户端能add，而只有request processor可以delete。（这有点像文件的APPEND权限）

因为Zookeeper没有文件所有者的权限才有ADMIN权限。在某种意义上ADMIN权限指定实体作为拥有者。Zookeeper不支持LOOKUP权限。每人都有LOOKUP权限。这可让你stat一个节点，但不能做其他的。

内嵌的ACL schemes

Zookeeper有下面的schemes：

- world：有单独的id，anyone,代表任何人
- auth:不适用任何id，代表任何授权的用户。
- digest：使用username;password字符串生成MD5哈希作为ACL ID身份。通过发送username:password明文授权。在ACL里使用时expression将会是username:base64编码的SHA1 password摘要。
- ip:使用客户端IP作为ACL ID身份。