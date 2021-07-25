package com.grupo.Diversitios.datos;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.grupo.Diversitios.Aplicacion;
import com.grupo.Diversitios.modelo.GeoPunto;
import com.grupo.Diversitios.modelo.Lugar;
import com.grupo.Diversitios.modelo.TipoLugar;

public class LugaresBD extends SQLiteOpenHelper implements RepositorioLugares {

    Context contexto;

    public LugaresBD(Context contexto){
        super(contexto,"lugares",null,1);
        this.contexto = contexto;
    }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE lugares ("+
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "nombre TEXT, " +
                "direccion TEXT, " +
                "longitud REAL, " +
                "latitud REAL, " +
                "tipo INTEGER, " +
                "foto TEXT, " +
                "telefono INTEGER, " +
                "url TEXT, " +
                "comentario TEXT, " +
                "fecha BIGINT, " +
                "valoracion REAL)");

        db.execSQL("INSERT INTO lugares VALUES (null, "+
                "'LA UIS', "+
                "'Calle 9 # 27, Bucaramanga, Santander', -73.121, 7.1377, "+
                TipoLugar.EDUCATIVO.ordinal() + ", '', 6344000, "+
                "'https://www.uis.edu.co/', "+
                "'Una de las mejores universidades de Colombia.', "+
                System.currentTimeMillis() +", 5.0)");

        db.execSQL("INSERT INTO lugares VALUES (null, "+
                "'Estadio Atanasio Girardot', "+
                "'Cra. 74 # 48010', -75.59013,6.256864, "+
                TipoLugar.DEPORTE.ordinal() + ", '', 0, "+
                "'http://comunasdemedellin.com/', "+
                "'Estadio de la ciudad de medellín', "+
                System.currentTimeMillis() +", 4.5)");

        db.execSQL("INSERT INTO lugares VALUES (null, "+
                "'Movistar Arena', "+
                "'Diagonal. 61c #26-36, Bogotá, Cundinamarca',-74.07695,4.64888,"+
                TipoLugar.ESPECTACULO.ordinal() + ", '', 5470183, "+
                "'https://movistararena.co/', "+
                "'Centro de eventos en Bogotá', "+
                System.currentTimeMillis() +", 4.0)");

        db.execSQL("INSERT INTO lugares VALUES (null, "+
                "'Páramo de Santurbán', "+
                "'Silos, Santander',-72.90982,7.2466845, "+
                TipoLugar.GASTRONOMICO.ordinal() + ", '', 0, "+
                "'', "+
                "'Centro de eventos en Bogotá', "+
                System.currentTimeMillis() +", 4.0)");

