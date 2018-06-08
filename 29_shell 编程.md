# Shell 编程

## 1 构建基本的脚本

1 创建 shell 脚本 , 必须在第一行指定要使用的shell :

```sh
#!/bin/bash
```

2 输出消息 :

```
echo 这是消息
```

​	当希望命令的输出和输出的消息在同一行显示时,加 	 ` -n`

```shell
echo -n "this is a message : "
```

3 通过 `$` 或 `${}`  引用变量

```shell
echo $USER
echo $HOME
# 转义后不会被当成变量处理
echo "Cost of the book is \$15"
```

> `"$USER"`   双引号中的 USER 被当成变量处理
>
> `'$USER'`  单引号中的  `&`  被当成字符处理 !!!!  

4 命令替换

有时希望把命令执行的结果赋值给变量,通过以下两种方式实现

```shell
# 方式一 , 使用 ``
testing=`date`
#方式二,使用 $() 格式
testing=$(date)
```

> 注意 : 变量赋值的等号两边不能存在空格!!!!

​	下面的脚本通过命令替换获得当前日期并用它来生成唯一文件名

```shell
#!/bin/bash
today=$(date +%y%m%d)  # +%y 年份用两位数输出
ls /usr/bin -al > log.$today
```

5 输出重定向 `>`

```shell
date > date.txt		
```

6 输入重定向 `<`

```shell
[root@VM_0_6_centos ~]# wc < anaconda-ks.cfg 
 148  228 2523
```

> 文本的行数     词数     字节数

7 管道 `|`

​	piping 管道 , 会将前面命令的输出交给后面的程序运行 .

```shell
rpm -qa | sort | more
```

> Linux系统实际上会同时运行上面的命令 , 在系统内部将他们连接起来 , 在第一个命令产生输出的同时,输出会被立即送个第二个命令..

8 执行整数数学运算  `$[]`

​	注意 bash shell 只支持整数运算!!!要进行实际的数学运算需要用其他的工具

```shell
var1=$[1+5]
var2=$[$var1 + 2]
```

9 退出脚本  exit

​	`exit`  

## 2 结构化命令

### 1 if - then -else

​	格式 :

```shell
if command1
then
	command2
elif command3
then
	command4
else
	command5
fi
```

```sh
if command1; then
	command2;
fi
```

​	if 语句的判断条件是 command1 命令的退出状态码 , **如果退出码是 0 则执行 if 语句**

```shell
#!/bin/bash
# 演示案例
testuser=NOSuchUser
if grep $testuser /etc/passwd
then
	echo "用户 $testuser 存在"
elif ls -d /home/$testuser
then
	echo "$testuser 有用户目录"
else
	echo nothing
fi
```

### 2 test 命令判断条件

​	if - then 语句只能测试 if 后的命令退出状态码来执行 , 要测试其他条件 , 则需要使用 test 命令 , 格式 :

```shell
if test condition
then
	commands
fi
```

​	**如果 condition 条件成立 , 则 test 测试返回 0 , 则执行 if 语句**

​	更简洁的写法 :

```sh
if [ condition ]
then
	commands
fi
```

> **`[]`  前后必须添加空格!!!否则报错**	

**test 命令可以判断三类条件 :**

#### 1 数值比较

| 比较 | 描述 |
| ---- | ---- |
| -eq  | =    |
| -ge  | >=   |
| -gt  | >    |
| -le  | <=   |
| -ne  | !=   |



#### 2 字符串比较

| 比较   | 描述     |
| ------ | -------- |
| = / == | 相同     |
| !=     | 不同     |
| <      | 小于     |
| >      | 大于     |
| -n     | 长度 > 0 |
| -z     | 长度 = 0 |

**注意 : **

**1. 脚本中出现大于号或小于号 , 必须进行转义  `/<`  `/>`  否则脚本会将其当成重定向符处理 , 导致意向不到的结果 !!!**

**2.使用字符串比较大小时 , 一定要注意 , sort 名利处理的大写字母的方法和 test命令相反 , 最好先自测 !!!**

#### 3 文件条件

| 比较                | 描述                 |
| ------------------- | -------------------- |
| -d    file          | file 是目录且存在    |
| -e    file          | file存在             |
| -f     file         | file是文件且存在     |
| -r    file          | 存在且可读           |
| -w   file           | 存在且可写           |
| -x    file          | 存在且可执行         |
| -O   file           | 存在且属于当前用户   |
| -G   file           | 存在且属于当前用户组 |
| file1   -nt   file2 | newer than           |
| file1   -ot   file2 | older  than          |
| -s   file           | 存在并非空           |



```shell
#!/bin/bash
# 测试代码
val1=testing
val2=''

if [ -n $val1 ]
then
	echo '字符串 $val1 非空'
fi

if [ -z $val2 ]; then
	echo '$val2 为空'
fi

if [ -z $val3 ]; then
	echo '$val3 没定义过,但是长度仍然是0'
fi
```

### 3 复合条件

```shell
[ condition1 ] && [ condition2 ]
[ condition1 ] || [ condition2 ]
```

```shell
#!/bin/bash
# 测试
if [ -d $HOME ] && [ -r $HOME/testing ]
then 
	echo '文件存在并且可写'
fi
```

### 4 if - then  高级特性

#### 1 使用双括号

```
(( expression ))
```

​	expression 是任意的数学赋值或比较表达式 , 并且双括号内的大(小)于号不需要转义 !!!

| 符号  | 描述   |
| ----- | ------ |
| val++ | +1     |
| val-- | -1     |
| ++val | +1     |
| --val | -1     |
| !     | 取反   |
| ~     | 位求反 |
| **    | 幂运算 |
| <<    | 左位移 |
| >>    |        |
| &     | 与     |
| \|    |        |
| &&    |        |
| \|\|  |        |

#### 2 双方括号

​	双方括号是针对字符串比较的高级特性 ,提供了 test 命令没有的特性 -- **模式匹配(pattern matching)**

```shell
[[ expression ]]
```

```shell
#!/bin/bash
if [[ $USER == r* ]]
then
	echo 'hello $USER'
else
	echo 'sorry,do not know you'
fi
```

### 5 case 命令

​	格式 如下 , 注意  **命令行后结束符有    `;;`**

```shell
case variable in
pattern1 | pattern2)
	command1 ;;
pattern3) 
	command2 ;;
*) 
	default commands ;;
esac
```

```shell
#!/bin/bash
case $USER in
rich | banan)
	echo 'welcome' ;;
testing)
	echo 'test just';;
*)
	echo 'sorry'
esac
```

### 6 for 命令

```shell
for var in list
do
	commands
done
```

```shell
#!/bin/bash
# 测试
for ch in a b c d 
do
	echo "当前的字符为 $ch"
done
```

> 当前的字符为 a
> 当前的字符为 b
> 当前的字符为 c
> 当前的字符为 d

```shell
#!/bin/bash
# 测试
list="aaa bbb ccc"
for ch in $list 
do
	echo $ch
done
```

> aaa
>
> bbb
>
> ccc

### 7 更改字段分隔符 IFS

​	默认情况下 for 命令切割字段的的分隔符如下 :

1. *空格*

2. *制表符*

3. *换行符*

环境变量 IFS 可以控制字段分隔符的字符 , 比如设置字段分割符为换行符

```shell
# 只识别换行符
IFS=$'\n'
# 识别换行符 , 冒号 ,分号和双引号
IFS=$'\n':;"
```

​	临时修改 ,使用后再恢复符方法

```shell
IFS.OLD=$IFS
IFS=$'\n'    		# 修改分隔符
....
IFS=$IFS.OLD		# 恢复分隔符
```

```shell
#!/bin/bash
# 测试
for file in /home/fmi110/b* /home/fmi110/a*
do
	if [ -d "$file" ]    # 文件名中可能含空格,所以加双引号,否则报错
	then
		echo $file is a 目录
	elif [ -f "$file" ]
	then
		echo $file 是文件
	else
		echo $file 不存在
	fi
done
```

