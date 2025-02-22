# 首页
## 欢迎来到Webapp Wiki
Android IFTC Webapp文档
欢迎来到Webapp Wiki！该Wiki包含接口文档、示例教程、打包方法
# 私有JavaScript接口说明
JavaScript接口调用方法：
`iftc.{方法名}({一些参数})`

这些JavaScript接口是在window对象的iftc下，可通过iftc直接调用这些Android原生接口
回调类接口：
```javascript
iftc.{方法名}({一些参数}, "回调函数名")
function {回调函数名}(e) { // e为回调的参数，返回值类型为列表
    // 处理回调函数返回值
}
/*
回调函数的出现原因：部分接口需要再主线程上运行，无法在JavaScript接口所在的线程上运行
*/
```
***注意：本Wiki中所有的传入参数和返回参数都是按照顺序的；标有“实验性”的JS接口为实验性接口，可能无法确定接口能永久存在或接口会改变；接口说明表格标题有：传入参数说明(向方法传参，表格顺序=传参顺序)、返回测试说明(返回一个参数，多个参数时为列表，表格顺序=返回参数顺序)、回调测试说明(返回一个参数列表，表格顺序=回调参数列表顺序)***
# 打包APP
***注：以下教程为第一次打包一个APP前建议要干的事，后续打包没必要再修改。***
1. 下载AIDE Pro<br>
AIDE Pro官网：[https://aidepro.top](https://aidepro.top)
2. 导入项目到AIDE<br>
AIDE项目文件夹：`/storage/emulated/0/AideProjects/`，将所有[https://github.com/IFTC-XLKJ/Webapp](https://github.com/IFTC-XLKJ/Webapp)中`Application`的文件全部复制到AIDE项目文件夹下
3. 编写APP<br>
APP的HTML、CSS、JS等资源都放在`assets`下
4. 修改APP名
APP名在`res/value/strings.xml`中修改
```xml
<resources>
    <string name="app_name">{要修改的的APP名}</string>
    
</resources>
```
5. 修改包名
包名在`AndroidManifest.xml`文件中修改
```xml
<?xml version='1.0' encoding='utf-8'?>
<manifest
    xmlns:android="http://schemas.android.com/apk/res/android"
    package="{要修改的包名(包名格式必须规范)}">

    <!-- 只有在前台运行时才能获取大致位置信息 -->
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>

    <!-- 只能在前台获取精确的位置信息 -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>

    <!-- 查看网络连接 -->
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
...more
        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="{包名}.provider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths"></meta-data>
        </provider>
...more
```
6. 修改应用版本名(字符串)和版本号(数值)<br>
在非项目主要文件夹下的build.gradle文件中
```gradle
apply plugin: 'com.android.application'

android {
    compileSdkVersion 30
    buildToolsVersion "33.0.0"

    defaultConfig {
        applicationId "{同时，在这里修改一下包名}"
        minSdkVersion 16
        targetSdkVersion 26
        versionCode {要修改的版本号}
        versionName "{要修改的版本名}"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    implementation 'androidx.appcompat:appcompat:1.0.0'
	implementation 'com.google.android.material:material:1.0.0'
    implementation fileTree(dir: 'libs', include: ['*.jar'])
}
```
7. 修改启动页面
在`MainActivity.java`中，`file:///android_asset/`为安装包内的`assets`文件夹目录
```java
...more
        webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                }
            });
        webView.loadUrl("file:///android_asset/{assets文件夹下的文件}");
        messageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String[] message = intent.getStringArrayExtra("message");
                String type = intent.getStringExtra("type");
                String callback = intent.getStringExtra("callback");
                if (type.equals("isVpn")) {
...more
```
8. 修改图标<br>
在`res/mipmap-anydpi-v26`目录下的`ic_launcher`（文件前缀名），文件后缀名只要是图片就行，不支持svg，需要转换成xml（Android专用矢量图），建议使用png
9. 修改`res/values`目录下的`colors.xml`，颜色值格式：#aarrggbb
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="colorPrimary">{默认颜色(修改相当于修改了个寂寞)}</color>
    <color name="colorPrimaryDark">{默认颜色深色(同上)}</color>
    <color name="colorAccent">{光标颜色}</color>
</resources>
```
10. 构建APP<br>
点击右上角的三角以构建APP
# JS接口
## `isVpn(回调函数名)`
接口说明：该接口可用于检测用户是否开启VPN。

传入参数说明：
| 参数名 | 参数类型 | 参数说明 |
| --- | --- | --- |
| callback | String | 回调函数名 |

返回参数说明：
| 参数名 | 参数类型 | 参数说明 |
| --- | --- | --- |
| is | Boolean | 是否 |
