package com.example.a1631088057.condomine.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.a1631088057.condomine.Condomine_Singleton;
import com.example.a1631088057.condomine.FriendlyMessage;
import com.example.a1631088057.condomine.MessageAdapter;
import com.example.a1631088057.condomine.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class Mural_Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    public static final String ANONYMOUS = "anonymous";

    private static final int RC_SIGN_IN = 1;

    private ListView mMessageListView;
    private MessageAdapter mMessageAdapter;
    private ProgressBar mProgressBar;

    private String mUsername;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;

    private ImageView imagemUsuarioHeader;
    private TextView nomeUsuarioHeader;
    private TextView emailUsuarioHeader;
    private GoogleApiClient googleApiClient;
    private boolean forcaLogout = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mural);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Toast.makeText(getApplicationContext(), Condomine_Singleton.getInstance().getTipoLogin().toString(), Toast.LENGTH_SHORT).show();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        View navHeaderView = navigationView.inflateHeaderView(R.layout.nav_header_mural);

        nomeUsuarioHeader = (TextView) navHeaderView.findViewById(R.id.nomeUser);
        emailUsuarioHeader = (TextView) navHeaderView.findViewById(R.id.emailUser);
        imagemUsuarioHeader = (ImageView) navHeaderView.findViewById(R.id.imagemUser);

        Menu menuNav = navigationView.getMenu();
        MenuItem nav_edita = menuNav.findItem(R.id.nav_edita);

        if (Condomine_Singleton.getInstance().getTipoLogin().toString().equals("Google")){
            nav_edita.setEnabled(false);
            verificaGoogleSignIn();
        }
        if (Condomine_Singleton.getInstance().getTipoLogin().toString().equals("Facebook")){
            nav_edita.setEnabled(false);
        }

        preparaCarregamentoMural();
    }

    private void verificaGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mural, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            FirebaseAuth.getInstance().signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_mural) {
            // Handle the camera action
        } else if (id == R.id.nav_fale) {
            Intent intentFaleSindico = new Intent(Mural_Activity.this, ChatActivity.class);
            startActivity(intentFaleSindico);

        } else if (id == R.id.nav_agenda) {

        } else if (id == R.id.nav_edita) {
            Intent intentEditaCadastro = new Intent(Mural_Activity.this, Atualiza_Activity.class);
            startActivity(intentEditaCadastro);

        } else if (id == R.id.nav_logout) {
            forcaLogout = false;
            if (Condomine_Singleton.getInstance().getTipoLogin().toString().equals("Google")) {
                logOutGoogle();
            }
            if (Condomine_Singleton.getInstance().getTipoLogin().toString().equals("Facebook")) {
                logOutFacebook();
            }
            if (Condomine_Singleton.getInstance().getTipoLogin().toString().equals("Firebase")){
                logOutFirebase();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOutFirebase() {
        FirebaseAuth.getInstance().signOut();
        direcionaTelaLogin();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (Condomine_Singleton.getInstance().getTipoLogin().toString().equals("Google")){
            googleOpr();
        }
        if (Condomine_Singleton.getInstance().getTipoLogin().toString().equals("Facebook")){
            facebookOpr();
        }
        if (Condomine_Singleton.getInstance().getTipoLogin().toString().equals("Firebase")){
            firebaseOpr();
        }


    }

    private void firebaseOpr() {
        nomeUsuarioHeader.setText(Condomine_Singleton.getInstance().getNome());
        emailUsuarioHeader.setText(Condomine_Singleton.getInstance().getEmail());
        Glide.with(this).load(Condomine_Singleton.getInstance().getFoto()).into(imagemUsuarioHeader);
    }

    private void facebookOpr() {
        nomeUsuarioHeader.setText(Condomine_Singleton.getInstance().getNome());
        emailUsuarioHeader.setText(Condomine_Singleton.getInstance().getEmail());
        Glide.with(this).load(Condomine_Singleton.getInstance().getFoto()).into(imagemUsuarioHeader);
    }

    private void googleOpr() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if(opr.isDone()){
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
//            Toast.makeText(this, account.getDisplayName().toString() , Toast.LENGTH_SHORT).show();
            nomeUsuarioHeader.setText(account.getDisplayName().toString());
            emailUsuarioHeader.setText(account.getEmail().toString());
            Glide.with(this).load(account.getPhotoUrl()).into(imagemUsuarioHeader);
//            Log.d("MeuApp", account.getPhotoUrl().toString());
        } else {
            direcionaTelaLogin();
        }
    }

    private void direcionaTelaLogin() {
        Intent intentDirecionarTelaLogin = new Intent ( Mural_Activity.this, Condomine_Activity.class);
        startActivity(intentDirecionarTelaLogin);
    }

    private void logOutGoogle(){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if(status.isSuccess()){
                    direcionaTelaLogin();
                } else {
                    Toast.makeText(getApplicationContext(), "Erro ao efetuar logout", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void logOutFacebook(){
        LoginManager.getInstance().logOut();
        direcionaTelaLogin();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (forcaLogout = true) {
//            if (Condomine_Singleton.getInstance().getTipoLogin().toString().equals("Google")) {
//                logOutGoogle();
//            }
//            if (Condomine_Singleton.getInstance().getTipoLogin().toString().equals("Facebook")) {
//                logOutFacebook();
//            }
//            if (Condomine_Singleton.getInstance().getTipoLogin().toString().equals("Firebase")) {
//                logOutFirebase();
//            }
//        }
    }

    private void preparaCarregamentoMural() {
        mUsername = ANONYMOUS;

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("mural");
        mChatPhotosStorageReference = mFirebaseStorage.getReference().child("mural_photos");

        // Inicializa as referências views
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageListView = (ListView) findViewById(R.id.messageListView);

        // Inicializa a Lista de mensagens e o adapter
        final List<FriendlyMessage> friendlyMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, R.layout.item_message, friendlyMessages);
        mMessageListView.setAdapter(mMessageAdapter);

        // Inicializa a progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    // usuario logado
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    // usuario deslogado
                    onSignedOutCleanup();
                    direcionaTelaLogin();
                }
            }
        };
    }

    private void onSignedInInitialize(String userName) {
        mUsername = userName;
        attachDatabaseReadListener();

    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        mMessageAdapter.clear();
        detachDatabaseListener();
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                    mMessageAdapter.add(friendlyMessage);
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseListener() {
        if (mChildEventListener != null){
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseListener();
        mMessageAdapter.clear();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}
