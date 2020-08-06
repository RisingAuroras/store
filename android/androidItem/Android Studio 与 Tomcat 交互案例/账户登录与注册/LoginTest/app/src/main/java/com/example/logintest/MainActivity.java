package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText admin;
    private EditText password;
    private Button login;
    private Button regist;

    final String URL_LOGIN ="http://10.0.2.2:8080/LoginTest/loginCheckServlet";//根据自己的项目需要修改

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        admin = (EditText)findViewById(R.id.admin);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        regist = (Button) findViewById(R.id.regist);

        login.setOnClickListener(this);
        regist.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login:
                Log.d("Main","Start");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String ad = admin.getText().toString();
                        String pw = password.getText().toString();
                        String flag = "";

                        try {
                            Log.d("Main","Continue");
                            URL url =new URL(URL_LOGIN);

                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                            conn.setRequestMethod("POST"); //设置请求方式为post

                            conn.setReadTimeout(5000);//设置超时信息
                            conn.setConnectTimeout(5000);//设置超时信息

                            conn.setDoInput(true);//设置输入流，允许输入
                            conn.setDoOutput(true);//设置输出流，允许输出
                            conn.setUseCaches(false);//设置POST请求方式不能够使用缓存

                            String data = "admin=" + ad + "&password=" + pw + "&flag=login";

                            OutputStream out = conn.getOutputStream();

                            out.write(data.getBytes());

                            out.flush();

                            out.close();

                            BufferedReader reader = new BufferedReader(new InputStreamReader(
                                    conn.getInputStream()));
                            String line = null;
                            if ((line = reader.readLine()) != null) {
                                 /*
                                   如果数据比较多的话要把if换成while，循环体代码也要小改一下，
                                    由于我当时只是测试，就没改
                                  */
                                 flag = line;

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String msg = "";
                        if(flag.equals("true")) msg = "登录成功";
                        else msg = "失败";

                        Log.d("Main",msg);
                        Looper.prepare();
                        Toast.makeText(MainActivity.this, msg,
                                Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }).start();

                break;
            case R.id.regist:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String ad = admin.getText().toString();
                        String pw = password.getText().toString();
                        String flag = "";

                        try {
                            URL url =new URL(URL_LOGIN);

                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                            conn.setRequestMethod("POST"); //设置请求方式为post

                            conn.setReadTimeout(5000);//设置超时信息
                            conn.setConnectTimeout(5000);//设置超时信息

                            conn.setDoInput(true);//设置输入流，允许输入
                            conn.setDoOutput(true);//设置输出流，允许输出
                            conn.setUseCaches(false);//设置POST请求方式不能够使用缓存

                            String data = "admin=" + ad + "&password=" + pw + "&flag=regist";

                            OutputStream out = conn.getOutputStream();

                            out.write(data.getBytes());

                            out.flush();

                            out.close();

                            BufferedReader reader = new BufferedReader(new InputStreamReader(
                                    conn.getInputStream()));
                            String line = null;
                            if ((line = reader.readLine()) != null) {
                                 /*
                                   如果数据比较多的话要把if换成while，循环体代码也要小改一下，
                                    由于我当时只是测试，就没改
                                  */
                                flag = line;

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String msg = "";
                        if(flag.equals("true")) msg = "注册成功";
                        else msg = "注册失败";

                        Looper.prepare();
                        Toast.makeText(MainActivity.this, msg,
                                Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }).start();

                break;
            default:


        }
    }
}