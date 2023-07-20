package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息,用于代替session对象. 作为一个容器
 * 因为session对象会对服务器的存储要求高
 */

@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUsers(User user){
        users.set(user);
    }

    public User getUsers(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