### 8 C 语言风格的for循环

​	这里使用到了 双括号 特性 !!!

```shell
for (( i=0;i<10;i++ ))
do
	echo $i
done
```

### 9 while 命令

```shell
while test command
do
	commands;
done
```

```shell
#!/bin/bash
var=5
while [ $var -gt 0 ]
do
	echo $var
	var=$[ $var - 1 ]
done
```

### 10 until 命令

```shell
until test commands
do
	command2
done
```

### 11 循环处理文件数据

​	通常遍历存在在文件中的数据 , 可能需要使用

 	1. 嵌套循环
	2. 修改 IFS 环境变量

```shell
#!/bin/bash	
IFS.O=$IFS
IFS=$'\n'

for entry in $(cat /etc/passwd)
do
	echo "Values in $entry -"
	IFS=:						# 修改分隔符
	for value in $entry
	do
		echo "   $value"
	done
done
```

> 1. 外部循环输出 /etc/passwd 的每一行数据
> 2. 内部循环输出每行数据的各个值

### 12 break

​	用于控制跳出循环

```shell
break
break n     # 跳出 n 层循环
```

### 13 continue

​	用于继续当前循环

### 14 处理循环的输出

​	对循环的输出可以使用管道或进行重定向 , 这可以通过在 done 之后添加一个处理命令实现

```shell
#!/bin/bash
for file in /home/fmi110/*
do
	if [ -d "$file" ]
	then
		echo "$file 是一个目录"
	elif [ -f "$file" ]
	then
		echo "$file 是一个文件"
	fi
done > output.txt
```

​	脚本执行后 , echo 的输出不再显示在屏幕上 , 而是输出到 output.txt 文件中 !!!

### 15 实例

​	创建多个用户账号 , 账号存储在 user.csv 文件中 , 文件格式如下 :

```
用户id,用户名称
```

```
文件内容 :
10001,fmi110
10002,Li Lei
10003,Han MeiMei
```

```shell
#!/bin/bash
input="user.csv"
IFS=','
while read -r userid name
do
	useradd -c "$name" -m $userid
done < "$input"
```

> 1.  `<`  重定向标准输入 ,使 read 读取文件的内容
> 2. read -r  按行读取文件内容 , 分隔后分别赋值给 userid , name

## 3 处理用户输入

### 1 命令行参数

```shell
cat /home/fmi110/demo.txt
```

​	上述命令中  `/home/fmi110/demo.txt` 是命令 cat 的入参 , shell 中位置参数可以如下读取 :  $0 是程序名 , $1 第一个参数 , $2 第二个参数 , ${10} 第10个参数 ...以此类推

### 2 读取脚本名

​	$0 指向脚本名 , 但是当输入的命令是整个脚本的绝对路径时 , $0 的值是携带了路径的字符串 ,如下 :

```shell
/home/fmi110/install.sh
```

​	此时如果想 只获取脚本名称   `install.sh`  可用如下命令 basename :

```shell
# 获取当前脚本的名字,不包含路径字符
filename=$(basename $0)
```

### 3 输入参数统计

​	`$#`  变量记录了脚本在运行时命令行上输入的参数的个数

```shell
$ cat /home/a.txt  /home/b.txt

#!/bin/bash
echo "输入参数的个数为 : $#"
```

​	如果希望使用   `${$#}`  获取最后一个参数会失败 , 正确用法:   `${!#}`	

### 4 获取所有的命令行参数

​	`$*`  和 `$@`  都可以访问所有的参数 . 区别如下 :

​	 `$*`  将所有参数当作一个单词保存 ;  `$@`  将所有参数当作同一字符串中的多个独立的单词 , 更方便进行遍历操作

```shell
#!/bin/bash

echo '====== testing $*'
count=1
for param in "$*"
do
	echo  $count : $param
	count=$[ $count + 1 ]
done

echo '====== testing $#'
count=1
for param in "$@"
do
	echo  $count : $param
	count=$[ $count + 1 ]
done
```

执行效果 :

```shell
[root@VM_0_6_centos ~]# ./test.sh aaa bbb ccc
====== testing $*
1 : aaa bbb ccc
====== testing $#
1 : aaa
2 : bbb
3 : ccc
```

### 5 shift 移动变量

​	shift 依次将参数往左移动

```shell
#!/bin/bash	
count=1
while [ -n "$1" ]
do
	echo $count : $1
	count=$[ $count + 1 ]
	shift
done
```

执行效果 :

```shell
[root@VM_0_6_centos ~]# ./test.sh aaa bbb ccc
1 : aaa
2 : bbb
3 : ccc
```

> 可以看到  $1 依次获取到了命令行上的参数

### 6 简单的处理选项

​	类似命令        `tar -z -x filepath`  `-z`  `-x`  是选项 ,file 是参数 , 我们如下实现几个简单的版本 :

​	识别   `./test.sh -a -b -c -- file`  命令 , 这里用  `--`  分隔了选项和参数

```shell
#!/bin/bash
while [ -n "$1" ]
do
	case "$1" in
	-a) 
		echo "-a 选项"
	;;
	-b)
		echo "-b 选项"
	;;
	-c)
		echo "-c 选项"
	;;
	--)
		shift	# 左移参数
		break
	;;
	esac
	shift		# 左移参数
done

for param in "$@"
do
	echo $param
done
```

​	运行效果 :

```shell
[root@VM_0_6_centos ~]# ./test.sh -b -c -a -- fmi110  fengmi
-b 选项
-c 选项
-a 选项
fmi110
fengmi
```

​	上面的选项处理有诸多限制 ,比如当出现    `-ab`  时 就没法处理了 ,此时需要更高级的处理方式

### 7 使用高级的 getopts 处理选项

​	命令格式 :

```shell
getopts optstring varible
```

​	`optstring`  是关键所在 , 它定义了命令行**有效的选项字母** , 还定义了哪些选项字母需要参数值 , 看个例子

```shell
getopts ab:cd opt
getopts :ab:cd opt    # optstring 前面加 ':' , 可以屏蔽命令的错误信息 
```

> 1. 有效的选项字母为 : a  b  c  d
> 2. 选项 b 后面有一个  `:`  , 说明选项 b 后需要参数
> 3. 参数将被保存在变量  opt 中
> 4. 如果选项需要一个参数值 , 如 b 选项 , 该参数的值会被保存在环境变量   `OPTARG`  中
> 5. 环境变量   `OPTIND`  保存了参数列表中 getopts 正在处理的参数的位置

​	示例如下 :

```shell
#!/bin/bash
while getopts :ab:cd opt
do
        case "$opt" in
                a) echo "选项 a";;
                b) echo "选项 b,携带的参数 $OPTARG";;
                c) echo "选项 c";;
                d) echo "选项 d";;
                *) echo "未知选项" ;;
        esac
done
#
shift $[ $OPTIND - 1 ]		# 移除前面的选项 , 留下参数列表
count=0
for p in "$@"
do
        echo "参数 : $p"
        count=$[ $count +1 ]
done
```

​	执行效果 :

```sh
[root@VM_0_6_centos ~]# ./test.sh -a -b aaa -cd bbb ccc
选项 a
选项 b,携带的参数 aaa
选项 c
选项 d
参数 : bbb
参数 : ccc
```

### 8 read 获取用户输入

#### 1 基本输入

​	read 命令用户与用户进行交互 , 获取用户输入的数据

```shell
read -p "请输入你的姓名 : " name
```

> 命令会读取用户输入的字符串 ,并保存到 变量 name 中

​	如果 read 后面只跟一个变量 ,则用户输入的内容全部赋值给该变量 , 如果提供了多个变量 , 会将用户输入依次赋值给变量 , 用户输入的分隔符可用环境变量 IFS 控制 , 默认是 空格 / 制表符  , 如 :

```shell
#!/bin/bash	
read -p "请输入你的名字 : " first  second
echo "first = $first"
echo "second = $second"
```

