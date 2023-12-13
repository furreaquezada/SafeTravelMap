package com.STM.safetravelmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.safetravelmap.R;
import com.STM.safetravelmap.entities.Estaticos;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class SubirComprobanteDePagoActivity extends AppCompatActivity {

    public Button btnCargarComprobante;
    private static final int REQUEST_PERMISSIONS = 1;
    private static final int PICK_IMAGE = 2;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_comprobante_de_pago);

        btnCargarComprobante = findViewById(R.id.btnCargarComprobante);
        btnCargarComprobante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Solicita los permisos
                requestPermissions();
            }
        });
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
        } else {
            seleccionarImagen();
        }
    }

    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                seleccionarImagen();
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            subirImagen();
        }
    }

    private void subirImagen() {
        if (imageUri == null) {
            Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        btnCargarComprobante.setEnabled(false);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String imageName = imageUri.getLastPathSegment();
        StorageReference imagenRef = storageRef.child("comprobantes/" + imageName);

        Toast.makeText(getApplicationContext(), "Cargando, espere...", Toast.LENGTH_SHORT).show();
        UploadTask uploadTask = imagenRef.putFile(imageUri);

        uploadTask.addOnFailureListener(exception -> {
            // Manejo de errores
        }).addOnSuccessListener(taskSnapshot -> {
            almacenar(imageName);
        });
    }



        public void almacenar(String imageName){

            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

            // Crear un nuevo objeto con los atributos que quieres almacenar
            Map<String, Object> comprobante = new HashMap<>();

            if(Estaticos.tipo_usuario == true) {
                comprobante.put("cod_usuario", Estaticos.cuenta_usuario.getCod_usuario()); // Aquí pon el número que corresponda
            } else {
                comprobante.put("cod_usuario", Estaticos.cuenta_administrador.getCod_usuario()); // Aquí pon el número que corresponda
            }

            comprobante.put("tipo_usuario", Estaticos.tipo_usuario); // Aquí pon el tipo de usuario

            comprobante.put("imagen", imageName);


            // Agregar un nuevo documento a la colección "desperfectos"
            mFirestore.collection("comprobantes")
                    .add(comprobante)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            // Documento añadido con éxito
                            Log.d(TAG, "Comprobante añadido con ID: " + documentReference.getId());
                            Toast.makeText(getApplicationContext(), "Comprobante añadido con ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
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