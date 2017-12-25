# Oracle

## Oracle 监听服务

### lsnrctl 监听工具

```sh
C:\Users\huangyunning>lsnrctl     # oracle 携带的监听工具

LSNRCTL for 32-bit Windows: Version 10.2.0.1.0 - Production on 25-12月-2017 09:59:30

Copyright (c) 1991, 2005, Oracle.  All rights reserved.

欢迎来到LSNRCTL, 请键入"help"以获得信息。

LSNRCTL> help        # 帮助命令
以下操作可用
星号 (*) 表示修改符或扩展命令:

start               stop                status
services            version             reload
save_config         trace               change_password
quit                exit                set*
show*

LSNRCTL> status
正在连接到 (DESCRIPTION=(ADDRESS=(PROTOCOL=IPC)(KEY=EXTPROC_FOR_XE)))
LISTENER 的 STATUS
------------------------
别名                      LISTENER
版本                      TNSLSNR for 32-bit Windows: Version 10.2.0.1.0 - Production
启动日期                  23-12月-2017 18:25:42
正常运行时间              1 天 15 小时 34 分 5 秒
跟踪级别                  off
安全性                    ON: Local OS Authentication
SNMP                      OFF
默认服务           XE
监听程序参数文件          C:\oraclexe\app\oracle\product\10.2.0\server\network\admin\listener.ora
监听程序日志文件          C:\oraclexe\app\oracle\product\10.2.0\server\network\log\listener.log
监听端点概要...
  (DESCRIPTION=(ADDRESS=(PROTOCOL=ipc)(PIPENAME=\\.\pipe\EXTPROC_FOR_XEipc)))
  (DESCRIPTION=(ADDRESS=(PROTOCOL=tcp)(HOST=huangyunning-PC)(PORT=1521)))
  (DESCRIPTION=(ADDRESS=(PROTOCOL=tcp)(HOST=127.0.0.1)(PORT=7000))(Presentation=HTTP)(Session=RAW))
服务摘要..
服务 "CLRExtProc" 包含 1 个例程。
  例程 "CLRExtProc", 状态 UNKNOWN, 包含此服务的 1 个处理程序...
服务 "PLSExtProc" 包含 1 个例程。
  例程 "PLSExtProc", 状态 UNKNOWN, 包含此服务的 1 个处理程序...
服务 "XEXDB" 包含 1 个例程。
  例程 "xe", 状态 READY, 包含此服务的 1 个处理程序...
服务 "XE_XPT" 包含 1 个例程。
  例程 "xe", 状态 READY, 包含此服务的 1 个处理程序...
服务 "xe" 包含 1 个例程。
  例程 "xe", 状态 READY, 包含此服务的 1 个处理程序...
命令执行成功
```

## 表空间创建和帐号创建

```Sql
/*分为四步 */
/*第1步：创建临时表空间  */
create temporarytablespace user_temp  
tempfile 'D:\oracle\oradata\Oracle9i\user_temp.dbf' 
size 50m  
autoextend on  
next 50m maxsize 20480m  
extent management local;  
 
/*第2步：创建数据表空间  */
create tablespace user_data  
logging  
datafile 'D:\oracle\oradata\Oracle9i\user_data.dbf' 
size 50m  
autoextend on  
next 50m maxsize 20480m  
extent management local;  
 
/*第3步：创建用户并指定表空间  */
create user username identified by password  
default tablespace user_data  
temporary tablespace user_temp;  
 
/*第4步：给用户授予权限  */
grant connect,resource,dba to username;
```

### 创建帐号scott

```mysql
-- Create the user 
create user scott
  identified by tiger
  default tablespace USERS
  temporary tablespace TEMP
  profile DEFAULT;
-- Grant/Revoke role privileges 
grant dba to scott with admin option;
-- Grant/Revoke system privileges 
grant select any table to scott with admin option;
grant update any table to scott with admin option;
grant unlimited tablespace to scott with admin option;
```

> ```
> with admin option 只能在赋予 system privilege 的时使用
> with grant option 只能在赋予 object privilege 的时使用
> 另外的区别是 
> with admin option 通过连续授权时 , 比如 管理员给 a 赋予建表权限 , a 给 b 服务建表权限 , 此时如果管理员撤回 a 的权限 , b 的权限不会被撤回 ; 如果是 with grant option , 则 此时 b 的权限也会被撤回
> ```

### 表空间操作

- 建表且指定表空间

