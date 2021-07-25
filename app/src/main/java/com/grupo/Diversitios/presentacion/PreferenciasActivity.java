package com.grupo.Diversitios.presentacion;

import android.app.Activity;
import android.os.Bundle;


public class PreferenciasActivity extends Activity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content,new PreferenciasFragment())
                .commit();
        //siguiente linea hace que la vista siempre esté vertical
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
