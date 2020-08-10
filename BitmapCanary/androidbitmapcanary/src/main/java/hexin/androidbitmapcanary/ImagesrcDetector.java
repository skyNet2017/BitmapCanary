package hexin.androidbitmapcanary;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;
import android.widget.ImageView;

import static hexin.androidbitmapcanary.DrawableDetectUtil.MAX_SCALE;

/**
 * 图片检测
 * Created by smallnew on 2018\5\5 0005.
 */

public class ImagesrcDetector extends Detector<ImageView>{
    public ImagesrcDetector() {
    }

    @Override
    public void detect(ImageView imageView) {
        Drawable srcDrawable = imageView.getDrawable();

        if(srcDrawable instanceof StateListDrawable){
            srcDrawable = srcDrawable.getCurrent();
        }
        Log.e("dd", "detectDrawable:" + srcDrawable);
        Bitmap bitmap = DrawableUnWrapBitmapUtil.unwrap(srcDrawable);
        handleBitmap(bitmap,imageView);
    }


}
