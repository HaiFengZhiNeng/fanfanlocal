package com.fanfan.robot.local.db.manager;

import com.fanfan.robot.local.db.LocalBeanDao;
import com.fanfan.robot.local.db.VoiceBeanDao;
import com.fanfan.robot.local.db.base.BaseManager;
import com.fanfan.robot.local.model.LocalBean;
import com.fanfan.robot.local.model.VoiceBean;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * Created by android on 2017/12/20.
 */

public class LocalDBManager extends BaseManager<LocalBean, Long> {

    @Override
    public AbstractDao<LocalBean, Long> getAbstractDao() {
        return daoSession.getLocalBeanDao();
    }


    public List<LocalBean> queryLikeLocalByQuestion(String question) {
        Query<LocalBean> build = null;
        try {
            build = getAbstractDao().queryBuilder()
                    .where(LocalBeanDao.Properties.ShowTitle.like("%" + question + "%"))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return build.list();
    }
}