​	运行效果 :

```shell
[root@VM_0_6_centos ~]# ./test.sh 
请输入你的名字 : nigulasi kai
first = nigulasi
second = kai

[root@VM_0_6_centos ~]# ./test.sh 
请输入你的名字 : aaa bbb ccc
first = aaa
second = bbb ccc
```

#### 2 超时	

​	read 默认是阻塞的 , 直到获取用户的输入 , 某些情况下我们希望有超时处理 , 此时用  `-t` 指定等待的秒数即可 , 超时时 , read 命令会返回一个非0状态码

```shell
read -t 5 -p "请输入内容 , 5s 后自动超时 : " arg
```

> 命令会等待 5 s

#### 3 隐藏方式读取

​	输入密码时 , 我们不希望用户输入显示在显示器上 ,此时用选项  `-s`

```shell
read -s -p "please enter your password : " pwd
```

#### 4 从文件中读取

​	read 命令也可以读取 linux 上的文件 , 每次调用 read 命令, 它会读取文件中的一行文本 , 当读取结束时 ,read会退出并返回 非0 状态码 .

​	其中最难的部分是将文件的数据传给 read 命令 , 最常见的方式是用 cat 通过管道输入 ; 此外也可以通过输入重定向实现.

```shell
#!/bin/bash
echo "cat + 管道方式读取"
#
cat input.txt | while read line		# 读取文件数据
do
	echo $line
done

echo "输入重定向方式读取"
#
while read line
do
	echo $line
done < input.txt  	# 输入重定向
```

​	运行效果 :

```shell
[root@VM_0_6_centos ~]# ./test.sh 
cat + 管道方式读取
aaa
bbb
ccc
输入重定向方式读取
aaa
bbb
ccc
```


## 4 呈现数据

​	linux 将每个对象当作文件处理 ,每个进程一次最多可以有9个文件描述符 . 目前 bash shell 保留了前三个文件描述符,作特殊用途 :

| 文件描述符 | 缩写   | 描述     |
| ---------- | ------ | -------- |
| 0          | STDIN  | 标准输入 |
| 1          | STDOUT | 标准输出 |
| 2          | STDERR | 标准错误 |

### 1 重定向错误

```shell
# 重定向错误到 test.txt
ls -al badfile 2> test.txt
# 同时重定向输出和错误到同一文件
ls -al badfile &> test.txt
```

### 2 临时重定向

​	有时在脚本中需要临时将消息重定向 , 则必须在文件描述符数字前加  `&`

```
echo "this is an error message" >&2
```

### 3 永久重定向

​	通过 exec 命令可以在 shell 脚本运行期间重定向某个特定的文件描述符

```shell
#!/bin/bash
exec 1>testout.log  # 重定向标准输入
exec 2>testerr.log
# 重定向文件
exec 3>testout.log
```

```shell
#!/bin/bash
exec 0<testfile
count=1
while read line
do
	echo $line
	
done
```

> 标准输入重定向为文件输入 , 所以 read 读取的是文件输入

​	重定向后恢复的办法 :

```shell
exec 3>&1
exec 1>test.log
.....
exec 1>&3   # 恢复标准输出
##############
exec 6<&0
exec 0<testfile
....
exec 0<&6
```

### 4 关闭文件描述符

```
exec 3>&-
```

​	一旦文件描述符被关闭 , 就不能在脚本中向它写入数据了 , 否则shell 会生成错误信息 . 如果重新使用文件描述符指向该文件, 新文件会覆盖旧文件.

### 5 列出打开的文件描述符

```shell
[root@VM_0_6_centos ~]# /usr/sbin/lsof -a -p $$ -d 0,1,2
COMMAND   PID USER   FD   TYPE DEVICE SIZE/OFF NODE NAME
bash    17182 root    0u   CHR  136,0      0t0    3 /dev/pts/0
bash    17182 root    1u   CHR  136,0      0t0    3 /dev/pts/0
bash    17182 root    2u   CHR  136,0      0t0    3 /dev/pts/0
```

> COMMAND : 命令的前9个字符
>
> FD : 文件描述符以及访问类型 ( r , w , u 表示可读可写)

### 6 阻止命令输出

​	`/dev/null`  文件黑洞 , 输出重定向到此将什么也看不到

```shell
ls -al / > /dev/null
```

### 7 tee 同时输出到显示器和文件

​	tee 命令相当于管道的 T 型接头 , 它将从 STDIN 过来的数据同属发往两处 : STDOUT 和 tee 命令指定的文件

```shell
tee filename
```

```shell
date | tee testfile
```

> 显示器和 testfile 将同时看到日期时间

### 8 实例

​	脚本读取 .csv 格式的文件 , 输出 insert 语句
 	memeber.csv

```
lily,xxx,beijing
fmi110,ooo,laibin
```


```shell
#!/bin/bash
outfile="members.sql"
# 设置字段分隔符
IFS=',' 
while read name address city 
do
	cat >> $outfile << EOF
	insert into members (name,address,city) values ('$name','$address','$city');
EOF
done < ${1}
```

> 1. `done<${1}`  重定向了标准输入, 指明了带读取的文件
> 2. `cat>>$outfile<<EOF`   输出重定向将 cat 命令的输出追加到 outfile ,
> 3. `<<` 重定向了 cat 的输入 ,  EOF 符号标记了追加到文件数据的起止.

​	运行效果 :

```shell
[root@VM_0_6_centos ~]# ./test.sh members.csv 
[root@VM_0_6_centos ~]# ll
total 20
-rw-------. 1 root root 2523 Apr 21  2016 anaconda-ks.cfg
-rw-r--r--  1 root root   12 May 26 12:57 input.txt
-rw-r--r--  1 root root   35 May 26 17:50 members.csv
-rw-r--r--  1 root root  149 May 26 17:50 members.sql
-rwxr--r--  1 root root  230 May 26 17:50 test.sh
[root@VM_0_6_centos ~]# cat members.sql 
	insert into members (name,address,city) values ('lily','xxx','beijing');
	insert into members (name,address,city) values ('fmi110','ooo','laibin');
```

## 5 控制脚本

​	目前为止 , 我们的运行脚本的唯一方式是以实时模式在命令行界面上直接运行;有一些控制方法可以向脚本发送信号 ,修改脚本的优先级 ,以及在脚本运行时切换到运行模式

### 1 处理信号

#### 1 中断进程

​	ctrl + c 会发送 sigint 信号 , 停止shell 中当前运行的进程

```
CTRL + C
```

#### 2 暂停进程

​	ctrl + z 会生成 sigtstp 信号.

​	停止(stopping) 进程和终止(terminating)进程的区别 : 停止进程会让程序继续保留在内存中 , 并能从上一次停止的地方继续运行

```shell
[root@VM_0_6_centos ~]# sleep 100
^Z
[1]+  Stopped                 sleep 100
```

> [1] 表示shell 分配的作业号(job number)

#### 3 捕获信号

​	trap 命令可以指定从shell 中拦截 Linux 信号 , 交个你处理,而不是shell处理

```shell
trap commands signals
```

```shell
#!/bin/bash
trap "echo '拦截到 ctrl-c 组合键,组织程序退出'" SIGINT
count=1
while [ count -le 10 ]
do
	echo $count
	count=$[ $count + 1 ]
done
```

​	本例中通过 trap 命令在每次检测到 SIGINT 信号时 ,输出一个语句.捕获这个信息阻止用户通过 Ctrl+ c 组合键退出程序

### 2 后台模式运行脚本

​	要让脚本以后台模式运行只需要在命令后加个  & 符即可

```
./test.sh &
```

```shell
[root@VM_0_6_centos ~]# ./test.sh &
[2] 20940
[root@VM_0_6_centos ~]# ps
  PID TTY          TIME CMD
17182 pts/0    00:00:00 bash
19909 pts/0    00:00:00 sleep
20950 pts/0    00:00:00 ps
20951 pts/0    00:00:00 ps
```

