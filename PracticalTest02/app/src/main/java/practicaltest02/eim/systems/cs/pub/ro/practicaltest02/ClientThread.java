package practicaltest02.eim.systems.cs.pub.ro.practicaltest02;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by matei on 14-May-16.
 */
public class ClientThread extends Thread {
    private int num1, num2;
    private int op;
    private int port;
    private Activity context;

    public ClientThread(int num1, int num2, int op, int port, Activity context) {
        this.num1 = num1;
        this.num2 = num2;
        this.op = op;
        this.port = port;
        this.context = context;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket("localhost", this.port);
            if (socket == null) {
                Log.d(Constants.TAG, "Could not create socket!");
            }

            BufferedReader sockerReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter socketWriter = new PrintWriter(socket.getOutputStream(), true);

            if (sockerReader == null || socketWriter == null) {
                Log.d(Constants.TAG, "Socker reader / writer is null");
            } else {
                String toWrite = "";
                if (op == Constants.ADD) {
                    toWrite = "add,";
                } else {
                    toWrite = "mul,";
                }
                toWrite = toWrite + String.valueOf(this.num1) + "," + String.valueOf(this.num2);

                socketWriter.println(toWrite);
                socketWriter.flush();

                String result;
                final Activity staticContext = this.context;
                while ((result = sockerReader.readLine()) != null) {
                    final String finalResult = result;
                    this.context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(staticContext, finalResult, Toast.LENGTH_SHORT).show();
                        }
                    });
//                    Log.d(Constants.TAG, "result: "+ result);
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
