package practicaltest02.eim.systems.cs.pub.ro.practicaltest02;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
    private EditText etClient3;
    private Button btnClient1;
    private Spinner spnClient1;

    private TextView tvDataDisplay;

    private ServerThread serverThread;

    private ButtonClickListener buttonClickListener = new ButtonClickListener();

    private void initGUI() {
        // Server
        etServer1   = (EditText) findViewById(R.id.etServer1);
        btnServer1  = (Button)   findViewById(R.id.btnServer1);

        // Client
        etClient1   = (EditText) findViewById(R.id.etClient1);
        etClient2   = (EditText) findViewById(R.id.etClient2);
        etClient3   = (EditText) findViewById(R.id.etClient3);
        btnClient1  = (Button)   findViewById(R.id.btnClient1);
        spnClient1  = (Spinner)  findViewById(R.id.spnClient1);

        tvDataDisplay = (TextView) findViewById(R.id.tvDataDisplay);

        // Click listeners
        btnServer1.setOnClickListener(buttonClickListener);
        btnClient1.setOnClickListener(buttonClickListener);
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
            this.serverThread.start();
        } else {
            Log.d(Constants.TAG, "Could not create server thread");
        }
    }

    private void btnClient1Callback() {
        // Get data
        String address = etClient1.getText().toString();
        String port = etClient2.getText().toString();
        String city = etClient3.getText().toString();
        String information_type = spnClient1.getSelectedItem().toString();

        if (address == null || address.isEmpty() ||
                port == null || port.isEmpty() ||
                city == null || city.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        tvDataDisplay.setText("");

        ClientThread clientThread = new ClientThread(address, Integer.parseInt(port), tvDataDisplay, city, information_type);
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
