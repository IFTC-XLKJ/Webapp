package cn.iftc.application;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;
import cn.iftc.application.BuildConfig;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WebAppInterface {
    private Context mContext;

    WebAppInterface(Context c) {
        mContext = c;
    }
    public void sendMessage(String type, String callback, String[] message) {
        Intent intent = new Intent("iftc");
        intent.putExtra("message", message);
        intent.putExtra("type", type);
        intent.putExtra("callback", callback);
        mContext.sendBroadcast(intent);
    }
    @JavascriptInterface
    public String copyright() {
        return "Copyright©️IFTC 2020-2024";
    }
    @JavascriptInterface
    public void showToast(String text, boolean longDuration) {
        if (longDuration) {
            Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
        }
    }
    @JavascriptInterface
    public void isVpn(String callback) {
        sendMessage("isVpn", callback, new String[]{});
    }
    @JavascriptInterface
    public void setStatusBarColor(String colorString) {
        sendMessage("setStatusBarColor", "", new String[]{colorString});
    }
    @JavascriptInterface
    public void setStatusBar(String type) {
        sendMessage("setStatusBar", "", new String[]{type});
    }
    @JavascriptInterface
    public void hideStatusBar() {
        sendMessage("hideStatusBar", "", new String[]{});
    }
    @JavascriptInterface
    public void showStatusBar() {
        sendMessage("showStatusBar", "", new String[]{});
    }
    @JavascriptInterface
    public void createFile(String filepath) {
        File file = new File(filepath);
        try {
            if (!file.exists()) {
                if (file.getParentFile() != null && !file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @JavascriptInterface
    public String readFile(String filePath) {
        try {
            File file = new File(filePath);
            InputStream inputStream = new FileInputStream(file);
            byte[] fileData = getBytesFromInputStream(inputStream);
            String base64String = Base64.encodeToString(fileData, Base64.NO_WRAP);
            return base64String;
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }
    public static byte[] getBytesFromInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        is.close();
        return os.toByteArray();
    }
    @JavascriptInterface
    public void writeFile(String filepath, String Base64Content) {
        byte[] decodedBytes = Base64.decode(Base64Content, Base64.DEFAULT);
        File file = new File(filepath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(decodedBytes);
        } catch ( IOException e) {
            e.printStackTrace();
        }
    }
    @JavascriptInterface
    public void addWriteFile(String filepath, String content) {
        String text = Base64ToContent(readFile(filepath));
        text = text + content;
        writeFile(filepath, ContentToBase64(text));
    }
    @JavascriptInterface
    public void removeFile(String filepath) {
        File file = new File(filepath);
        if (file.exists()) {
            file.delete();
        }
    }
    @JavascriptInterface
    public void renameFile(String filepath, String name) {
        File file = new File(filepath);
        int lastIndex = filepath.lastIndexOf("/");
        String result;
        if (lastIndex != -1) {
            result = filepath.substring(0, lastIndex);
            if (file.exists()) {
                this.writeFile(result + "/" + name, this.readFile(filepath));
                this.removeFile(filepath);
            }
        } else {
            Toast.makeText(mContext, "renameFile:执行出错", Toast.LENGTH_SHORT).show();
        }
    }
    @JavascriptInterface
    public boolean isExists(String filepath) {
        File file = new File(filepath);
        return file.exists();
    }
    @JavascriptInterface
    public boolean isDir(String filepath) {
        File file = new File(filepath);
        return file.isDirectory();
    }
    @JavascriptInterface
    public boolean isFile(String filepath) {
        File file = new File(filepath);
        return file.isFile();
    }
    @JavascriptInterface
    public String getDir(String dirpath) {
        File directory = new File(dirpath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("该路径为文件或文件夹不存在");
        }
        ArrayList result = new ArrayList();
        File[] filesAndDirs = directory.listFiles();
        if (filesAndDirs == null) {
            return result.toString();
        }
        for (int i = 0;i < filesAndDirs.length;i++) {
            File file = filesAndDirs[i];
            if (file.isDirectory()) {
                result.add(i, "\"" + file.getName() + "/\"");
            } else {
                result.add(i, "\"" + file.getName() + "\"");
            }
        }
        return result.toString();
    }
    @JavascriptInterface
    public String ContentToBase64(String content) {
        byte[] strByte = content.getBytes();
        return Base64.encodeToString(strByte, Base64.NO_WRAP);
    }
    @JavascriptInterface
    public String Base64ToContent(String base64) {
        byte[] byteBase64 = Base64.decode(base64, Base64.DEFAULT);
        return new String(byteBase64, StandardCharsets.UTF_8);
    }
    @JavascriptInterface
    public void exitApp() {
        sendMessage("exitApp", "", new String[]{});
    }
    @JavascriptInterface
    public void toBackstage() {
        sendMessage("toBackstage", "", new String[]{});
    }
    @JavascriptInterface
    public void shareText(String text, String shareTip) {
        sendMessage("shareText", "", new String[]{text, shareTip});
    }
    @JavascriptInterface
    public void shareFile(String Url, String MimeType, String shareTip) {
        sendMessage("shareFile", "", new String[]{Url, MimeType, shareTip});
    }
    @JavascriptInterface
    public void server(int port, String root_path) {
        sendMessage("server", "", new String[]{port + "", root_path});
    }
    @JavascriptInterface
    public String packageName() {
        String packageName = mContext.getPackageName();
        return packageName;
    }
    @JavascriptInterface
    public String appName() {
        PackageManager pm = mContext.getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(packageName(), 0);
            String appName = (String) pm.getApplicationLabel(ai);
            return appName;
        } catch (Exception e) {
            e.printStackTrace();
            return e + "";
        }}
    @JavascriptInterface
    public String verName() {
        PackageManager pm = mContext.getPackageManager();
        String versionName = BuildConfig.VERSION_NAME;
        try {
            PackageInfo pi = pm.getPackageInfo(packageName(), 0);
            versionName = pi.versionName;
            return versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return e + "";
        }
    }
    @JavascriptInterface
    public long verCode() {
        PackageManager pm = mContext.getPackageManager();
        long versionCode = BuildConfig.VERSION_CODE;
        try {
            PackageInfo pi = pm.getPackageInfo(packageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                versionCode = pi.getLongVersionCode();
            } else {
                versionCode = pi.versionCode;
            }
            return versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    @JavascriptInterface
    public void checkStoragePermission(String callback) {
        sendMessage("checkStoragePermission", callback, new String[]{});
    }
    @JavascriptInterface
    public String getAndroidID() {
        return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
    @JavascriptInterface
    public void browser(String Url) {
        sendMessage("browser", "", new String[]{Url});
    }
    @JavascriptInterface
    public void toAPPInfoPage(String packageName) {
        sendMessage("toAPPInfoPage", "", new String[]{packageName});
    }
    @JavascriptInterface
    public void toSettings() {
        sendMessage("toSettings", "", new String[]{});
    }
    @JavascriptInterface
    public void clipboardWriteText(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
    }
    @JavascriptInterface
    public String clipboardReadText() {
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard.hasPrimaryClip() && clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            String text = item.getText().toString();
            Log.d("Clipboard", "Copied Text: " + text);
            return text;
        }
        return null;
    }
    @JavascriptInterface
    public String getDeviceName() {
        return Build.MODEL;
    }
    @JavascriptInterface
    public String getDeviceMac() {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                return wifiInfo.getMacAddress();
            }
        }
        return null;
    }
    @JavascriptInterface
    public void sendBasicNotification(int id, String actionName, String title, String contentText, boolean ongoing) {
        sendMessage("sendBasicNotification", actionName, new String[]{id + "", title, contentText, ongoing + ""});
    }
    @JavascriptInterface
    public void sendProgressNotification(int id, String actionName, String title, String contentText, boolean ongoing, int progress) {
        sendMessage("sendProgressNotification", actionName, new String[]{id + "", title, contentText, ongoing + "", progress + ""});
    }
    @JavascriptInterface
    public void sendImageNotification(int id, String actionName, String title, String contentText, String url, boolean ongoing) {
        sendMessage("sendImageNotification", actionName, new String[]{id + "", title, contentText, url, ongoing + ""});
    }
    @JavascriptInterface
    public void sendBigTextNotification(int id, String actionName, String bigText, String bigContentText, String summaryText, String contentText, String title) {
        sendMessage("sendBigTextNotification", actionName, new String[]{id + "", bigText, bigContentText, summaryText, contentText, title});
    }
    @JavascriptInterface
    public void cancelNotification(int id) {
        sendMessage("cancelNotification", "", new String[]{id + ""});
    }
    @JavascriptInterface
    public void cancelAllNotification() {
        sendMessage("cancelAllNotification", "", new String[]{});
    }
    @JavascriptInterface
    public void keepScreenOn() {
        sendMessage("keepScreenOn", "", new String[]{});
    }
    @JavascriptInterface
    public void keepScreenOff() {
        sendMessage("keepScreenOff", "", new String[]{});
    }
    @JavascriptInterface
    public void getScreenBright(String callback) {
        sendMessage("getScreenBright", callback, new String[]{});
    }
    @JavascriptInterface
    public void setScreenBright(float value) {
        sendMessage("setScreenBright", "", new String[]{value + ""});
    }
    @JavascriptInterface
    public void goBack(String callback) {
        sendMessage("goBack", callback, new String[]{});
    }
    @JavascriptInterface
    public void allowRenew(boolean is) {
        sendMessage("allowRenew", "", new String[]{is + ""});
    }
    @JavascriptInterface
    public void setRenewColor(String color) {
        sendMessage("setRenewColor", "", new String[]{color});
    }
    @JavascriptInterface
    public void getAPPs(String callback) {
        sendMessage("getAPPs", callback, new String[]{});
    }
    @JavascriptInterface
    public void horScreen(boolean is) {
        sendMessage("horScreen", "", new String[]{is + ""});
    }
    @JavascriptInterface
    public void getAPPInfo(String packageName, String callback) {
        sendMessage("getAPPInfo", callback, new String[]{packageName});
    }

}
