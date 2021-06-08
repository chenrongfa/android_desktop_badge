package me.leolin.shortcutbadger.example;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import me.leolin.shortcutbadger.ShortcutBadger;
import me.leolin.shortcutbadger.util.RomUtil;


public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText numInput = findViewById(R.id.numInput);

        Button button = findViewById(R.id.btnSetBadge);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int badgeCount = 0;
                try {
                    badgeCount = Integer.parseInt(numInput.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Error input", Toast.LENGTH_SHORT).show();
                }
                Notification notification=null;
                if (RomUtil.isMiui()){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // 8.0之后添加角标需要NotificationChannel
                        NotificationManager notificationManager=
                                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                        NotificationChannel channel = new NotificationChannel("badge", "badge",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        //    channel.setShowBadge(true);
                        notificationManager.createNotificationChannel(channel);
                    }

                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(MainActivity.this,
                            "badge")
                            .setContentTitle("你的应用消息")
                            .setContentText("小米设备要消息内容与你的app结合")

                            .setSmallIcon(R.drawable.ic_launcher);
                    notification = builder.build();
                }
                boolean success = ShortcutBadger.applyCount(MainActivity.this,notification,
                        badgeCount);
                if (RomUtil.isMiui()&&notification!=null){
                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(10,notification);
                }
                Toast.makeText(getApplicationContext(), "Set count=" + badgeCount + ", success=" + success, Toast.LENGTH_SHORT).show();
            }
        });

        Button launchNotification = findViewById(R.id.btnSetBadgeByNotification);
        launchNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int badgeCount = 0;
                try {
                    badgeCount = Integer.parseInt(numInput.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Error input", Toast.LENGTH_SHORT).show();
                }

                finish();
                startService(
                    new Intent(MainActivity.this, BadgeIntentService.class).putExtra("badgeCount", badgeCount)
                );
            }
        });

        Button removeBadgeBtn = findViewById(R.id.btnRemoveBadge);
        removeBadgeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean success = ShortcutBadger.removeCount(MainActivity.this);

                Toast.makeText(getApplicationContext(), "success=" + success, Toast.LENGTH_SHORT).show();
            }
        });


        //find the home launcher Package
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        String currentHomePackage = "none";
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

        // in case of duplicate apps (Xiaomi), calling resolveActivity from one will return null
        if (resolveInfo != null) {
            currentHomePackage = resolveInfo.activityInfo.packageName;
        }

        TextView textViewHomePackage = findViewById(R.id.textViewHomePackage);
        textViewHomePackage.setText("launcher:" + currentHomePackage);
    }


    public void show_app_dialog(View view) {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(getApplicationContext());
        AlertDialog alertDialog1 = alertDialog.create();
        Window window = alertDialog1.getWindow();
        window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG);
        alertDialog1.setContentView(R.layout.dialog1);
        alertDialog1.show();


    }

    public void start_activity(View view) {
        startActivity(new Intent(this,testActivity.class));
    }
}
