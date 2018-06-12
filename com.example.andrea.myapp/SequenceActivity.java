package com.example.andrea.myapp;


import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;

import static com.example.andrea.myapp.CameraActivity.simpleProgressBar;


/**
 * Created by Andrea on 14/12/2017.
 */

public class SequenceActivity extends AppCompatActivity {
    public int i=0;
    public static String resultado="";




    /*SECUENCIAS DISPONIBLES*/
   public ArrayList<String> currentArray = new ArrayList<String>();

    String[] flan_array = {"TRIANGULO", "CIRCULO", "CUADRADO"}; //no arraylist porque no son variables
    String[] pez_array = {"CUADRADO", "CIRCULO", "TRIANGULO"};
    String[] nieve_array = {"CUADRADO", "RECTANGULO", "TRIANGULO", "CIRCULO"};
    String[] tobogan_array = {"RECTANGULO", "CUADRADO", "CIRCULO", "TRIANGULO"};
    String[] lluvia_array = {"TRIANGULO", "CUADRADO", "HEXAGONO", "RECTANGULO", "CIRCULO" };
    String[] pintor_array = {"HEXAGONO", "CUADRADO", "CIRCULO", "TRIANGULO", "RECTANGULO"};

    /*COLORES*/
    String color = "";



    void cargarSecuencia (String seq){

        switch (seq){
            case "flan":
                color = "azul";
                for (int x = 0; x < flan_array.length; x++) {
                    currentArray.add(x, flan_array[x]);
                    }
                simpleProgressBar.setMax(currentArray.size()); // maximum value for the progress value

                break;

            case "pez":
                color = "verde";
                    for (int x = 0; x < pez_array.length; x++) {
                        currentArray.add(x, pez_array[x]);
                    }
                simpleProgressBar.setMax(currentArray.size()); // maximum value for the progress value

                break;

            case "nieve":
                color = "amarillo";
              //  if(currentArray.size()<nieve_array.length) {
                    for (int x = 0; x < nieve_array.length; x++) {
                        currentArray.add(x, nieve_array[x]);
                    }
                simpleProgressBar.setMax(currentArray.size()); // maximum value for the progress value

                break;

            case "tobogan":
                color = "morado";
             //   if(currentArray.size()<tobogan_array.length) {
                    for (int x = 0; x < tobogan_array.length; x++) {
                        currentArray.add(x, tobogan_array[x]);
                    }
                simpleProgressBar.setMax(currentArray.size()); // maximum value for the progress value

                break;

            case "lluvia":
                color = "rojo";
             //   if(currentArray.size()<lluvia_array.length) {
                    for (int x = 0; x < lluvia_array.length; x++) {
                        currentArray.add(x, lluvia_array[x]);
                    }
                simpleProgressBar.setMax(currentArray.size()); // maximum value for the progress value

                break;

            case "pintor":
                color = "naranja";
             //   if(currentArray.size()<pintor_array.length) {
                    for (int x = 0; x < pintor_array.length; x++) {
                        currentArray.add(x, pintor_array[x]);
                    }
                simpleProgressBar.setMax(currentArray.size()); // maximum value for the progress value

                break;

            default:
                break;

        }


    }

    public boolean verifyColor(String color_detected){ //si está bien, que no haga nada. Si está mal, que saque error
        boolean colorIsTrue;

        if (color_detected.equals(color)){
            colorIsTrue = true;
        }
        else {
            colorIsTrue = false;
        }
        return colorIsTrue;

    }

    public void compareSequence (String detected_figure) {
        if(i<currentArray.size()) {
            if (detected_figure.equals(currentArray.get(i))) {
                simpleProgressBar.setProgress(i+1); // default progress value for the progress bar
                if (simpleProgressBar.getProgress()==currentArray.size()) { //secuencia completa
                    resultado="end";
                    i=0;
                }
                else{
                    i++;
                }


            } else {
                i = 0;
                resultado="fail";

                }

        }
    }

    /*public void isCorrect () {

        simpleProgressBar.setProgress(i+1); // default progress value for the progress bar
       if (simpleProgressBar.getProgress()==currentArray.size()) { //secuencia completa
           resultado="end";
           i=0;
       }

    }*/

 }