​	**注意 : 在 ps 命令的输出中 , 每一个后台进程都和终端会话(pts/0) 联系在一起 , 如果终端会话退出 , 那么后台进程也会随之退出 !!!**

### 3 在非控制台下运行脚本

​	有时希望在终端会话中启动脚本, 然后让脚本一直在后台运行  , 即使退出终端会话,也继续运行 . 这可以用 nohup 命令实现

```shell
nohup ./test.sh &
```

​	由于 nohup 命令会解除终端和进程的关联 , 进程也就不再同 STDOUT 和 STDERR 联系在一起 , 为了保存该命令产生的输出 , nohup 命令会自动将 STDOUT STDERR 的消息重定向到一个名为 nohup.out 的文件中.

### 4 作业控制

1 查看作业

```
[root@VM_0_6_centos ~]# jobs -l
[1]- 19909 Stopped                 sleep 100
[2]+ 1999  Running				  ./test.sh
```

> 1. 带 + 的作业被当成是默认作业 , 使用作业控制命令时 , 如果未指定作业号 , 该作业会被当成操作对象
> 2. 带 -  的作业会是下一个默认对象 ,当当前默认作业处理完成

2 重启停止的作业

​	以后台模式重启一个作业 , 用  bg 命令加上作业号

```shell
bg 1
```

​	以前台模式重启作业 , 用 fg 命令

```shell
fg 1
```

### 5 定时运行作业

#### 1 cron 时间表

​	corn格式 :     `分 	时	日	月	周	command`

