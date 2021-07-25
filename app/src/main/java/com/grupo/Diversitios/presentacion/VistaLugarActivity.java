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

import com.grupo.Diversitios.Aplicacion;
import com.grupo.Diversitios.R;
import com.grupo.Diversitios.casos_uso.CasosUsoLugar;
import com.grupo.Diversitios.datos.LugaresBD;
import com.grupo.Diversitios.modelo.Lugar;

import java.text.DateFormat;
import java.util.Date;

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

    public int _id;

    @Override protected void onCreate(Bundle savedInstanceState)
    {   super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_lugar);
        lugares = ((Aplicacion) getApplication()).lugares;
        adaptador= ((Aplicacion)getApplication()).adaptador;
        usoLugar = new CasosUsoLugar(this, lugares,adaptador);
        Bundle extras = getIntent().getExtras();
        if(extras !=null)pos = extras.getInt("pos", 0);
        else pos=0;
        _id = adaptador.idPosicion(pos);
        Log.d("tag","oncreate vla "+pos + "id "+_id);
        lugar = adaptador.lugarPosicion(pos);//lugares.elemento(pos);
        foto = findViewById(R.id.foto);
        galeria = findViewById(R.id.galeria);
        camara = findViewById(R.id.camara);
        eliminar = findViewById(R.id.eliminar);
        actualizaVistas();
        llamar();
        verWeb();
        abrirGaleria();
        tomarFotoCamara();
        eliminarFoto();
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
                usoLugar.llamarTelefono(lugar);
            }
        });

    }
    public void verWeb(){
        url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usoLugar.verPgWeb(lugar);

            }
        });
    }

    public void actualizaVistas() {
        nombre = findViewById(R.id.nombre);
        nombre.setText(lugar.getNombre());

        logo_tipo = findViewById(R.id.logo_tipo);
        logo_tipo.setImageResource(lugar.getTipo().getRecurso());

        tipo = findViewById(R.id.tipo);
        tipo.setText(lugar.getTipo().getTexto());

        direccion = findViewById(R.id.direccion);
        direccion.setText(lugar.getDireccion());

        telefono = findViewById(R.id.telefono);
        telefono.setText(Integer.toString(lugar.getTelefono()));

        url = findViewById(R.id.url);
        url.setText(lugar.getUrl());

        comentario = findViewById(R.id.comentario);
        comentario.setText(lugar.getComentario());

        fecha = findViewById(R.id.fecha);
        fecha.setText(DateFormat.getDateInstance().format(
                new Date(lugar.getFecha())));

        hora = findViewById(R.id.hora);
        hora.setText(DateFormat.getTimeInstance().format(
                new Date(lugar.getFecha())));

        valoracion = findViewById(R.id.valoracion);

        valoracion.setRating(lugar.getValoracion());
        valoracion.setOnRatingBarChangeListener(
                new RatingBar.OnRatingBarChangeListener()
        { @Override public void onRatingChanged(RatingBar
            ratingBar, float valor, boolean fromUser)
         { lugar.setValoracion(valor);
           usoLugar.actualizaPosLugar(pos,lugar);
           pos = adaptador.posicionId(_id);
         }
        });
        usoLugar.visualizarFoto(lugar,foto);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vista_lugar,menu);
        return true;
    }


    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accion_compartir:
                usoLugar.compartir(lugar);
                return true;
            case R.id.accion_llegar:
                usoLugar.verMapa(lugar);
                return true;
            case R.id.accion_editar:
                usoLugar.editar(pos,RESULTADO_EDITAR);
                return true;
            case R.id.accion_borrar:
                int id= adaptador.idPosicion(pos);
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
            lugar = lugares.elemento(_id);
            pos = adaptador.posicionId(_id);
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
