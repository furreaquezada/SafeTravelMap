package com.STM.safetravelmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.STM.safetravelmap.entities.Estaticos;
import com.STM.safetravelmap.entities.GaleriaTutorial;
import com.bumptech.glide.Glide;
import com.example.safetravelmap.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.nullness.qual.NonNull;

public class DetalleGaleriaTutorialActivity extends AppCompatActivity {
    public int indexGaleriaTutorial;
    public GaleriaTutorial galeriaTutorial;

    public TextView tituloGaleriaTutorial;
    public TextView descGaleriaTutorial;

    public Button btnEliminarGaleriaTutorial;

    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_galeria_tutorial);

        Intent intent = getIntent();
        indexGaleriaTutorial = intent.getIntExtra("indexGaleriaTutorial", -1);

        tituloGaleriaTutorial = (TextView) findViewById(R.id.tituloGaleriaTutorial);
        descGaleriaTutorial = (TextView) findViewById(R.id.descGaleriaTutorial);
        btnEliminarGaleriaTutorial = (Button) findViewById(R.id.btnEliminarGaleriaTutorial);
        if(Estaticos.tipo_usuario){
            btnEliminarGaleriaTutorial.setEnabled(false);
        }

        imageView = findViewById(R.id.imageView2);

        cargarGaleriaTutorial();



        btnEliminarGaleriaTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarGaleriaTutorialPorID();
                    }
        });
    }


    public void cargarImagen(){

        if(galeriaTutorial.getId() != null){
            // Crear referencia a la imagen en Firebase Storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(galeriaTutorial.getId() + ".jpg");

            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Usar Glide para cargar la imagen
                    Glide.with(getApplicationContext())
                            .load(uri)
                            .into(imageView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            });
        }
    }

    public void eliminarGaleriaTutorialPorID() {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection("galeriaTutoriales")
                .document(Estaticos.galeriaTutoriales.get(indexGaleriaTutorial).getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Galería tutorial eliminada con éxito.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), SistemaActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "ERROR al eliminar: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void cargarGaleriaTutorial(){
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("galeriaTutoriales")
                .document(Estaticos.galeriaTutoriales.get(indexGaleriaTutorial).getId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) {
                            Toast.makeText(getApplicationContext(), "No existe el tutorial en base de datos.", Toast.LENGTH_SHORT).show();
                            return;
                        }else {
                            galeriaTutorial = new GaleriaTutorial(
                                    documentSnapshot.getId(),
                                    documentSnapshot.getString("desc"),
                                    documentSnapshot.getString("idTutorial"),
                                    documentSnapshot.getString("titulo")
                            );

                            tituloGaleriaTutorial.setText(galeriaTutorial.getTitulo());
                            descGaleriaTutorial.setText(galeriaTutorial.getDesc());
                            cargarImagen();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "ERROR: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}