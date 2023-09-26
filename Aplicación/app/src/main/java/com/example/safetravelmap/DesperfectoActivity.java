package com.example.safetravelmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.safetravelmap.entities.Desperfecto;
import com.example.safetravelmap.entities.Estaticos;
import com.example.safetravelmap.entities.Voto;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class DesperfectoActivity extends AppCompatActivity {

    Desperfecto desperfecto = new Desperfecto();
    ImageView imageView;

    TextView tv_desperfecto;
    TextView textViewInfo;
    Button buttonFalso;

    ArrayList<Voto> votos = new ArrayList<Voto>();

    int puntaje = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desperfecto);

        imageView = findViewById(R.id.imageView);
        tv_desperfecto = (TextView) findViewById(R.id.tv_desperfecto);
        textViewInfo = (TextView) findViewById(R.id.textViewInfo);
        buttonFalso = (Button) findViewById(R.id.buttonFalso);

        buttonFalso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí va el código que quieres ejecutar cuando el botón es presionado
                votar(desperfecto.getLatitud());
            }
        });




        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            int index = intent.getIntExtra("index", -1);
            if(index != -1){
                desperfecto = Estaticos.desperfectos.get(index);
            }
        }
        if(desperfecto.getImagen() != null){
            tv_desperfecto.setText(desperfecto.getDesc());
            textViewInfo.setText(desperfecto.getDesperfecto() + "\n" + desperfecto.getPuntos() + " Puntos");

            cargarVotos();

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
    public void cargarVotos(){
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("votos")
                .whereEqualTo("latitud", desperfecto.getLatitud())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    Toast.makeText(getApplicationContext(), "No existen votos en base de datos.", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    boolean votoMio = false;
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        votos.add(new Voto(
                                document.getLong("cod_usuario").intValue(),
                                document.getDouble("latitud"),
                                document.getBoolean("tipo_usuario")
                        ));

                        if(Estaticos.tipo_usuario){
                            if(Estaticos.cuenta_usuario.getCod_usuario() == votos.get(votos.size() - 1).getCod_usuario() && votos.get(votos.size() - 1).isTipo_usuario() == true){
                                votoMio = true;
                            }
                        }else{
                            if(Estaticos.cuenta_administrador.getCod_usuario() == votos.get(votos.size() - 1).getCod_usuario() && votos.get(votos.size() - 1).isTipo_usuario() == false){
                                votoMio = true;
                            }
                        }
                    }
                    if(votoMio) {
                        textViewInfo.setText(desperfecto.getDesperfecto() + "\n" + (desperfecto.getPuntos() - votos.size() * puntaje) + " Puntos.\nUsted ya votó.");
                        buttonFalso.setEnabled(false);
                    }else{
                        textViewInfo.setText(desperfecto.getDesperfecto() + "\n" + (desperfecto.getPuntos() - votos.size() * puntaje) + " Puntos.");
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "ERROR: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void votar(double latitud){
        buttonFalso.setEnabled(false);
        Toast.makeText(getApplicationContext(), "Votando como NO encontrado.", Toast.LENGTH_SHORT).show();
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        // Agregar un nuevo documento a la colección "votos"
        // Crear un nuevo objeto con los atributos que quieres almacenar
        Map<String, Object> voto = new HashMap<>();

        if(Estaticos.tipo_usuario == true) {
            voto.put("cod_usuario", Estaticos.cuenta_usuario.getCod_usuario()); // Aquí pon el número que corresponda
        } else {
            voto.put("cod_usuario", Estaticos.cuenta_administrador.getCod_usuario()); // Aquí pon el número que corresponda
        }

        voto.put("latitud", latitud);
        voto.put("tipo_usuario", Estaticos.tipo_usuario);




        mFirestore.collection("votos")
                .add(voto)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Documento añadido con éxito
                        Log.d(TAG, "Voto añadido con ID: " + documentReference.getId());
                        Toast.makeText(getApplicationContext(), "Voto añadido con ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                        textViewInfo.setText(desperfecto.getDesperfecto() + "\n" + (desperfecto.getPuntos() - (votos.size() + 1) * puntaje) + " Puntos.");
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
