package com.example.andrea.myapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

    }

    public void goToSubmenu(View v){
        Button button = (Button)v;
        String buttonDescription = button.getContentDescription().toString();
        //equals compara dos cadenas tal cuál,no sus instancias
        // (se pueden comparar cadenas de texto literales sin necesitar una variable)
       if(buttonDescription.equals("Fácil")){
            setContentView(R.layout.submenu_facil);
        }

        else if(buttonDescription.equals("Medio")){
            setContentView(R.layout.submenu_medio);
        }

        else if(buttonDescription.equals("Difícil")){
            setContentView(R.layout.submenu_dificil);
        }


    }

    public void goToCam(View v){
        Button b = (Button)v;
        String description = b.getContentDescription().toString();
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("secuencia", description);
        startActivity(intent);

    }

    public void goToInfo(View v){

        setContentView(R.layout.help_text);
    }

    public void backToMenu(View v){
        setContentView(R.layout.activity_welcome);


    }



}
