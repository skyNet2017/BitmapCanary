package smallnew.bitmapcanary;

import android.app.Application;
import android.util.Log;

import de.robv.android.xposed.DexposedBridge;
import de.robv.android.xposed.XC_MethodHook;


public class ThreadSafeUtil {
    static String TAG = "ThreadMethodHook";
    public static void hookThread(Application application){
        DexposedBridge.hookAllConstructors(Thread.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Thread thread = (Thread) param.thisObject;
                thread.setUncaughtExceptionHandler(new SafeHandler());
                Class<?> clazz = thread.getClass();
                if (clazz != Thread.class) {
                    Log.d(TAG, "found class extend Thread:" + clazz);
                    DexposedBridge.findAndHookMethod(clazz, "run", new ThreadMethodHook());
                }
                Log.d(TAG, "Thread: " + thread.getName() + " class:" + thread.getClass() +  " is created.");
            }
        });
        DexposedBridge.findAndHookMethod(Thread.class, "run", new ThreadMethodHook());
    }

   static class ThreadMethodHook extends XC_MethodHook {

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
            Thread t = (Thread) param.thisObject;
            Log.i(TAG, "thread:" + t + ", started..");
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);
            Thread t = (Thread) param.thisObject;
            Log.i(TAG, "thread:" + t + ", exit..");
        }
    }

    static class SafeHandler implements Thread.UncaughtExceptionHandler{

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            Log.e(TAG,"thread crash:"+t.getName());
            e.printStackTrace();
        }
    }
}
