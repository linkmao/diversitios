package com.grupo.Diversitios.presentacion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.grupo.Diversitios.R;
import com.grupo.Diversitios.datos.Usuarios;

public class LoginActivity extends FragmentActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String correo = "";
    private String contraseña = "";
    private ViewGroup contenedor;
    private EditText etCorreo, etContraseña;
    private TextInputLayout tilCorreo, tilContraseña;
    private ProgressDialog dialogo;
    //inicio google
    private static final int RC_GOOGLE_SIGN_IN = 123;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etCorreo = findViewById(R.id.correo);
        etContraseña = findViewById(R.id.contraseña);
        tilCorreo = findViewById(R.id.til_correo);
        tilContraseña = findViewById(R.id.til_contraseña);
        contenedor = findViewById(R.id.contenedor);
        dialogo = new ProgressDialog(this);
        dialogo.setTitle("Verificando usuario");
        dialogo.setMessage("Por favor espere...");
        //Focus Cambiar imagenes
        ImageView img;
        EditText contrasena;
        img=(ImageView) findViewById(R.id.imageView5);
        img.setImageResource(R.drawable.ojosabiertos);
        contrasena= (EditText) findViewById(R.id.contraseña);
        contrasena.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (!hasFocus) {
                    img.setImageResource(R.drawable.ojosabiertos);
                }else{
                    img.setImageResource(R.drawable.ojoscerrados);
                }

            }
        });






        //sesion google
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)

                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)

                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
        verificaSiUsuarioValidado();
    }

    private void googleAuth(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(
                acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new
                        OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                if (!task.isSuccessful()) {

                                    mensaje(task.getException().getLocalizedMessage());
                                } else {
                                    verificaSiUsuarioValidado();
                                }
                            }
                        });
    }

    @Override
    protected void onActivityResult(int requestCode, int
            resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                GoogleSignInResult googleSignInResult =
                        Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (googleSignInResult.isSuccess()) {
                    googleAuth(googleSignInResult.getSignInAccount());
                } else {
                    mensaje("Error conexión");
                }
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        mensaje("Error de autentificación con Google");
    }


    private void verificaSiUsuarioValidado() {
        if (auth.getCurrentUser() != null) {
            Usuarios.guardarUsuario(auth.getCurrentUser());
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
    }























    public void registroCorreo(View v) {
        if (verificaCampos()) {
            dialogo.show();
            auth.createUserWithEmailAndPassword(correo, contraseña)
                    .addOnCompleteListener(this, new
                            OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        verificaSiUsuarioValidado();
                                    } else {
                                        dialogo.dismiss();
                                        mensaje(task.getException().getLocalizedMessage());
                                    }
                                }
                            });
        }
    }

    public void inicioSesionCorreo(View v) {
        if (verificaCampos()) {
            dialogo.show();
            auth.signInWithEmailAndPassword(correo, contraseña)
                    .addOnCompleteListener(this, new
                            OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        verificaSiUsuarioValidado();
                                    } else {
                                        dialogo.dismiss();
                                        mensaje(task.getException().getLocalizedMessage());
                                    }
                                }
                            });
        }
    }

    private void mensaje(String mensaje) {
        Snackbar.make(contenedor, mensaje, Snackbar.LENGTH_LONG).show();
    }

    public void autentificarGoogle(View v) {
        Intent i = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(i, RC_GOOGLE_SIGN_IN);
    }

    private boolean verificaCampos() {
        correo = etCorreo.getText().toString();
        contraseña = etContraseña.getText().toString();
        tilCorreo.setError("");
        tilContraseña.setError("");
        if (correo.isEmpty()) {
            tilCorreo.setError("Introduce un correo");
        } else if (!correo.matches(".+@.+[.].+")) {
            tilCorreo.setError("Correo no válido");
        } else if (contraseña.isEmpty()) {
            tilContraseña.setError("Introduce una contraseña");
        } else if (contraseña.length() < 6) {
            tilContraseña.setError("Ha de contener al menos 6 caracteres");
        } else if (!contraseña.matches(".*[0-9].*")) {
            tilContraseña.setError("Ha de contener un número");
        } else if (!contraseña.matches(".*[A-Z].*")) {
            tilContraseña.setError("Ha de contener una letra mayúscula");
        } else {
            return true;
        }
        return false;
    }

    public void restablecer_contra (View v){
        correo = etCorreo.getText().toString();
        tilCorreo.setError("");
        if (correo.isEmpty())
        { tilCorreo.setError("Introduce un correo"); }
        else if (!correo.matches(".+@.+[.].+"))
        { tilCorreo.setError("Correo no válido"); }
        else { dialogo.show();
            auth.sendPasswordResetEmail(correo).addOnCompleteListener(new OnCompleteListener<Void>()
            { @Override public void onComplete(Task<Void> task)
            { dialogo.dismiss();
            if (task.isSuccessful())
            { mensaje("Verifica tu correo para cambiar contraseña."); }
            else { mensaje("ERROR al mandar correo para cambiar contraseña"); }
            }
            });
        }
        }



}