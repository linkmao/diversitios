package com.grupo.Diversitios.presentacion;

import android.database.Cursor;
import android.util.Log;

import com.grupo.Diversitios.datos.LugaresBD;
import com.grupo.Diversitios.datos.RepositorioLugares;
import com.grupo.Diversitios.modelo.Lugar;

public class AdaptadorLugaresBD extends AdaptadorLugares {

    protected Cursor cursor;

    //CONSTRUCTOR
    public AdaptadorLugaresBD(RepositorioLugares lugares, Cursor cursor) {
        super(lugares);
        this.cursor = cursor;
    }

    //MÉTODOS GET Y SET DE CURSOR
    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public Lugar lugarPosicion(int posicion) {
        cursor.moveToPosition(posicion);
        Log.d("TAG","lugar posicion adaptador bd "+cursor.toString());
        return LugaresBD.extraeLugar(cursor);
    }
    public int idPosicion(int posicion) {
        cursor.moveToPosition(posicion);
        Log.d("TAG","id posicion adaprador "+cursor.moveToPosition(posicion));
        return cursor.getInt(0);
    }

    public int posicionId(int id) {
        int pos = 0;
        while (pos<getItemCount() && idPosicion(pos)!=id) pos++;
        if (pos >= getItemCount()) return -1;
        else                       return pos;
    }

    @Override public void onBindViewHolder(ViewHolder holder, int posicion) {
        //super.onBindViewHolder(holder, posicion);
        Lugar lugar = lugarPosicion(posicion);
        holder.personaliza(lugar);
        holder.itemView.setTag(new Integer(posicion));
    }

    @Override public int getItemCount() {
        Log.d("TAG","get item count "+cursor.getCount());
        return cursor.getCount();
    }


}
