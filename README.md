

axmleditor: editor for binary AndroidManifest.xml in APK.  
===================================

Can be used to change the value of android:value field for specific android:name.  
This is very useful to package multiple APKs so that every APK has it's own channel name.

### Usage:
Given one prebuilt apk (prebuilt.apk), run below commands:
```
java -jar axmleditor.jar prebuilt.apk UMENG_CHANNEL=baidu BD_APP_CHANNEL=baidu CHANNEL=baidu
```

It will parse the AndroidManifest.xml in prebuilt.apk, change the value of android:value to 'baidu', 
re-compile the xml, and put the compiled xml along with prebuilt.apk.  
Then user can use aapt or zip to add the compiled AndroidManifest.xml into apk. such as:  
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
