package meetledger.core.consensus.pbft;

import java.util.List;

import meetledger.core.Peer;
import meetledger.core.consensus.AbstractMessage;
import meetledger.core.consensus.Message;
import meetledger.core.consensus.MessageBroker;
import meetledger.util.OperatingMode;

public abstract class PbftMessage extends AbstractMessage{ 
	private static final long serialVersionUID = -7602791035850800840L;
	public String roundId = null;
	
	 
	public void broadcastMessage(Message message){

		List<String> peerList = Peer.getPeerIpList();
		
		for(String peerIp : peerList){
			if( !peerIp.equals(proposerIp) || OperatingMode.getInstance().isDev() ){
				MessageBroker.getInstance().sendMessage(peerIp, message);
			}
		}
	}
	
	public void replyProposer(PbftMessage message){
		MessageBroker.getInstance().sendMessage(message.proposerIp, message);
	}
}
