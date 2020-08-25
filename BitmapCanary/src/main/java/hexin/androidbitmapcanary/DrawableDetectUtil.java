package hexin.androidbitmapcanary;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;


import java.lang.reflect.ParameterizedType;

import java.util.List;

public class DrawableDetectUtil{

    public static float MAX_SCALE = 1.5f;
    public static float MAX_SCALE_2 = 2f;
    public static float MAX_SCALE_3 = 3f;



    public static void detectDrawableSize(ViewGroup rootView){
        if(rootView==null||rootView.getChildCount()==0){
            return;
        }
        detectDrawable(rootView);
    }

    public static void detectDrawable(View view){
        if(view==null||view.getVisibility()==View.GONE||view.getHeight()==0||view.getWidth()==0){
            return;
        }
        Detector detectorBackground = DetectorFactory.getDetector(DetectorFactory.DETECT_TYPE_BACKGROUND);
        detectorBackground.detect(view);



        List<Detector> detectors = DetectorFactory.detectors;
        if(!detectors .isEmpty()){
            for (int i = 0; i < detectors.size(); i++) {
                Detector detector = detectors.get(i);
                Class clazz = getClass(detector,0);
                if(clazz != null  && clazz.isInstance(view)){
                    detector.detect(view);
                    break;
                }
            }
        }


        /*if(view instanceof DraweeView){
            DraweeView draweeView = (DraweeView) view;
            Detector detectorImageSrc = DetectorFactory.getDetector(DetectorFactory.DETECT_TYPE_FRESCO);
            detectorImageSrc.detect(draweeView);
        }else if(view instanceof ImageView){
            ImageView imageView = (ImageView)view;
            Detector detectorImageSrc = DetectorFactory.getDetector(DetectorFactory.DETECT_TYPE_IMAGESRC);
            detectorImageSrc.detect(imageView);
        }else */
        if(view instanceof ViewGroup&&((ViewGroup) view).getChildCount()>0){
            ViewGroup viewGroup = (ViewGroup)view;
            for(int i=0;i<viewGroup.getChildCount();i++){
                View childView = viewGroup.getChildAt(i);
                detectDrawable(childView);
            }
        }

    }

    /**
     * 获取子类上第n个泛型的类型
     * @param obj
     * @param idx
     * @param <T>
     * @return
     */
    public static <T> Class<T> getClass(Object obj,int idx){
        if(obj!=null){
            try {
                return ((Class<T>) ((ParameterizedType) (obj.getClass()
                        .getGenericSuperclass())).getActualTypeArguments()[idx]);
            } catch (Throwable e) {
                e.printStackTrace();
            }

        }
        return null;
    }


    static public int getTipColorByScale(float scale){
        if(scale>MAX_SCALE&&scale<MAX_SCALE_2){
            return Color.argb(140,65,134,240);
        }else if(scale>=MAX_SCALE_2&&scale<MAX_SCALE_3){
            return Color.argb(140,254,198,0);
        }else if(scale>=MAX_SCALE_3){
            return Color.argb(140,234,34,50);
        }
        return Color.TRANSPARENT;
    }

    static public class TextDetectDrawable extends Drawable{

        private Paint textPaint;
        private String mText="";
        private int mBgColor = Color.TRANSPARENT;

        public TextDetectDrawable() {
            this.textPaint = new TextPaint();
            this.textPaint.setColor(Color.WHITE);
            this.textPaint.setStyle(Paint.Style.FILL);
            this.textPaint.setTextAlign(Paint.Align.CENTER);
        }

        public void setText(String text) {
            this.mText = text;
        }

        public void setBgColor(int bgColor) {
            this.mBgColor = bgColor;
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            Rect bounds = getBounds();
            int count = canvas.save();
            canvas.translate(bounds.left,bounds.top);
            int fontSize = Math.min(bounds.height(),bounds.width())/2;
            textPaint.setTextSize(fontSize);
            canvas.drawColor(mBgColor);
            canvas.drawText(mText,bounds.centerX(),bounds.height()/2-(textPaint.descent()+textPaint.ascent())/2,textPaint);
            canvas.restoreToCount(count);
        }

        @Override
        public void setAlpha(int i) {
            textPaint.setAlpha(i);
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            textPaint.setColorFilter(colorFilter);
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }
    }
}