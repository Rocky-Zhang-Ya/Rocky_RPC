package com.zxy.netty_rpc.service;

import java.util.List;

import com.zxy.netty_rpc.bean.User;
import com.zxy.netty_rpc.param.Response;

public interface UserRemote {

	public Response saveUser(User user);
	public Response saveUsers(List<User> userlist);
	
	

}
