package meetledger.core.consensus.pbft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 

/**
 * PRE-PREPARED -> PREPARED:
		Validator receives 2F + 1 of valid PREPARE messages to enter PREPARED state. Valid messages conform to the following conditions:
			Matched sequence and round.
			Matched block hash.
			Messages are from known validators.
		Validator broadcasts COMMIT message upon entering PREPARED state.
 * @author sangwon
 *
 */
public class PrePreparedMessage extends PbftMessage { 
	private static Logger logger = LoggerFactory.getLogger(PrePreparedMessage.class); 
	
	private static final long serialVersionUID = 3884385839514129932L;
	 
	@Override
	public void process0() {
		// TODO Auto-generated method stub 

		if( logger.isDebugEnabled() ){ 
			logger.debug("========================== PrePreparedMessage processing ============================");
			logger.debug(" this.proposerIp  : "+this.proposerIp);
			logger.debug(" this.block : "+ this.block.getBlockHash().toString());
			logger.debug(" this.roundId : "+ this.roundId);
		}
		 
		PreparedMessage preparedMessage = new PreparedMessage();
		preparedMessage.proposerIp = this.proposerIp;
		preparedMessage.block = this.block;
		preparedMessage.roundId = this.roundId;
		
		this.broadcastMessage(preparedMessage);
		
	}

}
