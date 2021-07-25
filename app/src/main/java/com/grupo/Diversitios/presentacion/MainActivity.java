package com.grupo.Diversitios.presentacion;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grupo.Diversitios.Aplicacion;
import com.grupo.Diversitios.R;
import com.grupo.Diversitios.casos_uso.CasosUsoActividades;
import com.grupo.Diversitios.casos_uso.CasosUsoLocalizacion;
import com.grupo.Diversitios.casos_uso.CasosUsoLugar;
import com.grupo.Diversitios.datos.LugaresBD;


public class MainActivity extends AppCompatActivity {

    //private RepositorioLugares lugares;
    private CasosUsoLugar usoLugar;
    private CasosUsoActividades usoActividades;

    static final int RESULTADO_PREFERENCIAS = 0;

    private RecyclerView recyclerView;
    //public AdaptadorLugares adaptador;


    //AÑADIENDO LOCALIZACION EN MIS LUGARES
    private static final int SOLICITUD_PERMISO_LOCALIZACION = 1;
    private CasosUsoLocalizacion usoLocalizacion;


    //BASE DATOS ADAPATADOR
    private AdaptadorLugaresBD adaptador;
    private LugaresBD lugares;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adaptador = ((Aplicacion) getApplication()).adaptador;
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptador);


        lugares = ((Aplicacion) getApplication()).lugares;
        usoLugar = new CasosUsoLugar(this,lugares,adaptador);
        usoActividades = new CasosUsoActividades(this);
        usoLocalizacion = new CasosUsoLocalizacion(this,SOLICITUD_PERMISO_LOCALIZACION);

        //barra de acciones
        Toolbar toolbar = findViewById(R.id.toolbar_Main);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout_Main);
        toolbar.setTitle(getTitle());

       //Boton flotante FAB circular
/**/
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, R.string.mensaje_fab, Snackbar.LENGTH_LONG).setAction("Accion",null).show();
                usoLugar.nuevo();
            }
        });

        Log.d("Tag en Main","Mensaje prueba por el logcat");

        adaptador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posicion = (Integer) v.getTag();//recyclerView.getChildAdapterPosition(v);
                Log.d("TAG main","posicion adaptador" +posicion);
                usoLugar.mostrar(posicion);
            }
        });

    }

    //abrir acerca de
    public void lanzarAcercade(View view){
        Intent abrir = new Intent(this, AcercaDeActivity.class);
        startActivity(abrir);
    }

    public void lanzarVistaLugar(View view){
        final EditText entrada = new EditText(this);
        entrada.setText("0");
        entrada.setInputType(InputType.TYPE_CLASS_NUMBER);

        new AlertDialog.Builder(this)
                .setTitle("Selección de lugar")
                .setMessage("indica su id:")
                .setIcon(R.mipmap.icono_app_round)
                .setView(entrada)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int id = Integer.parseInt (entrada.getText().toString());

                            Log.d("TAG"," buscar " +adaptador.idPosicion(id));
                            usoLugar.mostrar(id);
                        /*if(id >=0 && id< adaptador.idPosicion(id)) { //lugares.tamaño()} else {
                            Log.d("TAG"," buscar " +adaptador.idPosicion(id));
                            usoLugar.mostrar(0);
                        }*/

                    }})
                .setNegativeButton("Cancelar", null)
                .show();

    }

    //mostrar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.ajustes){
            usoActividades.lanzarPreferencias(RESULTADO_PREFERENCIAS);
            //Log.d("Tag en Main","Clic en la opcion ajustes");
            return true;
        }
        if (id == R.id.acercaDe){
            usoActividades.lanzarAcercaDe();
            //lanzarAcercade(null);
            return true;
        }
        if (id == R.id.menu_buscar){
            lanzarVistaLugar(null);
            Log.d("Tag main","clic a la opcion buscar");
            return true;
        }
        if (id == R.id.menu_usuario){
            return  true;
        }
        if (id == R.id.menu_mapa){
            usoActividades.lanzarMapa();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SOLICITUD_PERMISO_LOCALIZACION
                && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            usoLocalizacion.permisoConcedido();
    }

    @Override protected void onResume() {
        super.onResume();
        usoLocalizacion.activar();
    }

    @Override protected void onPause() {
        super.onPause();
        usoLocalizacion.desactivar();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULTADO_PREFERENCIAS) {
            adaptador.setCursor(lugares.extraeCursor());
            adaptador.notifyDataSetChanged();
            /*if (usoLugar.obtenerFragmentVista() != null)
                usoLugar.mostrar(0);*/
        }
    }
}