package com.example.andrea.myapp;


import android.support.v7.app.AppCompatActivity;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.cvtColor;

public class RecognitionActivity extends AppCompatActivity {

    public static RotatedRect rotrect;
    public static Rect rect;

    public static MatOfPoint2f findContours(Mat mat_img) {

        Mat canny = new Mat();
        Mat mat_gray = new Mat();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.cvtColor(mat_img, mat_gray, COLOR_BGR2GRAY);
        Imgproc.Canny(mat_gray, canny, 100, 100 * 3);
        Imgproc.findContours(canny, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        //Extraemos el contorno que nos interesa (el más grande):
        //Recorremos cada contorno y calculamos su área, si es mayor que la calculada anteriormente,
        //esa es la nueva maxArea (nos quedamos con la mayor)
        double maxArea = 0;
        int maxAreaIdx = 0; //cada contorno tiene un índice

        MatOfPoint2f maxContour2f = new MatOfPoint2f();
        MatOfPoint2f approxCurve2f = new MatOfPoint2f();
        for (int idx = 0; idx < contours.size(); idx++) {
            double area = Imgproc.contourArea(contours.get(idx));

            if (area > maxArea) {
                maxArea = area;
                maxAreaIdx = idx;
            }


            contours.get(maxAreaIdx).convertTo(maxContour2f, CvType.CV_32FC2); //MatOfPoint to MatOfPoint2f
            Imgproc.approxPolyDP(maxContour2f, approxCurve2f, Imgproc.arcLength(maxContour2f, true) * 0.02, true);

            /*Creamos un rectángulo que englobe ese contorno (uno recto para luego hacer el submat y
            otro rotado para el aspect ratio*/
            rect = Imgproc.boundingRect(contours.get(maxAreaIdx));
            rotrect = Imgproc.minAreaRect(maxContour2f); //argumento: MatOfPoint2f*/


            //Calculamos los momentos para calcular el centro de masas
            Moments moments = Imgproc.moments(contours.get(maxAreaIdx));
            Point centroid = new Point();
            centroid.x = moments.get_m10() / moments.get_m00();
            centroid.y = moments.get_m01() / moments.get_m00();


        }


        drawContours(mat_img, contours, maxAreaIdx);

        return approxCurve2f;

    }

    public static void drawContours(Mat mat_img, List<MatOfPoint> edgeContours, int figureIdx) { //no queremos que nos devuelva nada sino que lo haga directamente

        Imgproc.drawContours(mat_img, edgeContours, figureIdx, new Scalar(255, 255, 0, 255), 4); //-1 dibuja todos los contornos
    }

    public static String figureClasification(MatOfPoint2f approxCurve2f) {

        String figure = "";

        /**CLASIFICACIÓN DEL CONTORNO**/

        if (approxCurve2f.total() == 4) {
            double aspectratio = rotrect.size.width / rotrect.size.height;
            if (0.80 <= aspectratio && aspectratio <= 1.20) {
                figure = "CUADRADO";
            } else {
                figure = "RECTANGULO";
            }
        }


        else if (approxCurve2f.total() == 3) {
            figure = "TRIANGULO";
        }

        else if (approxCurve2f.total() >=7 && approxCurve2f.total() <= 10) { //normalmente son 8
            figure = "CIRCULO";

        }

        else if (approxCurve2f.total() == 6){
            figure = "HEXAGONO";
        }

        return figure;


    }

   public static String colorDetection (Mat mat_img){

        Mat img = mat_img.clone();
        Mat submat = img.submat(rect); //rectángulo con únicamente la figura que nos interesa
        Mat submatHSV = new Mat();
        cvtColor(submat, submatHSV,Imgproc.COLOR_RGB2HSV); //pasamos de RGB a HSV
        double[] pixelColor;
        int cols = submatHSV.cols();
        int rows = submatHSV.rows();
        int x = cols/2;
        int y = rows/2;
        pixelColor = submatHSV.get(x,y); //pixel del centro del rectángulo

        return extractColorFromPixel(pixelColor[0]);
        //return obtenerColorHSV(pixelColor);
    }

    public static String extractColorFromPixel (double pixel) {


        //TESTING: http://www.color-blindness.com/color-name-hue/ (remember: hue en OpenCV [0, 180])
        String color ="";

        double redDown = 0; //177
        double redUp = 6;


        double redDown2 = 169;
        double redUp2 = 179;

        double orangeDown = 7; //15
        double orangeUp = 20;

        double yellowDown = 21;
        double yellowUp = 35;

        double greenDown = 36; //61 aprox
        double greenUp = 83;

        double blueDown = 84; //Normal-> aprox 110
        double blueUp = 125;

        double purpleDown = 126; //166 aprox
        double purpleUp = 168;




        if (pixel>=greenDown && pixel<greenUp){
            color="verde";
        }else if (pixel>=purpleDown && pixel<=purpleUp){
            color="morado";
        }else if (pixel>=blueDown && pixel<=blueUp){
            color="azul";
        }else if (pixel>=redDown2 && pixel<=redUp2){
            color = "rojo";
        }else if (pixel>=redDown && pixel<=redUp){
            color = "rojo";
        }else if (pixel>=orangeDown && pixel<=orangeUp) {
            color = "naranja";
        }else if (pixel>=yellowDown && pixel<=yellowUp) {
            color = "amarillo";
        }

        return color;
    }



}