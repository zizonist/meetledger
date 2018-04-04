package meetledger.util;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set; 
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 

public class NamedExecutorService {
	private Logger logger = LoggerFactory.getLogger(NamedExecutorService.class);
	private Map<String, ExecutorService> map = null;
	private int totalExecutorThreadSize = 0;
	private NamedExecutorService(){
		map = new HashMap<String, ExecutorService>();
	}
	private static class NamedExecutorServiceFactory{
		private static NamedExecutorService nes = new NamedExecutorService();
	}
	
	public static NamedExecutorService getInstance(){
		return NamedExecutorServiceFactory.nes;
	}
	
	public ExecutorService initExecutorService(int size, String name){
		return initExecutorService(size, name, null);
	}
	
	
	public ExecutorService initExecutorService(final int size, final String name, ThreadFactory threadFactory){
		ExecutorService es = null;
		totalExecutorThreadSize += size;
		if( totalExecutorThreadSize > 4096 ){
			totalExecutorThreadSize -= size;
			logger.debug("[zeus-heracules] NamedExecutorService.initExecutorService : Threads-size over flow => "+totalExecutorThreadSize);
			return null;
		}
		if( threadFactory == null ){
			es = Executors.newFixedThreadPool(size,  new ThreadFactory() {  
				private int _size = size;
				@Override
				public Thread newThread(Runnable r) {
					// TODO Auto-generated method stub
					Thread t = new Thread(r);
					String threadName = name+(--_size); 
					//System.out.println(threadName);
					t.setDaemon(true);
					t.setName(threadName);
					return t;
				}
			});
		}else{
			es = Executors.newFixedThreadPool(size,threadFactory);
		}
		map.put(name, es);
		return es;
	}
	
	public void shutDown(String executorServiceName){
		map.get(executorServiceName).shutdown();
	}
	
	public void shutDownAll(){
		Set<Entry<String,ExecutorService>> set = map.entrySet();
		for(Entry<String, ExecutorService> e : set){
			e.getValue().shutdown();
		}
	}
	
}