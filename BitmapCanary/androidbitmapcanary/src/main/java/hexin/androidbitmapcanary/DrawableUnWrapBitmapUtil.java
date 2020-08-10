package hexin.androidbitmapcanary;

import android.graphics.Bitmap;
import android.graphics.NinePatch;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.util.Log;

import java.lang.reflect.Field;

public class DrawableUnWrapBitmapUtil {

    public static Bitmap unwrap(Drawable srcDrawable){
        if(srcDrawable instanceof StateListDrawable){
            srcDrawable = srcDrawable.getCurrent();
        }
        if(srcDrawable instanceof BitmapDrawable){
            Bitmap bitmap = ((BitmapDrawable) srcDrawable).getBitmap();
            return bitmap;
        }
        if(srcDrawable instanceof RoundedBitmapDrawable){
            try {
                RoundedBitmapDrawable drawable = (RoundedBitmapDrawable) srcDrawable;
                Field field = RoundedBitmapDrawable.class.getDeclaredField("mBitmap");
                field.setAccessible(true);
                Bitmap bitmap = (Bitmap) field.get(drawable);
                return bitmap;
            }catch (Throwable throwable){
                throwable.printStackTrace();
                return null;
            }
        }
        if(srcDrawable instanceof NinePatchDrawable){
            try {
                NinePatchDrawable ninePatchDrawable = (NinePatchDrawable) srcDrawable;
                Drawable.ConstantState state =  ninePatchDrawable.getConstantState();

                Class clazz = Class.forName("android.graphics.drawable.NinePatchDrawable.NinePatchState");
                Field declaredField = clazz.getDeclaredField("mNinePatch");
                declaredField.setAccessible(true);
                NinePatch ninePatch = (NinePatch) declaredField.get(state);
                Field declaredField1 = NinePatch.class.getDeclaredField("mBitmap");
                Bitmap bitmap = (Bitmap) declaredField1.get(ninePatch);
                return bitmap;
            }catch (Throwable throwable){
                throwable.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
