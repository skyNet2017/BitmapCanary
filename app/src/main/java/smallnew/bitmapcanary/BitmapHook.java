package smallnew.bitmapcanary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorSpace;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.InputStream;
import java.lang.reflect.Method;

import de.robv.android.xposed.DexposedBridge;
import de.robv.android.xposed.XC_MethodHook;
import hexin.androidbitmapcanary.BitmapListUtil;

import static smallnew.bitmapcanary.ThreadSafeUtil.TAG;

public class BitmapHook {

    public static void hookThread(){

        try {
            hookBitmapFactory();

            hookBitmap();

            hookDrawable();



        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private static void hookDrawable() {
        try {
            DexposedBridge.hookAllConstructors(Drawable.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Drawable drawable = (Drawable) param.thisObject;
                    Log.d(TAG,drawable+"");
                    if(drawable instanceof BitmapDrawable){
                        BitmapDrawable drawable1 = (BitmapDrawable) drawable;
                        //BitmapListUtil.add(drawable1.getBitmap());
                        DexposedBridge.findAndHookMethod(drawable.getClass(), "setBitmap", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                BitmapListUtil.add((Bitmap) param.args[0]);
                            }
                        });
                    }
                }
            });
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }
    }

    private static void hookBitmap() {
        try {

 /* public static Bitmap createBitmap(@NonNull Bitmap source, int x, int y, int width, int height,
            @Nullable Matrix m, boolean filter)*/
            // DexposedBridge.findAndHookMethod(Thread.class, "start", );
            Method method2 = Bitmap.class.getDeclaredMethod("createBitmap", Bitmap.class,
                    int.class,int.class,int.class,int.class,Matrix.class, boolean.class);
            DexposedBridge.hookMethod(method2,new BitmapCallback());


            /*public static Bitmap createBitmap(@Nullable DisplayMetrics display, int width, int height,
            @NonNull Config config, boolean hasAlpha, @NonNull ColorSpace colorSpace)*/

            Method method3 = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                method3 = Bitmap.class.getDeclaredMethod("createBitmap", DisplayMetrics.class,
                        int.class,int.class, Bitmap.Config.class,boolean.class, ColorSpace.class);
                DexposedBridge.hookMethod(method3,new BitmapCallback());
            }

            /*public static Bitmap createBitmap(@NonNull DisplayMetrics display,
            @NonNull @ColorInt int[] colors, int offset, int stride,
            int width, int height, @NonNull Config config)*/
            Method method4 = Bitmap.class.getDeclaredMethod("createBitmap", DisplayMetrics.class,
                    int[].class,int.class,int.class,int.class,int.class, Bitmap.Config.class);
            DexposedBridge.hookMethod(method4,new BitmapCallback());


            /* public static @NonNull Bitmap createBitmap(@NonNull Picture source, int width, int height,
            @NonNull Config config) {*/
            Method method5 = Bitmap.class.getDeclaredMethod("createBitmap", Picture.class,
                    int.class,int.class, Bitmap.Config.class);
            DexposedBridge.hookMethod(method5,new BitmapCallback());


        }catch (Throwable throwable){
            throwable.printStackTrace();
        }
    }

    private static void hookBitmapFactory() {
        try {
            Method method = BitmapFactory.class.getDeclaredMethod("decodeStream", InputStream.class, Rect.class, BitmapFactory.Options.class);
            DexposedBridge.hookMethod(method,new BitmapCallback());


            /* public static Bitmap decodeByteArray(byte[] data, int offset, int length, Options opts)*/

            Method method2 = BitmapFactory.class.getDeclaredMethod("decodeByteArray", byte[].class, int.class,int.class, BitmapFactory.Options.class);
            DexposedBridge.hookMethod(method2,new BitmapCallback());


            //Bitmap decodeFileDescriptor(FileDescriptor fd, Rect outPadding, Options opts)
            Method method3 = BitmapFactory.class.getDeclaredMethod("decodeFileDescriptor", FileDescriptor.class, Rect.class, BitmapFactory.Options.class);
            DexposedBridge.hookMethod(method3,new BitmapCallback());

        }catch (Throwable throwable){
            throwable.printStackTrace();
        }

    }

    static class BitmapCallback extends XC_MethodHook{

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);
            Log.d("ddd0",param.getResult()+"");
            if (param.getResult() instanceof Bitmap){
                Bitmap bitmap = (Bitmap) param.getResult();
                BitmapListUtil.add(bitmap);
            }
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
