package com.zxy.netty_rpc.service;

import java.util.List;

import javax.annotation.Resource;

import com.zxy.netty_rpc.annotation.Remote;
import com.zxy.netty_rpc.bean.User;
import com.zxy.netty_rpc.param.Response;
import com.zxy.netty_rpc.util.ResponseUtil;

@Remote
public class UserRemoteImpl  implements UserRemote{
	
	
	@Resource
	private UserService service;
	
	public Response saveUser(User user){
		service.saveUSer(user);
		Response response = ResponseUtil.createSuccessResponse(user);
		
		return response;
	}
	
	public Response saveUsers(List<User> userlist){
		service.saveUSerList(userlist);
		Response response = ResponseUtil.createSuccessResponse(userlist);
		
		return response;
	}
	
}
