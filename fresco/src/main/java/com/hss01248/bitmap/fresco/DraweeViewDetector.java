package com.hss01248.bitmap.fresco;

import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import android.util.Log;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.FadeDrawable;
import com.facebook.drawee.drawable.ForwardingDrawable;

import com.facebook.drawee.drawable.RoundedBitmapDrawable;
import com.facebook.drawee.drawable.ScaleTypeDrawable;
import com.facebook.drawee.generic.RootDrawable;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.interfaces.DraweeHierarchy;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

import java.lang.reflect.Field;

import hexin.androidbitmapcanary.BitmapCanaryUtil;
import hexin.androidbitmapcanary.Detector;
import hexin.androidbitmapcanary.DrawableUnWrapBitmapUtil;

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
    public void detect(final DraweeView imageView) {
        Drawable srcDrawable = imageView.getTopLevelDrawable();
        DraweeHierarchy hierarchy = imageView.getHierarchy();
        DraweeController controller = imageView.getController();
        if(controller  instanceof PipelineDraweeController){
            PipelineDraweeController controller1 = (PipelineDraweeController) controller;
            controller1.addControllerListener(new ControllerListener<ImageInfo>() {
                @Override
                public void onSubmit(String id, Object callerContext) {

                }

                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                    BitmapCanaryUtil.e("dd","onIntermediateImageSet:"+imageInfo.getWidth()+"x"+imageInfo.getHeight()+",q:"+imageInfo.getQualityInfo());
                    detect(imageView);
                }

                @Override
                public void onIntermediateImageSet(String id, ImageInfo imageInfo) {

                }

                @Override
                public void onIntermediateImageFailed(String id, Throwable throwable) {

                }

                @Override
                public void onFailure(String id, Throwable throwable) {

                }

                @Override
                public void onRelease(String id) {

                }
            });
        }


        BitmapCanaryUtil.e("dd","DraweeView:"+srcDrawable.toString());
        BitmapCanaryUtil.e("dd","hierarchy:"+hierarchy);
        BitmapCanaryUtil.e("dd","controller:"+controller);
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
                        BitmapCanaryUtil.e("dd","arr["+i+"]"+drawable.toString());
                        Drawable drawable0 = unWrap(drawable);
                        detectDrawable(drawable0,imageView);

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            detectDrawable(srcDrawable,imageView);
        }
    }

    private Drawable unWrap(Drawable drawable) {
        if(drawable instanceof ForwardingDrawable){
            ForwardingDrawable drawable1 = (ForwardingDrawable) drawable;
            Drawable drawable2 = drawable1.getCurrent();
            return unWrap(drawable2);
        }else {
            return drawable;
        }
    }

    private void detectDrawable(Drawable srcDrawable, DraweeView imageView) {
        BitmapCanaryUtil.e("dd","detectDrawable:"+srcDrawable);
        try {
            if(srcDrawable instanceof RoundedBitmapDrawable){
                RoundedBitmapDrawable drawable = (RoundedBitmapDrawable) srcDrawable;
                Field field = RoundedBitmapDrawable.class.getDeclaredField("mBitmap");
                field.setAccessible(true);
                Bitmap bitmap = (Bitmap) field.get(drawable);
                BitmapCanaryUtil.e("dd","bitmap:"+bitmap);
                handleBitmap(bitmap,imageView);
            }else {
                Bitmap bitmap = DrawableUnWrapBitmapUtil.unwrap(srcDrawable);
                handleBitmap(bitmap,imageView);
            }
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }
    }
}
