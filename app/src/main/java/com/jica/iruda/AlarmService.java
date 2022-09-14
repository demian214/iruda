package com.jica.iruda;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.jica.iruda.activities.HabitDetailActivity;
import com.jica.iruda.model.Habit;
import com.jica.iruda.utilities.Constants;

public class AlarmService extends Service {

    public AlarmService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null){
            return Service.START_STICKY;
        } else {
            processCommand(intent);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void processCommand(Intent intent){

        Intent newIntent = new Intent(this, HabitDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, HabitDetailActivity.class), 0);

        int notificationId = 45;
        Habit habit = (Habit) intent.getSerializableExtra(Constants.KEY_HABIT);
        if (habit != null){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.CHANNEL_ID)
                        .setContentTitle(habit.getTitle())
                        .setContentText(habit.getContent())
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setVibrate(new long[]{1, 1000})
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                createNotificationChannel();

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
                notificationManagerCompat.notify(notificationId, builder.build());

                builder.setContentIntent(pendingIntent);
            }


        }
    }

    private void createNotificationChannel() {
            // 채널 생성
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null, null);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManagerCompat notificationManagerCompat = getSystemService(NotificationManagerCompat.class);
            notificationManagerCompat.createNotificationChannel(channel);
        }
    }

}
