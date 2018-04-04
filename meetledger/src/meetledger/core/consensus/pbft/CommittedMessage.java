package meetledger.core.consensus.pbft;

import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import meetledger.core.consensus.MessageBroker;

/**
 * COMMITTED -> FINAL COMMITTED:
		Validator appends 2F + 1 commitment signatures to extraData and tries to insert the block into the blockchain.
		Validator enters FINAL COMMITTED state when insertion succeeds.
 * @author sangwon
 *
 */
public class CommittedMessage  extends PbftMessage {
	private static Logger logger = LoggerFactory.getLogger(CommittedMessage.class); 
	private static final long serialVersionUID = -1758074995284427199L;
	private static ReentrantLock lock = new ReentrantLock(true);

	@Override
	public void process0() {
		// TODO Auto-generated method stub
		
		if( logger.isDebugEnabled() ){ 
			logger.debug("========================== CommittedMessage processing ============================");
			logger.debug(" this.proposerIp  : "+this.proposerIp);
			logger.debug(" this.block : "+ this.block.getBlockHash().toString());
			logger.debug(" this.roundId : "+ this.roundId);
		}
		
		if( Consensus.getInstance().consensusCommittedState(this) ){
			
			//committed 합의가 완료되면 blockchain에 insert
			CommittedMessage.lock.lock();
			try{ 
				if( Consensus.getInstance().isCommittedStateCompleted(this.roundId) ){
					return;
				}
				/**
				 * 합의된 메시지를 전송
				 */
				CommittedMessage message = Consensus.getInstance().getCompletedCommittedMessage(this.roundId);
				boolean rtn = MessageBroker.getInstance().insertBlock(message.block);
				 
				if( rtn ){
					FinalCommittedMessage finalCommittedMessage = new FinalCommittedMessage(); 
					finalCommittedMessage.proposerIp = message.proposerIp;
					finalCommittedMessage.block = message.block;
					finalCommittedMessage.roundId = message.roundId;
					
					this.replyProposer(finalCommittedMessage);
					Consensus.getInstance().consensusRoundClear(this.roundId);
				}else{
					//block insert가 실패하면?
				}  
				Consensus.getInstance().setRoundState(roundId, Consensus.RoundState.FINALCOMMITTED);
				if( logger.isDebugEnabled() ){  
					logger.debug(" Consensus.getInstance().isCommittedStateCompleted(this.roundId) : "+Consensus.getInstance().isCommittedStateCompleted(this.roundId));
			 
				}
			}finally{
				CommittedMessage.lock.unlock();
			}
			
		}
	}

}
