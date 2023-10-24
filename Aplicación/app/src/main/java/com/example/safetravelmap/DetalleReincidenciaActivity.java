package com.example.safetravelmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.nullness.qual.NonNull;

public class DetalleReincidenciaActivity extends AppCompatActivity {
    ImageView imageView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_reincidencia);

        imageView3 = findViewById(R.id.imageView3);

        Intent intent = getIntent();
        String imagen = intent.getStringExtra("imagen");
        String desc = intent.getStringExtra("desc");
        TextView descReincidenciaDetalle = (TextView) findViewById(R.id.descReincidenciaDetalle);
        descReincidenciaDetalle.setText(desc);

        Toast.makeText(this, "La imagen es: " + imagen, Toast.LENGTH_SHORT).show();

        // Crear referencia a la imagen en Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imagen);

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Usar Glide para cargar la imagen
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(imageView3);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }
}