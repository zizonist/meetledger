package meetledger.test;
 
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import meetledger.core.block.Block;
import meetledger.core.consensus.Message;
import meetledger.core.consensus.MessageBroker;
import meetledger.core.consensus.MessageBuilder; 

public class PbftTest {
	public static void main(String[] args){ 
		//"192.168.0.9", new Block(), roundId
		
		ExecutorService es = Executors.newFixedThreadPool(100);
		
		for(int i=0; i<100; i++){
			final int a = i;
			es.execute(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					while(!Thread.currentThread().isInterrupted()){
						try { 
							Block block = new Block();
							//block.setBlockId(a+""); //sha256
							//block.setPrevBlockId(a+"");
							Message message = MessageBuilder.getInstance().build("192.168.0.9", block);
							MessageBroker.getInstance().sendMessage("192.168.0.9", message); 
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
		}
	}
}
