package com.cinle.wowcheat.Enum;

/**
 * @Author JunLe
 * @Time 2022/3/3 15:01
 */
public enum CheatStatus {
    
    /**正常 */
    Friend_Normal(1),

    /**屏蔽*/
    Friend_Shield(2),
    
    /**拉黑*/
    Friend_Block(3),

    /**被删除*/
    Friend_NotFriend(4);

    private int index;

    CheatStatus(int index) {
        this.index = index;
    }
    public int getIndex() {
        return this.index;
    }
}
