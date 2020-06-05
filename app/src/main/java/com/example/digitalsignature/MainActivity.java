package com.example.digitalsignature;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MyDigest myDigest = new MyDigest();
//                myDigest.testDigest();

//                TestDES testDES = new TestDES();
//                testDES.run();

                TestDHKey testDHKey = new TestDHKey();
                try {
                    testDHKey.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

    }
}
