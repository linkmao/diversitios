package com.grupo.Diversitios.casos_uso;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.grupo.Diversitios.Aplicacion;
import com.grupo.Diversitios.R;
import com.grupo.Diversitios.modelo.GeoPunto;
import com.grupo.Diversitios.presentacion.AdaptadorLugares;

import static android.content.Context.LOCATION_SERVICE;

public class CasosUsoLocalizacion implements LocationListener {

    private static final String TAG = "MisLugares";
    private Activity actividad;
    private int codigoPermiso;
    private LocationManager manejadorLoc;
    private Location mejorLoc;
    private GeoPunto posicionActual;
    private AdaptadorLugares adaptador;

    //CONSTRUCTOR DE LA CLASE
    public CasosUsoLocalizacion(Activity actividad, int codigoPermiso) {
        this.actividad = actividad;
        this.codigoPermiso = codigoPermiso;
        manejadorLoc = (LocationManager) actividad.getSystemService(LOCATION_SERVICE);
        posicionActual = ((Aplicacion) actividad.getApplication()).posicionActual;
        adaptador = ((Aplicacion) actividad.getApplication()).adaptador;
        ultimaLocalizazion();
    }

    // CASOS DE USO

    public void activar() {
        if (hayPermisoLocalizacion()) activarProveedores();
    }

    public void desactivar() {
        if (hayPermisoLocalizacion()) manejadorLoc.removeUpdates(this);
    }

    public void permisoConcedido() {
        ultimaLocalizazion();
        activarProveedores();
        adaptador.notifyDataSetChanged();
    }

    public boolean hayPermisoLocalizacion() {
        return (ActivityCompat.checkSelfPermission(actividad,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    public static void solicitarPermiso(final String permiso, String justificacion,final int requestCode, final Activity actividad)
    {  if (ActivityCompat.shouldShowRequestPermissionRationale(actividad, permiso)){
            new AlertDialog.Builder(actividad)
                    .setTitle("Solicitud de permiso en la app")
                    .setMessage(justificacion)
                    .setIcon(R.mipmap.icono_app)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                            { public void onClick(DialogInterface dialog, int whichButton)
                            {ActivityCompat.requestPermissions(actividad,new String[]{permiso}, requestCode);
                            }
                            })
                    .show();
        } else {
            ActivityCompat.requestPermissions(actividad, new String[]{permiso}, requestCode);
        }
    }

    //FUNCIONES AUXILIARES

    @SuppressLint("MissingPermission")
    void ultimaLocalizazion(){
        if (hayPermisoLocalizacion()) {
            if (manejadorLoc.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                actualizaMejorLocaliz(manejadorLoc.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            }
            if (manejadorLoc.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                actualizaMejorLocaliz(manejadorLoc.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
            }
        } else  {
            solicitarPermiso(Manifest.permission.ACCESS_FINE_LOCATION,
                    "Sin el permiso localización no puedo mostrar la distancia"+
                            " de los lugares o visualizar el mapa correctamente.", codigoPermiso, actividad);
        }
    }

    @SuppressLint("MissingPermission")
    private void activarProveedores()
    {if (hayPermisoLocalizacion())
    {
        if (manejadorLoc.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            manejadorLoc.requestLocationUpdates(LocationManager.GPS_PROVIDER,20 * 1000, 5, (LocationListener) this);
        }
        if
        (manejadorLoc.isProviderEnabled(LocationManager.NETWORK_PROVIDER))

        {manejadorLoc.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10*1000, 10, (LocationListener) this);
        }
    } else {
        solicitarPermiso(Manifest.permission.ACCESS_FINE_LOCATION,
                "Sin el permiso localización no puedo mostrar la " +
                        "distancia a los lugares de la app.", codigoPermiso, actividad);
    }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Nueva localización: "+location);
        actualizaMejorLocaliz(location);
        adaptador.notifyDataSetChanged();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "Se deshabilita: "+provider);
        activarProveedores();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "Se habilita: "+provider);
        activarProveedores();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "Cambia estado: "+provider);
        activarProveedores();
    }

    private static final long DOS_MINUTOS = 2 * 60 * 1000;

    private void actualizaMejorLocaliz(Location localiz) {
        if (localiz != null && (mejorLoc == null
                || localiz.getAccuracy() < 2*mejorLoc.getAccuracy()
                || localiz.getTime() - mejorLoc.getTime() > DOS_MINUTOS)) {
            Log.d(TAG, "Nueva mejor localización");
            mejorLoc = localiz;
            posicionActual.setLatitud(localiz.getLatitude());
            posicionActual.setLongitud(localiz.getLongitude());
        }
    }

}
