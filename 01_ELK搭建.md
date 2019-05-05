# ELK 搭建

​	**基于centOS 7 , 预先添加 elk 用户和elk用户组,用于启动相关的程序**

```sh
groupadd elk && useradd elk -g elk -p elk123456
```

## 1 下载及安装

[官网资料](https://www.elastic.co/guide/en/elastic-stack-get-started/current/get-started-elastic-stack.html#install-logstash)

### Elasticsearch

```sh
curl -L -O http://192.168.0.107/elasticsearch-6.6.0.tar.gz
tar -xzvf elasticsearch-6.6.0.tar.gz
./bin/elasticsearch
```

```sh
[root@localhost elasticsearch-6.6.0]# ./bin/elasticsearch
[2019-02-16T15:47:19,778][WARN ][o.e.b.ElasticsearchUncaughtExceptionHandler] [unknown] uncaught exception in thread [main]
# 不能以root身份启动elasticsearch
Caused by: java.lang.RuntimeException: can not run elasticsearch as root
```

​	**不能以root身份启动elasticsearch , 添加新用户用于启动elasticsearch , 并修改文件权限后启动**

```sh
[root@localhost src]# groupadd elk	&& useradd elk -g elk -p elk123456
# 修改文件权限
[root@localhost local]# chown -R elk:elk elasticsearch-6.6.0/
# 切换用户
[root@localhost elasticsearch-6.6.0]# su elasticsearch
# 启动el
[elk@localhost elasticsearch-6.6.0]$ ./bin/elasticsearch
```

> 验证启动 : curl http://127.0.0.1:9200

​	**以root身份运行,开放端口9200**

```sh
firewall-cmd --zone=public --add-port=9200/tcp --permanent && systemctl restart firewalld.service
```

### Logstash

```sh
curl -L -O http://121.228.244.49/logstash-6.6.0.tar.gz
tar -xzvf elasticsearch-6.6.0.tar.gz 
chown -R elk:elk logstash-6.6.0/
```

> 测试: `bin/logstash -e 'input { stdin { } } output { stdout {} }'`

```sh
[2019-02-16T16:36:17,573][INFO ][logstash.agent           ] Pipelines running {:count=>1, :running_pipelines=>[:main], :non_running_pipelines=>[]}
[2019-02-16T16:36:18,236][INFO ][logstash.agent           ] Successfully started Logstash API endpoint {:port=>9600}
hello
{
    "host" => "localhost.localdomain",
    "@version" => "1",
    "message" => "hello",
    "@timestamp" => 2019-02-16T08:37:14.977Z
}

```

官方提供的grok表达式：[https://github.com/logstash-plugins/logstash-patterns-core/tree/master/patterns](https://github.com/logstash-plugins/logstash-patterns-core/tree/master/patterns)
grok在线调试：[https://grokdebug.herokuapp.com/](https://grokdebug.herokuapp.com/)



### Filebeat

```sh
curl -L -O https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-6.6.0-x86_64.rpm
sudo rpm -vi filebeat-6.6.0-x86_64.rpm

curl -L -O https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-6.6.0-linux-x86_64.tar.gz

sudo ./filebeat -e -c filebeat.yml
```

> -L 当页面有跳转的时候，输出跳转到的页面
>
> -O  按服务器上的名称保存下载的文件

配置: https://www.elastic.co/guide/en/beats/filebeat/6.6/filebeat-configuration.html



### Kibana

```sh
curl -L -O https://artifacts.elastic.co/downloads/kibana/kibana-6.6.0-linux-x86_64.tar.gz
tar xzvf kibana-6.6.0-linux-x86_64.tar.gz
cd kibana-6.6.0-linux-x86_64/
./bin/kibana
```

> 验证启动: [http://127.0.0.1:5601](http://127.0.0.1:5601/)

## 2 配置

### Elasticsearch

[官网6.6配置说明](https://www.elastic.co/guide/en/elasticsearch/reference/current/settings.html)

[el 5.x配置详解](http://www.cnblogs.com/xiaochina/p/6855591.html)

[Elasticsearch重要配置](https://www.cnblogs.com/ginb/p/7027910.html)

#### 1 重要的elasticsearch 配置

- [Path settings](https://www.elastic.co/guide/en/elasticsearch/reference/current/path-settings.html)

  ```properties
  path.data: /var/data/elasticsearch
  path.logs: /var/log/elasticsearch
  ```

- [Cluster name](https://www.elastic.co/guide/en/elasticsearch/reference/current/cluster.name.html)

  ```properties
  cluster.name: elk-prod
  ```

- [Node name](https://www.elastic.co/guide/en/elasticsearch/reference/current/node.name.html)

  ```properties
  node.name: elk-node-1
  # node.name: ${HOSTNAME}
  ```

- [Network host](https://www.elastic.co/guide/en/elasticsearch/reference/current/network.host.html)

  ```properties
  # 默认 127.0.0.1 and [::1]
  network.host: 192.168.174.130
  ```

  > 主机设置后,elasticsearch 即由开发模式进入生产模式,任何配置错误将直接抛出异常!!
  >
  > 验证 : curl http://192.168.174.130:9200
  >
  > 当外部主机无法访问时,注意查看防火墙是否开放对应的端口

- [Discovery settings](https://www.elastic.co/guide/en/elasticsearch/reference/current/discovery-settings.html)

  ```properties
  discovery.zen.ping.unicast.hosts: ["host1", "host2"]
  # 为防止集群出现脑裂 , 数量应该为 (master_eligible_nodes / 2) + 1 , 主节点的一半 + 1
  discovery.zen.minimum_master_nodes: 2
  ```

- [Heap size](https://www.elastic.co/guide/en/elasticsearch/reference/current/heap-size.html)

  ​	在 jvm.options 文件中配置, 默认1g , 设置最好不要超过物理内存的一半,防止影响系统的运行

  ```
  -Xms1g 
  -Xmx1g
  ```

- [Heap dump path](https://www.elastic.co/guide/en/elasticsearch/reference/current/heap-dump-path.html)

  ​	`/var/lib/elasticsearch` for the [RPM](https://www.elastic.co/guide/en/elasticsearch/reference/current/rpm.html) and [Debian](https://www.elastic.co/guide/en/elasticsearch/reference/current/deb.html) package distributions, and the `data` directory under the root of the Elasticsearch installation for the [tar and zip](https://www.elastic.co/guide/en/elasticsearch/reference/current/zip-targz.html) archive distributions)

- [GC logging](https://www.elastic.co/guide/en/elasticsearch/reference/current/gc-logging.html)

  ```properties

  ```

- [Temp directory](https://www.elastic.co/guide/en/elasticsearch/reference/current/es-tmpdir.html)

  ​	set the `$ES_TMPDIR` environment variable to point to it before starting Elasticsearch.

  ​elasticsearch.yml

```properties
# ======================== Elasticsearch Configuration =========================
#
# NOTE: Elasticsearch comes with reasonable defaults for most settings.
#       Before you set out to tweak and tune the configuration, make sure you
#       understand what are you trying to accomplish and the consequences.
#
# The primary way of configuring a node is via this file. This template lists
# the most important settings you may want to configure for a production cluster.
#
# Please consult the documentation for further information on configuration options:
# https://www.elastic.co/guide/en/elasticsearch/reference/index.html
#
# ---------------------------------- Cluster -----------------------------------
#
# Use a descriptive name for your cluster:
#
cluster.name: elk
#
# ------------------------------------ Node ------------------------------------
#
# Use a descriptive name for the node:
#
node.name: elk-node-1
#
# Add custom attributes to the node:
#
#node.attr.rack: r1
#
# ----------------------------------- Paths ------------------------------------
#
# Path to directory where to store the data (separate multiple locations by comma):
#
path.data: /usr/local/elasticsearch-6.6.0/data
#
# Path to log files:
# 注意文件创建权限
path.logs: /var/log/elasticsearch/
#
# ----------------------------------- Memory -----------------------------------
#
# Lock the memory on startup:
#
bootstrap.memory_lock: true
#
# Make sure that the heap size is set to about half the memory available
# on the system and that the owner of the process is allowed to use this
# limit.
#
# Elasticsearch performs poorly when the system is swapping the memory.
#
# ---------------------------------- Network -----------------------------------
#
# Set the bind address to a specific IP (IPv4 or IPv6):
# 允许使用 回环地址或者主机地址链接
network.host: 0.0.0.0
#
# Set a custom port for HTTP:
#
#http.port: 9200
#
# For more information, consult the network module documentation.
#
# --------------------------------- Discovery ----------------------------------
#
# Pass an initial list of hosts to perform discovery when new node is started:
# The default list of hosts is ["127.0.0.1", "[::1]"]
#
#discovery.zen.ping.unicast.hosts: ["host1", "host2"]
#
# Prevent the "split brain" by configuring the majority of nodes (total number of master-eligible nodes / 2 + 1):
#
discovery.zen.minimum_master_nodes: 1
#
# For more information, consult the zen discovery module documentation.
#
# ---------------------------------- Gateway -----------------------------------
#
# Block initial recovery after a full cluster restart until N nodes are started:
#
#gateway.recover_after_nodes: 3
#
# For more information, consult the gateway module documentation.
#
# ---------------------------------- Various -----------------------------------
#
# Require explicit names when deleting indices:
#
#action.destructive_requires_name: true
#If you are using Logstash or Beats then you will most likely require additional index names in your action.auto_create_index setting, 
#and the exact value will depend on your local configuration. If you are unsure of the correct value for your environment,
#you may consider setting the value to * which will allow automatic creation of all indices.
action.auto_create_index: .monitoring*,.watches,.triggered_watches,.watcher-history*,.ml*,*
```

#### 2 重要的系统设置(上生产前需进行设置)

​	[官网文档](https://www.elastic.co/guide/en/elasticsearch/reference/current/system-config.html)

> 1 设置允许打开的文件句柄上限
>
> 2 禁用内存交换
>
> 3 设置虚拟内存的上限 virstual memory
>
> 4 设置线程数量上限
>
> 5 memory locking

##### 1 命令行方式

```sh
#1 执行如下命令
ulimit -n 65536 && swapoff -a && ulimit -u 4096 
sysctl -w vm.max_map_count=262144
swapoff -a

#2 编辑 /etc/security/limits.conf 添加如下内容
* soft memlock unlimited 
* hard memlock unlimited
```

##### 2 配置文件方式

```sh
#1 编辑 /etc/security/limits.conf 添加如下内容
* soft nofile 65536
* hard nofile 65536
* soft nproc 4096
* hard nproc 4096
* soft memlock unlimited
* hard memlock unlimited

#2 编辑 /etc/sysctl.conf
vm.max_map_count = 262144
```

> /etc/security/limits.conf  中 `*` 代表任意用户 , 这里如果使用 elk用户启动的elasticsearch 则可以把 `*`  替换成 `elk` 	

​	上生产未进行设置会保存如下:

```
[1]: max file descriptors [4096] for elasticsearch process is too low, increase to at least [65536]
[2]: memory locking requested for elasticsearch process but memory is not locked
[3]: max virtual memory areas vm.max_map_count [65530] is too low, increase to at least [262144]
```



设置开机启动

```sh
sudo /bin/systemctl daemon-reload
sudo /bin/systemctl enable elasticsearch.service
```

​	启动/停止命令

```sh
sudo systemctl start elasticsearch.service
sudo systemctl stop elasticsearch.service
```



### Logstash

[Logstash 讲解与实战应用](http://blog.51cto.com/tchuairen/1840596)

​	编辑配置文件

```sh
vim  /usr/local/logstash-6.6.0/config/logstash.conf
```



```json
input {
	stdin{}
 	file{
      type => "log"
      path => "/usr/local/src/*.log"
      start_position => "beginning"
 	}
}

output {
  stdout{
    codec => rubydebug{}
  }
  elasticsearch {
    hosts => ["http://127.0.0.1:9200"]
    index => "log-%{+YYYY.MM.dd}"
    #user => "elastic"
    #password => "changeme"
  }
}
```

> 1 elasticsearch 插件会将数据输出到对应的 elasticsearch , 所以启动logstash 前需要确保 elasticsearch 已经启动好并可以连接	
>
> 2 file 输入插件默认使用 “\n” 判断日志中每行的边界位置。error.log 是笔者自己编辑的错误日志，之前由于在复制粘贴日志内容时，忘记在内容末尾换行，导致日志数据始终无法导入到 Elasticsearch 中

启动logstash

```sh
./bin/logstash -f ./config/logstash.conf
```



### Kibana

基础入门:https://www.extlight.com/2017/10/31/Kibana-%E5%9F%BA%E7%A1%80%E5%85%A5%E9%97%A8/

配置说明:https://blog.csdn.net/wumeng2012/article/details/84860485

## 3 案例演示

### 1 [filebeat配置及案例](http://www.xiaot123.com/post/elk_filebeat1)

### 2 [filebeat和logstash收集处理java多行日志](https://www.cnblogs.com/huangweimin/articles/7967790.html)

### 3 [ELK+Filebeat 集中式日志解决方案详解](https://www.ibm.com/developerworks/cn/opensource/os-cn-elk-filebeat/index.html)

### 4 [logstash input jdbc 从数据库中导入数据](http://www.cnblogs.com/licongyu/p/5383334.html)

https://www.cnblogs.com/a-du/p/7611620.html

https://blog.csdn.net/camelcanoe/article/details/79759376



```json
input {
	stdin{}
	jdbc{
      # 数据库
      jdbc_connection_string => "jdbc:mysql://10.99.3.162:3306/vienna-web"
      # 用户名密码
      jdbc_user => "root"
      jdbc_password => "Aa123456"
      # jar包的位置
      jdbc_driver_library => "/root/mysql-connector-java-5.1.47.jar"
      # mysql的Driver
      jdbc_driver_class => "com.mysql.jdbc.Driver"
      #开启分页查询
      jdbc_paging_enabled => true
      jdbc_page_size => "10"
      #statement_filepath => "config-mysql/information.sql"
      statement => "SELECT ORDER_NO,PMS_ORDER_NO,HOTEL_NO,HOTEL_NAME,ROOM_TYPE_NAME,CHECK_IN_TIME,CREATE_DT from or_t_order where CREATE_DT > :sql_last_value order by CREATE_DT limit 100000"
      clean_run => false
      use_column_value => true
      tracking_column => "CREATE_DT"
      #numeric或者timestamp
      tracking_column_type => timestamp
      record_last_run => true
      # last_run_metadata_path => "/etc/logstash/run_metadata.d/my_info"
      schedule => "* * * * *"
      #索引的类型
      type => "vienna-order"
	}
}

output {
  stdout{
    codec => rubydebug{}
  }
  elasticsearch {
    hosts => ["http://127.0.0.1:9200"]
    index => "vienna-order"
    #user => "elastic"
    #password => "changeme"
  }
}
```



## 4 Linux Screen 命令

```sh
screen -S yourname -> 新建一个叫yourname的session
screen -ls -> 列出当前所有的session
screen -r yourname -> 回到yourname这个session
screen -d yourname -> 远程detach某个session
screen -d -r yourname -> 结束当前session并回到yourname这个session
```

在每个screen session 下，所有命令都以 ctrl+a(C-a) 开始

```sh
C-a ? # 显示所有键绑定信息
C-a c # 创建一个新的运行shell的窗口并切换到该窗口
C-a n # Next，切换到下一个 window 
C-a p # Previous，切换到前一个 window 
C-a 0..9 # 切换到第 0..9 个 window
Ctrl+a [Space] # 由视窗0循序切换到视窗9
C-a C-a # 在两个最近使用的 window 间切换 
C-a x # 锁住当前的 window，需用用户密码解锁
C-a d # detach，暂时离开当前session，将目前的 screen session (可能含有多个 windows) 丢到后台执行，并会回到还没进 screen 时的状态，此时在 screen session 里，每个 window 内运行的 process (无论是前台/后台)都在继续执行，即使 logout 也不影响。 
C-a z # 把当前session放到后台执行，用 shell 的 fg 命令则可回去。
C-a w # 显示所有窗口列表
C-a t # Time，显示当前时间，和系统的 load 
C-a k # kill window，强行关闭当前的 window
C-a [ # 进入 copy mode，在 copy mode 下可以回滚、搜索、复制就像用使用 vi 一样
    C-b Backward，PageUp 
    C-f Forward，PageDown 
    H(大写) High，将光标移至左上角 
    L Low，将光标移至左下角 
    0 移到行首 
    $ 行末 
    w forward one word，以字为单位往前移 
    b backward one word，以字为单位往后移 
    Space 第一次按为标记区起点，第二次按为终点 
    Esc 结束 copy mode 
C-a ] # Paste，把刚刚在 copy mode 选定的内容贴上
```

​	连接 screen 后 , 

```sh
[root@localhost src]# screen -r f1
There is a screen on:
        28024.f1        (Attached)
There is no screen to be resumed matching f1.
[root@localhost src]# vim aa.txt

dfsf
lsdfsd
sdfsd
~
~
~
"aa.txt" [新] 3L, 18C 已写入
[root@localhost src]#

```

当键入 `C-a d`  暂时离开当前screen 时 , 显示信息如下

```sh
[detached from 28024.f1]
```