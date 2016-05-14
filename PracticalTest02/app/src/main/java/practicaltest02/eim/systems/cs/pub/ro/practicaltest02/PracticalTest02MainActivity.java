package practicaltest02.eim.systems.cs.pub.ro.practicaltest02;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    private ButtonClickListener buttonClickListener = new ButtonClickListener();

    private void initGUI() {
        // Server
        etServer1 = (EditText) findViewById(R.id.etServer1);
        btnServer1  = (Button) findViewById(R.id.btnServer1);

        // Client
        etClient1 = (EditText) findViewById(R.id.etClient1);
        etClient2 = (EditText) findViewById(R.id.etClient2);
        etClient3 = (EditText) findViewById(R.id.etClient3);
        btnClient1  = (Button) findViewById(R.id.btnClient1);

        // Click listeners
        btnServer1.setOnClickListener(buttonClickListener);
        btnClient1.setOnClickListener(buttonClickListener);
    }

    private class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnServer1:
                    Toast.makeText(getApplicationContext(), "Server", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.btnClient1:
                    Toast.makeText(getApplicationContext(), "Client", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Log.d("MATEI", "Invalid view id: " + v.getId());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        initGUI();
    }
}
