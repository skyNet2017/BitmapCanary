package smallnew.bitmapcanary;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.robv.android.xposed.DexposedBridge;
import de.robv.android.xposed.XC_MethodHook;

public class AllClassObjectsUtil {

    static String TAG = "AllClassObjectsUtil";
   static HashMap<Class, List<WeakReference>> map = new HashMap<>();

    public static  void hook(final Class clazz){
        DexposedBridge.hookAllConstructors(clazz, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Log.d(TAG,  "class:" + clazz +  " is created. obj is "+param.thisObject);
                WeakReference weakReference =  new WeakReference( param.thisObject);
                if(!map.containsKey(clazz)){
                    List<WeakReference> list = new ArrayList<WeakReference>();
                    list.add(weakReference);
                    map.put(clazz,list);
                }else {
                    List<WeakReference> list = map.get(clazz);
                    list.add(weakReference);
                }
            }
        });
    }

    public static <T> List<T> getAllObj(Class<T> tClass){
        if(!map.containsKey(tClass)){
            return new ArrayList<>();
        }
        List<WeakReference> list = map.get(tClass);
        List<T> objs = new ArrayList<>();
        Iterator<WeakReference> iterator = list.iterator();
        while (iterator.hasNext()){
            WeakReference weakReference = iterator.next();
            if(weakReference == null || weakReference.get() == null){
                iterator.remove();
                continue;
            }else {
                if(weakReference.get().getClass().equals(tClass)){
                    objs.add((T) weakReference.get());
                }

            }
        }
        return objs;

    }
}
