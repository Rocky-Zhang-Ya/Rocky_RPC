package com.zxy.netty_rpc.client;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
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
	private Long timeout = 2*60*1000l;
	private Long startTime = System.currentTimeMillis();
	
	
	
	public Long getTimeout() {
		return timeout;
	}

	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}

	public Long getStartTime() {
		return startTime;
	}

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
	public Response get (Long time) {
		
		lock.lock();
		try {
			while (!done()) {
				
				condition.await(time,TimeUnit.MILLISECONDS);
				long t=System.currentTimeMillis()-startTime;
				System.out.println("t:"+t+"time:"+time);
				if (System.currentTimeMillis()-startTime>time) {
					System.out.println("异步future请求超时了！");
					break; // 超时时间 大于time 就返回null；
				}
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
	
	
	
	//清理线程
	static class ClearFutureThread extends Thread{
		@Override
		public void run() {
			Set<Long> ids = allResultFuture.keySet();
			for(Long id : ids){
				ResultFuture f = allResultFuture.get(id);
				if(f==null){ // 删除ResultFuture 为空的 记录
					allResultFuture.remove(f);
				}else if(f.getTimeout()<(System.currentTimeMillis()-f.getStartTime()))
				{//链路超时
					Response res = new Response();
					res.setId(id);
					res.setCode("33333");
					res.setMsg("链路超时");
					receive(res); // 在静态类的方法中 可以不用对象直接调用receive方法
				}
			}
		}
	}
	
	static{
		ClearFutureThread clearThread = new ClearFutureThread();
		clearThread.setDaemon(true);
		clearThread.start();
	}
	
	

}
