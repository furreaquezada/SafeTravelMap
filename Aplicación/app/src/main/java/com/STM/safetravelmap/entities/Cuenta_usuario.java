package com.STM.safetravelmap.entities;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Cuenta_usuario extends Cuenta{
    public int id_cuenta_usuario;
    public FirebaseFirestore mFirestore;
    public FirebaseAuth mAuth;

    public Cuenta_usuario(String cod_usuario, String nombre, String apellido, String rut, int edad, String pass, String nombre_usuario, String correo, int id_cuenta_usuario, int puntaje) {
        super(cod_usuario, nombre, apellido, rut, edad, pass, nombre_usuario, correo, puntaje);
        this.id_cuenta_usuario = id_cuenta_usuario;
    }

    public Cuenta_usuario() {
    }

    public int getId_cuenta_usuario() {
        return id_cuenta_usuario;
    }

    public void setId_cuenta_usuario(int id_cuenta_usuario) {
        this.id_cuenta_usuario = id_cuenta_usuario;
    }

    public boolean consultar_usuario(String correo, boolean test){
        if(!test) {
            boolean[] resultado = {true};
            mFirestore = FirebaseFirestore.getInstance();
            mFirestore.collection("usuarios").whereEqualTo("correo", correo.toString().trim()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.isEmpty()) {
                        resultado[0] = false;
                    } else {
                        resultado[0] = true;

                    }
                }
            });

            return resultado[0];
        }else{
            return true;
        }
    }


}
