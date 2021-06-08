package me.leolin.shortcutbadger.impl;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.os.UserManager;

import java.util.Arrays;
import java.util.List;

import me.leolin.shortcutbadger.Badger;
import me.leolin.shortcutbadger.ShortcutBadgeException;

/**
 * @author Gernot Pansy
 */
public class QikuHomeBadger implements Badger {

    public static final Uri f7077a = Uri.parse("content://com.qiku.android.launcher3.unread/bubbletip");

    /* renamed from: b  reason: collision with root package name */
    private Boolean mSupportUnread;



    @Override
    public void executeBadge(Context context, ComponentName componentName,
                             Notification notification, int badgeCount) throws ShortcutBadgeException {

        if (a(context)){
            c(context,componentName,badgeCount);
        }else{
            Intent intent = new Intent("qiku.intent.action.SHOW_NUM_CHANGED");
            intent.putExtra("packageName", context.getPackageName());
            intent.putExtra("className", componentName.getClassName());
            intent.putExtra("showNum", badgeCount);
            context.sendBroadcast(intent);
        }

    }

    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList(
                "com.qiku.android.launcher3"

        );
    }

    private void c(Context context, ComponentName componentName, int i2) {
        if (Build.VERSION.SDK_INT >= 17) {
            String packageName = componentName.getPackageName();
            String className = componentName.getClassName();
            @SuppressLint("WrongConstant") long serialNumberForUser =
                    ((UserManager) context.getSystemService("user")).getSerialNumberForUser(Process.myUserHandle());
            ContentResolver contentResolver = context.getContentResolver();
            Cursor query = contentResolver.query(f7077a, (String[]) null, "pkgName=? and className=? and profileId=?", new String[]{packageName, className, String.valueOf(serialNumberForUser)}, (String) null);
            if (query != null) {
                if (query.getCount() == 0) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("pkgName", packageName);
                    contentValues.put("className", className);
                    contentValues.put("profileId", Long.valueOf(serialNumberForUser));
                    contentValues.put("showNum", Integer.valueOf(i2));
                    contentResolver.insert(f7077a, contentValues);
                } else {
                    ContentValues contentValues2 = new ContentValues();
                    contentValues2.put("showNum", Integer.valueOf(i2));
                    contentResolver.update(f7077a, contentValues2, "pkgName=? and className=? and profileId=?", new String[]{packageName, className, String.valueOf(serialNumberForUser)});
                }
                query.close();
            }
        }
    }

    private boolean a(Context context) {
        if (this.mSupportUnread == null) {
            synchronized (QikuHomeBadger.class) {
                if (this.mSupportUnread == null) {
                    boolean z = false;
                    if (context.getPackageManager().resolveContentProvider("com.qiku.android.launcher3.unread", 0) != null) {
                        z = true;
                    }
                    this.mSupportUnread = Boolean.valueOf(z);
                }
            }
        }
        return this.mSupportUnread.booleanValue();
    }

}
