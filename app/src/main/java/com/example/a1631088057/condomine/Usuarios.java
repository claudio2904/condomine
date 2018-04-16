package com.example.a1631088057.condomine;

import android.net.Uri;

/**
 * Created by ander on 14/03/2018.
 */

public class Usuarios {
    private String id;
    private String login;
    private String nomeUsuario;
    private String password;
    private String telefone;
    private String cep;
    private String cidade;
    private String fotoURL;

    public Usuarios() {
    }


    public Usuarios(String nomeUsuario, String telefone, String cep, String cidade){
        this.nomeUsuario = nomeUsuario;
        this.telefone = telefone;
        this.cep = cep;
        this.cidade = cidade;
    }

    public Usuarios(String nomeUsuario, String telefone, String cep, String cidade, String fotoURL){
        this.nomeUsuario = nomeUsuario;
        this.telefone = telefone;
        this.cep = cep;
        this.cidade = cidade;
        this.fotoURL = fotoURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getCep() { return cep; }

    public void setCep(String cep) { this.cep = cep; }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getFotoURL() {
        return fotoURL;
    }

    public void setFotoURL(String fotoURL) {
        this.fotoURL = fotoURL;
    }
}

