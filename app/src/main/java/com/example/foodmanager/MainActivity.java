package com.example.foodmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.foodmanager.OCRDeclaration.RESULT_SUCSESS;

public class MainActivity extends AppCompatActivity {

    private final static int RESULT_OCR = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void OnButtonTapped(View view){
        Intent intent = new Intent(this, OCRActivity.class);
        startActivityForResult(intent, RESULT_OCR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_OCR) {
            if (resultCode == RESULT_SUCSESS) {
                ArrayList<String> list = (ArrayList<String>)data.getSerializableExtra("NameList");
                String result1 =list.get(0);
                TextView textView = (TextView) findViewById(R.id.text_name);
                textView.setText(result1);

                String date = data.getStringExtra("Date");
                TextView dateView = (TextView) findViewById(R.id.text_name);
                dateView.setText(date);

                Log.d("date is ",date);
                Log.d("name2 is ",list.get(1));

            }
        }
    }
}
