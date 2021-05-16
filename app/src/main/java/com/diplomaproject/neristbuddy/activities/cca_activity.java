package com.diplomaproject.neristbuddy.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.diplomaproject.neristbuddy.R;

public class cca_activity extends AppCompatActivity{
    Button shri;
    Button rac;
    Button syn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cca_activity);
        shri=findViewById(R.id.shristi);
        rac=findViewById(R.id.racaf);
        syn=findViewById(R.id.synergy);
        
        shri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                gotoUrl("http://shristi21.in/");
                String url="http://shristi21.in/";
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        rac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                gotoUrl("https://www.instagram.com/racafnerist/?igshid=1xhh8j0anxpiq");
                String url="https://www.instagram.com/racafnerist/?igshid=1xhh8j0anxpiq";
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }

        });
        syn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                gotoUrl("https://www.instagram.com/synergy19.nerist/?igshid=1knv36qozm0nk");
                String url="https://www.instagram.com/synergy19.nerist/?igshid=1knv36qozm0nk";
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });


    }

    private void gotoUrl(String s) {
    }
}
