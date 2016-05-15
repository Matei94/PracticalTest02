package practicaltest02.eim.systems.cs.pub.ro.practicaltest02;

import android.util.Log;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by matei on 14-May-16.
 */
public class ServerThread extends Thread {
    private ServerSocket serverSocket;
    private int port;
    private HashMap<String, WeatherForecastInformation> data;

    public ServerThread(int port) {
        this.port = port;
        this.data = new HashMap<String, WeatherForecastInformation>();

        try {
            this.serverSocket = new ServerSocket(port);
            Log.d(Constants.TAG, "Server is up and running at port: " + String.valueOf(port));
        } catch (Exception e) {
            Log.d(Constants.TAG, e.getMessage());
            if (Constants.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Log.d(Constants.TAG, "Waiting for a connection...");
                Socket socket = serverSocket.accept();
                Log.d(Constants.TAG, "Connection accepted from " + socket.getInetAddress() + ":" + socket.getLocalPort());

                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();
            }
        } catch (Exception e) {
            Log.d(Constants.TAG, e.getMessage());
            if (Constants.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }

    public synchronized HashMap<String, WeatherForecastInformation> getData() {
        return data;
    }

    public synchronized void setData(String city, WeatherForecastInformation weatherForecastInformation) {
        this.data.put(city, weatherForecastInformation);
    }

    public void stopThread() {
        if (serverSocket != null) {
            interrupt();
            try {
                serverSocket.close();
            } catch (Exception e) {
                Log.d(Constants.TAG, e.getMessage());
                if (Constants.DEBUG) {
                    e.printStackTrace();
                }
            }
        }
    }
}
