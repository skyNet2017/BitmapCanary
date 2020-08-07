package hexin.androidbitmapcanary;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;

import com.facebook.drawee.drawable.FadeDrawable;
import com.facebook.drawee.generic.RootDrawable;
import com.facebook.drawee.view.DraweeView;

import static hexin.androidbitmapcanary.DrawableDetectUtil.MAX_SCALE;

public class DraweeViewDetector extends Detector<DraweeView> {
    @Override
    public void detect(DraweeView imageView) {
        Drawable srcDrawable = imageView.getTopLevelDrawable();
        Log.e("dd","DraweeView:"+srcDrawable.toString());
        if(srcDrawable instanceof StateListDrawable){
            srcDrawable = srcDrawable.getCurrent();
        }
        if(srcDrawable instanceof RootDrawable){
            RootDrawable rootDrawable = (RootDrawable) srcDrawable;
            srcDrawable = rootDrawable.getDrawable();
        }
        if(srcDrawable instanceof FadeDrawable){
            FadeDrawable rootDrawable = (FadeDrawable) srcDrawable;
            srcDrawable = rootDrawable.getCurrent();
        }
        Log.e("dd","DraweeView2:"+srcDrawable.toString());

        Log.e("dd","getController:"+ imageView.getController());
        Log.e("dd","getController:"+ imageView.getHierarchy());
        if(srcDrawable instanceof BitmapDrawable){
            Bitmap bitmap = ((BitmapDrawable) srcDrawable).getBitmap();
            BitmapListUtil.add(bitmap);
            if(bitmap.getHeight()>imageView.getHeight()*MAX_SCALE
                    ||bitmap.getWidth()>imageView.getWidth()*MAX_SCALE){
                markScaleView(bitmap,imageView);
            }else {
                clearMark(imageView);
            }
        }
    }
}
