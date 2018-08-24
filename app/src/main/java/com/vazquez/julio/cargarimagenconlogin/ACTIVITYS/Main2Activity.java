package com.vazquez.julio.cargarimagenconlogin.ACTIVITYS;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.vazquez.julio.cargarimagenconlogin.R;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.IOException;
import java.util.UUID;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    private Button btnSeleccionar;
    private Button btnCargar;
    private ImageView imageView;
    private AutoCompleteTextView txtIdCurso;
    private Spinner spCurso;
    private Spinner spCateguria;
    private Spinner spDuracion;

    //Image request code
    private int PICK_IMAGE_REQUEST = 1;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;

    //Bitmap to get image from gallery
    private Bitmap bitmap;

    //Uri to store the image uri
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        setTitle("Registrar un curso");
        //Requesting storage permission
        requestStoragePermission();

        //Initializing views
        btnSeleccionar = (Button) findViewById(R.id.btnSeleccionar);
        btnCargar = (Button) findViewById(R.id.btnCargar);
        imageView = (ImageView) findViewById(R.id.imageView);
        txtIdCurso = (AutoCompleteTextView) findViewById(R.id.txtIdCurso);
        spCurso = (Spinner) findViewById(R.id.spCurso);
        spCateguria = (Spinner) findViewById(R.id.spCategoria);
        spDuracion = (Spinner) findViewById(R.id.spDuracion);

        //Setting clicklistener
        btnSeleccionar.setOnClickListener(this);
        btnCargar.setOnClickListener(this);

        Toast.makeText(getApplicationContext(), "Ingresaste como administrador", Toast.LENGTH_SHORT).show();
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
    
    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    public void uploadMultipart() {
        //getting name for the image
//        String name = editText.getText().toString().trim();

        //getting the actual path of the image
        String path = getPath(filePath);

        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, "http://jmacademy.juliovazquez.net/CargarImagenMovil.php")
                    .addFileToUpload(path, "imagen") //Adding file
                    .addParameter("destino", "images/"+spCurso.getSelectedItem().toString() +"-" + spCateguria.getSelectedItem().toString())
                    .addParameter("id", txtIdCurso.getText().toString())
                    .addParameter("nombre", spCurso.getSelectedItem().toString())
                    .addParameter("categoria", spCateguria.getSelectedItem().toString())
                    .addParameter("duracion", spDuracion.getSelectedItem().toString())
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload

        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }

//        editText.setText("");
        imageView.setImageBitmap(null);
        txtIdCurso.setText("");
        spDuracion.setSelection(0);
        spCateguria.setSelection(0);
        spCurso.setSelection(0);
        btnCargar.setVisibility(View.GONE);
        Toast.makeText(this, "Cargando imagen", Toast.LENGTH_LONG).show();
    }


    //method to show file chooser
    private void showFileChooser() {
        btnCargar.setVisibility(View.VISIBLE);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //method to get the file path from uri
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnSeleccionar) {
            showFileChooser();
        }
        if (v == btnCargar) {
            uploadMultipart();
        }
    }
}
