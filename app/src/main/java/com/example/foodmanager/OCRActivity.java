package com.example.foodmanager;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.foodmanager.OCRDeclaration.RESULT_INVALID_ERROR;
import static com.example.foodmanager.OCRDeclaration.RESULT_NOT_TAKE;
import static com.example.foodmanager.OCRDeclaration.RESULT_SUCSESS;

public class OCRActivity extends AppCompatActivity {

    private final static int RESULT_CAMERA = 1001;
    public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/FoodManager/";
    public static final String FILE_PATH = Environment.getExternalStorageDirectory().toString() + "/FoodManager/";
    private TessBaseAPI tessOCRAPI;

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

        if (RESULT_CAMERA == requestCode) {
            //未撮影の場合画像を取り込まずに返却する
            if(RESULT_OK != resultCode){
                setResult(RESULT_NOT_TAKE, intent);
                finish();
                return ;
            }
            Log.d("dir is ",System.getProperty("user.dir"));

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");              //カメラの画像データを取得する
            ArrayList<String> nameList = GetNameList(bitmap);                    //画像データから商品名リストを取得する
            String date = GetDate();                                             //日付データを取得する

            // インテントにデータを格納
            intent.putExtra("NameList", nameList);
            intent.putExtra("Date", date);

            setResult(RESULT_SUCSESS,intent);

            finish();
        }
    }


    // 商品データリスト変換メソッド
    protected  ArrayList<String> GetNameList(Bitmap bitmap){
        // OCRライブラリ
        tessOCRAPI = new TessBaseAPI();
        InitOCR("jpn");
        String text = ReadText(bitmap);
        ArrayList<String> nameList = ReadNameList(text);
        // クローズ
        tessOCRAPI.end();

        return nameList;
    }
    // OCR初期化
    // OCRライブラリを初期化し、言語を設定する。
    protected boolean InitOCR(String lang)
    {
        // 指定された言語のライブラリが無い場合
        if (!(new File(DATA_PATH + "langlibs/" + lang + ".traineddata")).exists()) {
            try {
                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("langlibs/" + lang + ".traineddata");                    // 書き込む言語データ
                Log.d("output path", DATA_PATH + "langlibs/" + lang + ".traineddata");
                makedirsParent(DATA_PATH + "langlibs/" + lang + ".traineddata");
                OutputStream out = new FileOutputStream(DATA_PATH + "langlibs/" + lang + ".traineddata");  // 書き込み先ファイル

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();

            } catch (IOException e) {
                Log.e("Error","Liblary",e);
                return false;
            }
        }
        // OCRライブラリを初期化する
        tessOCRAPI.init(DATA_PATH + "langlibs/", lang);
        return true;
    }

    /**
     * ファイルの親ディレクトリを作成する.
     * @param filePath
     */
    protected void makedirsParent(String filePath){
        File file = new File(filePath);
        makedirs(file.getParent());
    }

    /**
     * ディレクトリを作成する.
     * @param dirPath
     */
    protected void makedirs(String dirPath){
        File dir = new File(dirPath);
        if(!dir.exists()){
            boolean result = dir.mkdirs();
        }
        Log.d("mkdirs",dir.getPath());
        File dir2 = new File(dirPath);
        if(!dir2.exists()){
            Log.e("makedirs","mkdirerror");
        }
    }

    // テキスト読取
    // BitMapファイルからテキストデータを読み取る
    protected String ReadText(Bitmap bitmap)
    {
        String recognizedText = "";
        tessOCRAPI.setImage(bitmap);                        // 画像を設定
        recognizedText = tessOCRAPI.getUTF8Text();          // 文字列を取得
        Log.d("tess-two",recognizedText);

        return recognizedText;
    }

    // 商品データリスト変換メソッド
    protected  ArrayList<String> ReadNameList(String text){
        ArrayList<String> nameList= new ArrayList<String>();
        nameList.add(text);
        return nameList;
    }

    // 日付取得
    // 2,017年8月15日12時00分の場合は"2017-8-15-12-0"が取得できる。
    protected String GetDate(){
        Date now = new Date(System.currentTimeMillis());
        DateFormat formatter = new SimpleDateFormat("yyyy'-'M'-'d'-'H'-'m");
        String date = formatter.format(now);
        Log.d("now date is ",date);
        return date;
    }
}
