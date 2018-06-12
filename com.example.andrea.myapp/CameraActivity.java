package com.example.andrea.myapp;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ProgressBar;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


public class CameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    public static SequenceActivity seq = new SequenceActivity();
    public static ProgressBar simpleProgressBar;
    public static int contador_triangulo = 0;
    public static int contador_cuadrado = 0;
    public static int contador_rectangulo = 0;
    public static int contador_circulo = 0;
    public static int contador_hexagono = 0;


    private Mat mRgba; //frame (se va actualizando : frame actual)
    private static final String TAG = "OCVSample::Activity";
    private CameraBridgeViewBase mOpenCvCameraView;


    static {
        if (!OpenCVLoader.initDebug()) {
            Log.i("opencv", "inicializacion fallida");
        } else
            Log.i("opencv", "inicializacion correcta");
    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public CameraActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       String SECUENCIA;

        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_camera);
        simpleProgressBar = (ProgressBar) findViewById(R.id.progressBar); // initiate the progress bar


        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.OpenCvView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);//llama a OnCameraFrame
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (savedInstanceState == null) { //Para que solo ejecute "cargarSecuencia" una vez (por alguna razón se ejecuta onCreate dos veces)
            //Get the clicked sequence
            Intent intent = getIntent();
            SECUENCIA = intent.getStringExtra("secuencia");
            seq.cargarSecuencia(SECUENCIA);
            resetCounters();
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
}

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {

        mRgba = new Mat(height, width, CvType.CV_8UC4);

    }

    public void onCameraViewStopped() {

        mRgba.release();
    }


    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        Mat mRgba_copy = mRgba.clone();
        String tipo_figura, figuraDetectada;
        String color;
        boolean colorIsTrue;


        /*   //Dibujar rectángulo tarjeta

        //MOVIL int rec_side = 50;
        //TABLET int rec_side = 300;
        int rec_side = 50;
        int x_rec = (mRgba.cols() / 2) - rec_side / 2;
        int y_rec = (mRgba.rows() / 2) - 450 / 2; //Si es movil es 450, si es tablet es 450
        Rect tarjeta = new Rect(x_rec, y_rec, rec_side, 450); //Si es movil es 450
        Imgproc.rectangle(mRgba, new Point(tarjeta.x, tarjeta.y), new Point(tarjeta.x + tarjeta.width, tarjeta.y + tarjeta.height), new Scalar(240, 240, 240, 255), 5);
        */


        //Dibujar cuadrado ROI
        //MOVIL int square_side = 300;
        //TABLET int square_side = 100;
        int square_side = 300;
        int x = (mRgba.cols() / 2) - square_side / 2;
        int y = (mRgba.rows() / 2) - 200; //movil: -200
        Rect roi = new Rect(x, y, square_side, square_side);
        Imgproc.rectangle(mRgba, new Point(roi.x, roi.y), new Point(roi.x + roi.width, roi.y + roi.height), new Scalar(240, 240, 240, 255), 5);

        //Recortamos la imagen
        Mat matROI = new Mat(mRgba_copy, roi);


        /*ETAPA DE RECONOCIMIENTO*/
        RecognitionActivity recognition = new RecognitionActivity();

        //Encontrar los contornos de la imagen recortada
        MatOfPoint2f approxCurve2f;
        approxCurve2f = recognition.findContours(matROI);


        //Detectamos la figura
        tipo_figura = recognition.figureClasification(approxCurve2f);


        switch (tipo_figura) {
            case "TRIANGULO":
                contador_triangulo++;
                break;
            case "CUADRADO":
                contador_cuadrado++;
                break;
            case "CIRCULO":
                contador_circulo++;
                break;
            case "RECTANGULO":
                contador_rectangulo++;
                break;
            case "HEXAGONO":
                contador_hexagono++;
            default:
                break;
        }


        if (contador_triangulo == 30) {

            //Detectamos el color
            color = recognition.colorDetection(matROI);
            colorIsTrue = seq.verifyColor(color);
            if (colorIsTrue == false) {
                resetCounters();
                seq.resultado = "colorIsFalse";
                FragmentManager fm = getFragmentManager();
                MyDialogFragment dialogFragment = new MyDialogFragment();
                dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog);
                dialogFragment.show(fm, "Fragment Dialog");
                onPause();


            }
            if (colorIsTrue == true) {
                Imgproc.putText(mRgba, "TRIANGULO " + color, new Point(100, 100), 3, 1, new Scalar(255, 0, 0, 255), 2);
                figuraDetectada = "TRIANGULO";
                resetCounters();
                seq.compareSequence(figuraDetectada);

            }

        }

        if (contador_hexagono == 30) {
            color = recognition.colorDetection(matROI);
            colorIsTrue = seq.verifyColor(color);
            if (colorIsTrue == false) {
                resetCounters();

                seq.resultado = "colorIsFalse";
                FragmentManager fm = getFragmentManager();
                MyDialogFragment dialogFragment = new MyDialogFragment();
                dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog);
                dialogFragment.show(fm, "Fragment Dialog");
                onPause();


            }
            if (colorIsTrue == true) {
                Imgproc.putText(mRgba, "HEXAGONO " + color, new Point(100, 100), 3, 1, new Scalar(255, 0, 0, 255), 2);
                figuraDetectada = "HEXAGONO";
                resetCounters();
                seq.compareSequence(figuraDetectada);


            }
        }

        if (contador_cuadrado == 30) {
            color = recognition.colorDetection(matROI);
            colorIsTrue = seq.verifyColor(color);
            if (colorIsTrue == false) {
                resetCounters();

                seq.resultado = "colorIsFalse";
                FragmentManager fm = getFragmentManager();
                MyDialogFragment dialogFragment = new MyDialogFragment();
                dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog);
                dialogFragment.show(fm, "Fragment Dialog");
                onPause();


            }

            if (colorIsTrue == true) {
                Imgproc.putText(mRgba, "CUADRADO " + color, new Point(100, 100), 3, 1, new Scalar(255, 0, 0, 255), 2);
                figuraDetectada = "CUADRADO";
                resetCounters();
                seq.compareSequence(figuraDetectada);

            }

        }

        if (contador_rectangulo == 30) {
            color = recognition.colorDetection(matROI);
            colorIsTrue = seq.verifyColor(color);
            if (colorIsTrue == false) {
                resetCounters();

                seq.resultado = "colorIsFalse";
                FragmentManager fm = getFragmentManager();
                MyDialogFragment dialogFragment = new MyDialogFragment();
                dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog);
                dialogFragment.show(fm, "Fragment Dialog");
                onPause();


            }

            if (colorIsTrue == true) {
                Imgproc.putText(mRgba, "RECTANGULO " + color, new Point(100, 100), 3, 1, new Scalar(255, 0, 0, 255), 2);
                figuraDetectada = "RECTANGULO";
                resetCounters();
                seq.compareSequence(figuraDetectada);


            }

        }

        if (contador_circulo == 30) {
            color = recognition.colorDetection(matROI);
            colorIsTrue = seq.verifyColor(color);
            if (colorIsTrue == false) {
                resetCounters();

                seq.resultado = "colorIsFalse";
                FragmentManager fm = getFragmentManager();
                MyDialogFragment dialogFragment = new MyDialogFragment();
                dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog);
                dialogFragment.show(fm, "Fragment Dialog");
                onPause();


            }
            if (colorIsTrue == true) {
                Imgproc.putText(mRgba, "CIRCULO " + color, new Point(100, 100), 3, 1, new Scalar(255, 0, 0, 255), 2);
                figuraDetectada = "CIRCULO";
                resetCounters();
                seq.compareSequence(figuraDetectada);


            }

        }

        if (seq.resultado == "end") { //resultado es static

            resetCounters();
            FragmentManager fm = getFragmentManager();
            MyDialogFragment dialogFragment = new MyDialogFragment();
            dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog);
            dialogFragment.show(fm, "Fragment Dialog");

            onPause();


        } else if (seq.resultado == "fail") { //resultado es static

            resetCounters();
            FragmentManager fm = getFragmentManager();
            MyDialogFragment dialogFragment = new MyDialogFragment();
            dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog);
            dialogFragment.show(fm, "Fragment Dialog");
            onPause();

        }

        return mRgba; //lo que se ve
    }



    public void resetCounters() {

        contador_triangulo = 0;
        contador_cuadrado = 0;
        contador_rectangulo = 0;
        contador_circulo = 0;
        contador_hexagono = 0;
    }


  /*  public void esperar(int segundos) {
        try {
            Thread.sleep(segundos * 1000);
        } catch (Exception e) {
// Mensaje en caso de que falle
        }
    }*/


}