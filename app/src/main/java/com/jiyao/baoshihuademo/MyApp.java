package com.jiyao.baoshihuademo;

import android.app.Application;

import com.blankj.utilcode.util.CrashUtils;
import com.czm.library.LogUtil;
import com.czm.library.save.imp.CrashWriter;
import com.czm.library.upload.email.EmailReporter;
import com.umeng.commonsdk.UMConfigure;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //崩溃
        CrashUtils.init(getExternalCacheDir() + "/crash");

        //
        initCrashReport();

        //
        UMConfigure.init(this, "5e104fb4570df3b77d000171", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);
        UMConfigure.setLogEnabled(true);
    }

    private void initCrashReport() {
        LogUtil.getInstance()
                .setCacheSize(30 * 1024 * 1024)//支持设置缓存大小，超出后清空
                .setLogDir(getApplicationContext(), "sdcard/" + this.getString(this.getApplicationInfo().labelRes) + "/")//定义路径为：sdcard/[app name]/
                .setWifiOnly(true)//设置只在Wifi状态下上传，设置为false为Wifi和移动网络都上传
                .setLogLeve(LogUtil.LOG_LEVE_INFO)//设置为日常日志也会上传
                //.setLogDebugModel(true) //设置是否显示日志信息
                //.setLogContent(LogUtil.LOG_LEVE_CONTENT_NULL)  //设置是否在邮件内容显示附件信息文字
                .setLogSaver(new CrashWriter(getApplicationContext()))//支持自定义保存崩溃信息的样式
                //.setEncryption(new AESEncode()) //支持日志到AES加密或者DES加密，默认不开启
                .init(getApplicationContext());
        initEmailReporter();
    }

    /**
     * 使用EMAIL发送日志
     */
    private void initEmailReporter() {
        EmailReporter email = new EmailReporter(this);
        email.setReceiver("553761200@qq.com");//收件人
        email.setSender("553761200@qq.com");//发送人邮箱
        email.setSendPassword("nvhzfmnibloabdjj");//邮箱的客户端授权码，注意不是邮箱密码
        email.setSMTPHost("smtp.qq.com");//SMTP地址
        email.setPort("465");//SMTP 端口
        LogUtil.getInstance().setUploadType(email);
    }
}
