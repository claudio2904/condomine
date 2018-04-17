package com.example.a1631088057.condomine.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.a1631088057.condomine.Condomine_Singleton;
import com.example.a1631088057.condomine.ConfiguracaoFirebase;
import com.example.a1631088057.condomine.Endereco;
import com.example.a1631088057.condomine.R;
import com.example.a1631088057.condomine.Usuarios;
import com.example.a1631088057.condomine.ZipCodeListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by 1631088057 on 10/10/2017.
 */

public class Atualiza_Activity extends Activity {

    //    private EditText txtNome;
    private EditText txtNome, txtFone, txtCep, txtEmail, txtCidade, txtSenha, txtConfirmaSenha;
    private ImageView fotoUser;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDatabase, usersRef, usuarioRef;
    private StorageReference mStorage;
    private String status = " ";
    private Button btnImagem;
    private static final int GALLERY_INTENT = 2;
    private ProgressDialog mProgressDialog;
    private boolean fotoCarregada;
    private static final String local = "atualiza";
    private boolean estadoFoto = false;
    private String userID;
    private String downloadURL;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atualiza_activity);
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        usersRef = mDatabase.child("users");

        fotoUser = (ImageView) findViewById(R.id.imageUser);
        txtNome  = (EditText) findViewById(R.id.txtNome);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtFone  = (EditText) findViewById(R.id.txtFone);
        txtCep   = (EditText) findViewById(R.id.txtCEP);
        txtCidade   = (EditText) findViewById(R.id.txtCidade);
        txtSenha = (EditText) findViewById(R.id.txtSenha);
        txtConfirmaSenha = (EditText) findViewById(R.id.txtConfirmaSenha);
        txtCep.addTextChangedListener(new ZipCodeListener(this, local));
        btnImagem = (Button) findViewById(R.id.btnImagem);

        txtEmail.setText(mAuth.getCurrentUser().getEmail().toString());
        userID = mAuth.getCurrentUser().getUid().toString();
        if (!Condomine_Singleton.getInstance().getFoto().isEmpty()){
            Glide.with(Atualiza_Activity.this).load(Condomine_Singleton.getInstance().getFoto().toString()).into(fotoUser);
        }
        usuarioRef = usersRef.child(userID);

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
//        usuarioRef.child("nomeUsuario").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                String value = dataSnapshot.get
                Usuarios usuario = dataSnapshot.getValue(Usuarios.class);
                txtNome.setText(usuario.getNomeUsuario());
                txtCep.setText(usuario.getCep());
                txtCidade.setText(usuario.getCidade());
                txtFone.setText(usuario.getTelefone());
                downloadURL = usuario.getFotoURL();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Atualiza_Activity.this, "Erro ao recuperar dados.", Toast.LENGTH_SHORT).show();
            }
        });

        mProgressDialog = new ProgressDialog(this);

        btnImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });


        Button b = (Button) findViewById(R.id.btnCancela);
        b.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        Button atualizar = (Button) findViewById(R.id.btnOK);
        atualizar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (txtNome.getText().toString().equals("")){
                    Toast.makeText(Atualiza_Activity.this, "Digite o seu nome.", Toast.LENGTH_SHORT).show();
                } else if (txtFone.getText().toString().equals("")){
                    Toast.makeText(Atualiza_Activity.this, "Digite o número do seu telefone.", Toast.LENGTH_SHORT).show();
                } else if (txtCep.getText().toString().equals("")){
                    Toast.makeText(Atualiza_Activity.this, "Digite o seu CEP.", Toast.LENGTH_SHORT).show();
                } else {

                    if (txtSenha.getText().length() > 0) {
                        if (txtSenha.getText().toString().equals(txtConfirmaSenha.getText().toString())) {
                            user = mAuth.getCurrentUser();
                            user.updatePassword(txtSenha.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                updateAccount(txtEmail.getText().toString(), txtSenha.getText().toString(), txtNome.getText().toString(), txtCep.getText().toString(), txtFone.getText().toString(), txtCidade.getText().toString(), downloadURL.toString());
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(Atualiza_Activity.this, "Digite senhas iguais!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        updateAccount(txtEmail.getText().toString(), txtSenha.getText().toString(), txtNome.getText().toString(), txtCep.getText().toString(), txtFone.getText().toString(), txtCidade.getText().toString(), downloadURL.toString());
                    }
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

    private void updateAccount(final String email, String password, String nome, String cep, String fone, String cidade, String url) {
        // [START create_user_with_email]
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        updateUser(user.getUid(), nome, fone, cep, cidade, url);
        Condomine_Singleton.getInstance().setTipoLogin("Firebase");
        Condomine_Singleton.getInstance().setNome(txtNome.getText().toString());
        Condomine_Singleton.getInstance().setEmail(user.getEmail());
        abrirTelaPrincipal();
    }


    public void abrirTelaPrincipal(){
        Intent intentAbrirTelaPricipal = new Intent(Atualiza_Activity.this, Mural_Activity.class);
        startActivity(intentAbrirTelaPricipal);
    }


    private void updateUser(String userId, String nome, String telefone, String cep, String cidade, String urlFoto) {
        Usuarios usuarioAtualizado = new Usuarios(nome, telefone, cep, cidade, urlFoto);
        mDatabase.child("users").child(userId).setValue(usuarioAtualizado);
        Condomine_Singleton.getInstance().setFoto(urlFoto);
    }

    public String getUriZipCode(){
        String aux = "https://viacep.com.br/ws/"+txtCep.getText()+"/json/";
        return aux;
    }

    public void setEndereco(Endereco endereco){
        setCampos(R.id.txtCidade, endereco.getLocalidade());

    }

    private void setCampos (int id, String data){
        ((EditText) findViewById(id)).setText(data);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            mProgressDialog.setMessage("Carregando...");
            mProgressDialog.show();
            Uri uri = data.getData();
            StorageReference filepath = mStorage.child("Fotos").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(Atualiza_Activity.this, "Imagem carregada com sucesso!", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    downloadURL = taskSnapshot.getDownloadUrl().toString();
                    Toast.makeText(Atualiza_Activity.this, downloadURL , Toast.LENGTH_SHORT).show();

                    Glide.with(Atualiza_Activity.this).load(downloadURL.toString()).into(fotoUser);

                }
            });
        }
    }
}
