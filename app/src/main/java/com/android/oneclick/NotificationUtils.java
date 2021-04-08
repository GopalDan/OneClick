package com.android.oneclick;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.core.util.Pair;


public class NotificationUtils {

    public static final int NOTIFICATION_ID = 1337;
    private static final String NOTIFICATION_CHANNEL_ID = "com.mtsahakis.mediaprojectiondemo.app";
    private static final String NOTIFICATION_CHANNEL_NAME = "com.mtsahakis.mediaprojectiondemo.app";
    private static final String TAG = "NotificationUtils" ;

    public static Pair<Integer, Notification> getNotification(@NonNull Context context) {
        createNotificationChannel(context);
        Notification notification = createNotification(context, "", null);
        NotificationManager notificationManager
                = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
        return new Pair<>(NOTIFICATION_ID, notification);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static void createNotificationChannel(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    private static Notification createNotification(@NonNull Context context, String imageCount, Bitmap bitmap) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_camera);
        builder.setContentTitle("Screenshot captured");
        builder.setContentText("Tap here to view it.");//context.getString(R.string.recording)
        builder.setOngoing(false);
        builder.setCategory(Notification.CATEGORY_SERVICE);
        builder.setPriority(Notification.PRIORITY_LOW);
        builder.setShowWhen(true);
        builder.setLargeIcon(bitmap);//getBitmap(context)
        builder.setContentIntent(getIntent(context, imageCount));
        builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));
//        builder.addAction(openTakenScreenshot(context, imageCount));
        return builder.build();
    }

    private static Bitmap getBitmap(Context context) {
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_home_screen); //ic_launcher
        return bm;
    }

    public static void getNotificationToNotify(@NonNull Context context, String imageCount, Bitmap bitmap) {
        createNotificationChannel(context);
        Notification notification = createNotification(context, imageCount, bitmap);
        NotificationManager notificationManager
                = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public static NotificationCompat.Action openTakenScreenshot(Context context, String imagePath){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(imagePath), "image/*");
//        context.startActivity(intent);

         PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
         return new NotificationCompat.Action.Builder(R.drawable.ic_camera, "SeePreview" , contentIntent).build();

    }

    private static PendingIntent getIntent(Context context, String imagePath){
        Log.e(TAG, "imagepath: " + imagePath);
       /* Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(imagePath), "image/*");
//        context.startActivity(intent);//Uri.fromFile(new File(filePath))*/

        File file =  new File(imagePath);
        final Intent intent = new Intent(Intent.ACTION_VIEW)//
                .setDataAndType(FileProvider.getUriForFile(context, "com.android.oneclick" + ".provider", file),
                        "image/*").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
        return contentIntent;
    }

    public static void setNotification(Context context, String message){

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_camera);
        builder.setContentTitle("ShakeDetection");
        builder.setContentText("service state: " + message);//context.getString(R.string.recording)
        builder.setOngoing(false);
        builder.setCategory(Notification.CATEGORY_SERVICE);
        builder.setPriority(Notification.PRIORITY_LOW);
        builder.setShowWhen(true);
//        builder.setLargeIcon(bitmap);//getBitmap(context)
//        builder.setContentIntent(getIntent(context, imageCount));
//        builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));
//        builder.addAction(openTakenScreenshot(context, imageCount));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification =  builder.build();

        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
