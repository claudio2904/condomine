package com.example.a1631088057.condomine;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Anderson on 10/04/2018.
 */

public class EmailText extends android.support.v7.widget.AppCompatEditText {

    public EmailText(Context context) {
        super(context);
    }

    public EmailText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isEmail (){
        String expressaoRegular = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+$";
        String email = getText().toString();

        return (email.matches(expressaoRegular));
    }



}