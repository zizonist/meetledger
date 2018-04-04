package meetledger.core.consensus;
 
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import meetledger.MeetLedger;
import meetledger.core.block.Block;
import meetledger.thrift.client.LedgerClient;
import meetledger.thrift.meta.TMessage;

/**
 * 
 * @author sangwon
 *
 */
public class MessageBroker {
	private static Logger logger = LoggerFactory.getLogger(MeetLedger.class);
	private static class MessageBrokerInstance{
		public static MessageBroker instance = new MessageBroker();
	}
	private MessageBroker(){
		
	}
	public static MessageBroker getInstance(){
		return MessageBrokerInstance.instance;
	}
	
	public void putMessage(byte[] message) throws InterruptedException{ 
		Message imessage = (Message)SerializationUtils.deserialize(message);
		MessageQueue.getInstance().putMessage(imessage);
	}
	
	public void sendMessage(String peerIp, Message message) {
		TMessage tmessage = new TMessage(); 
		tmessage.setMessage( SerializationUtils.serialize(  message ) );
		LedgerClient.getInstance().sendMessage(peerIp, tmessage);
	} 
	
	public boolean insertBlock(Block block){
		if( logger.isDebugEnabled() ){
			logger.debug("insertBlock =====> "+block.getBlockHash().toString());
		}
		boolean bResult = true;
		return bResult;
	}
}
