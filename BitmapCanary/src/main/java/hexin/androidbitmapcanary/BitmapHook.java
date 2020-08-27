package hexin.androidbitmapcanary;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorSpace;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import de.robv.android.xposed.DexposedBridge;
import de.robv.android.xposed.XC_MethodHook;
import hexin.androidbitmapcanary.BitmapListUtil;



public class BitmapHook {
    static String TAG = "BitmapHook";

    public static void hookThread(Application application){

        try {
            if(isEmulator(application)){
                Log.w(TAG,"模拟器,不hook");
                return;
            }
            if(!isDebuggable(application)){
                Log.w(TAG,"非debug包,不hook");
            }
            hookBitmapFactory();

            hookBitmap();

            hookDrawable();



        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    static boolean isDebuggable(Application application) {
        boolean debuggable = false;
        PackageManager pm = application.getPackageManager();
        try{
            ApplicationInfo appinfo = pm.getApplicationInfo(application.getPackageName(), 0);
            debuggable = (0 != (appinfo.flags & ApplicationInfo.FLAG_DEBUGGABLE));
        }catch(PackageManager.NameNotFoundException e){
            /*debuggable variable will remain false*/
        }
        return debuggable;
    }

    static boolean isEmulator(Application application) {
        boolean checkProperty = Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.toLowerCase().contains("vbox")
                || Build.FINGERPRINT.toLowerCase().contains("test-keys")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
        if (checkProperty) return true;

        String operatorName = "";
        TelephonyManager tm = (TelephonyManager) application.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            String name = tm.getNetworkOperatorName();
            if (name != null) {
                operatorName = name;
            }
        }
        boolean checkOperatorName = operatorName.toLowerCase().equals("android");
        if (checkOperatorName) return true;

        String url = "tel:" + "123456";
        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        intent.setAction(Intent.ACTION_DIAL);
        boolean checkDial = intent.resolveActivity(application.getPackageManager()) == null;
        if (checkDial) return true;

//        boolean checkDebuggerConnected = Debug.isDebuggerConnected();
//        if (checkDebuggerConnected) return true;

        return false;
    }

    private static void hookDrawable() {
        try {
            DexposedBridge.hookAllConstructors(BitmapDrawable.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    BitmapDrawable thread = (BitmapDrawable) param.thisObject;
                    //thread.setUncaughtExceptionHandler(new SafeHandler());
                    Log.d("drawablehook",thread+"");
                  try {
                      BitmapListUtil.add(((BitmapDrawable) thread).getBitmap());
                  }catch (Throwable throwable){
                      throwable.printStackTrace();
                  }
                }
            });

            try {
                Method method5 = BitmapDrawable.class.getDeclaredMethod("setBitmap", Bitmap.class);
                DexposedBridge.hookMethod(method5,new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);

                        Object[] args =  param.args;
                        if(args !=null && args.length>0){
                            for (int i = 0; i < args.length; i++) {
                                Log.d(TAG,"arg:"+i+":"+args[i]);
                                if(args[i] instanceof Bitmap){
                                    BitmapListUtil.add((Bitmap) args[i]);
                                }
                            }
                        }
                    }
                });
            }catch (Throwable throwable){
                throwable.printStackTrace();
            }


            DexposedBridge.hookAllConstructors(Canvas.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    Object[] args =  param.args;
                    if(args !=null && args.length>0){
                        for (int i = 0; i < args.length; i++) {
                            Log.d(TAG,"arg:"+i+":"+args[i]);
                            if(args[i] instanceof Bitmap){
                                BitmapListUtil.add((Bitmap) args[i]);
                            }
                        }
                    }
                }
            });
            Method method2 = Canvas.class.getDeclaredMethod("setBitmap", Bitmap.class);
            DexposedBridge.hookMethod(method2,new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    Object[] args =  param.args;
                    if(args !=null && args.length>0){
                        for (int i = 0; i < args.length; i++) {
                            Log.d(TAG,"arg:"+i+":"+args[i]);
                            if(args[i] instanceof Bitmap){
                                BitmapListUtil.add((Bitmap) args[i]);
                            }
                        }
                    }
                }
            });
            Constructor method3 = Canvas.class.getConstructor( Bitmap.class);
            DexposedBridge.hookMethod(method3,new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    Object[] args =  param.args;
                    if(args !=null && args.length>0){
                        for (int i = 0; i < args.length; i++) {
                            Log.d(TAG,"arg:"+i+":"+args[i]);
                            if(args[i] instanceof Bitmap){
                                BitmapListUtil.add((Bitmap) args[i]);
                            }
                        }
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
