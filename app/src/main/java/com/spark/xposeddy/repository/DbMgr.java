package com.spark.xposeddy.repository;

import android.content.Context;

import com.spark.xposeddy.repository.bean.CommentBean;
import com.spark.xposeddy.repository.greendao.CommentBeanDao;
import com.spark.xposeddy.repository.greendao.DaoMaster;
import com.spark.xposeddy.repository.greendao.DaoSession;

import org.greenrobot.greendao.query.Query;

import java.util.List;

public class DbMgr {
    private final String latestName = "latest_comment.db";
    private Context mContext;
    private CommentBeanDao mLatestCommentDao;
    private static DbMgr mInstance;

    private DbMgr(Context context) {
        mContext = context.getApplicationContext();

        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(mContext, latestName, null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        DaoSession daoSession = daoMaster.newSession();
        mLatestCommentDao = daoSession.getCommentBeanDao();
    }

    public static DbMgr getSingleInstance(Context context) {
        if (mInstance == null) {
            synchronized (DbMgr.class) {
                if (mInstance == null) {
                    mInstance = new DbMgr(context);
                }
            }
        }

        return mInstance;
    }

    public long insertComment(CommentBean comment) {
        return mLatestCommentDao.insertOrReplace(comment);
    }

    public CommentBean readComment(String awemeId) {
        Query<CommentBean> query = mLatestCommentDao.queryBuilder().where(CommentBeanDao.Properties.AwemeId.eq(awemeId)).build();
        return query.unique();
    }

    public List<CommentBean> readComments() {
        return mLatestCommentDao.loadAll();
    }

    public void clearComment() {
        mLatestCommentDao.deleteAll();
    }
}
