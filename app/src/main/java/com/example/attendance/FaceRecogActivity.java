package com.example.attendance;

import static org.opencv.core.CvType.CV_32SC1;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.equalizeHist;
import static org.opencv.imgproc.Imgproc.rectangle;
import static org.opencv.imgproc.Imgproc.resize;
import static java.lang.Math.min;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FaceRecogActivity extends AppCompatActivity implements
        CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener {

    private final String TAG = FaceRecogActivity.class.getSimpleName();
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat rgba;
    private int h;
    String haarPath;

    final Size TRAINSIZE = new Size(300, 300);
//    Mat[] facesTrain = new Mat[100];
    List<Mat> facesTrain = new ArrayList<Mat>();
    int trainCount = 1;

    TextView statusTV;

    int trainStatus = 0;
    Intent intent;

    //    EigenFaceRecognizer faceRecognizer = EigenFaceRecognizer.create();
    LBPHFaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
    CascadeClassifier faceCascade;

    private String copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = {"haarcascade_frontalface_default.xml"};
//        try {
//            files = assetManager.list("");
//        } catch (IOException e) {
//            Log.e("tag", "Failed to get asset file list.", e);
//        }
        if (files != null) for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File outFile = new File(getExternalFilesDir(null), filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
                Log.d("File Copied", outFile.getAbsolutePath());
                return outFile.getAbsolutePath();
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
        }
        return "";
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this)
    {
        @Override
        public void onManagerConnected(int status)
        {
            switch (status)
            {
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "OpenCV loaded successfully");

                    mOpenCvCameraView.setOnTouchListener(FaceRecogActivity.this);
                    mOpenCvCameraView.enableView();

                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recog);

        if(OpenCVLoader.initDebug()) {
            Log.d("OPENCV DEBUG", "OpenCV configured correctly!");
        } else {
            Log.d("OPENCV DEBUG", "OpenCV NOT configured correctly!");
        }

        try {
            Log.d("ASSETS", ""+ Arrays.toString(getAssets().list("")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        intent = getIntent();
        Log.d("INTENT", ""+intent.getStringExtra("mode"));

        haarPath = copyAssets();
        if(haarPath.equals("")) {
            Log.d("COPY", "SOME ERROR OCCURED");
        }

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.java_camera_view);
        mOpenCvCameraView.setCameraPermissionGranted();

        mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);

        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        h = min(mOpenCvCameraView.getWidth(), mOpenCvCameraView.getHeight());

        statusTV = findViewById(R.id.statusTV);
        faceCascade = new CascadeClassifier(haarPath);


        if(intent.getStringExtra("mode").equals("train")) {
            Log.d("FaceRecog", "Training session in progress!");
            trainStatus = 1;
        } else {
            String path = getDataDir().toString();
            Log.d("PATH", path+"/faceRecog.xml");
            faceRecognizer.read("/data/data/com.example.attendance/files/faceRecog.xml");
            trainStatus = 0;
        }
    }
    @Override
    protected void onResume()
    {
        super.onResume();

        if (!OpenCVLoader.initDebug())
        {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
                    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this,
                            mLoaderCallback);
        }
        else
        {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
    @Override
    public void onCameraViewStarted(int width, int height)
    {
        Log.d(TAG, "onCameraViewStarted");
    }

    @Override
    public void onCameraViewStopped()
    {
        Log.d(TAG, "onCameraViewStopped");
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
        rgba = inputFrame.rgba();

        Mat gray = new Mat();

        cvtColor(rgba, gray, Imgproc.COLOR_BGR2GRAY);

        MatOfRect faces = new MatOfRect();
        faceCascade.detectMultiScale(gray, faces, 1.2,1, 0, new Size(200, 200), new Size(400, 400));

        Mat face = null;
        Rect rect = null;
        if(faces.toArray().length > 0) {
            rect = faces.toArray()[0];
            face = new Mat(gray, rect);
//            resize(face, face, TRAINSIZE);
        }

        if(trainStatus == 1) {

            Log.d("FACES", "TEST");
            if(facesTrain.size() < 100) {
                if(faces.toArray().length > 0) {

                    equalizeHist(face, face);
                    facesTrain.add(face);
                    rectangle(rgba, rect, new Scalar(0, 255, 0), 2);
                    Log.d("FACES DETECT", ""+rect.x+" "+rect.y+" "+rect.width+" "+rect.height+" ");

                    runOnUiThread(() -> statusTV.setText("Training Set: " + facesTrain.size() + "/" + 100));
                }

            } else {
                int[] array = new int[100];
                Arrays.fill(array, 0);

                Mat labels = new Mat(100, 1, CV_32SC1);
                for(int i=0; i<100; i+=1) {
                    labels.put(0, i, 0);
                }
                Log.d("SAMPLES", labels.toString());
                Log.d("SAMPLES", ""+labels.row(0));
                faceRecognizer.train(facesTrain, labels);

                // Save the model
                String path = getFilesDir().toString();
                faceRecognizer.save(path+"/faceRecog.xml");
                setResult(RESULT_OK);
                finish();
            }
        } else {
            if(faces.toArray().length > 0) {
                equalizeHist(face, face);
                int[] label = {-1};
                double[] conf = {-1};
                faceRecognizer.predict(face, label, conf);
                Log.d("PREDICT", ""+label[0]+" "+conf[0]);
                runOnUiThread(() -> statusTV.setText("Prediction: " + label[0] + ", conf_dist: " + conf[0]));
                if(conf[0] <= 50) {
                    runOnUiThread(() -> statusTV.setText("Face Recognized!"));
                    finish();
                }
            } else {
                runOnUiThread(() -> statusTV.setText("Face Not Found!"));
            }
        }

        return rgba;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        Log.d(TAG, "onTouch");
        return false;
    }

    @Override
    public void onBackPressed() {

    }
}