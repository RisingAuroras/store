package com.example.okhttpdownload;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button button;

    String netPath = "http://103.142.204.149:8080/musics/网易云.mp3";
    String savePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.bt_down_pic);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testOkhttpDownLoad();
            }
        });
        PermissionUtil.getPermissions(this, PermissionUtil.READ_EXTERNAL_STORAGE, PermissionUtil.WRITE_EXTERNAL_STORAGE);

    }

    private final int FAILURE_CODE=1001;//失败
    private final int SUCCESS_CODE=1000;//成功

    /*
           看数据库中是否存在该账户以及密码是否正确
        */
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SUCCESS_CODE://成功
                    String data= (String) msg.obj;
                    Log.d("MIN",data);
                    if("true".equals(data.trim()))
                        loginSuccess();
                    else
                        loginFaile();
                    break;
                case FAILURE_CODE://失败
                    loginFaile();
                    break;

            }
        }
    };

    private void loginSuccess() {
    }

    private void loginFaile() {
    }

    public void testOkhttpDownLoad(){
        DownloadUtil.get().download(netPath, "data_shp", new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                //成功
                Log.i("注意","下载成功");
            }

            @Override
            public void onDownloading(int progress) {
                //进度
                Log.i("注意",progress+"%");
            }

            @Override
            public void onDownloadFailed() {
                //失败
                Log.i("注意","下载失败");
            }
        });

    }
}