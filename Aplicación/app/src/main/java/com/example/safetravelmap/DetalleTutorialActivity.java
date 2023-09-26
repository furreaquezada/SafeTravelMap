package com.example.safetravelmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.safetravelmap.entities.Estaticos;
import com.example.safetravelmap.entities.GaleriaTutorial;
import com.example.safetravelmap.entities.Tutorial;
import com.example.safetravelmap.entities.Voto;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class DetalleTutorialActivity extends AppCompatActivity {
    public int indexTutorial;
    public Tutorial tutorial;

    Button btnEdit;
    Button btnEliminar;
    Button btnDetalle;
    Button btnAgregarDetalle;
    EditText tituloEdit;
    EditText desc2Edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_tutorial);

        btnEdit = (Button) findViewById(R.id.btnEdit);
        btnEliminar = (Button) findViewById(R.id.btnEliminar);

        btnDetalle = (Button) findViewById(R.id.btnDetalle);
        btnAgregarDetalle = (Button) findViewById(R.id.btnAgregarDetalle);

        if(Estaticos.tipo_usuario){
            btnEdit.setEnabled(false);
            btnEliminar.setEnabled(false);
            btnAgregarDetalle.setEnabled(false);
        }

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnEdit.setEnabled(false);
                btnEliminar.setEnabled(false);
                editarTutorialPorID();
            }
        });

        btnDetalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ListadoDetallesActivity.class);
                intent.putExtra("indexTutorial", indexTutorial);
                startActivity(intent);
            }
        });

        btnAgregarDetalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AgregarDetalleActivity.class);
                intent.putExtra("indexTutorial", indexTutorial);
                startActivity(intent);
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnEdit.setEnabled(false);
                btnEliminar.setEnabled(false);
                eliminarTutorialPorID();
            }
        });




        tituloEdit = (EditText) findViewById(R.id.tituloEdit);
        desc2Edit = (EditText) findViewById(R.id.desc2Edit);

        Intent intent = getIntent();
        indexTutorial = intent.getIntExtra("indexTutorial", -1);

        cargarTutorial();
        cargarDetalles();
    }

    public void cargarTutorial(){
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("tutoriales")
                .document(Estaticos.tutoriales.get(indexTutorial).getId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) {
                            Toast.makeText(getApplicationContext(), "No existe el tutorial en base de datos.", Toast.LENGTH_SHORT).show();
                            return;
                        }else {
                            tutorial = new Tutorial(
                                    documentSnapshot.getId(),
                                    documentSnapshot.getString("titulo"),
                                    documentSnapshot.getString("desc"),
                                    documentSnapshot.getBoolean("tipo_usuario"),
                                    documentSnapshot.getLong("cod_usuario").intValue()
                            );

                            tituloEdit.setText(tutorial.getTitulo());
                            desc2Edit.setText(tutorial.getDesc());

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "ERROR: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void eliminarTutorialPorID() {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("galeriaTutoriales").whereEqualTo("idTutorial", Estaticos.tutoriales.get(indexTutorial).getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Estaticos.galeriaTutoriales.clear();
                if(queryDocumentSnapshots.isEmpty()){
                    Toast.makeText(getApplicationContext(), "No existen detalles en base de datos, por lo que podemos eliminar el tutorial.", Toast.LENGTH_SHORT).show();
                }else{

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Estaticos.galeriaTutoriales.add(new GaleriaTutorial(
                                document.getId(),
                                document.getString("desc"),
                                document.getString("idTutorial"),
                                document.getString("titulo")
                        ));
                    }
                }

                // Eliminamos
                if(Estaticos.galeriaTutoriales.size() > 0){
                    Toast.makeText(getApplicationContext(), "No puede eliminar el tutorial si posee explicaciones asociadas.", Toast.LENGTH_SHORT).show();
                    btnEdit.setEnabled(true);
                    btnEliminar.setEnabled(false);
                    return;
                }
                FirebaseFirestore mFirestore2 = FirebaseFirestore.getInstance();
                mFirestore2.collection("tutoriales")
                        .document(Estaticos.tutoriales.get(indexTutorial).getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Tutorial eliminado con éxito.", Toast.LENGTH_SHORT).show();

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
                // Eliminamos
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "ERROR: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });





    }

    public void editarTutorialPorID() {
        tutorial.setTitulo(tituloEdit.getText().toString());
        tutorial.setDesc(desc2Edit.getText().toString());

        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        // Crear un mapa con los datos que deseas actualizar
        Map<String, Object> tutorialMap = new HashMap<>();
        tutorialMap.put("titulo", tutorial.getTitulo());
        tutorialMap.put("desc", tutorial.getDesc());
        tutorialMap.put("tipo_usuario", tutorial.isTipo_usuario());
        tutorialMap.put("cod_usuario", tutorial.getCod_usuario());

        mFirestore.collection("tutoriales")
                .document(Estaticos.tutoriales.get(indexTutorial).getId())
                .set(tutorialMap)  // Usar set() si deseas sobrescribir todo el documento
                //.update(tutorialMap)  // Usar update() si solo deseas actualizar campos específicos
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Tutorial actualizado con éxito.", Toast.LENGTH_SHORT).show();
                        btnEdit.setEnabled(true);
                        btnEliminar.setEnabled(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "ERROR al actualizar: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
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