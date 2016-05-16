package practicaltest02.eim.systems.cs.pub.ro.practicaltest02;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {
    // GUI
    // Server
    private EditText etServer1;
    private Button btnServer1;

    // Client
    private EditText etClient1;
    private EditText etClient2;
    private Button btnClient1;
    private Button btnClient2;

    private ServerThread serverThread;

    private int serverPort;

    private ButtonClickListener buttonClickListener = new ButtonClickListener();

    private void initGUI() {
        // Server
        etServer1   = (EditText) findViewById(R.id.etServer1);
        btnServer1  = (Button)   findViewById(R.id.btnServer1);

        // Client
        etClient1   = (EditText) findViewById(R.id.etClient1);
        etClient2   = (EditText) findViewById(R.id.etClient2);
        btnClient1  = (Button)   findViewById(R.id.btnClient1);
        btnClient2  = (Button)   findViewById(R.id.btnClient2);

        // Click listeners
        btnServer1.setOnClickListener(buttonClickListener);
        btnClient1.setOnClickListener(buttonClickListener);
        btnClient2.setOnClickListener(buttonClickListener);
    }

    private void btnServer1Callback() {
        // Get data
        String serverPort = etServer1.getText().toString();

        if (serverPort == null || serverPort.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        this.serverThread = new ServerThread(Integer.parseInt(serverPort));
        if (this.serverThread.getServerSocket() != null) {
            this.serverPort = Integer.parseInt(serverPort);
            this.serverThread.start();
        } else {
            Log.d(Constants.TAG, "Could not create server thread");
        }
    }

    private void btnClient1Callback() {
        if (this.serverThread == null) {
            Toast.makeText(getApplicationContext(), "Server not started", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get data
        String num1 = etClient1.getText().toString();
        String num2 = etClient2.getText().toString();

        if (num1 == null || num1.isEmpty() ||
                num2 == null || num2.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ClientThread clientThread = new ClientThread(Integer.parseInt(num1), Integer.parseInt(num2), Constants.ADD, this.serverPort, getApplicationContext());
        clientThread.start();
    }

    private void btnClient2Callback() {
        if (this.serverThread == null) {
            Toast.makeText(getApplicationContext(), "Server not started", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get data
        String num1 = etClient1.getText().toString();
        String num2 = etClient2.getText().toString();

        if (num1 == null || num1.isEmpty() ||
                num2 == null || num2.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ClientThread clientThread = new ClientThread(Integer.parseInt(num1), Integer.parseInt(num2), Constants.MUL, this.serverPort, getApplicationContext());
        clientThread.start();
    }

    private class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnServer1:
                    btnServer1Callback();
                    break;

                case R.id.btnClient1:
                    btnClient1Callback();
                    break;

                case R.id.btnClient2:
                    btnClient2Callback();
                    break;

                default:
                    Log.d(Constants.TAG, "Invalid view id: " + v.getId());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        initGUI();
    }

    @Override
    protected void onDestroy() {
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}
