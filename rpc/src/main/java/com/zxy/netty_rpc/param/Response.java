package com.zxy.netty_rpc.param;

public class Response {
	
	private Long id;
	private Object content;
	
	private String code = "00000";//00000表示成功，其他表示失败
	private String msg;//失败时的原因，失败信息
	
	
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
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
