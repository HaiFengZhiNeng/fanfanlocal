package com.fanfan.robot.local.db;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.fanfan.robot.local.model.LocalBean;
import com.fanfan.robot.local.model.VoiceBean;

import com.fanfan.robot.local.db.LocalBeanDao;
import com.fanfan.robot.local.db.VoiceBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig localBeanDaoConfig;
    private final DaoConfig voiceBeanDaoConfig;

    private final LocalBeanDao localBeanDao;
    private final VoiceBeanDao voiceBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        localBeanDaoConfig = daoConfigMap.get(LocalBeanDao.class).clone();
        localBeanDaoConfig.initIdentityScope(type);

        voiceBeanDaoConfig = daoConfigMap.get(VoiceBeanDao.class).clone();
        voiceBeanDaoConfig.initIdentityScope(type);

        localBeanDao = new LocalBeanDao(localBeanDaoConfig, this);
        voiceBeanDao = new VoiceBeanDao(voiceBeanDaoConfig, this);

        registerDao(LocalBean.class, localBeanDao);
        registerDao(VoiceBean.class, voiceBeanDao);
    }
    
    public void clear() {
        localBeanDaoConfig.clearIdentityScope();
        voiceBeanDaoConfig.clearIdentityScope();
    }

    public LocalBeanDao getLocalBeanDao() {
        return localBeanDao;
    }

    public VoiceBeanDao getVoiceBeanDao() {
        return voiceBeanDao;
    }

}