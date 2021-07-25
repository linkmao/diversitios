package com.grupo.Diversitios.modelo;

import com.grupo.Diversitios.R;

public enum TipoLugar {

    OTROS ("Otros", R.drawable.otro),
    AVENTURA("Aventura", R.drawable.aventuras),
    ECOTURISMO("Ecoturismo", R.drawable.ecoparque),
    HISTORICO("Historico", R.drawable.historico),
    ESPECTACULO ("Espectáculo", R.drawable.espectaculo),
    RELIGIOSO("Religioso", R.drawable.iglesia),
    CULTURAL("Cultural", R.drawable.cultural),
    EDUCATIVO("Educativo", R.drawable.educativo),
    DEPORTE ("Deportivo", R.drawable.deportivo),
    GASTRONOMICO("Gastronomico", R.drawable.gastronomico),
    SOLYPLAYA("Sol y Playa", R.drawable.solyplaya);

    private final String texto;
    private final int recurso;

    //CONSTRUCTOR
    TipoLugar(String texto, int recurso) {
        this.texto = texto;
        this.recurso = recurso;
    }

    //GET DE LOS DOS ATRIBUTOS DEL ENUM

    public String getTexto() { return texto; }

    public int getRecurso() { return recurso; }

    public static String[] getNombres() {
        String[] resultado = new String[TipoLugar.values().length];
        for (TipoLugar tipo : TipoLugar.values()) {
            resultado[tipo.ordinal()] = tipo.texto;
        }
        return resultado;
    }

}
