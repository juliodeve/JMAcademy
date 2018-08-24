package com.vazquez.julio.cargarimagenconlogin.ACTIVITYS;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vazquez.julio.cargarimagenconlogin.HTTPMANAGER.service;
import com.vazquez.julio.cargarimagenconlogin.R;

public class MainActivity extends AppCompatActivity {

    AutoCompleteTextView actMail;
    EditText etPass;
    Button btnAcces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Inicio de sesión");

        actMail = (AutoCompleteTextView) findViewById(R.id.actMail);
        actMail.setText("");
        etPass = (EditText) findViewById(R.id.etPass);
        etPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        btnAcces = (Button) findViewById(R.id.btnAccess);

        btnAcces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline()) {
                    taskBtnAcces();
                } else {
                    Toast.makeText(getApplicationContext(), "Sin acceso a internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }



    private void taskBtnAcces() {
        final String user = String.valueOf(actMail.getText());
        final String pass = String.valueOf(etPass.getText());
            final Thread tr = new Thread() {
                @Override
                public void run() {
                    super.run();
                    service s = new service();
                    final String res = s.enviarPost(actMail.getText().toString(), etPass.getText().toString(), "http://jmacademy.juliovazquez.net/loginmovil.php");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (res.equals("OK")) {
                                if (user.equals("joss") && pass.equals("1111")) {
                                    goMainScreenAdmin("");
                                } else {
                                    goMainScreenUser(actMail.getText().toString());
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            };
            tr.start();
    }

    private void goMainScreenAdmin(String mail) {
        Intent intent = new Intent(this, Main2Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("mail", mail);
        startActivity(intent);
    }

    private void goMainScreenUser( String mail) {
        Intent intent = new Intent(this, Main3Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("mail", mail);
        startActivity(intent);
    }
}
