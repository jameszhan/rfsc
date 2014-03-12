#使用ffmpeg进行视音频格式转换

###常用命令
~~~sh

#列出支持的编解码器
ffmpeg -codecs

#列出支持的滤镜
ffmpeg -filters

#列出支持的格式
ffmpeg -formats

返回版本信息
ffmpeg -version
~~~

###ffmpeg选项基本用法
ffmpeg选项基本用法
用法：
$ffmpeg [options] [[infile options] -i infile]... {[outfile options] outfile}...
选项 -> 输入选项 -> 输入文件 … -> 输出选项 -> 输出文件 …
我们称带横线-的参数为选项（option），选项的值这里称为参数（argument），即 -option argumet。
说明：

（1）输入文件用-i指定；输出文件无选项，直接指定。
（2）选项-y 输出文件直接覆盖已存在的文件；-n 不覆盖已存在文件。
（3）选项区分先后顺序，特别是输入选项应对应输入文件，输出选项对应输出文件；滤镜调用也有顺序；-map也是顺序相关的。
（4）可以输入多个文件，输出多个文件。

-ab bitrate 设置音频码率
-acodec codec 使用codec编解码
-ac channels 设置通道,缺省为1
-ar freq 设置音频采样率
-r fps 设置帧频,缺省25
-b bitrate 设置比特率,缺省200kb/s
-qscale 6或4 使用动态码率来设置

###使用示例
~~~sh

#例1、把3po.ac3压成128Kbps的mp3
ffmpeg -i 3po.ac3 -acodec libmp3lame -b:a 128000 3po_enc.mp3

#例2、把只有视频的、编码为xvid的obi-wan.avi压成编码为H264、封装为mkv的视频
ffmpeg -i obi-wan.avi -vcodec libx264 -crf 22 -preset medium -tune film -deblock 1:1 -refs 4 -bf 8 -psy-rd 0.6:0.2 -subq 6 obi-wan_enc.mkv

#例3、把DVD转换为MP4
cat VIDEO_TS.VOB VTS_01_0.VOB VTS_01_1.VOB VTS_01_2.VOB | ffmpeg -i - ~/mika_01.mp4
~~~

这个工具功能真的比较强大，有兴趣的同学可以去自己去摸索