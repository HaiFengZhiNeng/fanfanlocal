package com.fanfan.robot.local.service.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.robot.local.app.common.base.BaseEvent;

public class CameraForActivityEvent extends BaseEvent<String> {
    public CameraForActivityEvent(@Nullable String uuid) {
        super(uuid);
    }

    public CameraForActivityEvent(@Nullable String uuid, @NonNull Integer code, @Nullable String s) {
        super(uuid, code, s);
    }
}
