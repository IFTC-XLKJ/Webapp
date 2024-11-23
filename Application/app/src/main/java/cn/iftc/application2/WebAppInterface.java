package cn.iftc.application2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.widget.Toast;
import cn.iftc.application2.BuildConfig;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class WebAppInterface {
    private Context mContext;
    private WeakReference<MainActivity> activityRef;
    WebAppInterface(Context c, MainActivity activity) {
        mContext = c;
        this.activityRef = new WeakReference<>(activity);
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
    public void server(int port, String root_path, String options) {
        sendMessage("server", "", new String[]{port + "", root_path, options});
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
    public void getClipboard(String callback) {
        sendMessage("getClipboard", callback, new String[]{});
    }
    @JavascriptInterface
    public String getAndroidID() {
        return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
