package com.zxy.netty_rpc.handler;
import com.alibaba.fastjson.JSONObject;
import com.zxy.netty_rpc.future.ResultFuture;
import com.zxy.netty_rpc.param.Response;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

public class SimpleClientHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println(msg.toString());
		//为了将 异步接收到的 服务端响应rsp信息 msg 传回主线程 nettyclient中  
		// 使用 AttributeKey 来保存 服务端传来的信息
//		ctx.channel().attr(AttributeKey.valueOf("rsp")).set(msg);
		
		Response response = JSONObject.parseObject(msg.toString(), Response.class);
		ResultFuture.receive(response);
		
		
//		ctx.channel().close();

	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		// TODO Auto-generated method stub
		super.userEventTriggered(ctx, evt);
	}

	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	    // 1. 判断是否是“远程主机强迫关闭”错误
	    if (cause instanceof java.io.IOException) {
	        System.out.println("连接断开（对端关闭了连接）: " + ctx.channel().remoteAddress());
	    } else {
	        // 其他错误才打印堆栈
	        cause.printStackTrace();
	    }
	    
	    // 2. 出错后，通常需要关闭连接，防止资源泄露
	    ctx.close();
	}
	

}
