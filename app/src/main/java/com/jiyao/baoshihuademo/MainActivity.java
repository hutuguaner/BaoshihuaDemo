package com.jiyao.baoshihuademo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ToastUtils;
import com.czm.library.LogUtil;
import com.czm.library.upload.email.EmailReporter;
import com.jiyao.baoshihuademo.bean.HuodaoBean;
import com.jiyao.baoshihuademo.util.HexDump;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import android_serialport_api.SerialPort;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private TextView tvCode;
    private StringBuffer stringBuffer;
    private Button bt1, bt2, bt3, bt4, bt5, bt6, bt7, bt8, bt9, bt0, btDel, btOk, btUplog;
    private SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        stringBuffer = new StringBuffer();
        tvCode = findViewById(R.id.tv_code);
        bt0 = findViewById(R.id.bt_0);
        bt1 = findViewById(R.id.bt_1);
        bt2 = findViewById(R.id.bt_2);
        bt3 = findViewById(R.id.bt_3);
        bt4 = findViewById(R.id.bt_4);
        bt5 = findViewById(R.id.bt_5);
        bt6 = findViewById(R.id.bt_6);
        bt7 = findViewById(R.id.bt_7);
        bt8 = findViewById(R.id.bt_8);
        bt9 = findViewById(R.id.bt_9);
        btDel = findViewById(R.id.bt_del);
        btOk = findViewById(R.id.bt_ok);
        btUplog = findViewById(R.id.bt_uplog);

        bt0.setOnClickListener(this);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);
        bt4.setOnClickListener(this);
        bt5.setOnClickListener(this);
        bt6.setOnClickListener(this);
        bt7.setOnClickListener(this);
        bt8.setOnClickListener(this);
        bt9.setOnClickListener(this);
        btDel.setOnClickListener(this);
        btOk.setOnClickListener(this);
        btUplog.setOnLongClickListener(this);

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);


        getConfigInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(MainActivity.class.getSimpleName()); //手动统计页面("SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this); //统计时长
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(MainActivity.class.getSimpleName()); //手动统计页面("SplashScreen"为页面名称，可自定义)
        MobclickAgent.onPause(this);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_0:
                addText("0");
                break;
            case R.id.bt_1:
                addText("1");
                break;
            case R.id.bt_2:
                addText("2");
                break;
            case R.id.bt_3:
                addText("3");
                break;
            case R.id.bt_4:
                addText("4");
                break;
            case R.id.bt_5:
                addText("5");
                break;
            case R.id.bt_6:
                addText("6");
                break;
            case R.id.bt_7:
                addText("7");
                break;
            case R.id.bt_8:
                addText("8");
                break;
            case R.id.bt_9:
                addText("9");
                break;
            case R.id.bt_del:
                delText();
                break;
            case R.id.bt_ok:
                shipMent();
                break;
        }
    }

    //
    private void hehe() {
        EmailReporter email = new EmailReporter(this);
        email.setReceiver("553761200@qq.com");//收件人
        email.setSender("553761200@qq.com");//发送人邮箱
        email.setSendPassword("nvhzfmnibloabdjj");//邮箱的客户端授权码，注意不是邮箱密码
        email.setSMTPHost("smtp.qq.com");//SMTP地址
        email.setPort("465");//SMTP 端口
        LogUtil.getInstance().setUploadType(email);
        ToastUtils.showLong("上报日志");
    }

    private void updateText() {
        tvCode.setText(stringBuffer.toString());
    }

    private void addText(String text) {
        if (stringBuffer.length() == 10) {
            return;
        }
        stringBuffer.append(text);
        updateText();
    }

    private void delText() {
        if (stringBuffer.length() < 1) {
            return;
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        updateText();
    }


    //

    private void shipMent() {
        final String code = tvCode.getText().toString();
        if (code == null || code.trim().equals("")) {
            ToastUtils.showLong("请输入取药码");
            return;
        }
        OkGo.<String>get("http://jk.gfhealthcare.com/cabinet/validcode.ashx?code=" + code).execute(new StringCallback() {

            @Override
            public void onStart(Request<String, ? extends Request> request) {
                super.onStart(request);
                pDialog.show();
            }

            @Override
            public void onSuccess(Response<String> response) {
                String res = response.body();
                if (res.equals("-1")) {
                    ToastUtils.showLong("已取走");
                } else if (res.equals("-2")) {
                    ToastUtils.showLong("不存在");
                } else {
                    quYao(res);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pDialog.dismiss();
            }
        });
    }

    //
    private InputStream inputStream;
    private OutputStream outputStream;
    private int cmdSerialNumber = 0x00;

    private void quYao(final String huodao) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SerialPort serialPort = new SerialPort(new File("/dev/ttyS1"), 115200, 0);
                    inputStream = serialPort.getInputStream();
                    outputStream = serialPort.getOutputStream();

                    if (cmdSerialNumber > 0xfa) {
                        cmdSerialNumber = 0x00;
                    }

                    cmdSerialNumber++;

                    String first = getFirstPoint(huodao);
                    String second = getSecondPoint(huodao);
                    int delay = getDelay(huodao);
                    Log.i("hehe", "first: " + first + " second: " + second + " delay: " + delay);
                    String firstHexStr = Integer.toHexString(Integer.parseInt(first));
                    String secondHexStr = Integer.toHexString(Integer.parseInt(second));
                    String delayHexStr = Integer.toHexString(delay);
                    Log.i("hehe", "firsthxs: " + firstHexStr + " secondhxs: " + secondHexStr + " delayHxs: " + delayHexStr);
                    int firstHex = Integer.parseInt(firstHexStr, 16);
                    int secondHex = Integer.parseInt(secondHexStr, 16);
                    int delayHex = Integer.parseInt(delayHexStr, 16);
                    //AA 01 17 04 00 10 01 2C
                    byte[] writeCmd = new byte[]{(byte) 0xaa, 0x01, (byte) cmdSerialNumber, 0x04, (byte) firstHex, (byte) secondHex, (byte) delayHex};
                    Log.i("hehe", "写命令： " + HexDump.toHexString(writeCmd));
                    outputStream.write(writeCmd);
                    //AA 01 1D 09
                    //outputStream.write(new byte[]{(byte) 0xaa, 0x01, 0x1d, 0x09});
                    outputStream.flush();

                    //
                    int readCount = 0;
                    while (readCount <= 0) {
                        readCount = inputStream.available();
                        Log.i("hehe", "读到的字节数：" + readCount);
                        SystemClock.sleep(100);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("hehe", "找不到改设备文件： " + e.getMessage());
                }


            }
        }).start();
    }

    //根据货到编号取  第一第二控制点
    private String getFirstPoint(String huodao) {
        for (HuodaoBean huodaoBean : huodaoBeans) {
            String rail = huodaoBean.getRailCode();
            if (rail.equals(huodao)) {
                return huodaoBean.getFirstPoint();
            }

        }
        return null;
    }

    private String getSecondPoint(String huodao) {
        for (HuodaoBean huodaoBean : huodaoBeans) {
            String rail = huodaoBean.getRailCode();
            if (rail.equals(huodao)) {
                return huodaoBean.getSecondPoint();
            }
        }
        return null;
    }

    private int getDelay(String huodao) {
        for (HuodaoBean huodaoBean : huodaoBeans) {
            String rail = huodaoBean.getRailCode();
            if (rail.equals(huodao)) {
                return huodaoBean.getDelayTime();
            }
        }
        return 50;
    }


    static List<HuodaoBean> huodaoBeans;

    //获取配置信息
    private void getConfigInfo() {
        OkGo.<String>post("https://www.jiyaovip.com/app/getCabinetRail").params("mac", "22:77:0d:48:15:ca").execute(new StringCallback() {

            @Override
            public void onStart(Request<String, ? extends Request> request) {
                super.onStart(request);
                pDialog.show();
            }

            @Override
            public void onSuccess(Response<String> response) {

                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        String data = jsonObject.getString("data");
                        huodaoBeans = JSON.parseArray(data, HuodaoBean.class);
                        for (HuodaoBean huodaoBean : huodaoBeans) {
                            String railCode = huodaoBean.getRailCode();
                            String firstPoint = huodaoBean.getFirstPoint();
                            String secondPoint = huodaoBean.getSecondPoint();
                        }
                    } else {
                        String msg = jsonObject.getString("msg");
                        ToastUtils.showLong("获取配置信息出错: " + msg);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pDialog.dismiss();
            }
        });
    }


    @Override
    public boolean onLongClick(View view) {
        hehe();
        return true;
    }


}
