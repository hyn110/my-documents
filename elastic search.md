## 1 配置

### 1 安装

[6.5官网文档](https://www.elastic.co/guide/en/elasticsearch/reference/6.5/zip-targz.html)

####  1 .tar.gz 包安装

```sh
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-6.5.4.tar.gz
tar -xzf elasticsearch-6.5.4.tar.gz
cd elasticsearch-6.5.4/ 
```

​	以服务的方式启动

```sh
./bin/elasticsearch -d -p pid
```

​	通过命令行配置,使用 `-E` 进行设置

```sh
./bin/elasticsearch -d -Ecluster.name=my_cluster -Enode.name=node_1
```

> -Ecluster.name    	集群名
>
> -Enode.name 	节点名

​	在 elasticsearch.yml 中设置允许自动创建 `X-PACK` 索引

```
action.auto_create_index: .monitoring*,.watches,.triggered_watches,.watcher-history*,.ml*
```

#### 2 RPM 安装

1 安装签名

```sh
rpm --import https://artifacts.elastic.co/GPG-KEY-elasticsearch
```

2 更新镜像仓库

​	在 `/etc/yum.repos.d/` 目录下创建文件 `elasticsearch.repo`  文件 , 内容如下:

```properties
[elasticsearch-6.x]
name=Elasticsearch repository for 6.x packages
baseurl=https://artifacts.elastic.co/packages/6.x/yum
gpgcheck=1
gpgkey=https://artifacts.elastic.co/GPG-KEY-elasticsearch
enabled=1
autorefresh=1
type=rpm-md
```

> 基于 RedHat 系统 , 其它系统百度

3 安装

```sh
sudo yum install elasticsearch 
```
### 2 API 约定 (API Conventions)

#### 1 Multiple Indices 多索引操作

ES 支持同时对多个索引进行操作 , 支持的方式如下:

​	1 使用 `,` 隔开多个索引 , 例如 `test1,test2,test3`

​	2 使用通配符 , 如 `test*`  `te*st`

​	3 使用 `-` 排除特定索引 , 如 `test*,-test3` 

此外 , ES的多索引 restful  API 的url中支持如下查询参数:

​	1 `ignore_unavailable`   可选 `true` 或 `false`  , 是否忽略不可用的索引(不存在或已关闭的索引)

​	2 `allow_no_indices`  是否抛出异常,当通配符没匹配到对应的索引

​	3 `expand_wildcards`  可选值 `open,closed,none,all` ,  取 `closed` 通配符将只匹配属于关闭状态的索引 , 其他值以此类推

> ES的单索引API , 如 Document APIs 和 Single-index alias APIs 不支持多索引操作

#### 2 索引名支持日期筛选

​	类似 `logstash-2024.03.22` 的索引名,符合日期表达式时,可通过日期表达式进行匹配 , 并且支持日期筛选 , 日期匹配索引名的格式通常如下:

```sh
#logstash-2024.03.22
<static_name{date_math_expr{date_format|time_zone}}>
```

> `static_name`   不发生变化的部分
>
> `date_math_expr`  动态变化的部分 , 日期表达式
>
> `date_format` 日期格式 , 默认 YYYY.MM.dd
>
> `time_zone` 时区 , 可选 , 默认 utc

| Expression                               | Resolves to         |
| ---------------------------------------- | ------------------- |
| `<logstash-{now/d}>`                     | logstash-2024.03.22 |
| ` <logstash-{now/M}>`                    | logstash-2024.03.01 |
| ` <logstash-{now/M{YYYY.MM}}>`           | logstash-2024.03    |
| ` <logstash-{now/M-1M{YYYY.MM}}>`        | logstash-2024.02    |
| ` <logstash-{now/d{YYYY.MM.dd+12:00}}>` | logstash-2024.03.23 |

​	**日期表达式支持**: 大多数参数接受格式化日期表达式 , 如果 `gt` `lt`  或日期聚合中使用 `from to`  . 表达式设定的日期为 now 或者 使用日期字符 , 并用`||` 隔开表达式 

> +1h		增加一个小时
>
> -1d		减少一天
>
> /d		时间向下取整到最近的一天 , 即当天的 00:00
>
> now + 1h/d  
>
> 2019-01-25||+1M/d	2019-02-25 00:00:00 

### 3 Document APIs 

#### 1 Single Document APIs

##### 1 Inded API 索引操作

1 创建索引

```json
PUT twitter/_doc/1
{
  "user":"fmi110",
  "post_date":"2019-01-31T00:00:00",
  "message":"trying out elasticsearch"
}
```

```json
#! Deprecation: the default number of shards will change from [5] to [1] in 7.0.0; if you wish to continue using the default of [5] shards, you must manage this on the create index request or with an index template
{
  "_index" : "twitter",
  "_type" : "_doc",
  "_id" : "1",
  "_version" : 1,
  "result" : "created",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 0,
  "_primary_term" : 1
}
```







#### 2 Multi Document APIs