package me.leolin.shortcutbadger.impl;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import me.leolin.shortcutbadger.Badger;
import me.leolin.shortcutbadger.R;
import me.leolin.shortcutbadger.ShortcutBadgeException;
import me.leolin.shortcutbadger.util.BroadcastHelper;


/**
 * @author leolin
 */
@Deprecated
public class XiaomiHomeBadger implements Badger {

    public static final String INTENT_ACTION = "android.intent.action.APPLICATION_MESSAGE_UPDATE";
    public static final String EXTRA_UPDATE_APP_COMPONENT_NAME = "android.intent.extra.update_application_component_name";
    public static final String EXTRA_UPDATE_APP_MSG_TEXT = "android.intent.extra.update_application_message_text";
    private ResolveInfo resolveInfo;

    @Override
    public void executeBadge(Context context, ComponentName componentName, int badgeCount) throws ShortcutBadgeException {
        try {
            Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");
            Object miuiNotification = miuiNotificationClass.newInstance();
            Field field = miuiNotification.getClass().getDeclaredField("messageCount");
            field.setAccessible(true);
            try {
                field.set(miuiNotification, badgeCount == 0 ? "" : badgeCount);
            } catch (Exception e) {
                field.set(miuiNotification, badgeCount);
            }
        } catch (Exception e) {
            Intent localIntent = new Intent(
                    INTENT_ACTION);
            localIntent.putExtra(EXTRA_UPDATE_APP_COMPONENT_NAME, componentName.getPackageName() + "/" + componentName.getClassName());
            localIntent.putExtra(EXTRA_UPDATE_APP_MSG_TEXT, String.valueOf(badgeCount == 0 ? "" : badgeCount));

            try {
                BroadcastHelper.sendIntentExplicitly(context, localIntent);
            } catch (ShortcutBadgeException ignored) {}

        }

        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
            tryNewMiuiBadge(context, badgeCount);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void tryNewMiuiBadge(Context context, int badgeCount) throws ShortcutBadgeException {
        if (resolveInfo == null) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            resolveInfo = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        }

        if (resolveInfo != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // 8.0之后添加角标需要NotificationChannel
              NotificationManager notificationManager=
                      (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel channel = new NotificationChannel("badge", "badge",
                        NotificationManager.IMPORTANCE_DEFAULT);
           //    channel.setShowBadge(true);
                notificationManager.createNotificationChannel(channel);
            }
            NotificationManager mNotificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"badge")
                    .setContentTitle("")
                    .setContentText("")

                    .setSmallIcon(R.drawable.ic_launcher);
            Notification notification = builder.build();
            try {
                Field field = notification.getClass().getDeclaredField("extraNotification");
                Object extraNotification = field.get(notification);
                Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
                method.invoke(extraNotification, badgeCount);
              mNotificationManager.notify(10, notification);
            } catch (Exception e) {
                throw new ShortcutBadgeException("not able to set badge", e);
            }
        }
    }

    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList(
                "com.miui.miuilite",
                "com.miui.home",
                "com.miui.miuihome",
                "com.miui.miuihome2",
                "com.miui.mihome",
                "com.miui.mihome2",
                "com.i.miui.launcher"
        );
    }
}
