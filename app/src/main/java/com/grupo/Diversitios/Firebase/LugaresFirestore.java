package com.grupo.Diversitios.Firebase;

import android.util.Log;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.grupo.Diversitios.modelo.Lugar;

public class LugaresFirestore  implements LugaresAsinc {
    private CollectionReference lugares;

    public LugaresFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        lugares = db.collection("lugares");
    }

    @Override public void elemento(String id, EscuchadorElemento escuchador) {
        lugares.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Lugar lugar = task.getResult().toObject(Lugar.class);
                    escuchador.onRespuesta(lugar);
                } else {
                    Log.d("LugaresFi","Error leer lugares firestore ", task.getException());
                    escuchador.onRespuesta(null);
                }
            }
        });
    }

    @Override public void añade(Lugar lugar) {
        lugares.document().set(lugar);
    }

    @Override public String nuevo() {
        return lugares.document().getId();
    }

    @Override public void borrar(String id) {
        lugares.document(id).delete();
    }

    @Override public void actualiza(String id, Lugar lugar) {
        lugares.document(id).set(lugar);
    }

    @Override public void tamaño(EscuchadorTamaño escuchador) {
        lugares.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    escuchador.onRespuesta(task.getResult().size());
                }else{
                    Log.d("LugaresFi","Error tamaño firestore lugares ",task.getException());
                    escuchador.onRespuesta(-1);
                }
            }
        });
    }
}
