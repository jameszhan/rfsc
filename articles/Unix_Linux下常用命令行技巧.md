#Unix/Linux下常用命令行技巧

###SCP远程传输文件
~~~sh

#1、获取远程服务器上的文件
scp -P 22 wei@10.20.172.32:/home/wei/output/logs/sys/service/service.log ~/

#2、获取远程服务器上的目录
scp -P 22 -r wei@10.20.172.32/home/wei/output/logs/sys/service/ ~/service

#3、将本地文件上传到服务器上
scp -P 22 ~/myfile.txt wei@10.20.172.32:/home/wei/

#4、将本地目录上传到服务器上
scp -P 22 -r ~/mydir wei@10.20.172.32:/home/wei/

#命令格式
scp -P REMOTE_PORT -r SOURCE_FILE DEST_FILE
#-r 递归拷贝文件，一般拷贝目录的时候需要指定该选项

#5、可能有用的几个参数 :
#-v 和大多数 linux 命令中的 -v 意思一样 , 用来显示进度 . 可以用来查看连接 , 认证 , 或是配置错误 .
#-C 使能压缩选项 .
#-4 强行使用 IPV4 地址 .
#-6 强行使用 IPV6 地址 .
~~~

###查看局域网主机IP占用情况
~~~sh

nmap -sP 172.18.6.0/24
~~~

###批量文件重命名
~~~sh

#如果熟悉sed的话，这个真是太好用了
# 把jpeg后缀替换成jpg
rename 's/.jpeg$/.jpg/' *.jpeg
统一在尾部追加 .zip后缀：
rename 's/$/.zip/' *
#规则化数字编号名，比如 1.jpg, 2.jpg ..... 100.jpg , 现在要使文件名全部三位即 1.jpg .... 001.jpg
rename 's/^/00/' [0-9].jpg        # 这一步把 1.jpg ..... 9.jpg 变幻为 001.jpg .... 009.jpg
rename 's/^/0/'  [0-9][0-9].jpg   # 这一步把 10.jpg ..... 99.jpg 变幻为 010.jpg ..... 090.jpg
~~~

###使用tail实时监控log
~~~sh

tail -f /var/log/messages #参数-f使tail不停地去读最新的内容，这样有实时监视的效果
tail -fn 500 /var/log/messages
~~~

###使用ab进行压力测试
~~~sh

ab -c 100 -n 300 http://0.0.0.0:3000/
ab -c 100 -n 300 -k -p user.json http://0.0.0.0:3000/users
#-c 并发次数
#-n 请求次数
~~~

###使用命令行快速分析日志
假定日志格式为
> 27.38.39.22 - - - [13/Apr/2014:23:52:59 +0800] "GET /sugou.1688.com/box/index.htm?trace_log=bzsg20131016_tbmjzxbz HTTP/1.1" 200 11129 43917 "http://tao.1688.com/greeting/hotsale_15.htm?sc=taobao&tracelog=hipage_fz_hotsale_tbc_toptab" "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1" 27.38.39.96.1397041726487.734800.9 - "a=\"c_ms=1|c_mid=b2b-755419782|c_lid=%E6%88%91%E7%9A%84%E4%B8%AA%E6%80%A7%E6%88%91%E5%81%9A%E4%B8%BBwj\"; b=\"c_w_signed=Y\"; c=-" - 16016 sourcingmktweb-001.xyi

~~~sh
#取出所有访问的来源
less apache.log | grep sugou.1688.com | awk '{print $13;}' | sort | uniq
#多少个独立IP访问
less apache.log | grep sugou.1688.com | awk '{print $1;}' | sort | uniq | wc -l
~~~