```Sql
create table scott.test (id number(5),name varchar2(10)) tablespace users;
```

- 将scott.test表从users表空间移动到 TEMP 表空间

```Sql
alter table scott.test move tablespace TEMP;
```

- 将用户user1 的 table1 表的查询和更新权限赋予 user2

```Sql
grant select,update on user1.table1 to user2

grant all on user1.table1 to user2; # 授予所有操作权限
```

## 常用操作

### 1 创建表

```sql
create tables person(
	pid number(10),
  	pname varchar2(20)
);
```

### 2 修改表结构

#### 添加列

```mysql
alter table 表名
add
	(gender number(1))
```

> 添加列 gender

#### 修改列的类型

```mysql
alter table 表名
modify
	gender char(2);
```

#### 修改列名

```mysql
alter table 表明
rename column  列名
to 新列名;
```

#### 删除列

```mysql
alter table 表名
drop column	列名;
```

### 3 删除数据

删除表结构

```mysql
drop table 表名 ;  	--- 删除表结构
delete from 表名 ; 	--- 删除表数据
truncate table 表名 ;	--- 截断表(先删表再建表)
```

### 4 Oracle中的事务

#### 事务保存点 savepoint

```mysql
update person set pname='c' where pid=1;
savepoint a1;
update person set pname='b' where pid=2;
rollback to al;  --- 回滚到 a1
commit;
```

### 5 Oracle事务级别

**Oracle支持的事务级别:**

- 序列化
- 可重复度     --- 默认级别
- 读已提交

### 6 约束

#### 1 主键约束

​	主键约束,天然带非空,唯一特性!!!

- 建表时声明主键

```mysql
create table person(
	pid number(10) primary key,
	pname vachar2(10),
);
```

> Oracle数据库主键没有自增长特性,即不能使用 `auto_increment` !!!

- 建表时自定义主键约束名称

```mysql
create table person(
	pid number(10),
	pname varchar2(10),
	constraint pk_person primary key(pid)   --- 声明主键约束的名称为 pk_person ,作用于 pid 列
);
```

#### 2 非空约束和唯一约束

​	`not  null`	非空约束

​	`unique`		唯一约束

```mysql
create table person(
	pid number(10),
	pname varchar(10) unique not null, 		--- 添加唯一约束和非空约束
	constraint pk_person primary key(pid)   --- 声明主键约束的名称为 pk_person ,作用于 pid 列
);
```

#### 3 检查约束

​	语法  : `check(约束条件)`

```mysql
create table person(
	pid number(10),
	pname varchar(10) unique not null, 		
  	gender number(1) check(gender in(0,1))	--- 添加检查约束
	constraint pk_person primary key(pid)  
);
```

#### 4 外键约束

```mysql
create table orders(
	orders_id number(10),
	orders_name varchar2(20),
	constraint pk_orders primary key(orders_id)
);

create table order_detail(
	order_detail_id number(10),
	order_detail_name varchar2(20),
	orders_id number(10),
	constraint pk_order_detail primary key(order_detail_id),
	constraint fk_order_detail_orders foreign key(orders_id) references orders(orders_id)  --- 添加外键约束 
);
```

### 7 创建视图

- 同义词

```mysql
create synonym s_emp for scott.emp;  
```

- 查询语句创建表

```mysql
create table emp as select * from scott.emp;
```

- 创建视图

```mysql
create view view_temp as select e.name,e.job from emp e ;
```

> 视图本身不存储数据,所有数据来源于原表,故修改视图数据会修改原表数据

​	**创建只读视图**

```mysql
create view view_temp2 as select e.name,e.job from emp e with read only; 
```

- 查询视图

```
select * from view_emp;
```

### 8 序列

​	Oracle中没有主键生成策略,用序列的自增特性,可以在oracle中完成自增长的功能.

​	语法:

```mysql
create sequence 序列名
	increment by N 		--- 每次增长的大小
	start with N		--- 起始值
	maxvalue  Max		--- 最大值
	minvalue  Min		--- 最小值
	cache	N;	
```

```mysql
create sequence seq_person;
select seq_person.nextval from dual;
```

### 9 索引

#### 1 单列索引

​	索引是一个单独的、物理的数据库结构，它是某个表中一列或若干列值的集合和相应的指向表中物理标识这些值的数据页的逻辑指针清单。

**使用索引可以大幅度提高查询的效率,但是会影响增删改的效率**

​	语法: