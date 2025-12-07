package com.community.dao;

import com.community.dao.impl.UserDaoImpl;
import com.community.model.User;
import com.community.util.DBUtil;

import java.sql.Connection;
import java.util.ArrayList;

public class test {
    public static void main(String[] args)throws Exception {
        UserDao userDao = new UserDaoImpl();
        ArrayList<User> user =new ArrayList<User>();
        user= (ArrayList<User>) userDao.findAll(2,2);
        for(User user1:user){
            System.out.println(user1.getUsername());
            System.out.println(user1.getPassword());
            System.out.println(user1.getEmail());
            System.out.println(user1.getPhone());

        }
    }
}
