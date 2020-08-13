package hexin.androidbitmapcanary;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;
import android.view.View;

import static hexin.androidbitmapcanary.DrawableDetectUtil.MAX_SCALE;

/**
 * 背景图片检测
 * Created by smallnew on 2018\5\5 0005.
 */

public class BackgroundDetecotor extends Detector<View> {
    public BackgroundDetecotor() {
        super();
    }

    @Override
    public void detect(View view) {
        Drawable backGroupDrawable = view.getBackground();
        if(backGroupDrawable instanceof StateListDrawable){
            backGroupDrawable = backGroupDrawable.getCurrent();
        }
        //BitmapCanaryUtil.e("dd", "detectDrawable(background):" + backGroupDrawable);
        Bitmap bitmap = DrawableUnWrapBitmapUtil.unwrap(backGroupDrawable);
        handleBitmap(bitmap,view);
    }
}
