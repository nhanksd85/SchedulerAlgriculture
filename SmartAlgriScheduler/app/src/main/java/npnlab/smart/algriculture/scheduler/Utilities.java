package npnlab.smart.algriculture.scheduler;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class Utilities {
    public static Bitmap convertBase64ToBitmap(String imageString){
        byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return decodedImage;
    }
    public static String convertBitmapToBase64(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return imageString;
    }

    public static void saveKey(Activity activity, String key, String value) {
        if (key.isEmpty()) return;
        SharedPreferences settings = activity.getSharedPreferences("Scheduler", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String loadKey(Activity activity, String key) {
        SharedPreferences settings = activity.getSharedPreferences("Scheduler", Context.MODE_PRIVATE);
        String randomString = "[{\"cycle\":15,\"flow1\":20,\"flow2\":20,\"flow3\":20,\"schedulerName\":\"LỊCH TƯỚI 1\",\"startTime\":\"7:15\",\"stopTime\":\"7:45\"},{\"cycle\":15,\"flow1\":20,\"flow2\":20,\"flow3\":20,\"schedulerName\":\"LỊCH TƯỚI 2\",\"startTime\":\"8:15\",\"stopTime\":\"8:45\"}]";
        return settings.getString(key, randomString);
    }
}
