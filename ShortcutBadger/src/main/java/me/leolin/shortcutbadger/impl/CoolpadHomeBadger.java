package me.leolin.shortcutbadger.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.util.Arrays;
import java.util.List;

import me.leolin.shortcutbadger.Badger;
import me.leolin.shortcutbadger.ShortcutBadgeException;
import me.leolin.shortcutbadger.util.BroadcastHelper;

/**
 * @author Gernot Pansy
 */
public class CoolpadHomeBadger implements Badger {



    @Override
    public void executeBadge(Context context, ComponentName componentName, int badgeCount) throws ShortcutBadgeException {
        Intent intent = new Intent("yulong.intent.action.SHOW_NUM_CHANGED");
        intent.putExtra("packageName", context.getPackageName());
        intent.putExtra("className", componentName.getClassName());
        intent.putExtra("showNum", badgeCount);
        context.sendBroadcast(intent);

    }

    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList(
                "com.yulong.android.launcher3"

        );
    }
}
