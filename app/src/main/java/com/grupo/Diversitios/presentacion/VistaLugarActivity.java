package com.grupo.Diversitios.presentacion;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.grupo.Diversitios.Aplicacion;
import com.grupo.Diversitios.Firebase.AdaptadorLugaresFirestore;
import com.grupo.Diversitios.Firebase.LugaresAsinc;
import com.grupo.Diversitios.R;
import com.grupo.Diversitios.casos_uso.CasosUsoLugar;
import com.grupo.Diversitios.datos.LugaresBD;
import com.grupo.Diversitios.modelo.GeoPunto;
import com.grupo.Diversitios.modelo.Lugar;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VistaLugarActivity extends AppCompatActivity {
    //private RepositorioLugares lugares;
    private CasosUsoLugar usoLugar;
    private int pos;
    private Lugar lugar;
    final static int RESULTADO_EDITAR = 1;
    private TextView nombre, tipo,direccion,telefono,url,comentario,fecha, hora;
    private ImageView logo_tipo,foto,galeria,camara, eliminar;
    private RatingBar valoracion;
    final static int RESULTADO_GALERIA = 2;
    final static int RESULTADO_FOTO = 3;

    private Uri uriUltimaFoto;

    private LugaresBD lugares;
    private AdaptadorLugaresBD adaptador;

    private AdaptadorLugaresFirestore adaptadorLugaresFirestore;
    private String idLugar;
    public int _id;
    private CollectionReference instanciaVista = FirebaseFirestore.getInstance().collection("lugares");
    private LugaresAsinc lugaresAsinc;
    private int lugarRecurso;
    @Override protected void onCreate(Bundle savedInstanceState)
    {   super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_lugar);
        lugares = ((Aplicacion) getApplication()).lugares;
        adaptador= ((Aplicacion)getApplication()).adaptador;
        usoLugar = new CasosUsoLugar(this, lugaresAsinc,adaptadorLugaresFirestore);
        lugaresAsinc = ((Aplicacion) getApplication()).lugaresAsinc;
        Query query = FirebaseFirestore.getInstance()
                .collection("lugares")
                .limit(50);
        FirestoreRecyclerOptions<Lugar> opciones = new FirestoreRecyclerOptions
                .Builder<Lugar>().setQuery(query,Lugar.class).build();
        adaptadorLugaresFirestore = new AdaptadorLugaresFirestore(opciones,this);

        Bundle extras = getIntent().getExtras();
        idLugar = extras.getString("lugar_fire");
        lugarRecurso = extras.getInt("icono_recurso");
        Log.d("TAG","vista lugar elegido-> "+idLugar + " coleccion "+ instanciaVista.document(idLugar)+
                " lugar recurso "+lugarRecurso);
        pos = extras.getInt("pos");
        Log.d("tag","oncreate vla "+pos + "id "+_id);
        datosLugarFirestore();
        adaptadorLugaresFirestore.startListening();


        /*
        if(extras !=null);
        else pos=0;
        idLugar = adaptadorLugaresFirestore.getKey(pos);

        lugar = adaptadorLugaresFirestore.getItem(pos);*///lugares.elemento(pos);
        foto = findViewById(R.id.foto);
        galeria = findViewById(R.id.galeria);
        camara = findViewById(R.id.camara);
        eliminar = findViewById(R.id.eliminar);
        actualizaVistas();
        llamar();
        verWeb();
        //abrirGaleria();
        //tomarFotoCamara();
        //eliminarFoto();
    }

    private void datosLugarFirestore() {
        //Lugar item_lugar = adaptadorLugaresFirestore.getItem(pos);
        instanciaVista.document(idLugar).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override public void onComplete(Task<DocumentSnapshot> task) {
                String name= task.getResult().getString("nombre");
                nombre.setText(name);
                String comentario_ = task.getResult().getString("comentario");
                comentario.setText(comentario_);
                String direccion_ = task.getResult().getString("direccion");
                direccion.setText(direccion_);
                Double phone = task.getResult().getDouble("telefono");
                telefono.setText(String.valueOf(phone));
                String type = task.getResult().getString("tipo");
                tipo.setText(type);
                String la_url = task.getResult().getString("url");
                url.setText(la_url);
                String photo = task.getResult().getString("foto");
                if(photo==null||photo=="")foto.setImageResource(lugarRecurso);
                else {
                    Picasso.get().load(photo).error(R.drawable.icono_app_background).placeholder(R.drawable.otros).into(foto);
                    Log.d("TAG","imagen "+Uri.parse(photo));
                    //    foto.setImageURI(Uri.parse(photo));
                }//ImageResource(photo);
                logo_tipo.setImageResource(lugarRecurso);//item_lugar.getTipo().getRecurso());
                Double valor = task.getResult().getDouble("valoracion");
                valoracion.setRating(valor.floatValue());
                Long date = task.getResult().getLong("fecha");
                fecha.setText(DateFormat.getDateInstance().format(new Date(date)));
                hora.setText(DateFormat.getTimeInstance().format(new Date(date)));

            }
        });
    }

    @Override protected void onStart() {
        super.onStart();
        adaptadorLugaresFirestore.startListening();
    }

    @Override protected void onStop() {
        super.onStop();
        adaptadorLugaresFirestore.stopListening();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        adaptadorLugaresFirestore.stopListening();
    }

    public void eliminarFoto(){
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Foto eliminada",Toast.LENGTH_LONG).show();
                usoLugar.ponerFoto(pos,"",foto);
            }
        });

    }

    public void tomarFotoCamara(){
        camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uriUltimaFoto =  usoLugar.tomarFoto(RESULTADO_FOTO);
            }
        });
    }

    public void abrirGaleria(){
        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usoLugar.ponerDeGaleria(RESULTADO_GALERIA);
            }
        });

    }

    public void llamar(){
        telefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //usoLugar.llamarTelefono(lugar);
                //actividad.startActivity(new Intent(Intent.ACTION_DIAL,
                //                Uri.parse("tel:" + lugar.getTelefono())));
                instanciaVista.document(idLugar).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()){
                                Log.d("TAG","ver datos telefono"+documentSnapshot.getData() + " id "+documentSnapshot.getId());
                                Double telefono = task.getResult().getDouble("telefono");
                                Integer phone = telefono.intValue();
                                Log.d("VER","telefono "+Uri.parse("tel: " + phone));
                                Intent ver = new Intent(Intent.ACTION_VIEW,Uri.parse("tel: " + phone));
                                startActivity(ver);
                            }else {
                                Log.d("TAG", "No such document");
                            }
                        } else {
                            Log.d("TAG", "get failed with ", task.getException());
                        }
                    }
                });
            }
        });

    }
    public void verWeb(){
        url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //usoLugar.verPgWeb(lugar);
                instanciaVista.document(idLugar).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()){
                                Log.d("TAG","ver datos compartir"+documentSnapshot.getData() + " id "+documentSnapshot.getId());
                                String url= task.getResult().getString("url");
                                Uri web=Uri.parse(url);
                                if(!url.startsWith("http://")&&!url.startsWith("https://")){
                                    web =Uri.parse("http://"+url);
                                    Log.d("VER","url 1 "+web);
                                }
                                Log.d("VER","url "+web);
                                Intent ver = new Intent(Intent.ACTION_VIEW,web);//Uri.parse(Uri.encode(url)));
                                startActivity(ver);
                            }else {
                                Log.d("TAG", "No such document");
                            }
                        } else {
                            Log.d("TAG", "get failed with ", task.getException());
                        }
                    }
                });
            }
        });
    }

    public void actualizaVistas() {


        nombre = findViewById(R.id.nombre);
        //nombre.setText(lugar.getNombre());

        logo_tipo = findViewById(R.id.logo_tipo);
        //logo_tipo.setImageResource(lugar.getTipo().getRecurso());

        tipo = findViewById(R.id.tipo);
        //tipo.setText(lugar.getTipo().getTexto());

        direccion = findViewById(R.id.direccion);
        //direccion.setText(lugar.getDireccion());

        telefono = findViewById(R.id.telefono);
        //telefono.setText(Integer.toString(lugar.getTelefono()));

        url = findViewById(R.id.url);
        //url.setText(lugar.getUrl());

        comentario = findViewById(R.id.comentario);
        //comentario.setText(lugar.getComentario());

        fecha = findViewById(R.id.fecha);
        //fecha.setText(DateFormat.getDateInstance().format(      new Date(lugar.getFecha())));

        hora = findViewById(R.id.hora);
        //hora.setText(DateFormat.getTimeInstance().format(   new Date(lugar.getFecha())));

        valoracion = findViewById(R.id.valoracion);

        /*valoracion.setRating(lugar.getValoracion());
        valoracion.setOnRatingBarChangeListener( new RatingBar.OnRatingBarChangeListener()
        { @Override public void onRatingChanged(RatingBar
            ratingBar, float valor, boolean fromUser)
         { lugar.setValoracion(valor);
           usoLugar.actualizaPosLugar(pos,lugar);
           pos = adaptadorLugaresFirestore.getPos(idLugar);
         }
        })*/;
        //usoLugar.visualizarFoto(lugar,foto);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vista_lugar,menu);
        return true;
    }


    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accion_compartir:
                //usoLugar.compartir(lugar);
                instanciaVista.document(idLugar).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()){
                                Log.d("TAG","ver datos compartir"+documentSnapshot.getData() + " id "+documentSnapshot.getId());
                                String name= task.getResult().getString("nombre");
                                String url= task.getResult().getString("url");
                                String direccion_ = task.getResult().getString("direccion");
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("text/plain");
                                i.putExtra(Intent.EXTRA_TEXT,"Observa este lugar "
                                        +name + "\nSitio web: " + url+"\nDirección: "+direccion_);
                                startActivity(i);
                            }else {
                                Log.d("TAG", "No such document");
                            }
                        } else {
                            Log.d("TAG", "get failed with ", task.getException());
                        }
                    }
                });

                return true;
            case R.id.accion_llegar:
                //usoLugar.verMapa(lugar);
                instanciaVista.document(idLugar).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()){
                                Log.d("TAG","ver mapa datos "+documentSnapshot.getData() + " id "+documentSnapshot.getId());
                                String direccion_mapa = task.getResult().getString("direccion");
                                Uri uri = Uri.parse("geo:0,0?z=18&q="+Uri.encode(direccion_mapa));
                                Log.d("tag casos uso lugar","vermapa " +uri + " " + Uri.encode(direccion_mapa));
                                startActivity(new Intent("android.intent.action.VIEW", uri));
                            }else {
                                Log.d("TAG", "No such document");
                            }
                        } else {
                            Log.d("TAG", "get failed with ", task.getException());
                        }
                    }
                });

                return true;
            /*case R.id.accion_editar:
                //usoLugar.editar(pos,RESULTADO_EDITAR);
                String idEd= adaptadorLugaresFirestore.getKey(pos);
                Intent intent_ed_lugar = new Intent(this, EdicionLugarActivity.class);
                intent_ed_lugar.putExtra("posEd",idEd);
                startActivity(intent_ed_lugar);
                return true;*/
            case R.id.accion_borrar:
                String id= adaptadorLugaresFirestore.getKey(pos);// idPosicion(pos);
                Log.d("TAG","borrar " +id);
                usoLugar.borrar(id);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == RESULTADO_EDITAR){
            pos = adaptadorLugaresFirestore.getPos(idLugar);
            lugar = adaptadorLugaresFirestore.getItem(_id);
            actualizaVistas();
            findViewById(R.id.scrollView1).invalidate();
        } else if (requestCode == RESULTADO_GALERIA) {
            if (resultCode == Activity.RESULT_OK) {
                usoLugar.ponerFoto(pos, data.getDataString(), foto);
            } else {
                Toast.makeText(this, "Foto no cargada",Toast.LENGTH_LONG).show();
            }
        }
        else if (requestCode == RESULTADO_FOTO) {
            if (resultCode == Activity.RESULT_OK && uriUltimaFoto!=null) {
                lugar.setFoto(uriUltimaFoto.toString());
                usoLugar.ponerFoto(pos, lugar.getFoto(), foto);
            } else {
                Toast.makeText(this, "Error en captura", Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



}
