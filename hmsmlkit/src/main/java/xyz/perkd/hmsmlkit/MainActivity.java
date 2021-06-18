package xyz.perkd.hmsmlkit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;

import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.face.MLFace;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzerSetting;
import com.huawei.hms.mlsdk.face.MLFaceShape;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MLFaceAnalyzer analyzer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MLFaceAnalyzerSetting setting = new MLFaceAnalyzerSetting.Factory()
                // 设置是否检测人脸关键点。
                .setKeyPointType(MLFaceAnalyzerSetting.TYPE_KEYPOINTS)
                // 设置是否检测人脸特征和表情。
                .setFeatureType(MLFaceAnalyzerSetting.TYPE_FEATURES)
                // 设置仅启用人脸表情检测和性别检测。
                .setFeatureType(MLFaceAnalyzerSetting.TYPE_FEATURE_EMOTION | MLFaceAnalyzerSetting.TYPE_FEATURE_GENDAR)
                // 设置是否检测人脸轮廓点。
                .setShapeType(MLFaceAnalyzerSetting.TYPE_SHAPES)
                // 设置是否开启人脸追踪并指定快捷追踪模式。
                .setTracingAllowed(false, MLFaceAnalyzerSetting.MODE_TRACING_FAST)
                // 设置检测器速度/精度模式。
                .setPerformanceType(MLFaceAnalyzerSetting.TYPE_SPEED)
                // 设置是否开启Pose检测（默认开启）。
                .setPoseDisabled(true)
                .create();
        analyzer = MLAnalyzerFactory.getInstance().getFaceAnalyzer(setting);
    }

    public void detect(View view) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lss);
        MLFrame frame = MLFrame.fromBitmap(bitmap);
        long start = System.currentTimeMillis();
        SparseArray<MLFace> faces = analyzer.analyseFrame(frame);
        long end = System.currentTimeMillis();
        Log.d(TAG, "hms mlkit cost " + (end - start) + " size "+ faces.size());
        if (faces.size() > 0) {
            ImageView imageView = findViewById(R.id.result);
            imageView.setImageBitmap(mergeResult(bitmap, faces.get(0)));
        }
    }

    private Bitmap mergeResult(Bitmap origin, MLFace face) {
        Bitmap bitmap = Bitmap.createBitmap(origin.getWidth(), origin.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(origin, 0, 0, null);

        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(3F);

        for (MLFaceShape shape : face.getFaceShapeList()) {
            for (PointF pointF : shape.getCoordinatePoints()) {
                canvas.drawPoint(pointF.x, pointF.y, paint);
            }
        }
        return bitmap;
    }
}