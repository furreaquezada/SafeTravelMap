package com.example.safetravelmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.safetravelmap.entities.Desperfecto;
import com.example.safetravelmap.entities.Estaticos;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.nullness.qual.NonNull;

public class DesperfectoActivity extends AppCompatActivity {

    Desperfecto desperfecto = new Desperfecto();
    ImageView imageView;

    TextView tv_desperfecto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desperfecto);

        imageView = findViewById(R.id.imageView);
        tv_desperfecto = (TextView) findViewById(R.id.tv_desperfecto);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            int index = intent.getIntExtra("index", -1);
            if(index != -1){
                desperfecto = Estaticos.desperfectos.get(index);
            }
        }
        if(desperfecto.getImagen() != null){
            tv_desperfecto.setText(desperfecto.getDesc());

            // Crear referencia a la imagen en Firebase Storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(desperfecto.getImagen());

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
}
