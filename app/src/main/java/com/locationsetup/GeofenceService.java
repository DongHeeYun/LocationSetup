package com.locationsetup;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class GeofenceService extends IntentService {
    protected static final String TAG = "geofence-service";
    public GeofenceService() {
        super(TAG);
    }

/*    @Override
      public void onCreate() {
        super.onCreate();
    }
 */
    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "알수 없는 에러가 발생하였습니다.");
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            //geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            LocationItem geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );

            sendNotification(geofenceTransitionDetails);
            //Log.i(TAG, geofenceTransitionDetails);
        } else { // 위에 언급된 상수이외 다른 상수는 제공하지 않는다.
            Log.e(TAG, "잘못된 메시지입니다.");
        }
    }

    private LocationItem getGeofenceTransitionDetails(Context context, int geofenceTransition, List<Geofence> triggeringGeofences) {
        String message;
        switch (geofenceTransition) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    message = "영역에 들어왔습니다.";
                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    message = "영역밖으로 나갔습니다.";
                default:
                    message = "이미 영역내 들어왔습니다.";
        }

        for (Geofence geofence : triggeringGeofences) {
            for (LocationItem item : FileManager.items) {
                if (geofence.getRequestId().equals(item.getId())
                        || geofence.getRequestId().equals(item.getName())) {
                    return item;
                }
            }
        }
        return null;
    }

    private void sendNotification(LocationItem item) {
        if (item == null || !item.isEnabled()) return;
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), ApplyActivity.class);
        notificationIntent.putExtra("item", item);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ApplyActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(R.drawable.ic_noti)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                                        R.drawable.ic_noti))
                .setContentTitle("'" + item.getName() + "' 진입 감지")
                .setContentText("'" + item.getName() + "' 설정으로 변경하기가 준비되었습니다.")
                .setContentIntent(notificationPendingIntent);

        builder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }
}
