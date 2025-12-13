package com.community.service.impl;

import com.community.dao.UserDao;
import com.community.dao.BrowseHistoryDao;
import com.community.model.User;
import com.community.model.BrowseHistory;
import com.community.service.UserProfileService;
import com.community.util.StringUtil;
import java.util.Date;
import java.util.List;

public class UserProfileServiceImpl implements UserProfileService {

    private UserDao userDao;
    private BrowseHistoryDao browseHistoryDao;

    public UserProfileServiceImpl(UserDao userDao, BrowseHistoryDao browseHistoryDao) {
        this.userDao = userDao;
        this.browseHistoryDao = browseHistoryDao;
    }

    @Override
    public boolean updateSignature(int userId, String signature) {
        if (userId <= 0) {
            throw new IllegalArgumentException("用户ID无效");
        }

        if (StringUtil.isEmpty(signature)) {
            throw new IllegalArgumentException("签名内容不能为空");
        }

        if (signature.length() > 100) {
            throw new IllegalArgumentException("签名不能超过100个字符");
        }

        User user = userDao.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        if (user.getStatus() == 1) {
            throw new IllegalArgumentException("用户已被封禁");
        }

        user.setSignature(signature);
        int result = userDao.update(user);

        return result > 0;
    }

    @Override
    public void addBrowseHistory(int userId, int postId) {
        if (userId <= 0 || postId <= 0) {
            throw new IllegalArgumentException("参数无效");
        }

        boolean alreadyViewed = browseHistoryDao.checkUserViewedPost(userId, postId);
        if (alreadyViewed) {
            return;
        }

        BrowseHistory history = new BrowseHistory();
        history.setUserId(userId);
        history.setPostId(postId);
        history.setBrowseTime(new Date());

        browseHistoryDao.addBrowseHistory(history);
    }

    @Override
    public List<BrowseHistory> getBrowseHistory(int userId, int page, int pageSize) {
        if (page < 1 || pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("分页参数错误");
        }

        int offset = (page - 1) * pageSize;
        return browseHistoryDao.getUserBrowseHistoryList(userId, offset, pageSize);
    }

    @Override
    public boolean clearBrowseHistory(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("用户ID无效");
        }

        User user = userDao.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        int result = browseHistoryDao.clearUserBrowseHistory(userId);

        return result > 0;
    }
}