package practicaltest02.eim.systems.cs.pub.ro.practicaltest02;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by matei on 14-May-16.
 */
public class ClientThread extends Thread {
    private String address;
    private int port;
    private String city;
    private String informationType;
    private TextView tvDataDisplay;

    public ClientThread(
            String address,
            int port,
            TextView tvDataDisplay,
            String city,
            String informationType
    ) {
        this.address = address;
        this.port    = port;
        this.tvDataDisplay = tvDataDisplay;

        this.city            = city;
        this.informationType = informationType;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(this.address, this.port);
            if (socket == null) {
                Log.d(Constants.TAG, "Could not create socket!");
            }

            BufferedReader sockerReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter socketWriter = new PrintWriter(socket.getOutputStream(), true);

            if (sockerReader == null || socketWriter == null) {
                Log.d(Constants.TAG, "Socker reader / writer is null");
            } else {
                socketWriter.println(this.city);
                socketWriter.flush();
                socketWriter.println(this.informationType);
                socketWriter.flush();
                String weatherInformation;
                while ((weatherInformation = sockerReader.readLine()) != null) {
                    final String finalizedWeatherInformation = weatherInformation;
                    tvDataDisplay.post(new Runnable() {
                        @Override
                        public void run() {
                            tvDataDisplay.append(finalizedWeatherInformation + "\n");
                        }
                    });
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
