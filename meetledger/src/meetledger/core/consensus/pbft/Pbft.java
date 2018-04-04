package meetledger.core.consensus.pbft;

import java.util.concurrent.ExecutorService;

import meetledger.core.consensus.Message;
import meetledger.core.consensus.MessageQueue;
import meetledger.util.NamedExecutorService;

/** 
 * @author sangwon
 *
 */
public class Pbft implements Runnable{ 
	private ExecutorService es = NamedExecutorService.getInstance().initExecutorService(1024, "ProcessionMessage-");

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while( !Thread.currentThread().isInterrupted() ){
			try {
				Message message = MessageQueue.getInstance().getMessage();
				es.execute(new ProcessingMessage(message));  
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
