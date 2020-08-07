package smallnew.bitmapcanary;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import hexin.androidbitmapcanary.ActivityDrawableWatcher;

/**
 * Created by smallnew on 2018/5/3.
 */

public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActivityDrawableWatcher.watchDrawable(this);
        Fresco.initialize(this);
    }
}
