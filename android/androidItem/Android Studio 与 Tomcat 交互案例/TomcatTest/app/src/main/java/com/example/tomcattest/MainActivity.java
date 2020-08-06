package com.example.tomcattest;

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
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText shuju1;
    private EditText shuju2;

    String n1,n2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.sum);
        shuju1 = (EditText) findViewById(R.id.shuju1);
        shuju2 = (EditText) findViewById(R.id.shuju2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
//                    Log.d("MainActivity","Start");
                    final String URL="http://10.0.2.2:8080/WebProject_war/Servlet";//根据自己的项目需要修改

                    new Thread(new Runnable() {
                        public void run()
                        {
                            String msg = "";

                            n1 = shuju1.getText().toString();
                            n2 = shuju2.getText().toString();

                            try {

//                                Log.d("MainActivity","Continue");
                                URL url = new URL(URL);//生成一个URL实例，指向我们刚才设定的地址URL

                                /*
                                openConnection()方法只是创建了一个HttpURLConnection实例，
                                   并不是真正连接，在连接之前可以设置一些属性
                                 */
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                                conn.setRequestMethod("POST"); //设置请求方式为post

                                conn.setReadTimeout(5000);//设置超时信息
                                conn.setConnectTimeout(5000);//设置超时信息

                                conn.setDoInput(true);//设置输入流，允许输入
                                conn.setDoOutput(true);//设置输出流，允许输出
                                conn.setUseCaches(false);//设置POST请求方式不能够使用缓存

                                /*
                                定义我们要传给servlet的参数，格式好像一定要xxx=xxx，代表键值对，
                                  如果有多组，要加一个&，                                                                                         //如“cmd1=version&cmd2=value”
                                 */
                                String data = "n1=" + n1 + "&n2=" + n2;

//                                System.out.println("shuju" +" " +  n1 +" "+n2);

                                /*
                                获取输出流，其实在这之前还应该有一个操作：conn.connect()；
                                  意思为建立HttpURLConnection连接，只不过//getOutputStream()方法会隐含
                                     进行连接,所以不调用connect()也可以建立连接
                                 */
                                OutputStream out = conn.getOutputStream();

                                //把data里的数据以字节的形式写入out流中
                                out.write(data.getBytes());

                                //刷新，将数据缓冲区中的数据全部输出，并清空缓冲区
                                out.flush();

                                //关闭输出流并释放与流相关的资源
                                out.close();

                                /*
                                这里是将conn.getInputStream中的数据包装在字符流的缓冲流reader中
                                这里值得一说的是：无论是post还是get，http请求实际上直到HttpURLConnection
                                   的getInputStream()这个函数
                                里面才正式发出去，同时getInputStream返回的值就是servlet返回的数据
                                 */
                                BufferedReader reader = new BufferedReader(new InputStreamReader(
                                        conn.getInputStream()));
                                String line = null;
                                if ((line = reader.readLine()) != null) {
                                    /*
                                    如果数据比较多的话要把if换成while，循环体代码也要小改一下，
                                    由于我当时只是测试，就没改
                                     */
                                    msg = line;
                                }
                                Looper.prepare();

                                Toast.makeText(MainActivity.this, "The answer is " + msg, Toast.LENGTH_SHORT).show();

                                Looper.loop();

                                conn.disconnect();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}