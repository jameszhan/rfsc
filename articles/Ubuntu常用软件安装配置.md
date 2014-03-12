#Ubuntu常用软件安装配置

###apt-get常用命令
~~~sh

sudo apt-get -y update
sudo apt-get -y upgrade
# 如果提示add-apt-repository not found，请执行如下命令
sudo apt-get install python-software-properties
sudo apt-get install software-properties-common

apt-get dist-upgrade
#为源码包配置所需的构建依赖关系
apt-get build-dep build-essential
#通过dselect的“建议”和“推荐”功能更新系统。dselect是Debian中一个功能强大的包管理工具。它可帮助用户选择软件包来安装，其中一个有用功能是它会建议和推荐安装其它相关软件包。我们可在APT中使用它这个功能。
#apt-get dselect-upgrade
#删除下载了的软件包，当我们通过apt-get安装软件包时，APT会把软件包下载到本地/var/cache/apt/archives/目录。该命令会删除该文件夹内的除锁住外的所有软件包。
#apt-get clean
#删除已下载的旧版本的软件包。该命令类似于上面的命令，但它会有选择地删除旧版本的软件包
apt-get autoclean
#检查系统中已安装软件包的依赖性。
apt-get check

~~~

###常见的软件依赖包
~~~sh

# install pcre
apt-get install libpcre3 libpcre3-dev
# install openssl
apt-get install libssl-dev openssl
# install gd （命令行编辑图片的那个）
apt-get install php5-gd libgd2-xpm libgd2-xpm-dev
# Install JDK
apt-get install default-jdk
~~~


###安装Nginx相关依赖
~~~sh

apt-get install libpcre3 libpcre3-dev libssl-dev openssl libgd2-xpm libgd2-xpm-dev php5-gd geoip-database geoip-bin libgeoip-dev
~~~

###安装RVM(Ruby Version Manager)相关依赖
~~~sh

sudo apt-get --no-install-recommends install build-essential openssl libreadline6 libreadline6-dev curl \
git-core zlib1g zlib1g-dev libssl-dev libyaml-dev libsqlite3-dev sqlite3 libxml2-dev libxslt-dev autoconf \
libc6-dev libgdbm-dev ncurses-dev automake libtool bison subversion pkg-config libffi-dev
~~~

###安装Nginx
~~~sh

sudo add-apt-repository ppa:nginx/stable
sudo apt-get -y update
sudo apt-get install nginx
~~~

###安装Postgresql
~~~sh

sudo add-apt-repository ppa:pitti/postgresql
sudo apt-get -y update
sudo apt-get -y install postgresql libpq-dev
~~~

###安装Redis
~~~sh

sudo add-apt-repository ppa:chris-lea/redis-server
sudo apt-get -y update
sudo apt-get -y install redis-server
~~~

###安装Node
~~~sh

sudo add-apt-repository ppa:chris-lea/node.js
sudo apt-get -y update
sudo apt-get -y install nodejs
~~~

###安装MongoDB
~~~sh

sudo add-apt-repository ppa:gias-kay-lee/mongodb
sudo apt-get -y update
sudo apt-get -y install mongodb
~~~

###安装RVM和Ruby
~~~sh

curl -L https://get.rvm.io | sudo bash -s stable
sudo chown -R ubuntu:rvm /usr/local/rvm

rvm reload
rvm install 2.0.0
~~~
