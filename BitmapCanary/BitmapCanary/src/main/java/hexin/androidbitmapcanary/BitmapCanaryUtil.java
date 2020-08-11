package hexin.androidbitmapcanary;

import android.app.Activity;
import android.app.Application;

public class BitmapCanaryUtil {
    public static void init(Application application){
        ActivityDrawableWatcher.watchDrawable(application);
    }

    public static void addDetect(Detector detector){
        DetectorFactory.addDetect(detector);
    }

    public static void showBitmapList(Activity activity){
        BitmapListUtil.start(activity);
    }
}
