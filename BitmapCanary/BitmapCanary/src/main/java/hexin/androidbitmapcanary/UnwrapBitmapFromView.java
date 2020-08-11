package hexin.androidbitmapcanary;

import android.graphics.Bitmap;
import android.view.View;

public interface UnwrapBitmapFromView<V extends View> {


    Bitmap unWrap(V view);
}
