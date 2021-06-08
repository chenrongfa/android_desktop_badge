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
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import me.leolin.shortcutbadger.Badger;
import me.leolin.shortcutbadger.R;
import me.leolin.shortcutbadger.ShortcutBadgeException;
import me.leolin.shortcutbadger.util.BroadcastHelper;


/**
 * @author leolin
 */
//todo 原本的版本个人觉得不太合适，因为还需要显示一个空的通知？如果是这样还不如调用adaptXiaoMiBadger与应用想结合,没有显示就用原本的
public class XiaomiHomeBadger implements Badger {

    public static final String INTENT_ACTION = "android.intent.action.APPLICATION_MESSAGE_UPDATE";
    public static final String EXTRA_UPDATE_APP_COMPONENT_NAME = "android.intent.extra.update_application_component_name";
    public static final String EXTRA_UPDATE_APP_MSG_TEXT = "android.intent.extra.update_application_message_text";
    private ResolveInfo resolveInfo;
    //需要 notification 才能显示?
    @Override
    public void executeBadge(Context context, ComponentName componentName,
                             Notification notification, int badgeCount) throws ShortcutBadgeException {
       if ( !adaptXiaoMiBadger(notification,badgeCount)){
           throw new ShortcutBadgeException("不支持");
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

    /**
     *  微信方法
     * @param notification
     * @param messageCount
     * @return
     */
    public static boolean adaptXiaoMiBadger(Notification notification,int messageCount) {
        boolean success=false;
        try {
            Object newInstance = Class.forName("android.app.MiuiNotification").newInstance();
            Field declaredField = newInstance.getClass().getDeclaredField("messageCount");
            declaredField.setAccessible(true);
            declaredField.set(newInstance, Integer.valueOf(messageCount));
            notification.getClass().getField("extraNotification").set(notification, newInstance);
            success=true;
        } catch (NoSuchFieldException e2) {
            success = false;
        } catch (IllegalArgumentException e3) {
            success = false;
        } catch (IllegalAccessException e4) {
            success = false;
        } catch (ClassNotFoundException e5) {
            success = false;
        } catch (InstantiationException e6) {
            success = false;
        } catch (Exception e7) {
            success = false;
        }

        return success;
    }

}
