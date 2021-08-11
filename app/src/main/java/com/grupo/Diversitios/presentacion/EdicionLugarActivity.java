package com.grupo.Diversitios.presentacion;


import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.grupo.Diversitios.Aplicacion;
import com.grupo.Diversitios.Firebase.AdaptadorLugaresFirestore;
import com.grupo.Diversitios.Firebase.LugaresAsinc;
import com.grupo.Diversitios.R;
import com.grupo.Diversitios.casos_uso.CasosUsoLugar;
import com.grupo.Diversitios.datos.LugaresBD;
import com.grupo.Diversitios.datos.RepositorioLugares;
import com.grupo.Diversitios.modelo.GeoPunto;
import com.grupo.Diversitios.modelo.Lugar;
import com.grupo.Diversitios.modelo.TipoLugar;

import java.text.DateFormat;
import java.util.Date;

public class EdicionLugarActivity extends AppCompatActivity {

    //private RepositorioLugares lugares;
    private CasosUsoLugar usoLugar;
    private int pos;
    private String _id;
    private Lugar lugar;

    private EditText nombre;
    private Spinner tipo;
    private EditText direccion;
    private EditText telefono;
    private EditText url;
    private EditText comentario;
    private Toast msnToast;

    //sqlite
    private LugaresBD lugares;
    private AdaptadorLugaresBD adaptador;
    //FIREBASE
    private AdaptadorLugaresFirestore adaptadorLugaresFirestore;
    public LugaresAsinc lugaresAsinc;
    private CollectionReference instanciaEdicion = FirebaseFirestore.getInstance().collection("lugares");

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edicion_lugar);
        lugar = new Lugar();
        lugares = ((Aplicacion) getApplication()).lugares;
        adaptador = ((Aplicacion) getApplication()).adaptador;
        usoLugar = new CasosUsoLugar(this, lugaresAsinc,adaptadorLugaresFirestore);
        lugaresAsinc = ((Aplicacion) getApplication()).lugaresAsinc;
        actualizaVistas();
        Query query = FirebaseFirestore.getInstance()
                .collection("lugares")
                .limit(50);
        FirestoreRecyclerOptions<Lugar> opciones = new FirestoreRecyclerOptions
                .Builder<Lugar>().setQuery(query,Lugar.class).build();
        adaptadorLugaresFirestore = new AdaptadorLugaresFirestore(opciones,this);

        Bundle extras = getIntent().getExtras();
        pos = extras.getInt("pos", 0);
        _id= extras.getString("_id");
        String posEd = extras.getString("posEd");
        Log.d("TAG","pos edicion "+posEd );

        if (_id!=null) {
            setTitle("Nuevo lugar");

            //lugar = new Lugar();//lugares.elemento(_id);
        }
        else lugar = adaptadorLugaresFirestore.getItem(pos);//lugarPosicion(pos);//lugares.elemento(pos);

    }

    public void actualizaVistas() {
        nombre = findViewById(R.id.nombre);
        //nombre.setText(lugar.getNombre());

        direccion = findViewById(R.id.direccion);
        //direccion.setText(lugar.getDireccion());

        telefono = findViewById(R.id.telefono);
        //telefono.setText(Integer.toString(lugar.getTelefono()));

        url = findViewById(R.id.url);
        //url.setText(lugar.getUrl());

        comentario = findViewById(R.id.comentario);
        //comentario.setText(lugar.getComentario());

        tipo = findViewById(R.id.tipo);
        //tipo.setText(lugar.getTipo().getTexto());
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, TipoLugar.getNombres());
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipo.setAdapter(adaptador);
        //tipo.setSelection(lugar.getTipo().ordinal());
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edicion_lugar,menu);
        return true;
    }


    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accion_guardar:
                lugar.setNombre(nombre.getText().toString());
                lugar.setTipo(TipoLugar.values()[tipo.getSelectedItemPosition()]);
                lugar.setDireccion(direccion.getText().toString());
                lugar.setTelefono(Integer.parseInt(telefono.getText().toString()));
                lugar.setUrl(url.getText().toString());
                lugar.setComentario(comentario.getText().toString());
                GeoPunto posicion = ((Aplicacion) getApplication()).posicionActual;
                if (!posicion.equals(GeoPunto.SIN_POSICION)) {
                    lugar.setPosicion(posicion);
                }
                Log.d("TAG","posicion creada "+posicion);
                Log.d("TAG","nuevo lugar "+lugar.toString());
                instanciaEdicion.add(lugar).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                        msnToast = Toast.makeText(getApplicationContext(),"Lugar creado exitosamente en Firestore",Toast.LENGTH_LONG);
                        msnToast.setGravity(Gravity.CENTER,0,0);
                        msnToast.show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        msnToast = Toast.makeText(getApplicationContext(),"Error al crear el lugar en firestore "+e.getMessage(),Toast.LENGTH_LONG);
                        msnToast.setGravity(Gravity.CENTER,0,0);
                        msnToast.show();
                    }
                });

                msnToast = Toast.makeText(getApplicationContext(),"Cambios guardados exitosamente",Toast.LENGTH_LONG);
                msnToast.setGravity(Gravity.CENTER,0,0);
                msnToast.show();
                /*if (_id==null) _id = adaptadorLugaresFirestore.getKey(pos);
                Log.d("TAG","lugar creado " +lugar.toString());
                usoLugar.guardar(_id, lugar);*/
                finish();
                return true;
            case R.id.accion_cancelar:
                if (_id!=null) {
                    Log.d("TAG","EDL cancelar id"+_id);
                    instanciaEdicion.document(_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Canceló operación",Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override public void onFailure(Exception e) {
                            Toast.makeText(getApplicationContext(), "Error cancelar "+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
                msnToast = Toast.makeText(getApplicationContext(),"Canceló la edición no hay cambios",Toast.LENGTH_LONG);
                msnToast.setGravity(Gravity.CENTER,0,0);
                msnToast.show();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}