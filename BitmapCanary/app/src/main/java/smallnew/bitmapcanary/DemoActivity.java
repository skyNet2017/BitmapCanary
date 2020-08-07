package smallnew.bitmapcanary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;

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

       SimpleDraweeView view =  findViewById(R.id.my_image_view);
       view.setImageResource(R.drawable.printlogo);
    }

    public void showBitmapList(View view) {
        BitmapListUtil.start(this);
    }
}
