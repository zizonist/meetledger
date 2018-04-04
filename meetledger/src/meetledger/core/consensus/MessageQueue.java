package meetledger.core.consensus;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * @author sangwon
 *
 */
public class MessageQueue { 
	/**
	 * Queue 
	 */
	private LinkedBlockingQueue<Message> requestQueue = new LinkedBlockingQueue<Message>(8192); 
	
	private static class MessageQueueInstance{
		public static MessageQueue instance = new MessageQueue();
	} 
	private MessageQueue(){ 
	}
	public static MessageQueue getInstance(){
		return MessageQueueInstance.instance;
	} 
	
	public void putMessage(Message message) throws InterruptedException{
		requestQueue.put(message);
	}
	
	public Message getMessage() throws InterruptedException{
		return requestQueue.take();
	}
	
}
