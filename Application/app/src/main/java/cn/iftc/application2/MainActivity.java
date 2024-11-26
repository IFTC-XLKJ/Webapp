package cn.iftc.application2;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.R;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private Activity activity;
    private Context mContext;
    private WebView webView;
    private WebSettings webSettings;
    private BroadcastReceiver messageReceiver;
    private ValueCallback<Uri[]> mFilePathCallback;
    private boolean mHasResultBeenCalled = false;
    private static final int FILE_CHOOSER_RESULT_CODE = 100;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private String CHANNEL_ID = "IFTC_Webapp";
    private BroadcastReceiver notificationReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        activity = this;
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        Window window = getWindow();
        View view = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        view.setSystemUiVisibility(option);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("white"));
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webview);
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(this, this), "iftc");
        webView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_BACK:
                                if (webView.canGoBack()) {
                                    webView.goBack();
                                    return true;
                                } else {
                                    finish();
                                }
                                break;
                        }
                    }
                    return false;
                }
            });
        webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                                 FileChooserParams fileChooserParams) {
                    if (mFilePathCallback != null) {
                        mFilePathCallback.onReceiveValue(null);
                    }
                    mFilePathCallback = filePathCallback;
                    mHasResultBeenCalled = false;
                    String[] acceptTypes = fileChooserParams.getAcceptTypes();
                    int mode = fileChooserParams.getMode();
                    Intent intent = fileChooserParams.createIntent();
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(Intent.createChooser(intent, "选择文件"), FILE_CHOOSER_RESULT_CODE);
                    return true;
                }

                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                }
            });
        webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    view.evaluateJavascript("javascript:setTimeout(()=>{console.log(\"IFTC所有\");console.log(\"IFTC工作室室长QQ：3164417130\");},200)", null);
                }
            });
        webView.loadUrl("file:///android_asset/index.html");
        messageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String[] message = intent.getStringArrayExtra("message");
                String type = intent.getStringExtra("type");
                String callback = intent.getStringExtra("callback");
                if (type.equals("isVpn")) {
                    ArrayList r = new ArrayList();
                    r.add(isvpn());
                    sendResponse(callback, r);
                } else if (type.equals("setStatusBarColor")) {
                    int color = Color.parseColor(message[0]);
                    getWindow().setStatusBarColor(color);
                } else if (type.equals("setStatusBar")) {
                    int option = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    if (message[0].equals("1")) {
                        option = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    } else if (message[0].equals("2")) {
                        option = View.STATUS_BAR_HIDDEN;
                    } else if (message[0].equals("3")) {
                        option = View.STATUS_BAR_VISIBLE;
                    }
                    getWindow().getDecorView().setSystemUiVisibility(option);
                } else if (type.equals("hideStatusBar")) {
                    int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
                    getWindow().getDecorView().setSystemUiVisibility(option);
                } else if (type.equals("showStatusBar")) {
                    int option = View.SYSTEM_UI_FLAG_VISIBLE;
                    getWindow().getDecorView().setSystemUiVisibility(option);
                } else if (type.equals("exitApp")) {
                    activity.finish();
                } else if (type.equals("toBackstage")) {
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                } else if (type.equals("shareText")) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, message[0]);
                    startActivity(Intent.createChooser(shareIntent, message[1]));
                } else if (type.equals("shareFile")) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType(message[1]);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, message[0]);
                    startActivity(Intent.createChooser(shareIntent, message[2]));
                } else if (type.equals("server")) {
                    LocalServer ls = new LocalServer(mContext, Integer.parseInt(message[0]), message[1], message[2]);
                    new Thread(ls).start();
                } else if (type.equals("checkStoragePermission")) {
                    ArrayList r = new ArrayList();
                    r.add(checkStoragePermission());
                    sendResponse(callback, r);
                } else if (type.equals("browser")) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(message[0]));
                    startActivity(i);
                } else if (type.equals("toAPPInfoPage")) {
                    Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", message[0], null);
                    i.setData(uri);
                    startActivity(i);
                } else if (type.equals("toSettings")) {
                    Intent i = new Intent(android.provider.Settings.ACTION_SETTINGS);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } else if (type.equals("keepScreenOn")) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                } else if (type.equals("keepScreenOff")) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                } else if (type.equals("getScreenBright")) {
                    WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                    float currentBrightness = layoutParams.screenBrightness;
                    if (currentBrightness == -1) {
                        try {
                            currentBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS) / 255f;
                        } catch (Settings.SettingNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayList r = new ArrayList();
                    r.add(currentBrightness);
                    sendResponse(callback, r);
                } else if (type.equals("setScreenBright")) {
                    WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                    layoutParams.screenBrightness = Float.parseFloat(message[0]);
                    getWindow().setAttributes(layoutParams);
                } else if (type.equals("sendBasicNotification")) {
                    createNotificationChannel(mContext);
                    Intent i = new Intent(mContext, MainActivity.class);
                    i.setAction(callback);
                    PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(message[1])
                        .setContentText(message[2])
                        .setOngoing(Boolean.parseBoolean(message[3]))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent);
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(Integer.parseInt(message[0]), builder.build());
                } else if (type.equals("sendProgressNotification")) {
                    createNotificationChannel(mContext);
                    Intent i = new Intent(mContext, MainActivity.class);
                    i.setAction(callback);
                    i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(message[1])
                        .setContentText(message[2])
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .setOngoing(Boolean.parseBoolean(message[3]))
                        .setProgress(100, Integer.parseInt(message[4]), false)
                        .setContentIntent(pendingIntent);
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(Integer.parseInt(message[0]) , builder.build());
                } else if (type.equals("sendImageNotification")) {
                    final String actionName = callback;
                    final int id = Integer.parseInt(message[0]);
                    final String title = message[1];
                    final String contentText = message[2];
                    final String url = message[3];
                    final boolean ongoing = Boolean.parseBoolean(message[4]);
                    Toast.makeText(mContext, "参数获取完成", Toast.LENGTH_LONG);
                    new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap bitmap = getBitmapFromURL(url);
                                if (bitmap != null) {
                                    createNotificationChannel(mContext);
                                    Intent i = new Intent(mContext, MainActivity.class);
                                    i.setAction(actionName);
                                    i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setContentTitle(title)
                                        .setContentText(contentText)
                                        .setOngoing(ongoing)
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setContentIntent(pendingIntent)
                                        .setStyle(new NotificationCompat.BigPictureStyle()
                                                  .bigPicture(bitmap)
                                                  .bigLargeIcon(bitmap));
                                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    notificationManager.notify(id, builder.build());
                                } else {
                                    runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(mContext, "图片无法加载，请检查图片URL是否正确再发送图片通知", Toast.LENGTH_LONG).show();
                                                Log.e("Notification", "Failed to download image");
                                            }
                                        });
                                }
                            }
                        }).start();
                }
            }
        };
        registerReceiver(messageReceiver, new IntentFilter("iftc"));
        handleIntent(getIntent(), getIntent().getAction());
    }
    private void sendResponse(String callback, ArrayList response) {
        Log.d("resp", response.toString());
        webView.evaluateJavascript("javascript:try{" + callback + "(" + response.toString() + ")}catch(e){};", null);
        webView.evaluateJavascript("javascript:console.log(\"" + callback + " 已完成回调\");", null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE && resultCode == RESULT_OK && !mHasResultBeenCalled) {
            Uri[] results = null;
            if (data != null) {
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                } else {
                    Uri result = data.getData();
                    if (result != null) {
                        results = new Uri[] { result };
                    }
                }
            }
            if (results != null && mFilePathCallback != null && !mHasResultBeenCalled) {
                mFilePathCallback.onReceiveValue(results);
                mFilePathCallback = null;
                mHasResultBeenCalled = true;
            }
        } else {
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
                mFilePathCallback = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(null);
            mFilePathCallback = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void handleIntent(Intent intent, String actionName) {
        if (intent != null && intent.getAction() != null && intent.getAction().equals(actionName)) {
            ArrayList r = new ArrayList();
            sendResponse(actionName, r);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent, intent.getAction());
    }

    public boolean isvpn() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(mContext.CONNECTIVITY_SERVICE);
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork != null) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
            if (networkCapabilities != null) {
                return networkCapabilities.hasTransport(4);
            }
        }
        return false;
    }

    public boolean checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lux = event.values[0];
            ArrayList r = new ArrayList();
            r.add(lux);
            sendResponse("onDeviceLight", r);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 传感器精度改变时调用
    }
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "IFTC Webapp";
            String description = "此通知由Webapp发出";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
