

axmleditor: editor for binary AndroidManifest.xml in APK.  
===================================

Can be used to change the value of android:value field for specific android:name.  
This is very useful to package multiple APKs so that every APK has it's own channel name.

### Usage:
* Given one prebuilt apk (prebuilt.apk), run below commands:
```
java -jar axmleditor.jar prebuilt.apk UMENG_CHANNEL=baidu BD_APP_CHANNEL=baidu CHANNEL=baidu
```
It will parse the AndroidManifest.xml in prebuilt.apk, change the value of android:value to 'baidu', 
re-compile the xml, and put the compiled xml along with prebuilt.apk.  

* Then user can use aapt or zip to add the compiled AndroidManifest.xml into apk. such as:  
```
aapt remove prebuilt.apk AndroidManifest.xml
```
```
aapt add prebuilt.apk AndroidManifest.xml
```

### Example
#### before change:
```xml
<meta-data
	android:value="GooglePlay"
   	android:name="UMENG_CHANNEL"/>

<meta-data
    android:value="GooglePlay"
    android:name="BD_APP_CHANNEL"/>

<meta-data
    android:value="GooglePlay"
    android:name="CHANNEL"/>
```

#### after change:
```xml
<meta-data
	android:value="Baidu"
   	android:name="UMENG_CHANNEL"/>

<meta-data
    android:value="Baidu"
    android:name="BD_APP_CHANNEL"/>

<meta-data
    android:value="Baidu"
    android:name="CHANNEL"/>
```

### Reference:  

This project uses the xml parser of Google's ClassyShark project.  
   
------

## 中文说明  
用于更改APK中AndroidManifest.xml中的属性值。非常适用于多渠道的打包，支持中文渠道名。

### 用法:
##### 1.首先打一个包：prebuilt.apk, 运行命令:  

```
java -jar axmleditor.jar prebuilt.apk UMENG_CHANNEL=baidu BD_APP_CHANNEL=baidu CHANNEL=baidu
```

它会  

* 解析prebuilt.apk中的AndroidManifest.xml  

* 对android:name为UMENG_CHANNEL，BD_APP_CHANNEL，CHANNEL 为属性，更改android:value的值为baidu  

* 重新编译AndroidManifest.xml, 把编译后的AndroidManifest.xml放到prebuilt.apk同一目录下   

##### 2.然后用appt或者zip把AndroidManifest.xml放回到apk中:  
```
aapt remove prebuilt.apk AndroidManifest.xml
```
```
aapt add prebuilt.apk AndroidManifest.xml
```
##### 3.多渠道打包时，对每个渠道名，重复第2步，即可快速生成多个渠道包

### 示例：
#### 改变前的xml部分字段:
```xml
<meta-data
	android:value="GooglePlay"
   	android:name="UMENG_CHANNEL"/>

<meta-data
    android:value="GooglePlay"
    android:name="BD_APP_CHANNEL"/>

<meta-data
    android:value="GooglePlay"
    android:name="CHANNEL"/>
```

#### 改变后的xml部分字段:
```xml
<meta-data
	android:value="Baidu"
   	android:name="UMENG_CHANNEL"/>

<meta-data
    android:value="Baidu"
    android:name="BD_APP_CHANNEL"/>

<meta-data
    android:value="Baidu"
    android:name="CHANNEL"/>
```

#### 参考文献：
* 使用了Google ClassyShark项目的xml解析部分。
