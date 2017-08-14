package com.example.foodmanager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import static com.example.foodmanager.OCRDeclaration.RESULT_INVALID_ERROR;
import static com.example.foodmanager.OCRDeclaration.RESULT_NOT_TAKE;
import static com.example.foodmanager.OCRDeclaration.RESULT_SUCSESS;

public class OCRActivity extends AppCompatActivity {

    private final static int RESULT_CAMERA = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);    //Intent生成
        startActivityForResult(intent, RESULT_CAMERA);                    //CameraActivity起動
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // インテント生成
        Intent intent = new Intent();
        setResult(RESULT_INVALID_ERROR, intent);

        if (requestCode == RESULT_CAMERA) {
            //未撮影の場合画像を取り込まずに返却する
            if(resultCode != RESULT_OK){
                setResult(RESULT_NOT_TAKE, intent);
                finish();
                return ;
            }

            // インテントに格納
            setResult(RESULT_SUCSESS,intent);

            finish();
        }
    }
}
