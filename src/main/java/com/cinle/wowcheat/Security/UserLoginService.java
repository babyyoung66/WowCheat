package com.cinle.wowcheat.Security;

import com.cinle.wowcheat.Enum.RoleEnum;
import com.cinle.wowcheat.Model.Role;
import com.cinle.wowcheat.Service.RoleServices;
import com.cinle.wowcheat.Service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author JunLe
 * @Time 2022/2/20 18:26
 */
@Service
public class UserLoginService implements UserDetailsService {

    @Autowired
    UserServices userServices;
    @Autowired
    RoleServices roleServices;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomerUserDetails customerUserDetails = userServices.findForLogin(username);
        if (customerUserDetails == null ){
            /*将错误抛给LoginFailHandler处理*/
            throw new UsernameNotFoundException("未找到用户!");
        }
        //查找用户权限
        List<String> roles=new ArrayList<>();
        List<Role> r = roleServices.selectByUseruid(customerUserDetails.getUuid());
        if (r == null|| r.isEmpty()){
            roles.add(RoleEnum.NORMAL.getName());
        }else {
            for(Role rs : r){
                if(rs.getRole() == null || rs.getRole().equals("")){
                    roles.add(RoleEnum.NORMAL.getName());
                }else {
                    roles.add(rs.getRole().getName());
                }
            }
        }
        customerUserDetails.setAuthorities(AuthorityUtils.commaSeparatedStringToAuthorityList(
                String.join(",",roles)
        ));
        return customerUserDetails;
    }
}
