package meetledger.core.consensus.pbft;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import meetledger.conf.GlobalConfiguration;
import meetledger.core.Peer;
import meetledger.core.Primary;
import meetledger.core.block.Block;
import meetledger.core.consensus.MessageBroker;
import meetledger.util.OperatingMode; 

/**
 * NEW ROUND -> PRE-PREPARED:
		Proposer collects transactions from txpool.
		Proposer generates a block proposal and broadcasts it to validators. It then enters the PRE-PREPARED state.
		Each validator enters PRE-PREPARED upon receiving the PRE-PREPARE message with the following conditions:
			Block proposal is from the valid proposer.
			Block header is valid.
			Block proposal's sequence and round match the validator's state.
		Validator broadcasts PREPARE message to other validators.
 * @author sangwon
 *
 */
public class NewRoundMessage extends PbftMessage  {
	private static Logger logger = LoggerFactory.getLogger(NewRoundMessage.class); 
	private static final long serialVersionUID = -8581272330623049525L;
	
	public NewRoundMessage(String proposerIp, Block block  ){ 
		this.proposerIp = proposerIp;
		this.block = block;
		this.roundId = UUID.randomUUID().toString();
	} 

	@Override
	public void process0() {
		// TODO Auto-generated method stub 
		
		if( logger.isDebugEnabled() ){ 
			logger.debug("========================== newround processing ============================");
			logger.debug(" this.proposerIp  : "+this.proposerIp);
			logger.debug(" this.block : "+ this.block.getBlockHash().toString());
			logger.debug(" this.roundId : "+ this.roundId);
		}
		
		Consensus.getInstance().setRoundState(this.roundId, Consensus.RoundState.NEWROUND);
		
		if( Primary.isPrimary ){
			if( logger.isDebugEnabled() ){ 
				logger.debug(""); 
			}
			PrePreparedMessage prePreparedMessage = new PrePreparedMessage();  
			prePreparedMessage.proposerIp = this.proposerIp;
			prePreparedMessage.block = this.block;
			prePreparedMessage.roundId = this.roundId;
			
			List<String> peerList = Peer.getPeerIpList(); 
			for(String peerIp : peerList){
				if( ( !peerIp.equals(this.proposerIp) && !peerIp.equals(GlobalConfiguration.PRIMARY_PEER_IP )) || OperatingMode.getInstance().isDev()){
				 
					MessageBroker.getInstance().sendMessage(peerIp, prePreparedMessage);
				}
			}
		}
	}
	
}
