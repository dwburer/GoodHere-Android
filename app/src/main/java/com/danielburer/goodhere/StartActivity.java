package com.danielburer.goodhere;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {

    Button btMap, btList;
    TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        initGui();

    }

    public void initGui(){
        btMap = (Button) findViewById(R.id.btMap);
        btList = (Button) findViewById(R.id.btList);
        tvWelcome = (TextView)findViewById(R.id.tvWelcome);

        btMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Change view to Map, Will show nearby

                Intent intent = new Intent(StartActivity.this,MapsActivity.class);

                StartActivity.this.startActivity(intent);

            }
        });


        btList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Change view to List
                Intent intent = new Intent(StartActivity.this,MainActivity.class);

                StartActivity.this.startActivity(intent);
            }
        });

    }

}
