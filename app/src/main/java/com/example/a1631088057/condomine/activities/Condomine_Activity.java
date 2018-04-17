package com.example.a1631088057.condomine.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a1631088057.condomine.Condomine_Singleton;
import com.example.a1631088057.condomine.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import com.example.a1631088057.condomine.ConfiguracaoFirebase;
import com.example.a1631088057.condomine.Usuarios;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Condomine_Activity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private EditText txtLogin;
    private EditText txtPassword;
    private FirebaseAuth autenticacao;
    private GoogleApiClient googleApiClient;
    private CallbackManager callbackManager;
    private Usuarios usuarios;
    private int backButtonCount=0;
    LoginButton btnLoginFacebook;
    String estadoLogin="N";
    private DatabaseReference mDatabase, usersRef, usuarioRef;

    public static final int SIGN_IN_CODE = 777;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button Login = (Button) findViewById(R.id.btnOK);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercicio_android);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        boolean loggedIn = AccessToken.getCurrentAccessToken() == null;
//        googleOpr();

//        if (loggedIn = true){
//            Condomine_Singleton.getInstance().setTipoLogin("Facebook");
//            abrirTelaPrincipal();
//        }

        logarComGoogle();
        logarComFacebook();

        txtLogin = (EditText) findViewById(R.id.txtLogin);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        Button login = (Button) findViewById(R.id.btnOK);
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (!txtLogin.getText().toString().equals("") && !txtPassword.getText().toString().equals("")){
                    usuarios = new Usuarios();
                    usuarios.setLogin(txtLogin.getText().toString());
                    usuarios.setPassword(txtPassword.getText().toString());
                    validarLogin();
                } else {
                    Toast.makeText(Condomine_Activity.this, "Preencha os campos de e-mail e senha.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button chamaCadastro = (Button) findViewById(R.id.btnCadastro);
        chamaCadastro.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent abrirTelaCadastro =  new Intent(Condomine_Activity.this, Cadastro_Activity.class);
                startActivity(abrirTelaCadastro);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = autenticacao.getCurrentUser();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }

    private void validarLogin(){
//        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        autenticacao.signInWithEmailAndPassword(usuarios.getLogin(), usuarios.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Condomine_Singleton.getInstance().setTipoLogin("Firebase");
                    Condomine_Singleton.getInstance().setEmail(usuarios.getLogin());
                    String userID = autenticacao.getCurrentUser().getUid().toString();
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    usersRef = mDatabase.child("users");
                    usuarioRef = usersRef.child(userID);

                    usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Usuarios usuario = dataSnapshot.getValue(Usuarios.class);
                            Condomine_Singleton.getInstance().setNome(usuario.getNomeUsuario());
                            Condomine_Singleton.getInstance().setFoto(usuario.getFotoURL());
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(Condomine_Activity.this, "Erro ao recuperar dados.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    abrirTelaPrincipal();
                    Toast.makeText(Condomine_Activity.this, "Login efetuado com sucesso.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Condomine_Activity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void abrirTelaPrincipal(){
        Intent intentAbrirTelaPricipal = new Intent(Condomine_Activity.this, Mural_Activity.class);
        startActivity(intentAbrirTelaPricipal);
    }

    public void logarContaGoogle(){
        Condomine_Singleton.getInstance().setTipoLogin("Google");
        estadoLogin = "S";
        Intent intentLoginGoogle = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intentLoginGoogle, SIGN_IN_CODE);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Condomine_Singleton.getInstance().getTipoLogin().toString().equals("Google")){
            if (requestCode == SIGN_IN_CODE){
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                GoogleSignInAccount account = result.getSignInAccount();
                if (result.isSuccess()){
                    firebaseAuthWithGoogle(account);
                } else {
                    Toast.makeText(Condomine_Activity.this, "Não foi possível iniciar a sessão.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        autenticacao.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = autenticacao.getCurrentUser();
                            abrirTelaPrincipal();

                        } else {
                            Toast.makeText(Condomine_Activity.this, "Não foi possível iniciar a sessão.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

//    private void handleGoogleSignInResult(GoogleSignInResult result) {
//        if (result.isSuccess()){
//            abrirTelaPrincipal();
//            Toast.makeText(Condomine_Activity.this, "Login Google efetuado com sucesso.", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(Condomine_Activity.this, "Não foi possível iniciar a sessão.", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void logarComFacebook(){

        callbackManager = CallbackManager.Factory.create();
        btnLoginFacebook = (LoginButton) findViewById(R.id.btnLoginFacebook);
        btnLoginFacebook.setReadPermissions(Arrays.asList("email", "public_profile"));
        btnLoginFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Condomine_Singleton.getInstance().setTipoLogin("Facebook");
                handleFacebookAccessToken(loginResult.getAccessToken());


//                GraphRequest graphRequest = GraphRequest.newMeRequest(
//                        loginResult.getAccessToken(),
//                new GraphRequest.GraphJSONObjectCallback() {
//                            @Override
//                            public void onCompleted(JSONObject object, GraphResponse response) {
//                                setUserInfo(object);
//                                abrirTelaPrincipal();
//                            }
//                });
//                Bundle parameters = new Bundle();
//                parameters.putString("fields", "first_name, last_name, email, id");
//                graphRequest.setParameters(parameters);
//                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(Condomine_Activity.this, R.string.login_cancelado, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(Condomine_Activity.this, "Deu erro..." + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUserInfo(JSONObject object) {
        String first_name, last_name, email, id;
        try {
            first_name = object.getString("first_name");
            last_name = object.getString("last_name");
            email = object.getString("email");
            id = object.getString("id");
            Condomine_Singleton.getInstance().setNome(first_name + " " + last_name);
            Condomine_Singleton.getInstance().setEmail(email);
            Condomine_Singleton.getInstance().setFoto("https://graph.facebook.com/"+id+"/picture?height=120&width=120");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        autenticacao.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = autenticacao.getCurrentUser();
                            Condomine_Singleton.getInstance().setNome(user.getDisplayName().toString());
                            Condomine_Singleton.getInstance().setEmail(user.getEmail().toString());
                            Condomine_Singleton.getInstance().setFoto(user.getPhotoUrl().toString());
                            Toast.makeText(Condomine_Activity.this, "Logando com Facebook.", Toast.LENGTH_SHORT).show();
                            abrirTelaPrincipal();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Condomine_Activity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void logarComGoogle() {
        Button LoginGoogle = (Button) findViewById(R.id.btnLoginGoogle);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        LoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logarContaGoogle();
            }
        });
    }

    private void googleOpr() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if(opr.isDone()){
            Condomine_Singleton.getInstance().setTipoLogin("Google");
            abrirTelaPrincipal();
        }
    }
}
