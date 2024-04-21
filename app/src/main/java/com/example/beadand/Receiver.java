package com.example.beadand;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

public class Receiver extends BroadcastReceiver
{
    private static final String CHANNEL_ID = "The Note App";
    private static final String CHANNEL_NAME = "My Channel";
    private static final String CHANNEL_DESCRIPTION = "Reminders for notes you took";

    @Override
    public void onReceive(Context context, Intent intent) {
        //String note = intent.getStringExtra("note");
        Note note = intent.getParcelableExtra("note");
        // Create the notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(note.title)
                .setContentText(note.content)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent mainIntent = new Intent(context,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.putExtra("note",note);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int)note.id, mainIntent, PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if(notificationChannel==null)
            {
                notificationChannel = new NotificationChannel(CHANNEL_ID,CHANNEL_DESCRIPTION, NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription(CHANNEL_DESCRIPTION);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.BLUE);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        notificationManager.notify((int)note.id, builder.build());
    }
}
