package smallnew.bitmapcanary;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import hexin.androidbitmapcanary.BitmapListUtil;

public class DemoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        findViewById(R.id.iv_go2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DemoActivity.this,DemoActivity2.class));
            }
        });
        frescoNoResize();
        frescoWithResize();


        frescoRes();

    }

    private void frescoRes() {
        SimpleDraweeView view =  findViewById(R.id.fresco_res);
        //view.setImageURI();
    }

    private void frescoNoResize() {
        SimpleDraweeView view =  findViewById(R.id.fresco_noresize);
        view.setImageURI("https://desk-fd.zol-img.com.cn/t_s960x600c5/g5/M00/09/03/ChMkJl3I0dmIAiyXABDF46MVj1IAAvKLgFJsZUAEMX7384.jpg");
    }

    private void frescoWithResize() {
        SimpleDraweeView view =  findViewById(R.id.my_image_view);

        Uri uri = Uri.parse("https://desk-fd.zol-img.com.cn/t_s960x600c5/g5/M00/09/03/ChMkJl3I0dmIAiyXABDF46MVj1IAAvKLgFJsZUAEMX7384.jpg");
        ImageRequest request =
                ImageRequestBuilder.newBuilderWithSource(uri)
                        .setResizeOptions(new ResizeOptions(150,150))
                        //缩放,在解码前修改内存中的图片大小, 配合Downsampling可以处理所有图片,否则只能处理jpg,
                        // 开启Downsampling:在初始化时设置.setDownsampleEnabled(true)
                        .setProgressiveRenderingEnabled(true)//支持图片渐进式加载
                        .setAutoRotateEnabled(true) //如果图片是侧着,可以自动旋转
                        .build();

        PipelineDraweeController controller =
                (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setAutoPlayAnimations(true) //自动播放gif动画
                        .build();

        view.setController(controller);
    }

    public void showBitmapList(View view) {
        BitmapListUtil.start(this);
    }
}
