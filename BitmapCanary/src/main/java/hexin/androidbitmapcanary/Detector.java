package hexin.androidbitmapcanary;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewOverlay;
import android.widget.ImageView;

import static hexin.androidbitmapcanary.DrawableDetectUtil.MAX_SCALE;
import static hexin.androidbitmapcanary.DrawableDetectUtil.getTipColorByScale;

/**
 * Created by smallnew on 2018\5\5 0005.
 */

public abstract class Detector<T extends View> {

    public Detector(){

    }

    abstract public void detect(T view);

    private void markScaleView(Bitmap bitmap, T view){
        float scale = Math.max(bitmap.getHeight()*1.0f/view.getHeight(),bitmap.getWidth()*1.0f/view.getWidth());
        if(Build.VERSION.SDK_INT>=18){
            ViewOverlay overlay = view.getOverlay();
            overlay.clear();
            DrawableDetectUtil.TextDetectDrawable detectDrawable = new DrawableDetectUtil.TextDetectDrawable();
            //String s = bitmap.getWidth() +"x"+bitmap.getHeight();
            //detectDrawable.setText(s);
            detectDrawable.setText(String.format("%.1f",scale));
            detectDrawable.setBgColor(getTipColorByScale(scale));
            detectDrawable.setBounds(0,0,view.getWidth(),view.getHeight());
            overlay.add(detectDrawable);
        }else {
            view.setBackgroundColor(getTipColorByScale(scale));
        }
    }



    protected void handleBitmap(Bitmap bitmap, T imageView) {
        if(bitmap == null){
            if(Build.VERSION.SDK_INT>=18) {
                ViewOverlay overlay = imageView.getOverlay();
                overlay.clear();
            }
            return;
        }
        BitmapListUtil.add(bitmap);
        if(bitmap.getHeight()>imageView.getHeight()*MAX_SCALE
                ||bitmap.getWidth()>imageView.getWidth()*MAX_SCALE){
            markScaleView(bitmap,imageView);
        }else {
            clearMark(imageView);
        }
    }

    private void clearMark(T view) {
        if(Build.VERSION.SDK_INT>=18) {
            ViewOverlay overlay = view.getOverlay();
            overlay.clear();
        }else {
            view.setBackgroundColor(Color.WHITE);
        }
    }


}