​	[cron表达式在线生成](http://cron.qqe2.com/)

​	如果要在每个月的最后一天执行命令 , 直接使用cron 表达式做不到 , 常用的方法是加一条 if-then 语句来检查明天日期是不是 01 :

```shell
00 12 * * * if [ `date +%d -d tomorrow` = 01 ] ; then command
```

> 它会在每天中午12点检查是不是当月最后一天 , 是则 cron 将会运行命令

​	**构建cron时间表** :  每个系统用户都可以用自己的 cron 时间表来运行安排好的任务 , crontab 命令可以处理 cron 时间表

```shell
[root@VM_0_6_centos ~]# crontab -l
*/1 * * * * /usr/local/qcloud/stargate/admin/start.sh > /dev/null 2>&1 &
*/20 * * * * /usr/sbin/ntpdate ntpupdate.tencentyun.com >/dev/null &
```

> -l   : 列出时间表
>
> -e  : 编辑时间表 , crontab 会启动一个编辑器打开文件 , 在文件中添加 cron 任务列表

​	如果脚本对运行时间的精确性要求不高 , 用预配置的cron脚本目录会更方便 , 有四个目录 :

```shell
[root@VM_0_6_centos ~]# ls /etc/cron.*ly
/etc/cron.daily:		# 按天周期
0yum-daily.cron  logrotate  man-db.cron  mlocate

/etc/cron.hourly:		# 按小时周期
0anacron  0yum-hourly.cron  cron.sh

/etc/cron.monthly:		# 按月周期

/etc/cron.weekly:		# 按周周期
```

#### 2 anacron 程序

​	anacron 比 cron 不同的地方是 , 当服务器关闭时 , 某些时刻的定时任务可能会错过 , 当机器重启后 anacron 会重新运行这些错过的任务.

​	这个功能长用于进行常规日志维护的脚本. anacron 程序只会处理位于 cron 目录的程序 , 比如 /etc/cron.monthly . 每个 cron 目录都有个时间戳文件 , 位于 /var/spool/anacron , 它用于判断作业是否在正确的计划间隔内运行了.

​	anacron时间表的基本格式与 cron 稍有不同

```shell
period	delay	identifier	command
```

> period :	定义作业多久运行一次
>
> delay : 指定系统启动后 anacron 等待多久再运行错过的脚本

​	注意 : anacron 不会运行位于 /etc/cron.hourly 的脚本 . **这是因为 anacron 程序不会处理执行时间需求小于一天的脚本**

### 6 使用新 shell 启动脚本

​	类似开机自动启动某个程序的需求 .

​	通过用户登录 bash shell 时 ,需要运行一些启动文件 . 通过linux 发行版中都会包含这些启动文件 , 基本上依照下列顺序所找到的第一个文件会被执行 ,其余的文件会被忽略 :

1. $HOME/.bash_profile
2. $HOME/.bash_login
3. $HOME/.profile

> ~/.bash_profile:每个用户都可使用该文件输入专用于自己使用的shell信息,当用户登录时,该文件仅仅执行一次!默认情况下,他设置一些环境变量,执行用户的**.bashrc**文件.
>
> 此文件类似于/etc/profile，也是需要需要重启才会生效，/etc/profile对所有用户生效，~/.bash_profile只对当前用户生效。
> ~/**.bashrc**:该文件包含专用于你的bash shell的bash信息,当登录时以及每次打开新的shell时,该文件被读取.（每个用户都有一个.bashrc文件，在用户目录下）

​	因此 , 如果需要在登录时运行某个脚本 , 只需要将运行命令添加到上面的第一个文件中即可.

​	.bashrc 文件通常也是某个bash启动文件来运行的 . 因为 .bashrc 文件会运行两次: 一次是登入 bash shell 时 , 另一次是当你启动一个 bash shell 时 . 如果需要一个脚本在两个时刻都得以运行 ,可以将脚本放在该文件中

## 6 sed

​	sed 编辑器被称作流编辑器(stream editor) . sed 一般对输入的命令作如下处理 :

1. 一次从输入读取一行数据

2. 根据提供的命令匹配数据

3. 按照命令修改流中的数据

4. 将新的数据输出到 STDOUT

   ***注意 sed 编辑器不会修改原始文件 , 只是修改输出的内容 !!!***

   sed 的命令格式 :

```shell
sed options script file
```

| 选项       | 描述                                    |
| ---------- | --------------------------------------- |
| -e  script | 运行执行多个 script                     |
| -f  file   | 将 file 指定的命令添加到已有的命令      |
| -n         | 不产生命令输出 ,使用 print 命令完成输出 |

**在命令行产生命令输出**

```shell
[root@VM_0_6_centos ~]# echo "this is a test" | sed 's/test/big test/'
this is a big test
```

> s 命令会用斜线间第二个文本字符串替换第一个文本字符串

**在命令行使用多个编辑器命令**

```shell
[root@VM_0_6_centos ~]# echo "this is a brown dog" | sed -e 's/brown/green/; s/dog/cat/'
this is a green cat
```

> 1. -e  选项运行执行多个命令
> 2. 命令之间必须用  ";" 分隔 , 并且在命令末尾和分号之间不能有空格

### 1 替换选项

​	替换标记(substitution flag) 在替换命令字符串之后设置

```
s/pattern/replacement/flags
```

​	有四种替换标记 :

1. 数字 , 表明新文本将替换第几处模式匹配的地方
2. g , 替换所有匹配的地方
3. p , 表明原先的内容要打印出来
4. w  file , 将替换的结果写到文件中

```shell
[root@VM_0_6_centos ~]# cat seddemo.txt 
aaa bbb aaa ccc
[root@VM_0_6_centos ~]# sed 's/aaa/xxx/2' seddemo.txt 
aaa bbb xxx ccc
```

> 第二个 "aaa" 被替换掉了

```shell
[root@VM_0_6_centos ~]# cat seddemo.txt 
aaa bbb aaa ccc
eee fff ggg
[root@VM_0_6_centos ~]# sed -n  's/bbb/ooo/p' seddemo.txt 
aaa ooo aaa ccc
```

> -n 选项禁止 sed 编辑器输出 , 但 p 标记会输出修改过的行 , 二者配合使用就是只输出被替换命令修改过的行

```shell
[root@VM_0_6_centos ~]# sed 's/aaa/xxx/w a.txt' seddemo.txt
xxx bbb aaa ccc
eee fff ggg         
[root@VM_0_6_centos ~]# cat a.txt 
xxx bbb aaa ccc
```

> w 标记 , 可以看出只有符合匹配模式的行被写到指定文件中

### 2 替换字符

​	如果字符串中有   `/` , 此时需要进行转义 , 如

```shell
sed 's/\bin\/bash/\bin\/bash/' /etc/passwd
```

​	这样看起来很别扭 . sed 编辑器允许选择其他字符作为替换命令中字符串的分隔符 :

```shell
sed 's!/bin/bash!/bin/bash!' /etc/passwd
```

​	在这个例子中   `!` 被作为分隔符

### 3 使用行寻址

​	sed 中当希望命令只作用于特定的行或某些行时 , 需要用到行寻址(line addressing)

​	sed 中 , 行寻址有两种形式 :

1. 以数字形式表示区间
2. 用文本模式过滤行

#### 1 数字方式行寻址

```shell
# 只作用于第二行
sed '2s/dog/cat/' data.txt
# 作用于 2-10 行
sed '2,10s/dog/cat/' data.txt
# 作用于 2-末尾
sed '2,$s/dog/cat/' data.txt
```

#### 2 文字模式过滤

​	文字模式其实是正则匹配 , 能匹配上的行会受影响

```shell
sed '/fmi/s/bash/csh/' /etc/passwd
```

#### 3 命令组合

```
sed '2,${
    s/dog/cat/
    s/aaa/ccc/
}' data.txt
```

### 4 删除行

```shell
# 删除所有行
sed 'd' data.txt
# 删除第2,3行
sed '2,3d' data.txt
# 删除包含 "aaa" 字符的行
sed '/aaa/d' data.txt
```

​	**注意 sed 编辑器不会修改原始文件 , 只是修改输出的内容 !!!**

### 5 插入文本

1. 命令  `i`  在指定**行前插入**新行
2. 命令  `a`  在指定**行后追加**新行

```shell
[root@VM_0_6_centos ~]# echo "this line 2" | sed 'i\this line 1'
this line 1
this line 2
```

```shell
[root@VM_0_6_centos ~]# cat seddemo.txt 
aaa bbb aaa ccc
eee fff ggg
[root@VM_0_6_centos ~]# sed '/eee/i\new line' seddemo.txt 
aaa bbb aaa ccc
new line 		# 插入的行
eee fff ggg
```

```shell
# 在数据流的第三行后追加
sed '3a\new line' data.txt
```

​	要插入多行文本 , 就必须对要插入或附加的新文本的每一行使用反斜线 , 如下 :

```shell
sed '2i\new line1\new line2' data.txt
```

### 6 修改行

​	使用命令 c 对行进行替换 , 用法跟插入文本用法类似

```shell
[root@VM_0_6_centos ~]# cat seddemo.txt 
aaa bbb aaa ccc
eee fff ggg
[root@VM_0_6_centos ~]# sed '2c\line 2' seddemo.txt 
aaa bbb aaa ccc
line 2
```

> 第二行文本被替换了 , 也可以使用文本模式匹配指定行

### 7 打印

​	sed 中有三个命令用于打印数据流中的信息 :

1. p 命令用来打印文本
2. 等号( = )命令用来打印行号
3. l(小写的L) 命令用来列出行

```shell
# p 打印
echo "this is line 1" | sed 'p'
# 打印指定行
sed -n '2,3p' data.txt
```

```shell
# 打印行号
[root@VM_0_6_centos ~]# sed  '=' seddemo.txt 
1
aaa bbb aaa ccc
2
eee fff ggg
```

​	命令 l  可以打印数据流中的文本和不可打印的 ASCII 字符 , 任何不可打印字符要么在其八进制值前加一个反斜线 , 要么使用标准C风格的命名法 , 比如 \t ,来代表制表符

### 8 使用sed处理文件

#### 1 写入文件

```shell
# 将 data.txt 的第2 ,3 行数据写到 test.txt 中
sed '1,2w test.txt' data.txt
# 将包含 "aaa" 的行写入到 test.txt
sed -n '/aaa/w test.txt' data.txt
```

#### 2 从文本读取文件

```shell
[root@VM_0_6_centos ~]# cat a.txt 
xxx bbb aaa ccc
[root@VM_0_6_centos ~]# cat seddemo.txt 
aaa bbb aaa ccc
eee fff ggg
[root@VM_0_6_centos ~]# sed '2r a.txt' seddemo.txt 
aaa bbb aaa ccc
eee fff ggg			# 从 a.txt 读入的数据
xxx bbb aaa ccc
```

> sed '2r  a.txt'  seddemo.txt  :  在 seddemo.txt 的流的第二行插入 a.txt 的数据

```shell
sed '/ccc/r a.txt' seddemo.txt

sed '$r a.txt' seddemo.txt
```

 读取方法和删除命令配合使用的案例 :

```
$ cat notice.std
Would the follow people :
LIST
please report to the ship's captain.
```

​	`LIST`  是占位符 , 需要用 data.txt 的数据替换 , 此时如果只使用  `r`  命令时 ,输出结果中 仍然保留了 `LIST` 字符串 ,此时配合  `d` 命令即可

```
$ sed '/LIST/{
    r data.txt
    d
}' notice.std
```

输出 :

```shell
Would the follow people :
fmi110   广西来宾
fengmi   广东深圳
please report to the ship's captain.
```

## 7 gawk

### 1 入门

​	gawk 是处理文件数据的高级工具 , 是一门功能丰富的编程语言. gawk 的命令格式 :

```shell
gawk options program file
```

​	options 的选项 :

| 选项           | 描述                             |
| -------------- | -------------------------------- |
| -F   fs        | 指定行中字段的分隔符             |
| -f   file      | 从指定文件读取程序               |
| -v   var=value | 定义一个变量和默认值             |
| -mf   N        | 指定处理的数据文件中的最大字段数 |
| -mr   N        | 指定数据文件中的最大数据行数     |
| -W   keyword   | 指定兼容模式或警告等级           |

#### 1 从命令行读取程序脚本

​	gawk 脚本必须用一对花括号  `{}` 来定义 , 同时指定输入文件 , 未指定时默认是 STDIN

```shell
gawk '{print hello world}' 
```

> 程序运行后 , 只有在控制台输入内容后 , 才会看到输出 , 因为没有指定输入文件 , 程序会等待从 STDIN 输入内容 , 如果要退出程序 , 使用组合键 CTRL + D , 在bash 下产生一个 EOF 文件结束符即可

#### 2 使用数据字段变量

​	gawk 默认会如下分配给它在文本行中发现的数据字段 :

1. *$0  代表整个文本行*

2. *$1  代表第一个字段*

3. *$2   代表第二个字段*

4. *$n   代表第n字段...*

   默认的字段分隔符是任意的空白字符(空格,制表符) , 可通过 -F 指定分隔符

```shell
gawk -F: '{print $1}' /etc/passwd
```

> 指定分隔符为  `:`  , 并打印第一个字段

#### 3 在程序脚本中使用多个命令

​	多个命令间放   `;` 即可

```shell
echo "My name is fmi110" | gawk '{$4="fengmi" ; print $0}'
```

####  4 从文件中读取程序

​	gawk 的命令允许写在文件中 , 然后通过 -f 引用

```shell
$ cat script.gawk
{$4="fengmi" ; print $0}


$ echo "My name is fmi110" | gawk -f script.gawk 
```

#### 5 在处理数据前运行脚本

​	`BEGIN`  关键字后指定的脚本程序 , 会在 gawk 处理数据前先执行 , 通过可以用来做一些变量设置 , 比如分隔符之类的

```shell
gawk 'BEGIN {print "begin to handle data..."} {print $0}' input.txt	
```

#### 6 处理数据后运行脚本

​	`END`  关键字实现

```shell
$ gawk 'BEGIN {print "begin to handle data..."} {print $0} END {print "finished..."}' a.txt
begin to handle data...
aaa:bbb:ccc
finished...
```

### 2 使用变量

#### 1 内建数据字段变量

| 变量        | 描述                                           |
| ----------- | ---------------------------------------------- |
| FIELDWIDTHS | 定义每个字段的宽度,  如: FIELDWIDTHS="3 2 5 4" |
| FS          | 字段分隔符 , 默认为空格符                      |
| RS          | 输入记录的分隔符(行分隔符)                     |
| OFS         | 输出字段分隔符                                 |
| ORS         | 输入行分隔符                                   |

```shell
[root@VM_0_6_centos ~]# cat a.txt
aaa:bbb:ccc
[root@VM_0_6_centos ~]# gawk 'BEGIN{FS=":";OFS="--"} {print $1,$3}' a.txt
aaa--ccc
```

```shell
[root@VM_0_6_centos ~]# cat a.txt
123456789
[root@VM_0_6_centos ~]# gawk 'BEGIN{FIELDWIDTHS="1 2 3"} {print $1,$2,$3}' a.txt
1 23 456
```

​	如上 , 如果设置了   `FIELDWIDTHS` 变量 , gawk 会忽略 FS 变量

#### 2 内建数据变量

| 变量       | 描述                                                        |
| ---------- | ----------------------------------------------------------- |
| ARGC       | 命令行参数个数                                              |
| ARGIND     | 当前文件在 ARGC 的位置                                      |
| ARGV       | 包含命令行参数的数组                                        |
| CONVFMT    | 数字转换格式 , 默认为 %.8 g                                 |
| ENVIRON    | 关联的shell环境变量数组 , ENVIRON["PATH"] 指向 PATH 变量... |
| ERRNO      | 错误代码                                                    |
| FILENAME   | gawk输入的文件的文件名                                      |
| FNR        | 当前文件的数据行数                                          |
| IGNORECASE | 设为非0 时 , 忽略字符大小写                                 |
| NF         | 当前记录中最后一个字段值                                    |
| NR         | 已处理的输入记录数                                          |
| OFMT       | 数字的输出格式 , 默认 %.6 g                                 |
| RLENGTH    | 由match函数所匹配的子字符串的长度                           |
| RSTART     | 由match函数所匹配的子字符串的起始位置                       |

```shell
[root@VM_0_6_centos ~]# gawk '{
> print ENVIRON["HOME"]
> print ENVIRON["PATH"]
> }' a.txt
/root
/usr/local/redis/bin:/usr/local/zookeeper/bin:/usr/local/tomcat/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/usr/local/src/jdk1.8.0_162/bin:/root/bin
```

#### 3 自定义变量

​	在脚本中给变量赋值 , 跟shell变量类似

```shell
gawk 'BEGIN{x=4;x=x*3+2;print x}'
```

​	当 gawk 命令写在文件中 , 希望通过命令行给变量赋值时 , 直接在命令行上输入即可

```shell
gawk -f script.gawk n=3  input.txt
```

> 这里给变量 n 赋值 3 , 在脚本中访问 n=3

​	但是这种方法赋值的变量  在 BEGIN 部分的代码不可见 , 要解决这个问题需要用 -v 命令参数解决

```shell
gawk -v n=3 -f script.gawk  input.txt
```

> -v   必须放在脚本代码之前 !!!

### 3 处理数组

​	数组定义 :

```shell
# 定义数组 
var["a"]=1
var[1]=2

# 遍历数组
for(p in var)
{
    print "index:",p," -- value:",var[p]
}
```

> 输出 :
>
> ​	index:a -- value:1
>
> ​	index:1 -- value:2

```
# 删除数组变量
delete var[1]
```

### 4 使用模式

#### 1 正则表达式

```shell
gawk 'BEGIN{FS=","} /11/{print $1}' input.txt
```

> 使用了正则表达式 /11/

#### 2 匹配操作符

​	匹配操作符( matching opertor ) 允许将正则表达式限定在记录中的特定数据字段 . 匹配操作符是波浪线( ~ ) , 可以指定匹配操作符 ,数据字段变量以及要匹配的正则表达式.

```shell
$1 ~ /^data/
```

> 过滤第一字段以 data 开头的所有记录

​	也可以用   `!` 来排除正则表达式的匹配

```shell
$1 !~ /expression/
```

### 5 结构化命令

​	支持 if , while , do-while , for 语句

### 6 格式化打印

​	类似于 c 语言的打印格式, 格式化指定符格式 :

```shell
%[modifier] control-letter
```

​	控制字母如下

| 字母 | 描述                      |
| ---- | ------------------------- |
| c    | 将一个数作为ASCII字符显示 |
| d    | 显示一个整数值            |
| i    | 跟d一样                   |
| e    | 科学计数法显示            |
| f    | 显示浮点数                |
| g    | 科学计数法或浮点数显示    |
| o    | 八进制                    |
| s    | 显示字符串                |
| x    | 十六进制                  |
| X    | 十六进制 , 大小字母显示   |

```shell
gawk 'BEGIN{x=100*8;printf "the answer is : %e\n",x}
```

### 7 内建函数

#### 1 数学函数

| 函数       | 描述              |
| ---------- | ----------------- |
| atan2(x,y) |                   |
| cos(x)     |                   |
| exp(x)     |                   |
| int(x)     | 取整 , 靠近0 的数 |
| log(x)     |                   |
| rand()     |                   |
| sin(x)     |                   |
| sqrt(x)    |                   |
| srand      |                   |
| and(v1,v2) |                   |

#### 2 字符串函数	

| 函数               | 描述                                                         |
| ------------------ | ------------------------------------------------------------ |
| asort(s [,d])      | 数组s排序,索引值会被替换成排序后的连续数字 , 如果指定 数组d , 则结果会存到 d |
| asorti(s [,d])     | 按索引值排序                                                 |
| gensub(r,s,h [,t]) |                                                              |
| gsub(r,s [,t])     |                                                              |
| index(s , t)       | 返回字符串t 在s 中的索引值, 没找到则返回0                    |
| length([s])        | 返回字符串 s 的长度 , 不指定则返回 $0 的长度                 |
| match(s , r [,a])  | 返回字符串s中正则表达式 r 出现的位置 , 指定a时 , 匹配正则的那部分字符将存到a中 |
| split(s , a [,r])  | 将 s 用 FS 或正则 r 切割放到数组 a 中                        |
| tolower(s)         |                                                              |
| toupper(s)         |                                                              |
|                    |                                                              |
|                    |                                                              |

### 8 自定义函数

```shell
gawk 'function myprint(){printf "%-16s - %s\n",$1,$4} BEGIN{FS="\n";RS=""} {myprint()}' input.txt	
```

## 8 scp 命令

​    scp是有Security的文件copy，基于ssh登录。操作起来比较方便，比如要把当前一个文件**copy到远程**另外一台主机上，可以如下命令。

```shell
# 从本地文件复制到远程
scp /home/open/tools.tar.gz  root@113.223.228.175:/home/root
# 复制文件夹
scp -r /home/open/ root@113.223.228.175:/home/root
```

​	然后会提示你输入另外那台113.223.228.175主机的root用户的登录密码，接着就开始copy了。

​    	如果想反过来操作，把文件从远程主机copy到当前系统，也很简单。

```shell
# 从远程复制到本地
scp root@113.223.228.175:/home/root/tools.tar.gz  home/open/tools.tar.gz
```

## 9 安装脚本

​	**shell 编程,变量赋值    `=`  两边不能有空格 !!!** 

### 1 jdk 安装

```shell
#!/bin/bash

# 获取当前运行的脚本名称,不带路径
SHELL_NAME=$(basename $0)
if [ $# != 1 ]
then
	echo ===============================================
	echo === 输入命令格式错误!!!!
	echo === 命令格式 : $SHELL_NAME jdk压缩包路径 , 路径请使用绝对路径!!!
	echo === 如 : $SHELL_NAME /usr/local/src/jdk-8u162-linux-x64.tar.gz
	echo ===============================================
	exit
fi

JDK_SRC=$1
JDK_HOME=''
DIR=/usr/local/src/
if [ a$JAVA_HOME != 'a' ] 
then
	echo  已经安装jdk ,要强制安装 ,请先运行命令 : export JAVA_HOME=
	echo  JAVA_HOME =  $JAVA_HOME
	exit
else
	JDK_HOME=/usr/local/java
#	mkdir -p $JDK_HOME
fi

# read -p "请输入安装包的路径 : " JDK_SRC

if [ -f $JDK_SRC ] 
then
	# 获取压缩包内第一个文件夹的名称
#	DIR=$(tar -tf jdk-8u162-linux-x64.tar.gz | awk -F\/ '{print $1}' | head -n 1)

	echo 开始解压安装包到目录 : $DIR
	tar -xzf $JDK_SRC -C $DIR
	DIR=$DIR$(tar -tf $JDK_SRC | awk -F\/ '{print $1}' | head -n 1)
	echo 解压完成 , 建立软连接 /usr/local/java
	ln -s $DIR $JDK_HOME
else
	echo 压缩包路径错误...文件 $JDK_SRC 不存在
	exit
fi

######## 配置环境变量 start ######
echo  export JAVA_HOME=$DIR >> /etc/profile
echo 'export PATH=$PATH:$JAVA_HOME/bin' >> /etc/profile
source /etc/profile
######## 配置环境变量 end  ######
# java -version
echo ====================================
echo ===  
echo === 安装 jdk 完成 , 安装目录 : $JAVA_HOME
echo === 为使环境变量生效,请输入命令 : source /etc/profile
echo === 
echo ====================================
```
### 2 hadoop 安装

#### 1 创建 hadoop 用户,并添加 sudo 权限

​	这里使用 hadoop 用户来执行安装程序 , 以为有些步骤需要用到 sudo 权限 , 所以一并添加.

​	这里使用了四台主机 组建集群 , 四台主机都要创建该账户

```shell
# 以root 身份运行以下指令
# 添加用户
useradd hadoop 
# 设置密码为 123456
echo '123456' | passwd --stdin hadoop		

# 编辑 /etc/sudoers 文件 ,授予 sudo 权限
chmod u+w /etc/sudoers 

sed  '/^root/a\hadoop  ALL=(ALL)  ALL' /etc/sudoers > temp.txt && cat temp.txt > /etc/sudoers && rm -rf temp.txt 

chmod u-w /etc/sudoers
```

#### 2 关闭防火墙 ,并设为开机不启动

​	这个命令是针对 centos 7 的 , 四台主机上都关闭

```shell
sudo systemctl disable firewalld.service && sudo systemctl stop firewalld.service	
```

#### 3 配置 ssh 无密登录

​	ssh 无密登录主要是为了从主机 m01 无密的发送指令到集群里的其他主机.

1. 修改主机名

   这里使用了 m01 , m02 ,m03 , m04  四台主机 , 所以需要在四台主机上修改对应的主机名 ,这里演示修改第一台主机的 hostname 为 m01 的方法, 其他主机做法一致

```shell
echo m01 > /etc/hostname
```

2. 修改 /etc/hosts 文件

   这里使用了 m01 , m02 ,m03 , m04  四台主机 , 需要做ip地址映射 , 修改 /etc/hosts 文件

```shell
# 以root 身份运行以下指令
echo "127.0.0.1   localhost localhost.localdomain localhost4 localhost4.localdomain4" > /etc/hosts
echo "::1         localhost localhost.localdomain localhost6 localhost6.localdomain6" >> /etc/hosts
echo "192.168.204.129 m01" >> /etc/hosts
echo "192.168.204.130 m02" >> /etc/hosts
echo "192.168.204.131 m03" >> /etc/hosts
echo "192.168.204.132 m04" >> /etc/hosts
echo "192.168.204.133 m05" >> /etc/hosts
```

3. 在 m01 主机上生成 hadoop 用户的密钥对

```shell
# 切换到 hadoop 用户
su hadoop

# 生成密钥对
rm -rf /home/hadoop/.ssh && ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa
# 依次分发公钥到其他主机 , 执行时会要求输入 hadoop 账户的密码
ssh-copy-id -i ~/.ssh/id_rsa.pub m02
ssh-copy-id -i ~/.ssh/id_rsa.pub m03
ssh-copy-id -i ~/.ssh/id_rsa.pub m04
ssh-copy-id -i ~/.ssh/id_rsa.pub m05
```

​	此时 , 在 m01 上执行   `ssh m0X` 即可直接登录 m0X 主机了 

#### 4 执行脚本 , 安装 hadoop

​	配置文件是按照完全分布式配置的 , 需要自己修改脚本中的如下变量 :

```properties
# 压缩包解压的路径
BASE_HOME=$HOME/app

######## hadoop 相关的参数 ########
# 可以考虑抽取为命令行参数
fs_defaultFS=hdfs://m01 	
# 配置 hadoop.tmp.dirhadoop.tmp.dir
hadoop_tmp_dir=/home/hadoop/$DirName/tmp

# dfs.namenode.name.dir
dfs_namenode_name_dir=/home/hadoop/$DirName/hdfs/namenode/
# dfs.datanode.data.dir
dfs_datanode_data_dir=/home/hadoop/$DirName/hdfs/datanode/
# dfs.namenode.checkpoint.dir
dfs_namenode_checkpoint_dir=/home/hadoop/$DirName/hdfs/namenode/checkpoint
# dfs.replication
dfs_replication=3

# yarn.resourcemanager.hostname
yarn_resourcemanager_hostname=m02
# yarn.nodemanager.aux-services
yarn_nodemanager_aux_services=mapreduce_shuffle

slaves="m02 m03 m04"
```

> `gawk -F= '/=/{ var["$1"]=$2;print $1,var["$1"] }' config.properties`

​	以下为脚本内容 , 记住以 hadoop 身份运行 , 最好在 hadoop 用户的家目录下新建脚本然后执行

```shell
su hadoop
cd ~
# 新建脚本,然后复制内容到脚本中
vim intall.sh
# 给执行权限
sudo chmod u+x intall.sh
# 执行脚本 , 压缩包必须使用绝对路径 !!!
sudo -E ./install.sh /usr/local/src/hadoop-2.7.2.tar.gz
```

​	注意 :   一定要    **以   `sudo -E`  执行** , 否则 JAVA_HOME 环境变量设置失败 , 原因 : [sudo 读取不到环境变量](https://blog.csdn.net/x356982611/article/details/71169794)

```shell
#!/bin/bash
# 获取当前运行的脚本名称,不带路径
SHELL_NAME=$(basename $0)
if [ $# != 1 ]
then
	echo ===============================================
	echo === 输入命令格式错误!!!!
	echo === 命令格式 : $SHELL_NAME 压缩包路径 , 路径请使用绝对路径!!!
	echo === 如 : $SHELL_NAME /usr/local/src/jdk-8u162-linux-x64.tar.gz
	echo ===============================================
	exit
fi

SRC_PATH=$1
JDK_HOME=$(echo $JAVA_HOME)
BASE_HOME=$HOME/app
mkdir -p $BASE_HOME
DirName=$(tar -tf $SRC_PATH | gawk -F\/ '{print $1}' | head -n 1)

echo "开始解压文件到目录 $BASE_HOME"
# 解压文件
tar -zxf $SRC_PATH -C $BASE_HOME

HADOO_HOME=$BASE_HOME/$DirName ;
cd $HADOO_HOME # 切换到安装目录
cd etc/hadoop/

# 配置 hadoop-env.sh
echo ===============================================
echo === 
echo === 1 配置 hadoop-env.sh
echo ===
echo ===============================================
cat hadoop-env.sh > hadoop-env.sh.bak
old_java='${JAVA_HOME}'
new_java=$(echo $JAVA_HOME)
sed "s!$old_java!$new_java!" hadoop-env.sh > temp.t  # 修改 jdk 路径
cat temp.t > hadoop-env.sh && rm -rf temp.t

cat hadoop-env.sh | grep "export JAVA_HOME"




###################  HDFS 相关参数设置 ##################

fs_defaultFS=hdfs://m01 	

# 配置 hadoop.tmp.dirhadoop.tmp.dir
hadoop_tmp_dir=/home/hadoop/$DirName/tmp

# dfs.namenode.name.dir
dfs_namenode_name_dir=/home/hadoop/$DirName/hdfs/namenode/
# dfs.datanode.data.dir
dfs_datanode_data_dir=/home/hadoop/$DirName/hdfs/datanode/
# dfs.namenode.checkpoint.dir
dfs_namenode_checkpoint_dir=/home/hadoop/$DirName/hdfs/namenode/checkpoint
# dfs.replication
dfs_replication=3

# yarn.resourcemanager.hostname
yarn_resourcemanager_hostname=m02
# yarn.nodemanager.aux-services
yarn_nodemanager_aux_services=mapreduce_shuffle

slaves="m02 m03 m04"

###################  HDFS 相关参数设置 ##################




echo;echo;echo
echo ===============================================
echo === 
echo === 2 配置 core-site.xml
echo ===
echo ===============================================
# 配置 fs.defaultFS
# 可以考虑抽取为命令行参数
# fs_defaultFS=hdfs://m01 	
# 配置 hadoop.tmp.dirhadoop.tmp.dir
# hadoop_tmp_dir=/home/hadoop/$DirName

mkdir -p $hadoop_tmp_dir

function config_core_site(){
    local content='<?xml version="1.0" encoding="UTF-8"?>		   \n
	<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>   \n
		<configuration>   \n
			<property>   \n
				<name>fs.defaultFS</name>   \n
				<value>fs_defaultFS/</value>   \n
			</property>    \n
			<!-- 指定hadoop运行时产生文件的存储目录 -->    \n
			<property>    \n
				<name>hadoop.tmp.dir</name>    \n
				<value>hadoop_tmp_dir</value>    \n
    		</property> \n
	</configuration>'
	echo  $content | sed -e "s!fs_defaultFS!$fs_defaultFS!; s!hadoop_tmp_dir!$hadoop_tmp_dir!"
}

# 运行函数
core_site=$(config_core_site)
# -e  会将 \n 替换为换行符
echo -e $core_site > core-site.xml

echo =================core-site.xml=================
echo
cat core-site.xml
echo 
echo ===============================================
echo;echo;echo
echo ===============================================
echo === 
echo === 3 配置 hdfs-site.xml
echo ===
echo ===============================================

# dfs.namenode.name.dir
# dfs_namenode_name_dir=/home/hadoop/$DirName/hdfs/namenode
# dfs.datanode.data.dir
# dfs_datanode_data_dir=/home/hadoop/$DirName/hdfs/datanode
# dfs.namenode.checkpoint.dir
# dfs_namenode_checkpoint_dir=/home/hadoop/$DirName/hdfs/namesecondary
# dfs.replication
# dfs_replication=3

mkdir -p $dfs_namenode_name_dir $dfs_datanode_data_dir $dfs_namenode_checkpoint_dir

function config_hdfs_site(){
    local content='<?xml version="1.0"?> \n
		<!-- hdfs-site.xml --> \n
		<configuration> \n
			<property> \n
				<name>dfs.namenode.name.dir</name> \n
				<value>dfs_namenode_name_dir</value> \n
			</property> \n
			<property> \n
				<name>dfs.datanode.data.dir</name> \n
				<value>dfs_datanode_data_dir</value> \n
			</property> \n
			<property> \n
				<name>dfs.namenode.checkpoint.dir</name> \n
				<value>dfs_namenode_checkpoint_dir</value> \n
			</property> \n
			<property> \n
				<name>dfs.replication</name> \n
				<value>dfs_replication</value> \n
			</property> \n
		</configuration>' 
		echo $content | sed -e "s!dfs_namenode_name_dir!$dfs_namenode_name_dir!; s!dfs_datanode_data_dir!$dfs_datanode_data_dir!; s!dfs_namenode_checkpoint_dir!$dfs_namenode_checkpoint_dir!; s!dfs_replication!$dfs_replication!"
}

hdfs_site=$(config_hdfs_site)
# -e  会将 \n 替换为换行符
echo -e $hdfs_site > hdfs-site.xml

echo =================hdfs-site.xml=================
echo
cat hdfs-site.xml
echo 
echo ===============================================
echo;echo;echo
echo ===============================================
echo === 
echo === 4 配置 mapred-site.xml
echo ===
echo ===============================================


function config_mapred_site(){
	local content='<?xml version="1.0"?>\n
		<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>\n
		<configuration>\n
		        <property>\n
		                <name>mapreduce.framework.name</name>\n
		                <value>yarn</value>\n
		        </property>\n
		</configuration>'
		echo  $content
}

mapred_site=$(config_mapred_site)
echo -e $mapred_site > mapred-site.xml

echo =================mapred-site.xml=================
echo
cat mapred-site.xml
echo 
echo ===============================================
echo;echo;echo
echo ===============================================
echo === 
echo === 5 配置 yarn-site.xml
echo ===
echo ===============================================

# yarn.resourcemanager.hostname
# yarn_resourcemanager_hostname=m02
# yarn.nodemanager.aux-services
# yarn_nodemanager_aux_services=mapreduce_shuffle

function config_yarn_site(){
	local content='<?xml version="1.0"?> \n
		<!-- yarn-site.xml --> \n
		<configuration> \n
			<property> \n
				<name>yarn.resourcemanager.hostname</name> \n
				<value>yarn_resourcemanager_hostname</value> \n
			</property> \n
			<property> \n
				<name>yarn.nodemanager.aux-services</name> \n
				<value>yarn_nodemanager_aux_services</value> \n
			</property> \n
		</configuration>'
		echo  $content | sed -e "s!yarn_resourcemanager_hostname!$yarn_resourcemanager_hostname!; s!yarn_nodemanager_aux_services!$yarn_nodemanager_aux_services!"
}

yarn_site=$(config_yarn_site)
echo -e $yarn_site > yarn-site.xml

echo =================yarn-site.xml=================
echo
cat yarn-site.xml
echo 
echo ===============================================

echo ===============================================
echo === 
echo === 6 配置 slaves
echo ===
echo ===============================================
# 从机的域名地址列表
slaves="m02 m03 m04"
echo '' > slaves
for ip in $slaves
do
	echo $ip >> slaves
done

echo 配置好的 slaves如下 :
cat slaves
```

​	配置环境变量

```shell
# root用户编辑/etc/profile , 在末尾添加
export HADOOP_HOME=/home/hadoop/app/hadoop-2.7.2
export PATH=$HADOOP_HOME/bin:$PATH

# 使环境变量生效
. /etc/profile
```

> `.`  相当于 source 命令

#### 5 启动hdfs

​	需要在namemode 主机上格式化名称节点 , 这里是 m01

```shell
# 先格式化名称节点
hdfs namenode -format
# 启动 hdfs
cd $HADOOP_HOME/sbin
./start-dfs.sh
# 启动 yarn
./start-yarn.sh
```

​	启动完后通过 jps 查看进程

```shell
[hadoop@m01 sbin]$ jps
3671 ResourceManager
4039 Jps
2665 NameNode
2860 SecondaryNameNode
```

```shell
[root@m02 ~]# jps
2881 NodeManager
3045 Jps
2476 DataNode
```

```shell
[root@m03 ~]# jps
2481 DataNode
29574 Jps
2872 NodeManager
```

```shell
[root@m04 ~]# jps
2832 NodeManager
2442 DataNode
2987 Jps
```

​	http://192.168.204.129:50070 （HDFS管理界面）
	http://192.168.204.129:8088 （MR管理界面）