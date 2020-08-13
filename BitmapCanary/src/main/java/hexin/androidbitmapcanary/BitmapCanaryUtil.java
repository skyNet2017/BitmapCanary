package hexin.androidbitmapcanary;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

public class BitmapCanaryUtil {

    public static boolean debug = false;
    public static void init(Application application){
        ActivityDrawableWatcher.watchDrawable(application);
    }

     static void w(String tag,String msg){
        if(debug){
            Log.i(tag,msg);
        }
    }
   public static void e(String tag,String msg){
        if(debug){
            Log.w(tag,msg);
        }
    }

    public static void addDetect(Detector detector){
        DetectorFactory.addDetect(detector);
    }

    public static void showBitmapList(Activity activity){
        BitmapListUtil.start(activity);
    }
}
