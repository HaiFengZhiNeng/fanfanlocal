package com.fanfan.robot.local.service.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.robot.local.app.common.base.BaseEvent;
import com.fanfan.robot.local.model.SerialBean;

/**
 * Created by android on 2017/12/26.
 */

public class SerialForActivityEvent extends BaseEvent<SerialBean> {

    public SerialForActivityEvent(@Nullable String uuid) {
        super(uuid);
    }

    public SerialForActivityEvent(@Nullable String uuid, @NonNull Integer code, @Nullable SerialBean serialBean) {
        super(uuid, code, serialBean);
    }
}
