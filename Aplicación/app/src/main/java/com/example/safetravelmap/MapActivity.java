package com.example.safetravelmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.safetravelmap.entities.Cuenta_administrador;
import com.example.safetravelmap.entities.Cuenta_usuario;
import com.example.safetravelmap.entities.Desperfecto;
import com.example.safetravelmap.entities.Estaticos;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static android.content.ContentValues.TAG;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    FirebaseFirestore mFirestore;
    Button btnOpenNewActivity;
    Marker selectedMarker;

    Boolean graves;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFirestore = FirebaseFirestore.getInstance();

        btnOpenNewActivity = findViewById(R.id.btn_open_new_activity);

        Intent intent = getIntent();
        graves = intent.getBooleanExtra("graves", false);


    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        // Agrega esta línea de código en tu método onCreate() después de inicializar el botón
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                btnOpenNewActivity.setVisibility(View.VISIBLE);
            }
        });

        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("desperfectos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    Toast.makeText(getApplicationContext(), "No existen desperfectos registrados.", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    Toast.makeText(getApplicationContext(), "Sí existen desperfectos registrados.", Toast.LENGTH_SHORT).show();
                    boolean firstMarker = true;
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        if((graves == true && document.getLong("riesgo").intValue() == 1) || (graves == false && document.getLong("riesgo").intValue() == 0)){
                        Desperfecto desperfecto = new Desperfecto(
                                document.getLong("cod_usuario").intValue(),
                                document.getString("desc"),
                                document.getString("desperfecto"),
                                document.getString("imagen"),
                                document.getDouble("latitud"),
                                document.getDouble("longitud"),
                                document.getLong("riesgo").intValue(),
                                document.getBoolean("tipo_usuario")
                        );
                        Estaticos.desperfectos.add(desperfecto);

                        // Crear LatLng
                        LatLng desperfectoPosition = new LatLng(desperfecto.getLatitud(), desperfecto.getLongitud());

                        // Añadir marcador en el mapa
                        String usuario = "Sin usuario.";
                        if (desperfecto.isTipo_usuario() == true) {
                            for (int i = 0; i < Estaticos.cuenta_usuarios.size(); i++) {
                                Cuenta_usuario cuenta_usuario = Estaticos.cuenta_usuarios.get(i);
                                if (cuenta_usuario.getCod_usuario() == desperfecto.getCon_usuario()) {
                                    usuario = cuenta_usuario.getNombre() + " " + cuenta_usuario.getApellido();
                                }
                            }
                        }
                        if (desperfecto.isTipo_usuario() == false) {
                            for (int i = 0; i < Estaticos.cuenta_administradores.size(); i++) {
                                Cuenta_administrador cuenta_administrador = Estaticos.cuenta_administradores.get(i);
                                if (cuenta_administrador.getCod_usuario() == desperfecto.getCon_usuario()) {
                                    usuario = cuenta_administrador.getNombre() + " " + cuenta_administrador.getApellido();
                                }
                            }
                        }

                        Marker marker = googleMap.addMarker(new MarkerOptions().position(desperfectoPosition).title(usuario + " - " + desperfecto.getDesc()));
                        marker.setTag(desperfecto);

                        if (firstMarker) {
                            // Centrar el mapa en el primer marcador y hacer zoom
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(desperfectoPosition, 14));
                            firstMarker = false;
                        }
                      }
                    }

                    // Añadir un listener para cuando se presiona el marcador
                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            selectedMarker = marker;
                            // Mostrar la información del desperfecto
                            Desperfecto desperfecto = (Desperfecto) marker.getTag();
                            String usuario = "Sin usuario.";
                            if(desperfecto.isTipo_usuario() == true){
                                for(int i = 0; i< Estaticos.cuenta_usuarios.size(); i++){
                                    Cuenta_usuario cuenta_usuario = Estaticos.cuenta_usuarios.get(i);
                                    if(cuenta_usuario.getCod_usuario() == desperfecto.getCon_usuario()){
                                        usuario = cuenta_usuario.getNombre() + " " + cuenta_usuario.getApellido();
                                    }
                                }
                            }
                            if(desperfecto.isTipo_usuario() == false){
                                for(int i = 0; i< Estaticos.cuenta_administradores.size(); i++){
                                    Cuenta_administrador cuenta_administrador = Estaticos.cuenta_administradores.get(i);
                                    if(cuenta_administrador.getCod_usuario() == desperfecto.getCon_usuario()){
                                        usuario = cuenta_administrador.getNombre() + " " + cuenta_administrador.getApellido();
                                    }
                                }
                            }
                            Toast.makeText(getApplicationContext(), usuario + " - " + desperfecto.getDesc(), Toast.LENGTH_SHORT).show();
                            btnOpenNewActivity.setVisibility(View.GONE);
                            btnOpenNewActivity.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Aquí manejas el evento de clic en el botón y puedes abrir el nuevo Activity
                                    Desperfecto desperfecto = (Desperfecto) selectedMarker.getTag();
                                    Intent intent = new Intent(MapActivity.this, DesperfectoActivity.class);
                                    int index = 0;
                                    for(int i=0; i< Estaticos.desperfectos.size(); i++){
                                        Desperfecto desp = Estaticos.desperfectos.get(i);
                                        if(desp.getImagen().equals(desperfecto.getImagen())){
                                            index = i;
                                            break;
                                        }
                                    }
                                    intent.putExtra("index", index);
                                    startActivity(intent);

                                    // Esconde el botón nuevamente
                                    btnOpenNewActivity.setVisibility(View.GONE);
                                }
                            });
                            return false;
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "ERROR: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}