package com.pichillilorenzo.flutter_inappwebview;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.*;

public abstract class RequestPermissionHandler implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1;
    public static int REQUEST_CODE_CAMERA = 2;
    public static int REQUEST_CODE_ACCESS_FINE_LOCATION = 3;

    private static Map<Integer, List<Runnable>> actionDictionary = new HashMap<>();

    public static void checkAndRun(Activity activity, String permission, int requestCode, Runnable runnable) {

        int permissionCheck = ContextCompat.checkSelfPermission(activity.getApplicationContext(), permission);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            List<Runnable> list = actionDictionary.get(requestCode);
            if (null != list) {
                list.add(runnable);
            } else {
                list = new ArrayList<>();
                list.add(runnable);
                actionDictionary.put(requestCode, list);
                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            }
        } else
            runnable.run();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            List<Runnable> callbacks = actionDictionary.get(requestCode);
            if(null == callbacks){
                return;
            }
            Iterator<Runnable> iterator = callbacks.iterator();
            while (iterator.hasNext()) {
                Runnable runnable = iterator.next();
                runnable.run();
                iterator.remove();
            }
        }
    }

    public static void onCustomRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            List<Runnable> callbacks = actionDictionary.get(requestCode);
            if(null == callbacks){
                return;
            }
            Iterator<Runnable> iterator = callbacks.iterator();
            while (iterator.hasNext()) {
                Runnable runnable = iterator.next();
                runnable.run();
                iterator.remove();
            }
        }
    }

}
