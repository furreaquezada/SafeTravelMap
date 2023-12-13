package com.STM.safetravelmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.safetravelmap.R;
import com.STM.safetravelmap.entities.Desperfecto;
import com.STM.safetravelmap.entities.Estaticos;
import com.STM.safetravelmap.entities.Reincidencia;
import com.STM.safetravelmap.entities.Voto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
    Button buttonReportar;

    Button btnDelete;

    Button buttonSolucionado;

    ArrayList<Voto> votos = new ArrayList<Voto>();

    ArrayList<Reincidencia> reincidencias = new ArrayList<Reincidencia>();

    int puntaje = 10;

    private ListView reincidenciasList;

    int total_solucionados = 3;
    int total_tres_reincidencias = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desperfecto);

        imageView = findViewById(R.id.imageView);
        tv_desperfecto = (TextView) findViewById(R.id.tv_desperfecto);
        textViewInfo = (TextView) findViewById(R.id.textViewInfo);
        buttonFalso = (Button) findViewById(R.id.buttonFalso);
        buttonReportar = (Button) findViewById(R.id.buttonReportar);
        buttonSolucionado = (Button) findViewById(R.id.buttonSolucionado);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí va el código que quieres ejecutar cuando el botón es presionado
                String cod_usuario;
                if(Estaticos.tipo_usuario) {
                    cod_usuario = Estaticos.cuenta_usuario.getCod_usuario();
                } else {
                    cod_usuario = Estaticos.cuenta_administrador.getCod_usuario();
                }
                if(desperfecto.getCon_usuario().equals(cod_usuario)){
                    eliminarDesperfectoConImagen(desperfecto.getLatitud());
                }else{
                    Toast.makeText(getApplicationContext(), "No puede eliminar un desperfecto que no sea suyo.", Toast.LENGTH_SHORT).show();
                }

            }
        });


        buttonFalso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí va el código que quieres ejecutar cuando el botón es presionado
                votar(desperfecto.getLatitud());
            }
        });

        buttonSolucionado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí va el código que quieres ejecutar cuando el botón es presionado
                solucionar(String.valueOf(desperfecto.getLatitud()));
            }
        });

        buttonReportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AgregarReincidenciaActivity.class);
                intent.putExtra("latitud", desperfecto.getLatitud());
                startActivity(intent);
            }
        });


        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            int index = intent.getIntExtra("index", -1);
            if (index != -1) {
                desperfecto = Estaticos.desperfectos.get(index);
                this.actualizarDesperfectosPorLatitud(String.valueOf(desperfecto.getLatitud()));
            }
        }
        if (desperfecto.getImagen() != null) {
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


        if (desperfecto.isSolucionado()) {
            buttonFalso.setEnabled(false);
            buttonReportar.setEnabled(false);
            buttonSolucionado.setEnabled(false);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        requestLocationPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarReincidencias();
    }

    public void eliminarDesperfectoConImagen(double latitud) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();

        // Crear la consulta filtrando por latitud
        Query query = mFirestore.collection("desperfectos").whereEqualTo("latitud", latitud);

        // Obtener documentos que coincidan con la consulta
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        // Obtener el nombre de la imagen del documento
                        String imageName = document.getString("imagen");

                        // Eliminar el documento de Firestore
                        document.getReference().delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Después de eliminar el documento, eliminar la imagen de Firebase Storage
                                        StorageReference imageRef = mFirebaseStorage.getReference().child(imageName);
                                        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(), "Imagen eliminada con éxito.", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), SistemaActivity.class);
                                                startActivity(intent);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Error al eliminar la imagen: " + e.toString(), Toast.LENGTH_SHORT).show();


                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Error al eliminar el desperfecto: " + e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else {
                    Log.d("TAG", "Error obteniendo documentos: ", task.getException());
                }
            }
        });
    }


    public void cargarVotos() {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("votos")
                .whereEqualTo("latitud", desperfecto.getLatitud())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "No existen votos en base de datos.", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            boolean votoMio = false;
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                                votos.add(new Voto(
                                        document.getString("cod_usuario"),
                                        document.getDouble("latitud"),
                                        document.getBoolean("tipo_usuario")
                                ));

                                if (Estaticos.tipo_usuario) {
                                    if (Estaticos.cuenta_usuario.getCod_usuario().equals(votos.get(votos.size() - 1).getCod_usuario()) && votos.get(votos.size() - 1).isTipo_usuario() == true) {
                                        votoMio = true;
                                    }
                                } else {
                                    if (Estaticos.cuenta_administrador.getCod_usuario().equals(votos.get(votos.size() - 1).getCod_usuario()) && votos.get(votos.size() - 1).isTipo_usuario() == false) {
                                        votoMio = true;
                                    }
                                }
                            }

                            if (votos.size() >= 2) {
                                buttonFalso.setEnabled(false); // Esta variale se activa cuando ya se descontaron 20 puntos, osea dos votos
                            }

                            if (votoMio) {
                                textViewInfo.setText(desperfecto.getDesperfecto() + "\n" + (desperfecto.getPuntos() - votos.size() * puntaje) + " Puntos.\nUsted ya votó.");
                                buttonFalso.setEnabled(false);
                            } else {
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

    public void cargarReincidencias() {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("reincidencias")
                .whereEqualTo("latitud", desperfecto.getLatitud())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "No existen reincidencias en base de datos.", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            ArrayList<String> opciones = new ArrayList<String>();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                reincidencias.add(new Reincidencia(
                                        document.getDouble("latitud"),
                                        document.getString("imagen"),
                                        document.getString("desc"),
                                        document.getString("cod_usuario"),
                                        document.getBoolean("tipo_usuario")
                                ));
                                opciones.add(document.getString("desc"));
                            }

                            if(opciones.size() == total_tres_reincidencias){
                                FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
                                mFirestore = FirebaseFirestore.getInstance();
                                actualizarDesperfectosTres_reincidencias(mFirestore, desperfecto.getLatitud());
                            }

                            Toast.makeText(DesperfectoActivity.this, "Hay " + reincidencias.size() + " reinicidencias.", Toast.LENGTH_SHORT).show();


                            reincidenciasList = (ListView) findViewById(R.id.reincidencias);


                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, opciones);
                            reincidenciasList.setAdapter(adapter);
                            reincidenciasList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent(getApplicationContext(), DetalleReincidenciaActivity.class);
                                    intent.putExtra("imagen", reincidencias.get(i).getImagen());
                                    intent.putExtra("desc", reincidencias.get(i).getDesc());
                                    intent.putExtra("tipo_usuario", reincidencias.get(i).isTipo_usuario());
                                    intent.putExtra("cod_usuario", reincidencias.get(i).getCod_usuario());
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "ERROR: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void votar(double latitud) {
        buttonFalso.setEnabled(false);
        Toast.makeText(getApplicationContext(), "Votando como NO encontrado.", Toast.LENGTH_SHORT).show();
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        // Agregar un nuevo documento a la colección "votos"
        // Crear un nuevo objeto con los atributos que quieres almacenar
        Map<String, Object> voto = new HashMap<>();

        if (Estaticos.tipo_usuario == true) {
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

    public void solucionar(String latitud) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        String cod_usuario;
        if(Estaticos.tipo_usuario) {
            cod_usuario = Estaticos.cuenta_usuario.getCod_usuario();
        } else {
            cod_usuario = Estaticos.cuenta_administrador.getCod_usuario();
        }

        // Consultar primero si ya existe un documento con los mismos datos
        mFirestore.collection("solucionados")
                .whereEqualTo("cod_usuario", cod_usuario)
                .whereEqualTo("latitud", latitud)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                // Ya existe un documento con los mismos datos
                                Toast.makeText(getApplicationContext(), "Usted ya marcó como solucionado este desperfecto.", Toast.LENGTH_SHORT).show();
                                actualizarDesperfectosPorLatitud(latitud);
                            } else {
                                // No existe un documento, agregar uno nuevo
                                agregarNuevoDocumento(mFirestore, cod_usuario, latitud);
                            }
                        } else {
                            Log.w("Firestore", "Error al consultar documentos", task.getException());
                        }
                    }
                });
    }

    private void agregarNuevoDocumento(FirebaseFirestore mFirestore, String cod_usuario, String latitud) {
        Map<String, Object> nuevoDocumento = new HashMap<>();
        nuevoDocumento.put("cod_usuario", cod_usuario);
        nuevoDocumento.put("latitud", latitud);

        mFirestore.collection("solucionados")
                .add(nuevoDocumento)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Firestore", "Solución agregada con ID: " + documentReference.getId());
                        Toast.makeText(getApplicationContext(), "Solución agregada con ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                        actualizarDesperfectosPorLatitud(latitud);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error al agregar documento", e);
                    }
                });
    }


    public void actualizarDesperfectosPorLatitud(String latitud) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        // Contar documentos en 'soluciones' que tienen la misma latitud
        mFirestore.collection("solucionados")
                .whereEqualTo("latitud", latitud)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int contador = 0;
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            contador = querySnapshot.size();
                        }
                        Toast.makeText(getApplicationContext(), "Hay " + contador + " soluciones marcadas.", Toast.LENGTH_SHORT).show();

                        // Si hay 3 o más soluciones, actualizar los documentos en 'desperfectos'
                        if (contador >= total_solucionados) {
                            actualizarDesperfectos(mFirestore, latitud);
                        }
                    } else {
                        Log.w("Firestore", "Error al consultar documentos", task.getException());
                    }
                });
    }

    private void actualizarDesperfectos(FirebaseFirestore mFirestore, String latitud) {
        // Actualizar todos los documentos en 'desperfectos' con la misma latitud
        mFirestore.collection("desperfectos")
                .whereEqualTo("latitud", Double.parseDouble(latitud))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                mFirestore.collection("desperfectos").document(document.getId())
                                        .update("solucionado", true)
                                        .addOnSuccessListener(aVoid -> Toast.makeText(getApplicationContext(), "Desperfecto actualizado correctamente.", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error al actualizar desperfecto", Toast.LENGTH_SHORT).show());
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Error al obtener documentos de 'desperfectos'" + task.getException(), Toast.LENGTH_SHORT).show();
                        Log.w("Firestore", "Error al obtener documentos de 'desperfectos'", task.getException());
                    }
                });
    }

    private void actualizarDesperfectosTres_reincidencias(FirebaseFirestore mFirestore, Double latitud) {
        // Actualizar todos los documentos en 'desperfectos' con la misma latitud
        mFirestore.collection("desperfectos")
                .whereEqualTo("latitud", latitud)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                mFirestore.collection("desperfectos").document(document.getId())
                                        .update("tres_reincidencias", true)
                                        .addOnSuccessListener(aVoid -> Toast.makeText(getApplicationContext(), "Desperfecto actualizado correctamente.", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error al actualizar desperfecto", Toast.LENGTH_SHORT).show());
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Error al obtener documentos de 'desperfectos'" + task.getException(), Toast.LENGTH_SHORT).show();
                        Log.w("Firestore", "Error al obtener documentos de 'desperfectos'", task.getException());
                    }
                });
    }

    private static final int REQUEST_LOCATION_PERMISSION_CODE = 2;
    // Coordenadas
    LocationManager locationManager;
    String locationProvider = LocationManager.GPS_PROVIDER;
    double latitud, longitud; // Añadir para almacenar las coordenadas

    // Ubicación
    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION_CODE);
        } else {
            fetchLocation();
        }
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = locationManager.getLastKnownLocation(locationProvider);

        if (location != null) {
            latitud = location.getLatitude();
            longitud = location.getLongitude();
            Toast.makeText(DesperfectoActivity.this, "La lat: " + latitud, Toast.LENGTH_SHORT).show();
            Toast.makeText(DesperfectoActivity.this, "La lon: " + longitud, Toast.LENGTH_SHORT).show();
            checkCoordinates();

        } else {
            Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkCoordinates() {
        if (isWithinRadius(latitud, longitud, desperfecto.getLatitud(), desperfecto.getLongitud(), 25)) {
            buttonFalso.setEnabled(false);
            buttonReportar.setEnabled(false);
            btnDelete.setEnabled(false);
            buttonSolucionado.setEnabled(false);
        }
    }

    private boolean isWithinRadius(double currentLat, double currentLon, double targetLat, double targetLon, float radius) {
        Location currentLocation = new Location("currentLocation");
        currentLocation.setLatitude(currentLat);
        currentLocation.setLongitude(currentLon);

        Location targetLocation = new Location("targetLocation");
        targetLocation.setLatitude(targetLat);
        targetLocation.setLongitude(targetLon);

        float distanceInMeters = currentLocation.distanceTo(targetLocation);
        return distanceInMeters <= radius;
    }






}
