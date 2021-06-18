package xyz.perkd.facedetect.tenginekit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.tenginekit.Face;
import com.tenginekit.model.FaceDetectInfo;
import com.tenginekit.model.FaceLandmarkInfo;
import com.tenginekit.model.FaceLandmarkPoint;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void detect(View view) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lss);
//        MLFrame frame = MLFrame.fromBitmap(bitmap);
//        long start = System.currentTimeMillis();
//        SparseArray<MLFace> faces = analyzer.analyseFrame(frame);
//        long end = System.currentTimeMillis();
//        Log.d(TAG, "hms mlkit cost " + (end - start) + " size "+ faces.size());
//        if (faces.size() > 0) {
//            ImageView imageView = findViewById(R.id.result);
//            imageView.setImageBitmap(mergeResult(bitmap, faces.get(0)));
//        }

        try {

            Log.d(TAG, "hms mlkit start");
            long start = System.currentTimeMillis();
            Face.FaceDetect faceDetect = Face.detect(bitmap2Bytes(bitmap));

            Log.d(TAG, "hms mlkit 111");
            List<FaceDetectInfo> faceDetectInfos = new ArrayList<>();
            List<FaceLandmarkInfo> landmarkInfos = new ArrayList<>();
            if (faceDetect.getFaceCount() > 0) {
                faceDetectInfos = faceDetect.getDetectInfos();
                Log.d(TAG, "hms mlkit 222");
                landmarkInfos = faceDetect.landmark2d();
                Log.d(TAG, "hms mlkit 333");
            }
            long end = System.currentTimeMillis();
            Log.d(TAG, "hms mlkit cost " + (end - start) + " size ");

            if (faceDetectInfos != null && faceDetectInfos.size() > 0) {
                Rect[] face_rect = new Rect[faceDetectInfos.size()];

                List<List<FaceLandmarkPoint>> face_landmarks = new ArrayList<>();
                for (int i = 0; i < faceDetectInfos.size(); i++) {
                    Rect rect = new Rect();
                    rect = faceDetectInfos.get(i).asRect();
                    face_rect[i] = rect;
                    face_landmarks.add(landmarkInfos.get(i).landmarks);
                }

                // do something with face_rect, face_landmarks
                ImageView imageView = findViewById(R.id.result);
                imageView.setImageBitmap(mergeResult(bitmap, face_landmarks));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap mergeResult(Bitmap origin, List<List<FaceLandmarkPoint>> face_landmarks) {
        Bitmap bitmap = Bitmap.createBitmap(origin.getWidth(), origin.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(origin, 0, 0, null);

        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(3F);

        for (List<FaceLandmarkPoint> landmarkPoints : face_landmarks) {
            for (FaceLandmarkPoint pointF : landmarkPoints) {
                canvas.drawPoint(pointF.X, pointF.Y, paint);
            }
        }
        return bitmap;
    }

    private static byte[] bitmap2Bytes(Bitmap image) {
        // calculate how many bytes our image consists of
        int bytes = image.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes); // Create a new buffer
        image.copyPixelsToBuffer(buffer); // Move the byte data to the buffer
        byte[] temp = buffer.array(); // Get the underlying array containing the
        return temp;
    }
}