package com.zxy.netty_rpc.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;

import com.zxy.netty_rpc.bean.User;
import com.zxy.netty_rpc.param.Response;
import com.zxy.netty_rpc.service.UserService;
import com.zxy.netty_rpc.util.ResponseUtil;

@Controller
public class UserController {
	
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
