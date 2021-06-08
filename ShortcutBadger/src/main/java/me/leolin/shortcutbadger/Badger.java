package me.leolin.shortcutbadger;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;

import java.util.List;

public interface Badger {

    /**
     * Called when user attempts to update notification count
     * @param context Caller context
     * @param componentName Component containing package and class name of calling application's
     *                      launcher activity
     * @notification 给xiaomi 开后门
     * @param badgeCount Desired notification count
     *
     * @throws ShortcutBadgeException
     */
    //void executeBadge(Context context, ComponentName componentName, int badgeCount) throws
    // ShortcutBadgeException;
    void executeBadge(Context context, ComponentName componentName,
                      Notification notification, int badgeCount) throws ShortcutBadgeException;

    /**
     * Called to let {@link ShortcutBadger} knows which launchers are supported by this badger. It should return a
     * @return List containing supported launchers package names
     */
    List<String> getSupportLaunchers();
}
