package cn.iftc.application2;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.w3c.dom.Element;

public class LocalServer implements Runnable {

    private Context context;
    private String options;
    private static final String TAG = "SimpleFileServer";
    private int PORT = 8081;
    private String ROOT_PATH = "/storage/emulated/0/";

    private ServerSocket serverSocket;

    public LocalServer(Context c, int port, String root_path, String ops) {
        options = ops;
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
    private ArrayList<String[]> keyPath(String xml) {
        try {
            ArrayList r1 = new ArrayList();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            Document document = builder.parse(is);
            NodeList itemNodes = document.getElementsByTagName("item");
            for (int i = 0;i < itemNodes.getLength();i ++) {
                Node itemNode = itemNodes.item(i);
                Element itemElement = (Element)itemNode;
                String key  = itemElement.getAttribute("key");
                if (key == "keyPath") {
                    NodeList items = itemElement.getElementsByTagName("item");
                    Node item1 = items.item(0);
                    Node item2 = items.item(1);
                    String[] Key = item1.getTextContent().split("\n");
                    String[] Path = item2.getTextContent().split("\n");
                    r1.add(0, Key);
                    r1.add(1, Path);
                    return r1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ArrayList r2 = new ArrayList();
            return r2;
        }
        ArrayList r = new ArrayList();
        return r;
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
                ArrayList r = keyPath(options);
                File file = new File(ROOT_PATH, decodedFileName);
                if (!file.isDirectory()) {
                    if (file.exists()) {
                        sendFile(out, file);
                    } else {
                        send404(out);
                    }
                } else {
                    File indexFile = new File(ROOT_PATH, decodedFileName + "/index.html");
                    if (indexFile.exists()) {
                        sendFile(out, indexFile);
                    } else {
                        File index1File = new File(ROOT_PATH, decodedFileName + "/index2.html");
                        if (indexFile.exists()) {
                            sendFile(out, index1File);
                        } else {
                            send404(out);
                        }
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
