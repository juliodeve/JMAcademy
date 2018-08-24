package com.vazquez.julio.cargarimagenconlogin.ACTIVITYS;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.vazquez.julio.cargarimagenconlogin.PARSERS.ParserUsuario;
import com.vazquez.julio.cargarimagenconlogin.R;
import com.vazquez.julio.cargarimagenconlogin.POJOS.Usuario;
import com.vazquez.julio.cargarimagenconlogin.HTTPMANAGER.service;

import java.util.ArrayList;
import java.util.List;

public class Main3Activity extends AppCompatActivity {

    String user;
    List<Usuario> usuarioList = new ArrayList<>();
    TextView txtNombre;
    TextView txtEdad;
    TextView txtSexo;
    TextView txtTelefono;
    TextView txtDireccion;
    TextView txtFechaNacimiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Bundle extras = this.getIntent().getExtras();
        user =  extras.getString("mail");
        Toast.makeText(getApplicationContext(), "Ingresaste como usuario", Toast.LENGTH_SHORT).show();
        setTitle("Bienvenido "+ user);
        final Usuario[] usuarioConectado = {null};

        txtNombre = (TextView) findViewById(R.id.txtNombre);
        txtEdad = (TextView) findViewById(R.id.txtEdad);
        txtSexo = (TextView) findViewById(R.id.txtSexo);
        txtTelefono = (TextView) findViewById(R.id.txtTel);
        txtDireccion = (TextView) findViewById(R.id.txtDireccion);
        txtFechaNacimiento = (TextView) findViewById(R.id.txtFechaNacimiento);

        if (isOnline()){
            final Thread tr = new Thread() {
                @Override
                public void run() {
                    super.run();
                    service s = new service();
                    final String usuario = s.enviarPost(user, "http://jmacademy.juliovazquez.net/UsuarioMovil.php");
                    final String cursos = s.enviarPost(user, "http://jmacademy.juliovazquez.net/CursosUsuarioMovil.php");
                    usuarioList = ParserUsuario.parser(usuario);
                    usuarioConectado[0] = usuarioList.get(0);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtNombre.setText(usuarioConectado[0].getNombre());
                            txtEdad.setText(usuarioConectado[0].getEdad()+" años");
                            txtSexo.setText(usuarioConectado[0].getSexo());
                            txtTelefono.setText(""+usuarioConectado[0].getTelefono());
                            txtDireccion.setText(usuarioConectado[0].getDireccion());
                            txtFechaNacimiento.setText(usuarioConectado[0].getFecha_nacimiento());
                        }
                    });
                }
            };
            tr.start();
        } else {
            Toast.makeText(getApplicationContext(), "Sin acceso a internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                goLoginScreen();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
}
