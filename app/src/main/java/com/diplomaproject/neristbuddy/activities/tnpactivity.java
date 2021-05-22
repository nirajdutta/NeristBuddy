package com.diplomaproject.neristbuddy.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.diplomaproject.neristbuddy.R;

public class tnpactivity extends AppCompatActivity {
    Button link;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tnp);
        setTitle("Nerist T&P Cell");

        link=findViewById(R.id.tnpcontact);
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                gotoUrl("https://nerist.ac.in/placement-cell");
                String url="https://nerist.ac.in/placement-cell";
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

    }

    private void gotoUrl(String s) {
    }
}
