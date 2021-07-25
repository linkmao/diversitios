package com.grupo.Diversitios.casos_uso;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.grupo.Diversitios.Aplicacion;
import com.grupo.Diversitios.datos.LugaresBD;

import com.grupo.Diversitios.modelo.GeoPunto;
import com.grupo.Diversitios.modelo.Lugar;
import com.grupo.Diversitios.presentacion.AdaptadorLugaresBD;
import com.grupo.Diversitios.presentacion.EdicionLugarActivity;
import com.grupo.Diversitios.presentacion.VistaLugarActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class CasosUsoLugar {

    private Activity actividad;
    //private RepositorioLugares lugares;
    private LugaresBD lugares;
    private AdaptadorLugaresBD adaptador;

    //constructor de la clase
    public CasosUsoLugar(Activity actividad, LugaresBD lugares, AdaptadorLugaresBD adaptador) {
        this.actividad = actividad;
        this.lugares = lugares;
        this.adaptador = adaptador;
    }

    // OPERACIONES BÁSICAS
    public void mostrar(int pos) {
        Intent mostrar = new Intent(actividad, VistaLugarActivity.class);
        mostrar.putExtra("pos", pos);
        actividad.startActivity(mostrar);
    }

    public void editar(int pos, int codigoSolicitud) {
        Intent intent_ed_lugar = new Intent(actividad, EdicionLugarActivity.class);
        intent_ed_lugar.putExtra("pos",pos);
        actividad.startActivityForResult(intent_ed_lugar,codigoSolicitud);
    }

    public void actualizaPosLugar(int pos, Lugar lugar) {
        int id = adaptador.idPosicion(pos);
        guardar(id, lugar);
    }

    public void guardar(int id, Lugar nuevoLugar){
        Log.d("TAG","usulugar guardar " + id + " "+ nuevoLugar+"\ncursor guardar casos uso lugar"+lugares.extraeCursor()) ;
        lugares.actualiza(id,nuevoLugar);
        adaptador.setCursor(lugares.extraeCursor());
        adaptador.notifyDataSetChanged();
    }

    public void borrar(final int id) {
        Log.d("usoslugar"," tamaño " + adaptador.getItemId(id));
        new AlertDialog.Builder(actividad)
                .setTitle("Borrado de lugar")
                .setMessage("¿Seguro de eliminar este lugar?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        lugares.borrar(id);
                        adaptador.setCursor(lugares.extraeCursor());
                        adaptador.notifyDataSetChanged();
                        actividad.finish();

                    }})
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // INTENCIONES
    public void compartir(Lugar lugar) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT,"Observa este lugar "
        +lugar.getNombre() + " - " + lugar.getUrl()+ "\n"
        +lugar.getFoto()) ;
        actividad.startActivity(i);
    }

    public void llamarTelefono(Lugar lugar) {
        actividad.startActivity(new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:" + lugar.getTelefono())));
    }

    public void verPgWeb(Lugar lugar) {
        actividad.startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(lugar.getUrl())));
    }

    public final void verMapa(Lugar lugar) {
        double lat = lugar.getPosicion().getLatitud();
        double lon = lugar.getPosicion().getLongitud();
        Uri uri = lugar.getPosicion() != GeoPunto.SIN_POSICION
                ?Uri.parse("geo:" + lat + ',' + lon+"?z=18&q="+Uri.encode(lugar.getDireccion()))//? Uri.parse("geo:" + lat + ',' + lon)
                : Uri.parse("geo:0,0?q="+Uri.encode(lugar.getDireccion()));
        Log.d("tag casos uso lugar","vermapa " +uri + " " + Uri.encode(lugar.getDireccion())+ "\n" + lugar.getPosicion() + " geopto "+GeoPunto.SIN_POSICION);
        actividad.startActivity(new Intent("android.intent.action.VIEW", uri));
    }

    // FOTOGRAFÍAS
    public void ponerDeGaleria(int codidoSolicitud) {
        String action;
        if (android.os.Build.VERSION.SDK_INT >= 19) { // API 19 - Kitkat
            action = Intent.ACTION_OPEN_DOCUMENT;
        } else {
            action = Intent.ACTION_PICK;
        }
        Intent i = new Intent(action,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        /*if (fragment != null)
            fragment.startActivityForResult(i, codidoSolicitud); else*/
        actividad.startActivityForResult(i, codidoSolicitud);
    }

    public void ponerFoto(int pos, String uri, ImageView imageView) {
        Lugar lugar = adaptador.lugarPosicion(pos);//lugares.elemento(pos);
        lugar.setFoto(uri);
        visualizarFoto(lugar, imageView);
        actualizaPosLugar(pos, lugar);
    }

    public void nuevo() {
        int id = lugares.nuevo();
        GeoPunto posicion = ((Aplicacion) actividad.getApplication()).posicionActual;
        if (!posicion.equals(GeoPunto.SIN_POSICION)) {
            Lugar lugar = lugares.elemento(id);
            lugar.setPosicion(posicion);
            lugares.actualiza(id, lugar);
        }
        Log.d("TAG","posicion creada "+posicion);
        Intent nuevo_lugar = new Intent(actividad, EdicionLugarActivity.class);
        nuevo_lugar.putExtra("_id", id);
        actividad.startActivity(nuevo_lugar);
    }


    public void visualizarFoto(Lugar lugar, ImageView imageView) {
        if (lugar.getFoto() != null && !lugar.getFoto().isEmpty()) {
            imageView.setImageBitmap(reduceBitmap(actividad, lugar.getFoto(), 1024, 1024));//maxancho estaba 1024 ambos
            //imageView.setImageURI(Uri.parse(lugar.getFoto()));
        } else {
            imageView.setImageBitmap(null);
        }
    }

    public Uri tomarFoto(int codidoSolicitud) {
        try {
            Uri uriUltimaFoto;
            File file = File.createTempFile(
                    "img_" + (System.currentTimeMillis() / 1000), ".jpg",
                    actividad.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            if (Build.VERSION.SDK_INT >= 24) {
                uriUltimaFoto = FileProvider.getUriForFile(
                        actividad, "misiontic.uis.mislugarestic.fileProvider", file);
            } else {
                uriUltimaFoto = Uri.fromFile(file);
            }
            Intent intento_tomarFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intento_tomarFoto.putExtra(MediaStore.EXTRA_OUTPUT, uriUltimaFoto);
            actividad.startActivityForResult(intento_tomarFoto, codidoSolicitud);
            /*if (fragment != null)
                fragment.startActivityForResult(intento_tomarFoto, codidoSolicitud);
            else actividad.startActivityForResult(intento_tomarFoto, codidoSolicitud);*/
            return uriUltimaFoto;
        } catch (IOException ex) {
            Toast.makeText(actividad, "Error al crear fichero de imagen",
                    Toast.LENGTH_LONG).show();
            return null;
        }
    }


    private Bitmap reduceBitmap(Context contexto, String uri,int maxAncho, int maxAlto) {
        try {
            InputStream input = null;
            Uri u = Uri.parse(uri);
            if (u.getScheme().equals("http") || u.getScheme().equals("https")) {
                input = new URL(uri).openStream();
            } else {
                input = contexto.getContentResolver().openInputStream(u);
            }
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = (int) Math.max(
                    Math.ceil(options.outWidth / maxAncho),
                    Math.ceil(options.outHeight / maxAlto));
            Log.d("TAG cul","tamaño foto " + (int) Math.max(
                    options.outWidth / maxAncho,
                    options.outHeight / maxAlto));
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(input, null, options);
        } catch (FileNotFoundException e) {
            Toast.makeText(contexto, "Fichero/recurso de imagen no encontrado",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            Toast.makeText(contexto, "Error accediendo a imagen",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return null;
        }
    }/**/
}
