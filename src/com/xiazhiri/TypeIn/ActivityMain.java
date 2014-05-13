package com.xiazhiri.TypeIn;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;

public class ActivityMain extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        NotificationManager notiManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification();
        notification.icon = R.drawable.ic_launcher;
        notification.tickerText = "Hello";
        notification.defaults = Notification.DEFAULT_SOUND;
        notification.audioStreamType = AudioManager.ADJUST_LOWER;
        Intent intent = new Intent(this, ActivityMain.class);
        PendingIntent pendIntent = PendingIntent.getActivity(this, 0, intent, 0);
        notification.setLatestEventInfo(this, "点击查看", "点击查看详细内容", pendIntent);
        notiManager.notify(1,notification);
    }
}
