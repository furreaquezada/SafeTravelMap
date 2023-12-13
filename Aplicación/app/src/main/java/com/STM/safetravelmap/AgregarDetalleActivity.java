package com.STM.safetravelmap;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class AgregarDetalleActivity extends AppCompatActivity {

    // Variables miembro
    private Uri imagenUri;
    public String id;
    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        imagenUri = uri;
                        subirImagen();
                    }
                }
            }
    );






    public int indexTutorial;
    EditText tituloGaleriaTutorialIng;
    EditText descGaleriaTutorialIng;

    Button btnIngresoGaleriaTutorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_detalle);

        tituloGaleriaTutorialIng = (EditText) findViewById(R.id.tituloGaleriaTutorialIng);
        descGaleriaTutorialIng = (EditText) findViewById(R.id.descGaleriaTutorialIng);
        btnIngresoGaleriaTutorial = (Button) findViewById(R.id.btnIngresoGaleriaTutorial);

        btnIngresoGaleriaTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearGaleriaTutorial();
            }
        });

        Intent intent = getIntent();
        indexTutorial = intent.getIntExtra("indexTutorial", -1);


        crearGaleriaTutorial();
    }

    public void crearGaleriaTutorial(){
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        // Agregar un nuevo documento a la colección "tutorial"
        // Crear un nuevo objeto con los atributos que quieres almacenar
        Map<String, Object> galeriaTutorial = new HashMap<>();

        String tituloGaleriaTutorialIngStr = tituloGaleriaTutorialIng.getText().toString();
        String descGaleriaTutorialIngStr = descGaleriaTutorialIng.getText().toString();
        if (!esValido(tituloGaleriaTutorialIngStr) || !esValido(descGaleriaTutorialIngStr)) {
            // Mostrar un mensaje al usuario indicando que hay campos vacíos o inválidos
            Toast.makeText(this, "Por favor, rellene todos los campos correctamente.", Toast.LENGTH_SHORT).show();
            return;
        }
        btnIngresoGaleriaTutorial.setEnabled(false);



        galeriaTutorial.put("titulo", tituloGaleriaTutorialIngStr);
        galeriaTutorial.put("desc", descGaleriaTutorialIngStr);
        galeriaTutorial.put("idTutorial", Estaticos.tutoriales.get(indexTutorial).getId());





        mFirestore.collection("galeriaTutoriales")
                .add(galeriaTutorial)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Documento añadido con éxito
                        id = documentReference.getId();
                        seleccionarImagen();
                        Log.d(TAG, "Tutorial añadido con ID: " + documentReference.getId());
                        btnIngresoGaleriaTutorial.setEnabled(true);
                        tituloGaleriaTutorialIng.setText("");
                        descGaleriaTutorialIng.setText("");
                        Toast.makeText(getApplicationContext(), "Galería tutorial añadida con ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
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

    private static final int PICK_IMAGE_REQUEST = 1;




    private boolean esValido(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }


    private void seleccionarImagen() {
        mGetContent.launch("image/*");
    }

    private void subirImagen() {
        if (imagenUri == null) {
            Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String imageFileName = id; // Reemplaza esto con el ID de tu imagen
        StorageReference imagenRef = storageRef.child(imageFileName + ".jpg");

        Toast.makeText(this, "Cargando, espere...", Toast.LENGTH_SHORT).show();
        UploadTask uploadTask = imagenRef.putFile(imagenUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(AgregarDetalleActivity.this, "EL ERROR: " + exception.getCause(), Toast.LENGTH_SHORT).show();
                System.out.println("EL ERROR: " + exception.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AgregarDetalleActivity.this, "Imagen cargada con éxito!!!", Toast.LENGTH_SHORT).show();
                //almacenar();
            }
        });
    }
}