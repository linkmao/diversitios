package com.grupo.Diversitios.presentacion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.grupo.Diversitios.R;

import java.util.Timer;
import java.util.TimerTask;

public class iniciador extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iniciador);

        TimerTask tarea = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(iniciador.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        Timer tiempo = new Timer();
        tiempo.schedule(tarea, 3000);
    }
}
