package com.STM.safetravelmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.STM.safetravelmap.entities.Estaticos;
import com.STM.safetravelmap.entities.Tutorial;
import com.example.safetravelmap.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TutorialesActivity extends AppCompatActivity {

    private ListView listTutoriales;
    private ArrayList<String> opciones;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutoriales);
        cargarTutoriales();
    }

    public void cargarTutoriales(){
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("tutoriales").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    Toast.makeText(getApplicationContext(), "No existen categorías en base de datos.", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    Estaticos.tutoriales.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Estaticos.tutoriales.add(new Tutorial(
                                document.getId(),
                                document.getString("titulo"),
                                document.getString("desc"),
                                document.getBoolean("tipo_usuario"),
                                document.getString("cod_usuario")
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
        listTutoriales = (ListView) findViewById(R.id.listTutoriales);
        opciones = new ArrayList<String>();

        for(int i=0; i< Estaticos.tutoriales.size(); i++){
            opciones.add(Estaticos.tutoriales.get(i).getTitulo());
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, opciones);
        listTutoriales.setAdapter(adapter);
        listTutoriales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), DetalleTutorialActivity.class);
                intent.putExtra("indexTutorial", i);
                startActivity(intent);
            }
        });
    }
}