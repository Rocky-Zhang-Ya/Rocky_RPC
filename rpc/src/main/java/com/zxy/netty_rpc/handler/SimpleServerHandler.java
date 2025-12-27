package com.zxy.netty_rpc.handler;


import com.alibaba.fastjson.JSONObject;
import com.zxy.netty_rpc.param.Response;
import com.zxy.netty_rpc.param.ServerRequest;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class SimpleServerHandler extends ChannelInboundHandlerAdapter  {

	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		
		
		ServerRequest request = JSONObject.parseObject(msg.toString(), ServerRequest.class);
		Response response = new Response();
		response.setId(request.getId());
		response.setContent("the server has received, its ok");
		ctx.channel().writeAndFlush(JSONObject.toJSONString(response));
		ctx.channel().writeAndFlush("\r\n");
//		ctx.channel().close();
	}
	@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
			if (evt instanceof IdleStateEvent) {
				IdleStateEvent event = (IdleStateEvent)evt;
				if (event.state().equals(IdleState.READER_IDLE)) {
					System.out.println("读空闲触发");
					ctx.channel().close();
				}else if (event.state().equals(IdleState.WRITER_IDLE)) {
					System.out.println("写空闲触发");
				}else if (event.state().equals(IdleState.ALL_IDLE)) {
					System.out.println("读写空闲");
					ctx.channel().writeAndFlush("read and write idle");
				}
			}
		}

}
