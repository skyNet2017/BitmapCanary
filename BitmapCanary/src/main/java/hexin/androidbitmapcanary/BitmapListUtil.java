package hexin.androidbitmapcanary;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BitmapListUtil {
    static HashMap<Integer,WeakReference<Bitmap>> map = new HashMap<>();

    public static void start(Activity activity){
        ImgMemoryActivity.start(activity);
    }

    public static void add(Bitmap bitmap){
        BitmapCanaryUtil.w("BitmapList","add:"+bitmap.hashCode()+","+getInfo(bitmap));
        map.put(bitmap.hashCode(),new WeakReference<Bitmap>(bitmap));
    }

    public static List<Bitmap> getList(){
        List<Bitmap> bitmaps = new ArrayList<>();
        BitmapCanaryUtil.w("BitmapList","size:"+map.size());
        for (Map.Entry<Integer, WeakReference<Bitmap>> entry : map.entrySet()) {
            WeakReference<Bitmap> weakReference = entry.getValue();
            if(weakReference != null && weakReference.get() != null && !weakReference.get().isRecycled()){
                if(!bitmaps.contains(weakReference.get())){
                    bitmaps.add(weakReference.get());
                }
            }
        }
        if(!bitmaps.isEmpty()){
            Collections.sort(bitmaps, new Comparator<Bitmap>() {
                @Override
                public int compare(Bitmap o1, Bitmap o2) {
                    return (int) (getSize(o2) - getSize(o1));
                }
            });
        }
        //另外一个方式,直接从glide的bitmappool中取:
        return bitmaps;
    }

    static long getSize(Bitmap bitmap){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        }else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    static String formatFileSize(long size) {
        try {
            DecimalFormat dff = new DecimalFormat(".00");
            if (size >= 1024 * 1024) {
                double doubleValue = ((double) size) / (1024 * 1024);
                String value = dff.format(doubleValue);
                return value + "MB";
            } else if (size > 1024) {
                double doubleValue = ((double) size) / 1024;
                String value = dff.format(doubleValue);
                return value + "KB";
            } else {
                return size + "B";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(size);
    }

    static String getInfo(Bitmap bitmap){
        StringBuilder builder = new StringBuilder();
        builder.append(bitmap.getWidth()).append("x").append(bitmap.getHeight())
                .append(",内存占用:").append(formatFileSize(getSize(bitmap))).append(",config:")
                .append(bitmap.getConfig().name()).append("\n").append(bitmap);

        return builder.toString();
    }
}
