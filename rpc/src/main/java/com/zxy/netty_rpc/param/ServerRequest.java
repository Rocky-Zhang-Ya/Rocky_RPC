package com.zxy.netty_rpc.param;

public class ServerRequest {
	// 用来接收和封装 客户端的请求数据
	
	private Long id;
	private Object content;
	
	private String command;
	
	
	
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Object getContent() {
		return content;
	}
	public void setContent(Object content) {
		this.content = content;
	}
	
	
	
	
	
}
