package hexin.androidbitmapcanary;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

/**
 * 图片检测工厂
 * Created by smallnew on 2018\5\5 0005.
 */

public class DetectorFactory {

    public static final int DETECT_TYPE_BACKGROUND = 1;
    public static final int DETECT_TYPE_IMAGESRC = 2;
    private static HashMap<Integer,Detector> detectorCache = new HashMap<>();
     static List<Detector> detectors ;

     static {
         detectors = new ArrayList<>();
         detectors.add(new ImagesrcDetector());
     }

     public static void addDetect(Detector detector){
         if(detector == null){
             return;
         }
         detectors.add(detector);
         Collections.sort(detectors, new Comparator<Detector>() {
             @Override
             public int compare(Detector o1, Detector o2) {
                 try {
                     Class clazz1 = DrawableDetectUtil.getClass(o1,0);
                     Class clazz2 = DrawableDetectUtil.getClass(o2,0);
                     boolean isChild = clazz1.isAssignableFrom(clazz2);
                     boolean isParent = clazz2.isAssignableFrom(clazz1);
                 /*man instanceof Person：man是否是Person的子类
                Person.class.isAssignableFrom(Man.class)：Person是否是Man的父类*/
                     if(isChild){
                         return 1;
                     }
                     if(isParent){
                         return -1;
                     }
                     return 0;
                 }catch (Throwable throwable){
                     throwable.printStackTrace();
                     return 0;
                 }

             }
         });
         for (int i = 0; i < detectors.size(); i++) {
             Log.d("dd",detectors.get(i)+",i:"+i);
         }
     }


    public static Detector getDetector(int detectType){
        if(detectorCache.containsKey(detectType)){
            return detectorCache.get(detectType);
        }else {
            return produceDetector(detectType);
        }
    }

    private static Detector produceDetector(int detectType){
        if(DETECT_TYPE_BACKGROUND == detectType){
            return new BackgroundDetecotor();
        }else if(DETECT_TYPE_IMAGESRC == detectType){
            return new ImagesrcDetector();
        }else {//todo checkbox detector、progress detecor ... adding furtue
            throw new IllegalArgumentException("detectType not support "+detectType);
        }
    }
}
