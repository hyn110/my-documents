# 23_zookeeper 笔记

```xml
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.4.10</version>
</dependency>
```

## 1 连接并输出子节点

```java
import org.apache.zookeeper.ZooKeeper;

import java.util.List;

/**
 * @author fmi110
 * @Description:
 * @Date 2018/3/7 17:11
 */
public class Demo1 {

    private static ZooKeeper zk;

    public static void main(String[] args) throws Exception {
//        ZooKeeper    zk       = new ZooKeeper("111.230.199.247:2181", 5000, null);
//        List<String> children = zk.getChildren("/a", null);
//        children.stream()
//                .forEach(System.out::print);
        zk = new ZooKeeper("111.230.199.247:2181", 5000, null);
        ls("/a");
    }

    /**
     * 递归显示指定路径的文件
     */
    public static void ls(String path) throws Exception {

        List<String> children = zk.getChildren(path, null);

        if (children == null || children.isEmpty()) {
//            System.out.println("/");
            return;
        }

        children.stream()
                .forEach(s -> {
                    if ("/".equals(path)) {
                        System.out.println("/" + s);

                        try {
                            ls("/" + s);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        System.out.println(path + "/" + s);
                        try {
                            ls(path + "/" + s);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

    }
}
```

## 2 设置节点数据

​	znode节点上的数据有版本信息，实现了类似乐观锁的检查机制，再 setData 时必须传递 version ，version 号对应不上时将抛异常 。**默认单个节点存储的数据最大为 1M !!**

```java
 public static void main(String[] args) throws Exception {
        zk = new ZooKeeper("111.230.199.247:2181", 5000, null);
        byte[] data = zk.getData("/a", null, null);
        System.out.println(data == null ? null : new String(data));  // 输出 ：tom

        zk.setData("/a", "fmi110".getBytes(), 1);
        data = zk.getData("/a", null, null);
        System.out.println(data == null ? null : new String(data));  // 输出 ：fmi110
 }
```

## 3 节点类型

```
create [-s] [-e] path data acl	# 创建节点 , 并存储数据  -s 顺序节点 , -e  临时节点
```

### 1 持久节点

​	client 结束，节点仍然存在

### 2 临时节点

​	client客户端活动时有效，client断开时自动删除，**所以不能存在子节点**。临时节点再leader选举中起重要作用

### 3 序列节点

​	再节点名的后面自动加10个数字，比如创建的节点叫tom，生成后的节点交 tom0000000001，主要用于同步和锁

## 4 Session

​	Session中的请求以FIFO执行，一旦client连接到server，session就建立了。sessionId分配给client,client以固定的间隔向server发送心跳，表示会话可用，zk集群超时未收到心跳则判定client挂了，此时将移除会话期间创建的临时节点！！！

## 5 Watches

​	观察者。

​	client能够再节点发生改变时（节点删除，增加/删除子节点）收到通知

​	client在getData 时可以设置观察者，注意，**观察者只能收到一次通知，所以如果想多次接收通知，必须重复设置观察者！！！**

```java
 	@Test
    public void testWatches() throws Exception {
        zk = new ZooKeeper("111.230.199.247:2181", 5000, null);
        Stat stat = new Stat(); // 接收节点的状态信息
        // 观察者
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {

                System.out.println("子节点发生改变");
                try {
                    if (null != zk.exists("/a", null)) {
                        zk.getData("/a", this, null); // 重新注册观察者
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        zk.getData("/a", watcher, stat);

        while (true)
            new Scanner(System.in).next();

    }
```

## 6 zookeeper 读写过程

​	读 ：client发送读请求给znode,znode读取自己数据库，返回节点数据给client

​	写 ：client将路径和数据发送给server，server转发给leader，leader再补发请求给所有的follower，只有大多数（半数+1)节点都成功响应，则写操作成功。（zab 原子性广播）

## 7 leader节点的选举

1. 所有节点创建一个临时有序节点，路径为 `/app/leader_election/guid_`
2. zookeeper会自动为节点追加数字序号 `/app/leader_election/guid_0000000001` ...
3. 在所有的实例中，创建的临时有序节点，**序号最小的节点将成为leader节点**,其余是follewer节点
4. 每一个节点将watches最近的比它小的节点，比如 `xxx008` 将监听 `xxx007` 节点
5. 如果leader节点宕机，则它创建的临时节点 `/app/leader_election/guid_N `节点将被删除
6. 则leader的下一个节点（假设叫B节点）可以通过watcher知道leader节点被移除
7. B节点检查是否有比自己更小的节点，如果没有则假定自己是leader节点，有的话则推举序号最小的节点作为leader
8. 同理，其他follower节点选举序号最小的节点作为leader





### x0 zookeeper 相关概念

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