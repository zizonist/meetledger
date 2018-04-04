package meetledger.core.consensus.pbft;

import meetledger.core.consensus.Message;

/**
 * Message 처리하는 Thread
 * @author sangwon
 *
 */
public class ProcessingMessage implements Runnable {
	
	private Message message;
	public ProcessingMessage(Message message){
		this.message = message;
	}
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.message.process();
	}

}
