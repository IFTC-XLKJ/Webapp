package cn.iftc.application7;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;

public class LocalServer implements Runnable {

    private Context context;
    private static final String TAG = "SimpleFileServer";
    private int PORT = 8081;
    private String ROOT_PATH = "/storage/emulated/0/";

    private ServerSocket serverSocket;

    public LocalServer(Context c, int port, String root_path) {
        context = c;
        PORT = port;
        ROOT_PATH = root_path;
        try {
            serverSocket = new ServerSocket(PORT);
            Log.d(TAG, "本地服务器已在端口" + PORT  + "开启");
        } catch (Exception e) {
            Log.e(TAG, "无法启动本地服务器", e);
        }
    }
    @Override
    public void run() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            } catch (Exception e) {
                Log.e(TAG, "错误的接收客户端连接", e);
            }
        }
    }
    private class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream out = clientSocket.getOutputStream();

                String line;
                StringBuilder request = new StringBuilder();
                while ((line = in.readLine()) != null && !line.isEmpty()) {
                    request.append(line).append("\n");
                }

                String[] parts = request.toString().split(" ");
                String method = parts[0];
                String path = parts[1];

                String decodedFileName = URLDecoder.decode(path.substring(1), "UTF-8");
                WebAppInterface wi = new WebAppInterface(context);
                File file = new File(ROOT_PATH, decodedFileName);
                if (!file.isDirectory()) {
                    if (file.exists()) {
                        sendFile(out, file);
                    } else {
                        File index1File = new File(ROOT_PATH, decodedFileName + ".html");
                        wi.writeFile("/storage/emulated/0/Android/data/cn.iftc.application/cache/.txt", wi.ContentToBase64(index1File.getPath()));
                        if (index1File.exists()) {
                            sendFile(out, index1File);
                        } else {
                            send404(out);
                        }
                    }
                } else {
                    File indexFile = new File(ROOT_PATH, decodedFileName + "/index.html");
                    if (indexFile.exists()) {
                        sendFile(out, indexFile);
                    } else {
                        send404(out);
                    }
                }

                clientSocket.close();
            } catch (Exception e) {
                Log.e(TAG, "无法处理客户端", e);
            }
        }

        private void sendFile(OutputStream out, File file) throws Exception {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;

            out.write(("HTTP/1.1 200 OK\r\n").getBytes());
            out.write(("Content-Type: " + getMimeType(file) + "\r\n").getBytes());
            out.write("\r\n".getBytes());

            while ((bytesRead = fis.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            fis.close();
            out.flush();
        }

        private void send404(OutputStream out) throws Exception {
            out.write(("HTTP/1.1 404 Not Found\r\n").getBytes());
            out.write(("Content-Type: text/plain\r\n").getBytes());
            out.write("\r\n".getBytes());
            out.write("404 Not Found".getBytes());
            out.flush();
        }

        private String getMimeType(File file) {
            String name = file.getName();
            if (name.endsWith(".html") ||
                name.endsWith(".htm")
                ) {
                return "text/html";
            } else if (name.endsWith(".js") ||
                       name.endsWith(".jsx")
                       ) {
                return "application/javascript";
            } else if (name.endsWith(".css")) {
                return "text/css";
            } else if (name.endsWith(".png")) {
                return "image/png";
            } else if (name.endsWith(".jpg") ||
                       name.endsWith(".jpeg")
                       ) {
                return "image/jpeg";
            } else if (name.endsWith(".mp3")) {
                return "audio/mp3";
            } else if (name.endsWith(".mp4")) {
                return "vedio/mp4";
            } else if (name.endsWith(".lrc") ||
                       name.endsWith(".txt") ||
                       name.endsWith(".mtsx") ||
                       name.endsWith(".config")
                       ) {
                return "text/plain";
            } else if (name.endsWith(".md") ||
                       name.endsWith(".markdown")
                       ) {
				return "text/md";
			} else if (name.endsWith(".svg")) {
				return "image/svg+xml";
			} else if (name.endsWith(".xml")) {
                return "text/xml";
            } else if (name.endsWith(".json")) {
                return "application/json";
            } else {
                return "application/octet-stream";
            }
        }
    }

    public void stop() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
                Log.d(TAG, "本地服务器已关闭");
            }
        } catch (Exception e) {
            Log.e(TAG, "无法关闭本地服务器", e);
        }
    }
}