        db.execSQL("INSERT INTO lugares VALUES (null, "+
                "'Loma Mesa de Ruitoque', "+
                "'Loma Mesa de Ruitoque, Floridablanca, Santander',0,0, "+
                TipoLugar.ECOTURISMO.ordinal() + ", '', 0, "+
                "'', "+
                "'Centro de eventos en Bogotá...', "+
                System.currentTimeMillis() +", 4.0)");
        Log.d("TAG","base datos sql "+ db.toString());
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override public Lugar elemento(int id) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM lugares WHERE _id = "+id, null);
        try {
            if (cursor.moveToNext()){
                Log.d("TAG"," curso lugar elemento "+cursor);
                return extraeLugar(cursor);
            }
            else
                //Toast.makeText(contexto,"SQL excepción Error al acceder al elemento _id =" +id,Toast.LENGTH_LONG).show();
                throw new SQLException("Error al acceder al elemento _id = "+id);
        } catch (Exception e) {
            Log.d("TAG","error elemento lugares db exec "+e.getMessage());
            throw e;
        } finally {
            if (cursor!=null) cursor.close();
        }
    }

    @Override
    public void añade(Lugar lugar) {

    }

    @Override public int nuevo() {
        int _id = -1;
        Lugar lugar = new Lugar();
        getWritableDatabase().execSQL("INSERT INTO lugares (nombre, " +
                "direccion, longitud, latitud, tipo, foto, telefono, url, " +
                "comentario, fecha, valoracion) VALUES ('', '', " +
                lugar.getPosicion().getLongitud() + ","+
                lugar.getPosicion().getLatitud() + ", "+ lugar.getTipo().ordinal()+
                ", '', 0, '', '', " + lugar.getFecha() + ", 0)");
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT _id FROM lugares WHERE fecha = " + lugar.getFecha(), null);
        if (c.moveToNext()) _id = c.getInt(0);
        c.close();
        Log.d("TAG","NUEVO LUGAR "+_id + " "+lugar.toString());
        return _id;
    }

    @Override public void borrar(int id) {
        Log.d("TAG","borrar lugares bd " );
        getWritableDatabase().execSQL("DELETE FROM lugares WHERE _id = " + id);
    }

    @Override
    public int tamaño() {
        return 0;
    }

    @Override public void actualiza(int id, Lugar lugar) {
        getWritableDatabase().execSQL("UPDATE lugares SET" +
                " nombre = '" + lugar.getNombre() +
                "', direccion = '" + lugar.getDireccion() +
                "', longitud = " + lugar.getPosicion().getLongitud() +
                " , latitud = " + lugar.getPosicion().getLatitud() +
                " , tipo = " + lugar.getTipo().ordinal() +
                " , foto = '" + lugar.getFoto() +
                "', telefono = " + lugar.getTelefono() +
                " , url = '" + lugar.getUrl() +
                "', comentario = '" + lugar.getComentario() +
                "', fecha = " + lugar.getFecha() +
                " , valoracion = " + lugar.getValoracion() +
                " WHERE _id = " + id);
        Log.d("TAG","actualiza bd " +lugar.toString());

    }

    public static Lugar extraeLugar(Cursor cursor) {
        Lugar lugar = new Lugar();
        lugar.setNombre(cursor.getString(1));
        lugar.setDireccion(cursor.getString(2));
        lugar.setPosicion(new GeoPunto(cursor.getDouble(3),
                cursor.getDouble(4)));
        lugar.setTipo(TipoLugar.values()[cursor.getInt(5)]);
        lugar.setFoto(cursor.getString(6));
        lugar.setTelefono(cursor.getInt(7));
        lugar.setUrl(cursor.getString(8));
        lugar.setComentario(cursor.getString(9));
        lugar.setFecha(cursor.getLong(10));
        lugar.setValoracion(cursor.getFloat(11));
        Log.d("TAG","extrae lugar bd "+lugar.toString());
        return lugar;
    }

    public Cursor extraeCursor() {
        //String consulta = "SELECT * FROM lugares";
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(contexto);
        String consulta, max;
        max= pref.getString("maximo","12");
        Log.d("TAG","pref max "+ pref.getString("maximo",max));
        switch (pref.getString("orden", "0")) {
            case "0":
                consulta = "SELECT * FROM lugares ";
                Log.d("TAG"," consulta  " +consulta);
                break;
            case "1":
                consulta = "SELECT * FROM lugares ORDER BY valoracion DESC";
                Log.d("TAG"," consulta orden valoracion " +consulta);
                break;
            default:
                double lon = ((Aplicacion) contexto.getApplicationContext()).posicionActual.getLongitud();
                double lat = ((Aplicacion) contexto.getApplicationContext()).posicionActual.getLatitud();
                consulta = "SELECT * FROM lugares ORDER BY " +
                        "(" + lon + "-longitud)*(" + lon + "-longitud) + " +
                        "(" + lat + "-latitud )*(" + lat + "-latitud )";
                break;
        }

        consulta += " LIMIT "+pref.getString("maximo",max);
        Log.d("","extrae cursor "+consulta);
        SQLiteDatabase bd = getReadableDatabase();
        return bd.rawQuery(consulta, null);
    }
}
