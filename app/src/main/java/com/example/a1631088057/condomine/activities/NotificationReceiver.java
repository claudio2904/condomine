package com.example.a1631088057.condomine.activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;

import com.example.a1631088057.condomine.activities.Repeating_Activity;

/**
 * Created by ander on 16/04/2018.
 */

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent repeating_intent = new Intent(context, Repeating_Activity.class);

        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pi = PendingIntent.getActivity(context,100, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pi).setSmallIcon(android.R.drawable.arrow_up_float)
                .setContentTitle("Vencimento Taxa Condomínio")
                .setContentText("Dia de pagar o boleto!")
                .setAutoCancel(true);

        nm.notify(100,builder.build());

    }

}
