window.a1;
window.b1;
window.c1;
window.d1;
window.e1;
onload = () => {
    eruda.init();
    console.log(iftc);
    console.log("接口数量：", Object.keys(iftc).length)
    console.log("版权：", iftc.copyright())
    console.log("Base64转文本(UTF-8)", iftc.Base64ToContent("SUZUQyBXZWJhcHAKSUZUQ+e9kemhteW6lOeUqA=="))
    console.log("文本转Base64(UTF-8)", iftc.ContentToBase64("IFTC Webapp\nIFTC网页应用"))
        //iftc.showToast("短时间Toast提示", false)
        //iftc.showToast("长时间Toast提示", true)
    console.log("应用名：", iftc.appName())
    console.log("包名：", iftc.packageName())
    console.log("版本名：", iftc.verName())
    console.log("版本号：", iftc.verCode())
    console.log("本地服务器已在 8081 端口开启")
    console.log("AndroidID", iftc.getAndroidID())
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
    console.log('创建文件', iftc.createFile(exampleFile));
    console.log('写入文件', iftc.writeFile(exampleFile, iftc.ContentToBase64("IFTC Webapp\n")));
    console.log('追加写入文件', iftc.addWriteFile(exampleFile, "IFTC网页应用"))
    console.log('读取文件', iftc.Base64ToContent(iftc.readFile(exampleFile)))
    console.log('是否为文件', iftc.isFile(exampleFile))
    console.log('是否为文件夹', iftc.isDir(exampleFile))
    console.log('重命名文件', iftc.renameFile(exampleFile, "newExample.txt"))
    console.log('删除文件', iftc.removeFile(exampleFile))
    console.log('是否存在', iftc.isExists(exampleFile))
    const examplePath = "/storage/emulated/0/"
    console.log('文件夹目录', JSON.parse(iftc.getDir(examplePath)))
    console.groupEnd();
    console.log("执行 iftc.toAPPInfoPage(包名) 跳转到对应包名应用的应用信息页面")
    console.log("执行 iftc.toSettings() 跳转到系统设置页面")
    console.log("设备名", iftc.getDeviceName())
    iftc.isVpn("isVpn")
    iftc.checkStoragePermission("checkStoragePermission")
    iftc.server(8081, "/storage/emulated/0/", "")
    iftc.sendBasicNotification(0, 'b', '114514', '114514', true)
    iftc.sendProgressNotification(1, 'c', '114514', '114514', true, 50)
    iftc.sendImageNotification(2, 'd', '114514', '114514', 'http://localhost:8081/IFTC_ba.jpg', true)
    iftc.sendBigTextNotification(3, 'e', '114514', '114514', '114514', '114514', '114514')

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