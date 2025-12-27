package com.zxy.netty_rpc.handler;

import java.awt.MediaTracker;

import com.alibaba.fastjson.JSONObject;
import com.zxy.netty_rpc.future.ResultFuture;
import com.zxy.netty_rpc.medium.Media;
import com.zxy.netty_rpc.param.Response;
import com.zxy.netty_rpc.param.ServerRequest;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ServerRequest serverRequest = JSONObject.parseObject(msg.toString(),ServerRequest.class);
		Media media = Media.newInstance();
		Response result=media.process(serverRequest);
		
		//向客户端发送Response
		ctx.channel().writeAndFlush(JSONObject.toJSONString(result));
		ctx.channel().writeAndFlush("\r\n");
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
