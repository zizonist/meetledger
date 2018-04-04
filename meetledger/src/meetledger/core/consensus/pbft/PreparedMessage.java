package meetledger.core.consensus.pbft;
 
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PREPARED -> COMMITTED:
		Validator receives 2F + 1 of valid COMMIT messages to enter COMMITTED state. Valid messages conform to the following conditions:
			Matched sequence and round.
			Matched block hash.
			Messages are from known validators.
 * @author sangwon
 *
 */
public class PreparedMessage  extends PbftMessage  {
	private static Logger logger = LoggerFactory.getLogger(PreparedMessage.class); 
	private static final long serialVersionUID = 8925086167675607860L;
	private static ReentrantLock lock = new ReentrantLock(true);

	@Override
	public void process0() {
		// TODO Auto-generated method stub
		if( logger.isDebugEnabled() ){ 
			logger.debug("========================== PreparedMessage processing ============================");
			logger.debug(" this.proposerIp  : "+this.proposerIp);
			logger.debug(" this.block : "+ this.block.getBlockHash().toString());
			logger.debug(" this.roundId : "+ this.roundId);
		}
		
		if( Consensus.getInstance().consensusPreparedState(this) ){
			
			PreparedMessage.lock.lock();
			try{
				if( Consensus.getInstance().isPreparedStateCompleted(this.roundId) ){
					return;
				}
				
				/**
				 * 합의된 메시지를 전송
				 */
				PreparedMessage preparedMessage = Consensus.getInstance().getCompletedPreparedMessage(this.roundId);
				CommittedMessage committedMessage = new CommittedMessage(); 
				committedMessage.proposerIp = preparedMessage.proposerIp;
				committedMessage.block = preparedMessage.block;
				committedMessage.roundId = preparedMessage.roundId;
				
				this.broadcastMessage(committedMessage);
				Consensus.getInstance().setRoundState(roundId, Consensus.RoundState.COMMITTED); 
				

				if( logger.isDebugEnabled() ){  
					logger.debug("  Consensus.getInstance().isPreparedStateCompleted(this.roundId) : "+ Consensus.getInstance().isPreparedStateCompleted(this.roundId));
			 
				}
			}finally{
				PreparedMessage.lock.unlock();
			}
		}
	} 

}
