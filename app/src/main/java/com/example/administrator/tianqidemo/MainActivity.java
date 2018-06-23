package com.example.administrator.tianqidemo;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import FileSet.FileUtil;
import request.HttpThread;

public class MainActivity extends AppCompatActivity {
    private TextView tv;
    private Button btn0,btn1,btn2;
    private EditText et;
    private String url = "https://www.sojson.com/open/api/weather/json.shtml?city=";
    private String output = "";
    private String filetxt="";



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                String obj = (String) msg.obj;
                try {
                    JSONObject jsonobject = new JSONObject(obj);
                    filetxt =jsonobject.toString();
                    output +=jsonobject.getString("city")+"的的天气情况" +"\n";
                    JSONObject jsonobject1 = new JSONObject(jsonobject.getString("data"));
                    JSONArray jsonArray = new JSONArray(jsonobject1.getString("forecast"));
                    Log.i("tag", "handleMessage: "+jsonArray.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonobject2 = (JSONObject) jsonArray.get(i);
                        String date = jsonobject2.getString("date");
                        String sunrise = jsonobject2.getString("sunrise");
                        String high = jsonobject2.getString("high");
                        String low = jsonobject2.getString("low");
                        String sunset = jsonobject2.getString("sunset");
                        String aqi = jsonobject2.getString("aqi");
                        String fx = jsonobject2.getString("fx");
                        String fl = jsonobject2.getString("fl");
                        String type = jsonobject2.getString("type");
                        String notice = jsonobject2.getString("notice");
                        output += date + " " + high + " " + low + " " + fx + " " + fl + " " + type + " " + notice + "\n";
                    }
                    Toast.makeText(MainActivity.this,"请求成功 ！",Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            super.handleMessage(msg);


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);
        et= findViewById(R.id.et);
        btn0 = findViewById(R.id.btn0);
        btn1 = findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FileUtil.saveFile(output, "weather.txt");// 保存为了一个txt文本
                Toast.makeText(MainActivity.this,"保存成功 ！",Toast.LENGTH_SHORT).show();
            }
        });
        btn2 = findViewById(R.id.btn2);

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = FileUtil.getFile("weather.txt");
                tv.setText(data);
            }
        });
        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = et.getText().toString();
                if (!city.equals("")) {

                    try {
                        url = url + URLEncoder.encode(city, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(MainActivity.this,"输入框不可为空！",Toast.LENGTH_SHORT).show();
                }
                et.setText("");
                HttpThread httpThread = new HttpThread(url, handler);
                httpThread.start();
            }
        });
    }
}
