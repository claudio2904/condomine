package com.example.a1631088057.condomine.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.example.a1631088057.condomine.Condomine_Singleton;
import com.example.a1631088057.condomine.ConfiguracaoFirebase;
import com.example.a1631088057.condomine.EmailText;
import com.example.a1631088057.condomine.Endereco;
import com.example.a1631088057.condomine.R;
import com.example.a1631088057.condomine.Usuarios;
import com.example.a1631088057.condomine.ZipCodeListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;

/**
 * Created by 1631088057 on 10/10/2017.
 */

public class Cadastro_Activity extends FragmentActivity implements OnMapReadyCallback {

    //    private EditText txtNome;
    private EditText txtNome, txtFone, txtCep, txtCidade;
    private EditText txtSenha, txtConfirmaSenha;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private EmailText et;
    private String status = " ";
    private static final String local = "cadastro";
    private String fotoAvatar;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private Location location;
    private String logradouro = "SGAS 613, Brasilia";
    private Address endereco;
    private String bairro;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private boolean permissaoGPS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_activity);
        verificaPermissao();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        et = new EmailText(this);
        et.setHint("Email");

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

        txtNome  = (EditText) findViewById(R.id.txtNome);
        txtFone  = (EditText) findViewById(R.id.txtFone);
        txtCep   = (EditText) findViewById(R.id.txtCEP);
        txtCidade   = (EditText) findViewById(R.id.txtCidade);
        txtSenha = (EditText) findViewById(R.id.txtSenha);
        txtConfirmaSenha = (EditText) findViewById(R.id.txtConfirmaSenha);
        txtCep.addTextChangedListener(new ZipCodeListener(this, local));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout ll = (LinearLayout) findViewById(R.id.LinearLayout1);
        ll.addView(et);

        Button b = (Button) findViewById(R.id.btnCancela);
        b.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        Button cadastrar = (Button) findViewById(R.id.btnOK);
        cadastrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (txtNome.getText().toString().equals("")){
                    Toast.makeText(Cadastro_Activity.this, "Digite o seu nome.", Toast.LENGTH_SHORT).show();
                } else if (txtFone.getText().toString().equals("")){
                    Toast.makeText(Cadastro_Activity.this, "Digite o número do seu telefone.", Toast.LENGTH_SHORT).show();
                } else if (txtCep.getText().toString().equals("")){
                    Toast.makeText(Cadastro_Activity.this, "Digite o seu CEP.", Toast.LENGTH_SHORT).show();
                }

                if (!et.getText().toString().equals("") && !txtSenha.getText().toString().equals("")) {
                    if (txtSenha.getText().toString().equals(txtConfirmaSenha.getText().toString())){
                        verificaEmail();
                        createAccount(et.getText().toString(), txtSenha.getText().toString());
                    } else {
                        Toast.makeText(Cadastro_Activity.this, "Você precisa digitar senhas iguais.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Cadastro_Activity.this, "Preencha os campos de e-mail e senha.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }

    private void createAccount(final String email, String password) {
        // [START create_user_with_email]
        mAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    user = mAuth.getCurrentUser();
                    status = "Cadastro efetuado com sucesso.";
                    Toast.makeText(Cadastro_Activity.this, status, Toast.LENGTH_SHORT).show();
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    writeNewUser(user.getUid(), txtNome.getText().toString(), txtFone.getText().toString(), txtCep.getText().toString(), txtCidade.getText().toString());
                    Condomine_Singleton.getInstance().setTipoLogin("Firebase");
                    Condomine_Singleton.getInstance().setNome(txtNome.getText().toString());
                    Condomine_Singleton.getInstance().setEmail(user.getEmail());

                    abrirTelaPrincipal();
                } else {
                    status = task.getException().getMessage();
                    Toast.makeText(Cadastro_Activity.this, status, Toast.LENGTH_SHORT).show();
                }
            }
        });
        // [END create_user_with_email]
    }


    public void abrirTelaPrincipal(){
        Intent intentAbrirTelaPricipal = new Intent(Cadastro_Activity.this, Mural_Activity.class);
        startActivity(intentAbrirTelaPricipal);
    }

    public void verificaEmail(){

        if(!et.isEmail()){
            Toast.makeText(this, "Email inválido.", Toast.LENGTH_SHORT).show();
        }
    }

    private void writeNewUser(String userId, String nome, String telefone, String cep, String cidade) {
        fotoAvatar = "https://firebasestorage.googleapis.com/v0/b/projeto-mobile-2017-and.appspot.com/o/Fotos%2F4660?alt=media&token=cb7e12fc-35de-4479-a191-eefd0588ff36";
        Usuarios user = new Usuarios(nome, telefone, cep, cidade, fotoAvatar);
        mDatabase.child("users").child(userId).setValue(user);
    }

    public String getUriZipCode(){
        String aux = "https://viacep.com.br/ws/"+txtCep.getText()+"/json/";
        return aux;
    }

    public void setEndereco(Endereco endereco){
        setCampos(R.id.txtCidade, endereco.getLocalidade());
        bairro = endereco.getBairro();
        if (txtCidade.length()!=0) {
            logradouro = endereco.getLogradouro() + ", " + bairro.toString();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setCampos (int id, String data){
        ((EditText) findViewById(id)).setText(data);
    }

    @Override
    public void onResume() {
        super.onResume();

//        //Ativa o GPS
//        locationManager = (LocationManager)Cadastro_Activity.this.getSystemService(Context.LOCATION_SERVICE);
//
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Cadastro_Activity.this);
//            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//
//        } else {
//            Toast.makeText(Cadastro_Activity.this, "Sem autorização", Toast.LENGTH_SHORT).show();
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        //Desativa o GPS
        locationManager = (LocationManager)Cadastro_Activity.this.getSystemService(Context.LOCATION_SERVICE);
//        locationManager.removeUpdates((LocationListener) this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);

        }


        // Define zooo padrão
        float zoom = 17;

        //Define coordenadas a partir da latitude e longitude atuais
        //LatLng alvo = new LatLng(location.getLatitude(), location.getLongitude());
//        Toast.makeText(Cadastro_Activity.this, logradouro.toString(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(Cadastro_Activity.this, logradouro.toString(), Toast.LENGTH_SHORT).show();
        try {
            endereco = buscarCoordenadas(logradouro.toString());
        }
        catch (IOException e){
            Log.d("GPS", e.getMessage());
        }

        LatLng alvo = new LatLng(endereco.getLatitude(), endereco.getLongitude());

        String bairro = "";
        String cidade = "";
        String estado = "";
        String tituloMarcador = "";

        if (endereco.getSubLocality()!=null){
            bairro = endereco.getSubLocality()+", ";
        }

        if (endereco.getLocality()!=null){
            cidade = endereco.getLocality()+", ";
        }

        if (endereco.getAdminArea()!=null){
            estado = endereco.getAdminArea();
        }

        tituloMarcador = bairro + cidade + estado;

        //Posiciona um marcador nas coordenadas definidas
        mMap.addMarker(new MarkerOptions().position(alvo).title(tituloMarcador));

        //Move a câmera para as coordenadas definidas e dá um zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(alvo, zoom));
    }


    public Address buscarCoordenadas(String rua)
            throws IOException {

        Geocoder geocoder;
        Address address = null;
        List<Address> addresses;


        geocoder = new Geocoder(getApplicationContext());

        addresses = geocoder.getFromLocationName(rua, 1);

        if (addresses.size() > 0){

            address = addresses.get(0);
        }

        return address;

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void verificaPermissao() {
        if (ContextCompat.checkSelfPermission(Cadastro_Activity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissaoGPS = true;
                } else {
                    permissaoGPS = false;
                }
                return;
            }
        }
    }
}
