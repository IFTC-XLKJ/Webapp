onload = () => {
    eruda.init();
    console.log(iftc);
    console.log("接口数量：", Object.keys(iftc).length)
    console.log("版权：", iftc.copyright())
    iftc.showToast("短时间Toast提示", false)
    iftc.showToast("长时间Toast提示", true)
    console.log("应用名：", iftc.appName())
    console.log("包名：", iftc.packageName())
    console.log("版本名：", iftc.verName())
    console.log("版本号：", iftc.verCode())
    iftc.isVpn("isVpn")
    iftc.checkStoragePermission("checkStoragePermission")
    iftc.server(8081, "/storage/emulated/0/", "")
    console.log("本地服务器已在 8081 端口开启")
    console.log("AndroidID", iftc.getAndroidID())
}

function isVpn(e) {
    console.log("VPN开启：", e[0])
}

function checkStoragePermission(e) {
    console.log("读写权限：", e[0])
}