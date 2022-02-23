package com.cinle.wowcheat.Enum;

/**
 * @Author JunLe
 * @Time 2022/2/21 9:00
 * 数据库权限枚举
 */
public enum RoleEnum {
    /**
     * 管理员，对应数据库0
     * */
    ADMIN("admin",0),

    /**
     * 普通用户，对应数据库1
     * */
    NORMAL("normal",1),

    /**
     * VIP，对应数据库2
     * */
    VIP("vip",2);
    private  String name;
    private  int index;


    RoleEnum(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public int getIndex() {
        return this.index;
      }
    public String getName(){
        return this.name;
    }

    public static String getNameByIndex(int index){
        String name = null;
            for (RoleEnum r : RoleEnum.values()){
                if (r.getIndex() == index)
                    name = r.getName();
            }

            return name;
    }

}
