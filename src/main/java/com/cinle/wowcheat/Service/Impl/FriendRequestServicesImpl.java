package com.cinle.wowcheat.Service.Impl;

import com.cinle.wowcheat.Constants.MessageConst;
import com.cinle.wowcheat.Dao.FriendsRequestDao;
import com.cinle.wowcheat.Model.FriendsRequest;
import com.cinle.wowcheat.Service.FriendRequestServices;
import com.cinle.wowcheat.Utils.SecurityContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author JunLe
 * @Time 2022/4/5 23:56
 */
@Service
public class FriendRequestServicesImpl implements FriendRequestServices {
    @Autowired
    private FriendsRequestDao requestDao;

    @Override
    public int deleteByPrimaryKey(Integer autoId) {
        return requestDao.deleteByPrimaryKey(autoId);
    }

    @Override
    public int insertSelective(FriendsRequest record) {
        record.setRequestTime(new Date());
        record.setRequestStatus(0);
        //查询数据库是否已存在请求信息，已存在则改为更新
        FriendsRequest request = requestDao.selectByRequestAndReceiverUuid(record);
        if (request != null){
            //初始化为0
            request.setRequestStatus(0);
            request.setRequestTime(record.getRequestTime());
            request.setRequestMessage(record.getRequestMessage());
            return requestDao.updateByPrimaryKeySelective(request);
        }
        return requestDao.insertSelective(record);
    }

    @Override
    public FriendsRequest selectByPrimaryKey(Integer autoId) {
        return requestDao.selectByPrimaryKey(autoId);
    }

    @Override
    public int updateByPrimaryKeySelective(FriendsRequest record) {
        return requestDao.updateByPrimaryKeySelective(record);
    }

    @Override
    public List<FriendsRequest> selectListWithUserInfoByShelfUuid(String Uuid) {
        //获取允许查询的起始时间
        Date start = MessageConst.getQueryStartTime();
        return requestDao.selectListWithUserInfoByShelfUuid(Uuid, start);
    }

    @Override
    public FriendsRequest selectWithUserInfoByEachUuid(String requestUuid, String receiverUuid,String infoUuid) {
        return requestDao.selectWithUserInfoByEachUuid(requestUuid, receiverUuid,infoUuid);
    }

    /**
     * 更新时， 确保数据与当前操作者相关，保证数据准确性
     * @param record
     * @return
     */
    @Override
    public int updateRequestStatusByUuid(FriendsRequest record) {
        String shelf = SecurityContextUtils.getCurrentUserUUID();
        //只有接受者为自己时才需要更新状态
        if (shelf.equals(record.getReceiverUuid())){
            return requestDao.updateRequestStatusByUuid(record);
        }
        return 0;
    }

    @Override
    public FriendsRequest selectByRequestAndReceiverUuid(FriendsRequest request) {
        return requestDao.selectByRequestAndReceiverUuid(request);
    }
}
