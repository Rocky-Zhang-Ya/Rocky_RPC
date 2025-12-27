package com.zxy.netty_rpc.util;

import com.zxy.netty_rpc.param.Response;

public class ResponseUtil {

	
	public static Response createSuccessResponse(){
		return new Response();
	}
	
	public static Response createSuccessResponse(Object content){
		Response response = new Response();
		response.setContent(content);
		
		return response;
	}
	
	public static Response createFailResponse(String code,String msg){
		Response response = new Response();
		response.setCode(code);
		response.setMsg(msg);
		
		return response;
	}
	
}
