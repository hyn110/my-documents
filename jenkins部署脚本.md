```shell
# /bin/bash
# 变量赋值等号两边不能有空格
userHome=/home/fmi110
tomcatHome=/home/fmi110/tomcat
password=/$userHome/aa.txt
appname="tomcat"

export JAVA_HOME=/usr/local/src/java

PID=$(ps -ef|grep $appname | grep -v grep | gawk '{print $2}')
# 遍历kill tomcat 进程
for var in ${PID[@]};
do
	echo "loop pid=$var"
	cat $password |sudo -S kill -9 $var
done
echo "tomcat 已停止，开始备份war。。。"

mv $tomcatHome/webapps/*.war $userHome/app_bak
echo "war包已备份到目录 ：$userHome/app_bak"

echo "开始部署新war，tomcat目录 ："$tomcatHome

cp /home/fmi110/*.war $tomcatHome/webapps
echo "启动tomcat"
sh $tomcatHome/bin/startup.sh
```

放行端口

```
[root@centos-6 src]# /sbin/iptables -I INPUT -p tcp --dport 7070 -j ACCEPT   # 放行3306端口
[root@centos-6 src]# /etc/init.d/iptables save								# 保存规则到数据库
iptables：将防火墙规则保存到 /etc/sysconfig/iptables：     [确定]
[root@centos-6 src]# service iptables restart								# 重启防火墙
iptables：将链设置为政策 ACCEPT：filter                    [确定]
iptables：清除防火墙规则：                                 [确定]
iptables：正在卸载模块：                                   [确定]
iptables：应用防火墙规则：                                 [确定]
```

```
# 开放端口
firewall-cmd --zone=public --add-port=8080/tcp --permanent
# 重启防火墙
systemctl restart firewalld.service
```

