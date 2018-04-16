package com.example.a1631088057.condomine;

/**
 * Created by ander on 02/04/2018.
 */

public class Condomine_Singleton {
    private static final Condomine_Singleton ourInstance = new Condomine_Singleton();

    public static Condomine_Singleton getInstance() {
        return ourInstance;
    }
    public String tipoLogin = "Condomine";
    public String nome, email, foto;

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipoLogin() {
        return tipoLogin;
    }

    public void setTipoLogin(String tipoLogin) {
        this.tipoLogin = tipoLogin;
    }

    private Condomine_Singleton() {

    }
}
