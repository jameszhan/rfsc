#Linux下的权限管理技巧

##代码示例

###直接在远端机器上执行代码
~~~sh

ssh ubuntu@10.211.55.13 'ls -l'
~~~

###登陆远程机器免输入密码
~~~sh

cat ~/.ssh/id_rsa.pub | ssh ubuntu@10.211.55.13 'cat >> ~/.ssh/authorized_keys'

#下面是Amazon EC2的例子
cat ~/.ssh/id_rsa.pub | ssh -v -i ~/.ssh/trail.pem ubuntu@ec2-54-241-192-74.us-west-1.compute.amazonaws.com 'cat >> ~/.ssh/authorized_keys'
~~~
对于自己的专属电脑来说，每次sudo输入密码确实非常啰嗦，尤其是在编写远程部署脚本时，更是遍地是坑

###sudo免输入密码
~~~sh

#在/etc/sudoers中更改用户相应的条目如下即可
ubuntu ALL=(ALL) NOPASSWD:ALL
james ALL=(ALL) NOPASSWD:ALL
~~~

###添加sudo免输密码用户
~~~sh

adduser deploy --ingroup admin
#添加以下内容到/etc/sudoers
deploy ALL=(ALL) NOPASSWD: ALL
~~~

##关键文件详解

用户，组及权限控制的信息主要和/etc/sudoers，/etc/passwd，/etc/shadow，/etc/group几个文件相关，可以使用id，finger查看用户信息。

####/etc/sudoers
~~~sh

#常见格式规范
# User privilege specification
root ALL=(ALL)      ALL
# Members of the admin group may gain root privileges
%admin ALL=(ALL)     ALL

#第一栏，用户或组（%开头表示组用户）
#第二栏，主机名称
#第三栏（括号中的那个），表明用户以何种身份执行命令
#最后一栏，用户可执行的命令列表，用逗号分隔，如果不想输入密码，可以在之前加上NOPASSWD。

%users ALL=(root) /sbin/mount /cdrom,/sbin/umount /cdrom
%users localhost=(ALL) NOPASSWD:/sbin/shutdown -h now
~~~


####/etc/passwd
~~~sh

ubuntu:x:1001:27:Ubuntu,,,:/home/ubuntu:/bin/bash

# ubuntu => 用户名
# x => 表示登陆需要密码，若此处无x，则表示该账户登陆不需要密码
# 1001 => 用户ID(uid)
# 27 => 用户组ID(gid)
# Ubuntu,,, => finger inform, 记录用户的相关信息，比如公司，地区等
# /home/ubuntu => 用户Home目录
# /bin/bash => 该用户使用bash shell的路径.如果是 /sbin/nologin,表示无法登陆系统,只能以FTP形式登录; 如果是 /bin/false,则表示只能发邮件.
~~~

####/etc/shadow
~~~sh

ubuntu:$6$9M43z06q$w/Q8D7pD7a/JNJKaI2jmcrNmumMHBu8poxU6VYzWFDac7FhtDspeaOLlKmEkgOGRRDW.Sxcow8kwzp7hGtUGf.:15758:0:99999:7:6:5:

#ubuntu => 用户名
#$6$yIUbtdK… => 用户加密后的密码，若为!!，则表示无密码，若为!$6$yIUbtdK...表示该账户已被锁定。
#15758 =>  该账户密码最新变更的日期,以1970年1月1日为基数开始计算,是2010年2月7日 (可用chage –l user 来查看)
#0 => 至少使用多久才允许变更密码,0表示没有限制;若次数为9,则表示该密码创建或变更后至少过9天才能再次变更密码.
#99999 => 表示该密码使用的有效周期,99999表示无限制;若此为9,则表示每过9天就需要变更密码
#7 => 表示密码到期前7天,提醒用户需要变更密码
#6 => 密码到期日起的6天后,如果该用户仍未变更密码,则该账户将被锁定 (默认留空)
#5 => 帐号到期日,以1970年1月1日开始计算,5天后账户锁定,此处表示的锁定日为1970便1月6日. (默认留空)
#最后一个(:后) => 是为了以后的新开发项做准备的
~~~


####/etc/group
~~~sh

sudo:x:27:james

#sudo => 用户组名
#x => 表示该用户组登录需要密码,若此出无x,则表示该用户组登录时不需要密码
#27 => 用户组ID ( gid)
#james => 这个用户组中的成员 ( 此处可多个,用户名之间用逗号隔开)
~~~
