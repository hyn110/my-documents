## 192.168.174.130

[安装教程](http://blog.paascloud.net/2018/09/04/java-env/paascloud/kai-fa-huan-jing/)

### 1 redis

```sh
/usr/local/src/redis/bin/redis-server /usr/local/src/redis/redis.conf
```

### 2 nginx

```
systemctl start nginx
```

> 默认配置文件:  /etc/nginx/nginx.conf

### 3 rocketmq

```
nohup /usr/local/rocketmq/bin/mqnamesrv &

nohup sh /usr/local/rocketmq/bin/mqbroker -n localhost:9876 -c /usr/local/rocketmq/conf/broker.conf &
```

> web控制台 : http://localhost:12581/#/

### 4 rabbitmq

web控制台: 192.168.174.130:15672

帐号: admin / 123456

```
/sbin/service rabbitmq-server start

/sbin/service rabbitmq-server stop

# 查看用户列表
rabbitmqctl list_users
```

> ```
> node           : rabbit@example
> home dir       : /var/lib/rabbitmq
> config file(s) : /etc/rabbitmq/advanced.config
>                : /etc/rabbitmq/rabbitmq.conf
> ```

### 5 启动zookeeper

```
/usr/local/src/zookeeper00/bin/zkServer.sh start
/usr/local/src/zookeeper01/bin/zkServer.sh start
/usr/local/src/zookeeper02/bin/zkServer.sh start

/usr/local/src/zookeeper00/bin/zkServer.sh status
```

### 6 zkui

​	web控制台:192.168.174.130:9090

​	帐号: admin/123456

```
nohup java -jar /usr/local/01_zkui/zkui-2.0.jar &
```

### 7 rocketmq-console

​	web控制台:192.168.174.130:12581

​	帐号: admin/123456

```
nohup java -jar /usr/local/00_rocketmq-console/rocketmq-console-ng-1.0.1.jar --server.port=12581 --rocketmq.config.namesrvAddr=192.168.174.130:9876  &
```

### 8 zkweb

​	web控制台:192.168.174.130:9345

```
nohup java -jar /usr/local/02_zkweb/zooweb-2.1.jar &
```

### 放行端口

```
firewall-cmd --permanent --add-port=15672/tcp
firewall-cmd --permanent --add-port=12581/tcp

firewall-cmd --permanent --add-port=2181/tcp
firewall-cmd --permanent --add-port=2182/tcp
firewall-cmd --permanent --add-port=2183/tcp

firewall-cmd --permanent --add-port=12581/tcp
firewall-cmd --permanent --add-port=9090/tcp
firewall-cmd --permanent --add-port=9345/tcp
firewall-cmd --reload
```