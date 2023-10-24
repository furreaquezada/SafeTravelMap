package com.example.safetravelmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.safetravelmap.entities.Estaticos;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class AgregarReincidenciaActivity extends AppCompatActivity {

    public Button agregarReincidencia;
    public EditText descReincidencia;


    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PERMISSIONS = 1;
    String currentPhotoPath, imageFileName;
    Double latitud;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_reincidencia);

        agregarReincidencia = (Button) findViewById(R.id.agregarReincidencia);
        descReincidencia = (EditText) findViewById(R.id.descReincidencia);
        Intent intent = getIntent();
        latitud = intent.getDoubleExtra("latitud", 0.0);



        agregarReincidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Solicita los permisos
                if(!descReincidencia.getText().toString().equals("")) {
                    requestPermissions();
                }else{
                    Toast.makeText(getApplicationContext(), "Debe ingresar una descripción", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void requestPermissions() {
        String[] permissions = {
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        if (!hasPermissions(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
        } else {
            tomarFoto();
        }
    }

    private boolean hasPermissions(String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void tomarFoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {
                Toast.makeText(this, "ex: " + ex.toString(),Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                currentPhotoPath = photoFile.getAbsolutePath();

            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Al terminar el proceso de captura de imagen
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            subirImagen();
        }
    }

    private void subirImagen(){
        agregarReincidencia.setEnabled(false);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagenRef = storageRef.child(imageFileName + ".jpg");

        Uri file = Uri.fromFile(new File(currentPhotoPath));

        Toast.makeText(getApplicationContext(), "Cargando, espere...", Toast.LENGTH_SHORT).show();
        UploadTask uploadTask = imagenRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "EL ERROR: " + exception.getCause(), Toast.LENGTH_SHORT).show();
                System.out.println("EL ERROR: " + exception.toString());


            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "Desperfecto cargado con éxito!!!", Toast.LENGTH_SHORT).show();
                almacenar();
            }
        });
    }

    public void almacenar(){

        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        // Crear un nuevo objeto con los atributos que quieres almacenar
        Map<String, Object> desperfecto = new HashMap<>();

        if(Estaticos.tipo_usuario == true) {
            desperfecto.put("cod_usuario", Estaticos.cuenta_usuario.getCod_usuario()); // Aquí pon el número que corresponda
        } else {
            desperfecto.put("cod_usuario", Estaticos.cuenta_administrador.getCod_usuario()); // Aquí pon el número que corresponda
        }

        desperfecto.put("tipo_usuario", Estaticos.tipo_usuario); // Aquí pon el tipo de usuario

        desperfecto.put("desc", descReincidencia.getText().toString()); // Aquí pon la descripción
        desperfecto.put("imagen", imageFileName + ".jpg"); // Aquí pon la URL de la imagen


        desperfecto.put("latitud", latitud);

        // Agregar un nuevo documento a la colección "desperfectos"
        mFirestore.collection("reincidencias")
                .add(desperfecto)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Documento añadido con éxito
                        Log.d(TAG, "Documento añadido con ID: " + documentReference.getId());
                        Toast.makeText(getApplicationContext(), "Documento añadido con ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al añadir documento
                        Log.w(TAG, "Error añadiendo documento", e);
                    }
                });
    }
}