package practicaltest02.eim.systems.cs.pub.ro.practicaltest02;

import android.os.SystemClock;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by matei on 15-May-16.
 */
public class CommunicationThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (serverThread == null || socket == null) {
            Log.d(Constants.TAG, "serverThread or socket is null");
            return;
        }

        try {
            BufferedReader socketReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            PrintWriter socketWriter = new PrintWriter(this.socket.getOutputStream(), true);

            if (socketReader == null || socketWriter == null) {
                Log.d(Constants.TAG, "socket reader or writer is null");
            } else {
                String line = socketReader.readLine();
                int firstComma = line.indexOf(",");
                int secondComma = line.indexOf(",", firstComma + 1);
                String op = line.substring(0, firstComma);
                int num1 = Integer.parseInt(line.substring(firstComma + 1, secondComma));
                int num2 = Integer.parseInt(line.substring(secondComma + 1));

                Log.d(Constants.TAG, "op:   " + op);
                Log.d(Constants.TAG, "num1: " + String.valueOf(num1));
                Log.d(Constants.TAG, "num2: " + String.valueOf(num2));

                if (op.equals("add")) {
                    socketWriter.println(String.valueOf(num1 + num2));
                } else {
                    SystemClock.sleep(1000);
                    socketWriter.println(String.valueOf(num1 * num2));
                }
            }

            socket.close();
        } catch (Exception e) {
            Log.d(Constants.TAG, e.getMessage());
            if (Constants.DEBUG) {
                e.printStackTrace();
            }
        }
    }
}
