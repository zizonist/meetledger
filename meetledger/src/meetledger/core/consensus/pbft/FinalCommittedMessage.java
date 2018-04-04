package meetledger.core.consensus.pbft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
/**
 * FINAL COMMITTED -> NEW ROUND:
		Validators pick a new proposer and starts a new round timer.
 * @author sangwon
 *
 */
public class FinalCommittedMessage  extends PbftMessage {

	private static Logger logger = LoggerFactory.getLogger(FinalCommittedMessage.class);
	private static final long serialVersionUID = 8609635324382726734L;

	@Override
	public void process0() {
		// TODO Auto-generated method stub 
		//Consensus.getInstance().consensusRoundClear(this.roundId);
		
		if( logger.isDebugEnabled() ){
			logger.debug("========================================================================================");
			logger.debug("========================             FinalCommittedMessage           ===================");
			logger.debug("========================================================================================");
		}
		
	}

}
