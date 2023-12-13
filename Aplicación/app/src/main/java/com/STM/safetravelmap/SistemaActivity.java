package com.STM.safetravelmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.safetravelmap.R;
import com.STM.safetravelmap.entities.Desperfecto;
import com.STM.safetravelmap.entities.Estaticos;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.google.android.gms.tasks.OnFailureListener;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import android.Manifest;

import static android.content.ContentValues.TAG;


public class SistemaActivity extends AppCompatActivity {

    public Button cargarImagen;
    public Button btnMapa;

    public Button btnMapa2;

    public Button btnTuto;

    public Button btnComprobante;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PERMISSIONS = 1;
    String currentPhotoPath, imageFileName;

    TextView textView;
    EditText desc;
    Switch switch1;

    Spinner categorias;

    boolean cargando = false;
    private static final int REQUEST_LOCATION_PERMISSION_CODE = 2;

    ArrayList<Desperfecto> desperfectos = new ArrayList<Desperfecto>();

    boolean hayDesperfecto =false;




    // Coordenadas
    LocationManager locationManager;
    String locationProvider = LocationManager.GPS_PROVIDER;
    double latitud, longitud; // Añadir para almacenar las coordenadas






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sistema);


        cargarCombo();


        cargarImagen = (Button) findViewById(R.id.cargarImagen);
        btnMapa = (Button) findViewById(R.id.btnMapa);
        btnMapa2 = (Button) findViewById(R.id.btnMapa2);
        btnTuto = (Button) findViewById(R.id.btnTuto);
        btnComprobante = (Button) findViewById(R.id.btnComprobante);

        textView = (TextView) findViewById(R.id.textView);
        desc = (EditText) findViewById(R.id.desc);
        switch1 = findViewById(R.id.switch1);

        if(Estaticos.tipo_usuario == true){
            textView.setText("Bienvenido: " + Estaticos.cuenta_usuario.getNombre_usuario());
        }else{
            textView.setText("Bienvenido administrador: " + Estaticos.cuenta_administrador.getNombre_usuario());
        }

        cargarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Solicita los permisos
                requestPermissions();
            }
        });

        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra("graves", false);
                startActivity(intent);
            }
        });

        btnComprobante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SubirComprobanteDePagoActivity.class);
                startActivity(intent);
            }
        });

        btnMapa2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra("graves", true);
                startActivity(intent);
            }
        });

        btnTuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NuevoTutorialActivity.class);
                startActivity(intent);
            }
        });

        locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
        requestLocationPermissions();

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

    private void checkCoordinates() {
        for (int i=0; i< desperfectos.size(); i++) {
            if (isWithinRadius(latitud, longitud, desperfectos.get(i).getLatitud(), desperfectos.get(i).getLongitud(), 25)) {
                hayDesperfecto = true;
            }
        }

        if(hayDesperfecto){
            Toast.makeText(this, "Ya existen desperfectos, dentro de un radio de 25m!!! No puede agregar otro desde esta ubiación.", Toast.LENGTH_SHORT).show();
            habilitarYaHayDesperfecto();
        }else{
            Toast.makeText(this, "NO existen desperfectos, dentro de un radio de 25m!!!", Toast.LENGTH_SHORT).show();
        }
    }



    private void requestPermissions() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        if (!hasPermissions(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
        } else {
            tomarFoto();
        }
    }

    private boolean hasPermissions(String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                if (grantResults.length > 1 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // Los permisos fueron concedidos
                    tomarFoto();
                } else {
                    // Los permisos fueron denegados
                    Toast.makeText(this, "Permiso de cámara y almacenamiento requerido", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_LOCATION_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                } else {
                    Toast.makeText(this, "Se necesita el permiso para acceder a la ubicación", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void tomarFoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {
                Toast.makeText(this, "ex: " + ex.toString(),Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                currentPhotoPath = photoFile.getAbsolutePath();

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Al terminar el proceso de captura de imagen
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            subirImagen();
        }
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void subirImagen(){

        habilitar(false);


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagenRef = storageRef.child(imageFileName + ".jpg");

        Uri file = Uri.fromFile(new File(currentPhotoPath));

        Toast.makeText(SistemaActivity.this, "Cargando, espere...", Toast.LENGTH_SHORT).show();
        UploadTask uploadTask = imagenRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(SistemaActivity.this, "EL ERROR: " + exception.getCause(), Toast.LENGTH_SHORT).show();
                System.out.println("EL ERROR: " + exception.toString());


            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SistemaActivity.this, "Desperfecto cargado con éxito!!!", Toast.LENGTH_SHORT).show();
                almacenar();
            }
        });
    }

    public void almacenar(){
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        // Crear un nuevo objeto con los atributos que quieres almacenar
        Map<String, Object> desperfecto = new HashMap<>();

        if(Estaticos.tipo_usuario == true) {
            desperfecto.put("cod_usuario", Estaticos.cuenta_usuario.getCod_usuario()); // Aquí pon el número que corresponda
        } else {
            desperfecto.put("cod_usuario", Estaticos.cuenta_administrador.getCod_usuario()); // Aquí pon el número que corresponda
        }

        desperfecto.put("desc", desc.getText().toString()); // Aquí pon la descripción
        desperfecto.put("imagen", imageFileName + ".jpg"); // Aquí pon la URL de la imagen
        if(switch1.isChecked()){
            desperfecto.put("riesgo", 1);
        }else{
            desperfecto.put("riesgo", 0);
        }
        desperfecto.put("tipo_usuario", Estaticos.tipo_usuario); // Aquí pon el tipo de usuario

        String selectedValue = categorias.getSelectedItem().toString();
        desperfecto.put("desperfecto", selectedValue);
        desperfecto.put("latitud", latitud);
        desperfecto.put("longitud", longitud);
        desperfecto.put("puntos", 100);
        desperfecto.put("solucionado", false);
        desperfecto.put("tres_reincidencias", false);



        // Agregar un nuevo documento a la colección "desperfectos"
        mFirestore.collection("desperfectos")
                .add(desperfecto)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Documento añadido con éxito
                        Log.d(TAG, "Documento añadido con ID: " + documentReference.getId());
                        Toast.makeText(SistemaActivity.this, "Documento añadido con ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                        habilitar(true);
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

    public void habilitar(boolean estado){
        cargarImagen.setEnabled(estado);
        btnMapa.setEnabled(estado);
        btnMapa2.setEnabled(estado);
        textView.setEnabled(estado);
        desc.setEnabled(estado);
        switch1.setEnabled(estado);
        categorias.setEnabled(estado);
    }

    public void habilitarYaHayDesperfecto(){
        cargarImagen.setEnabled(false);
        btnMapa.setEnabled(true);
        btnMapa2.setEnabled(true);
        textView.setEnabled(false);
        desc.setEnabled(false);
        switch1.setEnabled(false);
        categorias.setEnabled(false);
    }

    public void cargarCombo(){
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("categorias").orderBy("creacion").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    Toast.makeText(getApplicationContext(), "No existen categorías en base de datos.", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    ArrayList<String> list = new ArrayList<String>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        list.add(document.getString("nombre"));
                    }
                    completarCombo(list);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "ERROR: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void cargarDesperfectos(){
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("desperfectos")
                .whereEqualTo("solucionado", false)
                .whereEqualTo("tres_reincidencias", false)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    Toast.makeText(getApplicationContext(), "No existen desperfectos en base de datos.", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        desperfectos.add(new Desperfecto(
                                document.getString("cod_usuario"),
                                document.getString("desc"),
                                document.getString("desperfecto"),
                                document.getString("imagen"),
                                document.getDouble("latitud"),
                                document.getDouble("longitud"),
                                document.getLong("riesgo").intValue(),
                                document.getBoolean("tipo_usuario"),
                                document.getLong("puntos").intValue(),
                                false,
                                false
                        ));
                    }
                }

                checkCoordinates();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "ERROR: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    public void completarCombo(ArrayList<String> list){
        categorias = (Spinner) findViewById(R.id.categorias);
        // Crear un ArrayAdapter usando el array y un layout predeterminado de Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);

        // Especificar el layout a usar cuando se muestra la lista de opciones
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Aplicar el adapter al spinner
        categorias.setAdapter(adapter);
    }


    // Ubicación
    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION_CODE);
        } else {
            fetchLocation();
        }
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = locationManager.getLastKnownLocation(locationProvider);

        if (location != null) {
            latitud = location.getLatitude();
            longitud = location.getLongitude();
            Toast.makeText(SistemaActivity.this, "La lat: " + latitud, Toast.LENGTH_SHORT).show();
            Toast.makeText(SistemaActivity.this, "La lon: " + longitud, Toast.LENGTH_SHORT).show();
            cargarDesperfectos();

        } else {
            Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
        }
    }
}