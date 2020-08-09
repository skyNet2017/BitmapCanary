package smallnew.bitmapcanary;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;

import de.robv.android.xposed.DexposedBridge;
import de.robv.android.xposed.XC_MethodHook;
import hexin.androidbitmapcanary.ActivityDrawableWatcher;

import static de.robv.android.xposed.XposedBridge.TAG;

/**
 * Created by smallnew on 2018/5/3.
 */

public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActivityDrawableWatcher.watchDrawable(this);
        Fresco.initialize(this,ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .build());
        hook();
    }

    private void hook() {
        DexposedBridge.hookAllConstructors(CloseableStaticBitmap.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                CloseableStaticBitmap thread = (CloseableStaticBitmap) param.thisObject;
                Log.d(TAG, "CloseableStaticBitmap: " + thread);
                // bitmap: 960x600 obj:com.facebook.imagepipeline.image.CloseableStaticBitmap@ebb5bb2 is created.

                //downsample+resize:
                // bitmap: 240x150 obj:com.facebook.imagepipeline.image.CloseableStaticBitmap@ebb5bb2 is created.
                Bitmap bitmap = thread.getUnderlyingBitmap();
                if(bitmap != null){
                    //BitmapListUtil.add(bitmap);
                    Log.d(TAG, "bitmap: " + bitmap.getWidth()+"x"+bitmap.getHeight() + " obj:" + thread +  " is created.");
                }else {

                }
            }
        });

    }
}
