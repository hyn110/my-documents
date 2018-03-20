# 27_kafka

## 1 JMS	

​	JMS : java message service，通常有三个主要部分 ：消息生产者，消息服务（队列），消息消费者。

​	消息服务（队列）可分为 ：queue(点对点)，topic(发布/订阅)

## 2 kafka

​	**只有JMS规范中的 topic ，没有queue ！！！**消费者使用“拉”模式消费消息

​	高吞吐量的分布式发布订阅消息系统

- `Broker` Kafka集群包含一个或多个服务器，这种服务器被称为broker
- `Topic` 每条发布到Kafka集群的消息都有一个类别，这个类别被称为Topic。(物理上不同Topic的消息分开存储，逻辑上一个Topic的消息虽然保存于一个或多个broker上但用户只需指定消息的Topic即可生产或消费数据而不必关心数据存于何处)
- `Partition` Partition是物理上的概念，每个Topic包含一个或多个Partition.
- `Producer` 负责发布消息到Kafka broker
- `Consumer` 消息消费者，向Kafka broker读取消息的客户端。
- `Consumer Group` 每个Consumer属于一个特定的Consumer Group(可为每个Consumer指定group name，若不指定group name则属于默认的group)。

## 3 常用操作

1. 创建一个topic，带两个备份

```sh
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 2 --partitions 1 --topic my-replicated-topic
```

1. 查看broker的执行情况

```sh
> bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic my-replicated-topic
Topic:my-replicated-topic   PartitionCount:1    ReplicationFactor:3 Configs:
    Topic: my-replicated-topic  Partition: 0    Leader: 1   Replicas: 1,2,0 Isr: 1,2,0
```

1. 生产消息

```
> bin/kafka-console-producer.sh --broker-list localhost:9092 --topic my-replicated-topic
...
my test message 1
my test message 2
^C

```

1. 消费消息

```
> bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --from-beginning --topic my-replicated-topic
...
my test message 1
my test message 2
^C

```

## 4 kafka 在zookeeper上的配置

```sh
[zk: localhost:2181(CONNECTED) 4] ls /
[controller_epoch, controller, brokers, isr_change_notification, consumers, log_dir_event_notification, latest_producer_id_block, config]
```

```
[zk: localhost:2181(CONNECTED) 7] get /controller      
{"version":1,"brokerid":3,"timestamp":"1520585755072"}



```

## 5 副本

​	broker存放消息以消息到达的顺序存放。生产和消费都是副本感知的。支持n-1故障。每个分区都有leader和follower，leader挂掉时，消息分区写入本地log。或者向生产者发送消息确认回执之前，生产者向新的leader发送消息。新leader的选举是isr进行的，第一个注册的follower成为leader

## 6 kafka支持的副本模式

### 1 同步副本

1. producer联系zk识别leader
2. 向leader发送消息
3. leader收到消息写入到本地log
4. follower从 leader  pull消息
5. follower向本地写入log
6. follower向leader发送ack消息
7. leader收到所有follower的ack消息
8. leader向producer回传ack

### 2 异步副本

​	和同步复制的区别在于leader写入本地log之后，直接向client回传ack消息，不需要等待所有follower复制完成。这种模式下不能保证消息投递到所有的broker