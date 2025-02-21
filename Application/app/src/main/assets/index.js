window.a1;
window.b1;
window.c1;
window.d1;
window.e1;
window.f1;
onload = () => {
    eruda.init();// 启动Eruda控制台
    console.log(iftc);// 输出所有JavaScript接口，返回一个对象，接口位置在window对象下
    console.log("接口数量：", Object.keys(iftc).length)// 获取接口数量
    console.log("版权：", iftc.copyright())// 获取接口版权信息
    console.log("Base64转文本(UTF-8)", iftc.Base64ToContent("SUZUQyBXZWJhcHAKSUZUQ+e9kemhteW6lOeUqA=="))// Base64转文本（UTF-8）
    console.log("文本转Base64(UTF-8)", iftc.ContentToBase64("IFTC Webapp\nIFTC网页应用"))// 文本转Base64（UTF-8）
    // iftc.showToast("短时间Toast提示", false)// 弹出短时间提示
    // iftc.showToast("长时间Toast提示", true)// 弹出长时间提示
    console.log("应用名：", iftc.appName())// 获取该应用名
    console.log("包名：", iftc.packageName())// 获取该应用包名
    console.log("版本名：", iftc.verName())// 获取该应用版本名
    console.log("版本号：", iftc.verCode())// 获取该应用版本号
    console.log("本地服务器已在 8081 端口开启")
    console.log("AndroidID", iftc.getAndroidID())// 获取设备唯一ID
    console.log("执行 iftc.exitApp() 退出应用")
    console.log("执行 iftc.toBackstage() 将应用切换到后台")
    console.log("执行 iftc.setStatusBar(1) 状态栏恢复默认")
    console.log("执行 iftc.setStatusBar(2) 状态栏变为亮色")
    console.log("执行 iftc.setStatusBarColor(\"{颜色值}\") 设置状态栏背景色")
    console.log("执行 iftc.shareText(\"分享的文本\", \"分享到\") 分享文本")
    console.log("执行 iftc.shareFile(\"/storage/emulated/0/example.png\", \"image/png\", \"分享图片到\") 分享文件")
    console.log("执行 iftc.browser(\"https://iftc.free.nf\") 系统默认浏览器打开网页")
    console.log("执行 iftc.hideStatusBar() 隐藏状态栏")
    console.log("执行 iftc.showStatusBar() 显示状态栏")
    const exampleFile = "/storage/emulated/0/example.txt"
    console.group('文件操作：(以/storage/emulated/0/example.txt为例)');
    console.log('创建文件', iftc.createFile(exampleFile));// 创建一个空文件
    console.log('写入文件', iftc.writeFile(exampleFile, iftc.ContentToBase64("IFTC Webapp\n")));// 向刚刚创建的文件写入内容，传入Base64
    console.log('追加写入文件', iftc.addWriteFile(exampleFile, "IFTC网页应用"))// 追加写入文本
    console.log('读取文件', iftc.Base64ToContent(iftc.readFile(exampleFile)))// 读取文件内容并转换成文本
    console.log('是否为文件', iftc.isFile(exampleFile))// 判断是否为文件
    console.log('是否为文件夹', iftc.isDir(exampleFile))// 判断是否为目录
    console.log('重命名文件', iftc.renameFile(exampleFile, "newExample.txt"))// 重命名文件
    console.log('删除文件', iftc.removeFile(exampleFile))// 删除文件
    console.log('是否存在', iftc.isExists(exampleFile))// 判断文件是否存在
    const examplePath = "/storage/emulated/0/"
    console.log('文件夹目录', JSON.parse(iftc.getDir(examplePath)))// 获取目录下所有文件
    console.groupEnd();
    console.log("执行 iftc.toAPPInfoPage(包名) 跳转到对应包名应用的应用信息页面")
    console.log("执行 iftc.toSettings() 跳转到系统设置页面")
    console.log("设备名", iftc.getDeviceName())// 获取设备名
    iftc.isVpn("isVpn")// 获取是否开启VPN，并回调至isVpn
    iftc.checkStoragePermission("checkStoragePermission")// 检查是否有存储权限，并回调至checkStoragePermission
    iftc.server(8081, "/storage/emulated/0/")// 在8081端口启动本地服务器，本地服务器根目录为/storage/emulated/0/
    iftc.sendBasicNotification(0, 'b', '114514', '114514', true)// 发送基础通知，参数：ID、点击后回调函数名、标题、内容、是否常驻
    iftc.sendProgressNotification(1, 'c', '114514', '114514', true, 50)// 发送进度条通知，参数：ID、点击后回调函数名、标题、内容、是否常驻、进度(0-100)
    iftc.sendImageNotification(2, 'd', '114514', '114514', 'http://localhost:8081/IFTC_ba.jpg', true)// 发送图片通知，参数：ID、点击后回调函数名、标题、内容、图片URL(建议使用dataURL，避免图片下载所耗时间)、是否常驻
    iftc.sendBigTextNotification(3, 'e', '114514', '114514', '114514', '114514', '114514')// 发送大文本通知（似乎没什么用），参数：ID、点击后回调函数名、大文本、大文本内容、描述文本、内容、标题
    iftc.allowRenew(false);// 是否允许下拉刷新
    iftc.goBack("goBack")// 当返回键按下时，回调至goBack，设置null时恢复默认
    iftc.getAPPs('f')// 获取应用列表，并回调至f

    b1 = e => {
        console.log('Basic', e)
    }
    c1 = e => {
        console.log('Progress', e)
    }
    d1 = e => {
        console.log('Image', e)
    }
    e1 = e => {
        console.log('BigText', e)
    }

    a1 = e => {
        a.innerHTML = "环境亮度：" + e[0] + "流明"
    }
    
    f1 = e => {
        b.innerHTML = "重力：" + e[0] + "," + e[1] + ","+ e[2]
    }
}

function isVpn(e) {
    console.log("VPN开启：", e[0])
}

function checkStoragePermission(e) {
    console.log("读写权限：", e[0])
}

function onDeviceLight(e) {
    console.log(e[0], "流明")
    try {
        a1(e)
    } catch (e) {}
}

function onGravity(e) {
    try {
        f1(e)
    } catch (e) {}
}

function b(e) {
    console.log(e)
    try {
        b1(e)
    } catch (e) {}
}

function c(e) {
    console.log(e)
    try {
        c1(e)
    } catch (e) {}
}

function d(e) {
    console.log(e)
    try {
        d1(e)
    } catch (e) {}
}

function e(event) {
    console.log(event)
    try {
        e1(event)
    } catch (e) {}
}

function goBack(e) {
    iftc.showToast("返回键被按下", false)
}

function f(e) {
    console.log('应用列表：', e);
    if (e.length > 1) {
        iftc.getAPPInfo(e[0], 'g');// 获取应用列表第一个应用信息
    }
}

function g(e) {
    console.group('应用列表第一个应用信息')
    console.log('应用名', e[0])// 应用名
    console.log('版本名', e[1])// 应用版本名
    console.log('版本号', e[2])// 应用版本号
    console.groupEnd();
}
