package com.zxy.netty_rpc.future;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.zxy.netty_rpc.param.ClientRequest;
import com.zxy.netty_rpc.param.Response;

public class ResultFuture {
	
	public static final ConcurrentHashMap<Long, ResultFuture> allResultFuture = new ConcurrentHashMap<Long,ResultFuture>();
	final Lock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();
	private Response response;
	
	public ResultFuture(ClientRequest request) {
		allResultFuture.put(request.getId(), this);
	}
	
	



	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}




	// 主线程 想要获取数据，首先要等待结果
	public Response get () {
		
		lock.lock();
		try {
			while (!done()) {
				condition.await();
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			lock.unlock();
		}
		
		return this.response;
	}

	// TCP长连接 线程安全的 接收response  和修改rf的内容， condition 必须和lock 配合使用
	public static void receive(Response response) {
		ResultFuture rf = allResultFuture.get(response.getId());
		System.out.println(rf.toString());
		if (rf != null) {
			Lock lock = rf.lock;
			lock.lock();
			try {
				rf.setResponse(response);
				rf.condition.signal();
				allResultFuture.remove(rf);
			} finally {
				lock.unlock();
			}
			
		}
	}
	
	
	private boolean done() {
		if (this.response != null) {
			return true;
		
		}
		return false;
	}
	

}
