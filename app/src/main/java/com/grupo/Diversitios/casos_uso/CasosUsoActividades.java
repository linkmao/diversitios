package com.grupo.Diversitios.casos_uso;

import android.app.Activity;
import android.content.Intent;

import com.grupo.Diversitios.presentacion.AcercaDeActivity;
import com.grupo.Diversitios.presentacion.MapaActivity;
import com.grupo.Diversitios.presentacion.PreferenciasActivity;
import com.grupo.Diversitios.presentacion.UsuarioActivity;

public class CasosUsoActividades {

    protected Activity actividad;

    //constructor de la clase
    public CasosUsoActividades(Activity actividad) {
        this.actividad = actividad;
    }

    public void lanzarAcercaDe() {
        actividad.startActivity(new Intent(actividad, AcercaDeActivity.class));
    }

    public void lanzarPreferencias(int codidoSolicitud) {
      actividad.startActivityForResult(new Intent(actividad,
        PreferenciasActivity.class), codidoSolicitud);
   }

    public void lanzarMapa() {
        actividad.startActivity(new Intent(actividad, MapaActivity.class));
   }

   public void lanzarUsuario(){ actividad.startActivity(new
            Intent(actividad, UsuarioActivity.class));
    }

}
