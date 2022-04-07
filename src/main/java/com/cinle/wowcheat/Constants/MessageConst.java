package com.cinle.wowcheat.Constants;

import java.util.Calendar;
import java.util.Date;

/**
 * @Author JunLe
 * @Time 2022/3/5 1:44
 */
public class MessageConst {
    /**
     * 消息过期时间
     * 消息最大查询日期区间
     * 单位：天
     */
    public static final int Message_Expiration = 180;

    /**
     * 分页数量
     */
    public static final int Page_Num = 40;

    /**
     * 计算查询开始时间
     * @return
     */
    public static Date getQueryStartTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, - MessageConst.Message_Expiration);
        return calendar.getTime();
    }

}
