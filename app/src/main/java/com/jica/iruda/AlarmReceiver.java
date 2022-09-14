package com.jica.iruda;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.jica.iruda.model.Habit;
import com.jica.iruda.utilities.Constants;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null){
            Habit habit = (Habit) intent.getSerializableExtra(Constants.KEY_HABIT);
            if (habit != null){
                Log.d(Constants.TAG, "브로드캐스트리시버를 통해 받은 해빗:" + habit.getId());
            }
            Intent serviceIntent = new Intent(context, AlarmService.class);
            serviceIntent.putExtra(Constants.KEY_HABIT, habit);

            // Oreo(26) 버전 이후부터는 Background 에서 실행을 금지하기 때문에 ForeGround 에서 실행해야 함
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        }
    }
}
