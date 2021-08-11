package com.grupo.Diversitios.presentacion;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.grupo.Diversitios.R;

import java.util.Timer;
import java.util.TimerTask;

public class iniciador extends Activity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iniciador);
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                Intent abrirApp = new Intent(iniciador.this,
                        LoginActivity.class);
                ActivityOptions activityOptions =
                        ActivityOptions.makeCustomAnimation(iniciador.this,R.anim.fui_slide_in_right
                                ,R.anim.transicion_iniciador);
                startActivity(abrirApp,activityOptions.toBundle());
                finish();
            }
        },3500);
    }
}
