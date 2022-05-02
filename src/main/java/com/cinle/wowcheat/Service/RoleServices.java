package com.cinle.wowcheat.Service;

import com.cinle.wowcheat.Enum.RoleEnum;
import com.cinle.wowcheat.Model.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleServices {

   List<Role>  selectByUseruid(String uuid);

   List<String> selectUserIdsByRoleType(RoleEnum roleType);
}
