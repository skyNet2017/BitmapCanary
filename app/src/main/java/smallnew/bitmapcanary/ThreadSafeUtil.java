package smallnew.bitmapcanary;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.util.Log;

import java.util.WeakHashMap;

import de.robv.android.xposed.DexposedBridge;
import de.robv.android.xposed.XC_MethodHook;


public class ThreadSafeUtil {
    static String TAG = "ThreadMethodHook";
    public volatile static int count = 0;
    static WeakHashMap<Thread,StackTraceElement[]> map = new WeakHashMap<>();

    public static void hookThread(){
        DexposedBridge.hookAllConstructors(Thread.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Thread thread = (Thread) param.thisObject;
                //thread.setUncaughtExceptionHandler(new SafeHandler());
                Class<?> clazz = thread.getClass();
                if (clazz != Thread.class) {
                    Log.d(TAG, "found class extend Thread:" + clazz);
                    DexposedBridge.findAndHookMethod(clazz, "start", new ThreadStartMethodHook());
                }
                Log.d(TAG, "Thread: " + thread.getName() + " class:" + thread.getClass() +  " is created.");
            }
        });
        //DexposedBridge.findAndHookMethod(Thread.class, "run", new ThreadMethodHook());
        DexposedBridge.findAndHookMethod(Thread.class, "start", new ThreadStartMethodHook());
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

    static class ThreadStartMethodHook extends XC_MethodHook {

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
            Thread t = (Thread) param.thisObject;
            Log.i(TAG, "thread:" + t + ", start begin..");
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);
            Thread t = (Thread) param.thisObject;
            Log.i(TAG, "thread:" + t + ", started");
            count++;
            Log.i(TAG, "thread count:" + count);

           StackTraceElement[] stackTraceElements =  new Exception().getStackTrace();
            StackTraceElement[] stackTraceElements2 = new StackTraceElement[stackTraceElements.length -5];
            for (int i = 0; i < stackTraceElements.length-5; i++) {
                stackTraceElements2[i] = stackTraceElements[i+5];
            }
            map.put(t,stackTraceElements2);
            Log.i(TAG, "map count2:" + map.keySet().size());

            Exception exception = new Exception();
            exception.setStackTrace(stackTraceElements2);
            exception.printStackTrace();
            /*new Exception().printStackTrace();

            Exception exception = new Exception();
            exception.setStackTrace(t.getStackTrace());
            exception.printStackTrace();*/
        }
    }




    static class SafeHandler implements Thread.UncaughtExceptionHandler{

        @Override
        public void uncaughtException(Thread t, Throwable e) {
           Log.e(TAG,"thread crash:"+t.getName());
            e.printStackTrace();
        }
    }

    public static void showThreadList(Activity activity){
        Dialog dialog = new Dialog(activity);
       // dialog.setContentView();
    }
}
