package me.leolin.shortcutbadger.impl;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.util.Arrays;
import java.util.List;

import me.leolin.shortcutbadger.Badger;
import me.leolin.shortcutbadger.ShortcutBadgeException;

/**
 * @author Gernot Pansy
 */
public class Qihoo360HomeBadger implements Badger {



    @Override
    public void executeBadge(Context context, ComponentName componentName,
                             Notification notification, int badgeCount) throws ShortcutBadgeException {
        Intent intent = new Intent("com.qihoo360.launcher.action.APP_ICON_NOTIFICATION_COUNT");
        intent.putExtra("package_name", componentName.getPackageName());
        intent.putExtra("class_name", componentName.getClassName());
        intent.putExtra("notification_count", badgeCount);
        context.sendBroadcast(intent);


    }

    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList(
                "com.qihoo360.launcher"

        );
    }
}
