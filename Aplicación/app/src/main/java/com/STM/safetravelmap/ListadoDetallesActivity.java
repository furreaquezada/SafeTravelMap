package com.STM.safetravelmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.safetravelmap.R;
import com.STM.safetravelmap.entities.Estaticos;
import com.STM.safetravelmap.entities.GaleriaTutorial;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListadoDetallesActivity extends AppCompatActivity {
    private ListView listDetalles;
    private ArrayList<String> opciones;

    public int indexTutorial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_detalles);

        Intent intent = getIntent();
        indexTutorial = intent.getIntExtra("indexTutorial", -1);
        cargarDetalles();
    }

    public void cargarDetalles(){
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("galeriaTutoriales").whereEqualTo("idTutorial", Estaticos.tutoriales.get(indexTutorial).getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    Toast.makeText(getApplicationContext(), "No existen detalles en base de datos.", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    Estaticos.galeriaTutoriales.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Estaticos.galeriaTutoriales.add(new GaleriaTutorial(
                                document.getId(),
                                document.getString("desc"),
                                document.getString("idTutorial"),
                                document.getString("titulo")
                        ));
                    }
                    cargarMenu();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "ERROR: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void cargarMenu(){
        listDetalles = (ListView) findViewById(R.id.listDetalles);
        opciones = new ArrayList<String>();

        for(int i=0; i< Estaticos.galeriaTutoriales.size(); i++){
            opciones.add(Estaticos.galeriaTutoriales.get(i).getTitulo());
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, opciones);
        listDetalles.setAdapter(adapter);
        listDetalles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), DetalleGaleriaTutorialActivity.class);
                intent.putExtra("indexGaleriaTutorial", i);
                startActivity(intent);
            }
        });
    }


}