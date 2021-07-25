package com.grupo.Diversitios;

import android.app.Application;

import com.grupo.Diversitios.datos.LugaresBD;
import com.grupo.Diversitios.datos.RepositorioLugares;
import com.grupo.Diversitios.modelo.GeoPunto;
import com.grupo.Diversitios.presentacion.AdaptadorLugaresBD;

public class Aplicacion extends Application {

    public LugaresBD lugares; //= new LugaresLista();

    //public AdaptadorLugares adaptador = new AdaptadorLugares(lugares);

    //AÑADIENDO LOCALIZACIÓN EN MIS LUGARES
    public GeoPunto posicionActual = new GeoPunto(0.0, 0.0);

    //ADAPTADOR BASE DATOS SQLite
    public AdaptadorLugaresBD adaptador;
    @Override public void onCreate() {
        super.onCreate();
        lugares = new LugaresBD(this);
        adaptador= new AdaptadorLugaresBD(lugares, lugares.extraeCursor());
    }

    //get de RepositorioLugares
    public RepositorioLugares getLugares() { return lugares; }
}
