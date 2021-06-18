package xyz.perkd.facedetect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseVisionFaceDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseVisionFaceDetectorOptions highAccuracyOpts =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.NO_LANDMARKS)
                        .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.NO_CLASSIFICATIONS)
                        .build();


         detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(highAccuracyOpts);
    }


    public void detect(View view){


        try {

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lss);
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
            long start = System.currentTimeMillis();
            Log.d(TAG, "hms mlkit start" );
            Task<List<FirebaseVisionFace>> result =
                    detector.detectInImage(image)
                            .addOnSuccessListener(
                                    new OnSuccessListener<List<FirebaseVisionFace>>() {
                                        @Override
                                        public void onSuccess(List<FirebaseVisionFace> faces) {
                                            // Task completed successfully
                                            // ...
                                            Log.d(TAG, "hms mlkit end" );
                                            long end = System.currentTimeMillis();
                                            Log.d(TAG, "hms mlkit cost" + (end - start) + " size "+ faces.size());


                                            if (faces.size() > 0) {
                                                ImageView imageView = findViewById(R.id.result);
                                                imageView.setImageBitmap(mergeResult(bitmap, faces.get(0)));
                                            }
                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                            // ...
                                            long end = System.currentTimeMillis();
                                            Log.d(TAG, "hms mlkit exception", e);
                                        }
                                    });
//        long end = System.currentTimeMillis();
//        Log.d(TAG, "hms mlkit cost" + (end - start) + result.getResult().size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private Bitmap mergeResult(Bitmap origin, FirebaseVisionFace face) {
        Bitmap bitmap = Bitmap.createBitmap(origin.getWidth(), origin.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(origin, 0, 0, null);

        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(3F);

        FirebaseVisionFaceContour contour = face.getContour(FirebaseVisionFaceContour.ALL_POINTS);

        for (FirebaseVisionPoint shape : contour.getPoints()) {
                canvas.drawPoint(shape.getX(), shape.getY(), paint);
        }
        return bitmap;
    }
}