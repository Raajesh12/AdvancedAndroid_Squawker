package android.example.com.squawker.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.example.com.squawker.MainActivity;
import android.example.com.squawker.R;
import android.example.com.squawker.provider.SquawkContract;
import android.example.com.squawker.provider.SquawkProvider;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class SquawkMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> map = remoteMessage.getData();
        String author = map.get(SquawkContract.COLUMN_AUTHOR);
        String authorKey = map.get(SquawkContract.COLUMN_AUTHOR_KEY);
        String message = map.get(SquawkContract.COLUMN_MESSAGE);
        String date = map.get(SquawkContract.COLUMN_DATE);

        ContentValues newMessage = new ContentValues();
        newMessage.put(SquawkContract.COLUMN_AUTHOR, author);
        newMessage.put(SquawkContract.COLUMN_MESSAGE, map.get(SquawkContract.COLUMN_MESSAGE));
        newMessage.put(SquawkContract.COLUMN_DATE, date);
        newMessage.put(SquawkContract.COLUMN_AUTHOR_KEY, authorKey);
        getContentResolver().insert(SquawkProvider.SquawkMessages.CONTENT_URI, newMessage);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        if(message.length() > 30) {
            message = message.substring(0, 30) + "\u2026";
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_duck)
                .setContentTitle(String.format(getString(R.string.notification_message), author))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
