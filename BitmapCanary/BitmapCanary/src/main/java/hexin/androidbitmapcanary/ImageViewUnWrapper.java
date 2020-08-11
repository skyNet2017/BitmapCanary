package hexin.androidbitmapcanary;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;
import android.widget.ImageView;

public class ImageViewUnWrapper implements UnwrapBitmapFromView<ImageView> {
    @Override
    public Bitmap unWrap(ImageView imageView) {
        Drawable srcDrawable = imageView.getDrawable();

        if(srcDrawable instanceof StateListDrawable){
            srcDrawable = srcDrawable.getCurrent();
        }
        Log.e("dd", "imageView -detectDrawable:" + srcDrawable);
        Bitmap bitmap = DrawableUnWrapBitmapUtil.unwrap(srcDrawable);
        return bitmap;
    }
}
