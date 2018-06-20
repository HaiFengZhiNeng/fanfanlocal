package com.fanfan.robot.local.service.callback;

import com.fanfan.robot.local.utils.GsonUtil;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;

import okhttp3.ResponseBody;

public abstract class JsonCallback<T> extends AbsCallback<T> {

    private Class<T> clazz;

    public JsonCallback(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T convertResponse(okhttp3.Response response) throws Throwable {

        ResponseBody body = response.body();
        String string = body.string();
        return GsonUtil.GsonToBean(string, clazz);
    }
}
