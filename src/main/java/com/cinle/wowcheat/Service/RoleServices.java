package com.cinle.wowcheat.Service;

import com.cinle.wowcheat.Model.Role;

import java.util.List;

public interface RoleServices {

   List<Role>  selectByUseruid(String uuid);

}
