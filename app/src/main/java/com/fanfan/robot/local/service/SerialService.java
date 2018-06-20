package com.fanfan.robot.local.service;

import com.fanfan.robot.local.app.common.base.BaseService;
import com.fanfan.robot.local.model.SerialBean;
import com.fanfan.robot.local.service.event.SerialForServiceEvent;
import com.fanfan.robot.local.service.event.SerialForActivityEvent;
import com.fanfan.serial.HexUtils;
import com.fanfan.serial.SerialPortManager;
import com.fanfan.serial.listener.OnOpenSerialPortListener;
import com.fanfan.serial.listener.OnSerialPortDataListener;
import com.robot.seabreeze.log.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

/**
 * Created by android on 2017/12/26.
 */

public class SerialService extends BaseService implements OnOpenSerialPortListener, OnSerialPortDataListener {

    public static final String DEV = "/dev/";

    public static final String baudrateName = "ttyS4";

    public static final int VOICE_BAUDRATE = 115200;

    private SerialPortManager mManager;

    @Override
    public void onCreate() {
        super.onCreate();

        mManager = init(mManager, new File(DEV + baudrateName), VOICE_BAUDRATE);

    }

    private SerialPortManager init(SerialPortManager serialPortManager, File file, int baudRate) {
        serialPortManager = new SerialPortManager();
        serialPortManager.setOnOpenSerialPortListener(this)
                .setOnSerialPortDataListener(this)
                .openSerialPort(file, baudRate);
        return serialPortManager;
    }

    @Override
    public void onDestroy() {
        close(mManager);
        super.onDestroy();
    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onResultEvent(SerialForServiceEvent event) {
        if (event.isOk()) {
            SerialBean serialBean = event.getBean();
            Log.e("activity发送到service中的数据 " + serialBean.toString());
            int iBaudRate = serialBean.getBaudRate();
            if (iBaudRate == VOICE_BAUDRATE) {
//                byte[] bOutArray = HexUtils.HexToByteArr(serialBean.getMotion());
                mManager.sendBytes(serialBean.getMotion().getBytes());
            }
        } else {
            Log.e("ReceiveEvent error");
        }
    }


    private void close(SerialPortManager serialPortManager) {
        if (null != serialPortManager) {
            serialPortManager.closeSerialPort();
        }
    }


    //*****************************打开
    @Override
    public void onSuccess(File device, int baudRate) {
        Log.e(String.format("串口 [%s] 打开成功   波特率 %s", device.getPath(), baudRate));
    }

    @Override
    public void onFail(File device, Status status) {
        switch (status) {
            case NO_READ_WRITE_PERMISSION:
                Log.e(device.getPath() + " 没有读写权限");
                break;
            case OPEN_FAIL:
            default:
                Log.e(device.getPath() + " 串口打开失败");
                break;
        }
    }

    //****************************收发数据
    @Override
    public void onDataReceived(String absolute, int baudRate, byte[] bytes) {

        StringBuilder sMsg = new StringBuilder();
        if (baudRate == VOICE_BAUDRATE) {
            //在十六进制转换为字符串后的得到的是Unicode编码,此时再将Unicode编码解码即可获取原始字符串
            sMsg.append(HexUtils.hexStringToString(HexUtils.byte2HexStr(bytes)));
        } else {
            sMsg.append(new String(bytes));
        }
        SerialBean serialBean = new SerialBean();
        serialBean.setAbsolute(absolute);
        serialBean.setBaudRate(baudRate);
        serialBean.setMotion(sMsg.toString());

        Log.e("service中接受到串口的数据" + serialBean.toString());

        SerialForActivityEvent serviceToActivityEvent = new SerialForActivityEvent("");
        serviceToActivityEvent.setEvent(200, serialBean);
        EventBus.getDefault().post(serviceToActivityEvent);
    }

    @Override
    public void onDataSent(String absolute, int baudRate, byte[] bytes) {
        Log.e("send success " + absolute + " " + baudRate);
    }
}
