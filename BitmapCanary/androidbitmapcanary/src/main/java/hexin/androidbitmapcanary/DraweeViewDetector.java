package hexin.androidbitmapcanary;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;

import com.facebook.drawee.drawable.FadeDrawable;
import com.facebook.drawee.drawable.ForwardingDrawable;
import com.facebook.drawee.drawable.ScaleTypeDrawable;
import com.facebook.drawee.generic.RootDrawable;
import com.facebook.drawee.interfaces.DraweeHierarchy;
import com.facebook.drawee.view.DraweeView;

import java.lang.reflect.Field;

import static hexin.androidbitmapcanary.DrawableDetectUtil.MAX_SCALE;

/**GenericDraweeHierarchy:
 *  *  o RootDrawable (top level drawable)
 *  *  |
 *  *  +--o FadeDrawable
 *  *     |
 *  *     +--o ScaleTypeDrawable (placeholder branch, optional)
 *  *     |  |
 *  *     |  +--o Drawable (placeholder image)
 *  *     |
 *  *     +--o ScaleTypeDrawable (actual image branch)
 *  *     |  |
 *  *     |  +--o ForwardingDrawable (actual image wrapper)
 *  *     |     |
 *  *     |     +--o Drawable (actual image)
 *  *     |
 *  *     +--o null (progress bar branch, optional)
 *  *     |
 *  *     +--o Drawable (retry image branch, optional)
 *  *     |
 *  *     +--o ScaleTypeDrawable (failure image branch, optional)
 *  *        |
 *  *        +--o Drawable (failure image)
 *
 *  FadeDrawable里是一个数组,反射获取
 */
public class DraweeViewDetector extends Detector<DraweeView> {
    @Override
    public void detect(DraweeView imageView) {
        Drawable srcDrawable = imageView.getTopLevelDrawable();
        DraweeHierarchy hierarchy = imageView.getHierarchy();


        Log.e("dd","DraweeView:"+srcDrawable.toString());
        if(srcDrawable instanceof StateListDrawable){
            srcDrawable = srcDrawable.getCurrent();
        }
        if(srcDrawable instanceof RootDrawable){
            RootDrawable rootDrawable = (RootDrawable) srcDrawable;
            srcDrawable = rootDrawable.getDrawable();
        }
        if(srcDrawable instanceof FadeDrawable){
            FadeDrawable fadeDrawable = (FadeDrawable) srcDrawable;
            try {
               Field mLayers =  FadeDrawable.class.getDeclaredField("mLayers");
               mLayers.setAccessible(true);
                Drawable[] drawables = (Drawable[]) mLayers.get(fadeDrawable);
                if(drawables != null){
                    for (int i = 0; i < drawables.length; i++) {
                        Drawable drawable = drawables[i];
                        if(drawable == null){
                            continue;
                        }
                        Log.e("dd","arr["+i+"]"+drawable.toString());
                        if(drawable instanceof ScaleTypeDrawable){
                            ScaleTypeDrawable scaleTypeDrawable = (ScaleTypeDrawable) drawable;
                          Drawable drawable1 =   scaleTypeDrawable.getCurrent();
                            Log.e("dd","arr["+i+"]"+"getCurrent"+drawable1.toString());
                          if(drawable1 instanceof ForwardingDrawable){
                              ForwardingDrawable drawable2 = (ForwardingDrawable) drawable1;
                              Drawable drawable3 = drawable2.getDrawable();
                              Log.e("dd","arr["+i+"]"+"getCurrent-ForwardingDrawable"+drawable3.toString());
                              detectDrawable(drawable3,imageView);
                          }else {
                              detectDrawable(drawable1,imageView);
                          }
                        }else {
                            detectDrawable(drawable,imageView);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            detectDrawable(srcDrawable,imageView);
        }
    }

    private void detectDrawable(Drawable srcDrawable, DraweeView imageView) {
        Log.e("dd","detectDrawable:"+srcDrawable.toString());
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
