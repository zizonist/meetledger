package meetledger.core.consensus.pbft;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import meetledger.MeetLedger;
import meetledger.conf.GlobalConfiguration;
import meetledger.core.Peer;

/**
 * Prepared and Committed 합의
 * @author sangwon
 *
 */
public class Consensus {
	private static Logger logger = LoggerFactory.getLogger(MeetLedger.class); 
	private ReentrantLock preparedLock = new ReentrantLock(true);
	private ReentrantLock committedLock = new ReentrantLock(true);
	private Map<String, Thread> preparedTimerMap = new HashMap<String, Thread>();
	private Map<String, RoundCount> preparedRoundCountMap = new HashMap<String, RoundCount>();
	private Map<String, Thread> committedTimerMap = new HashMap<String, Thread>();
	private Map<String, RoundCount> committedRoundCountMap = new HashMap<String, RoundCount>();
	private long preparedStateTimeout = 10000L;
	private long committedStateTimeout = 10000L; 
	
	/** 
	 *  https://github.com/ethereum/EIPs/issues/650 를 참조하고 PBFT원형 그대로를 최대한 구현함
		
		NEW ROUND -> PRE-PREPARED:
			Proposer collects transactions from txpool.
			Proposer generates a block proposal and broadcasts it to validators. It then enters the PRE-PREPARED state.
			Each validator enters PRE-PREPARED upon receiving the PRE-PREPARE message with the following conditions:
				Block proposal is from the valid proposer.
				Block header is valid.
				Block proposal's sequence and round match the validator's state.
			Validator broadcasts PREPARE message to other validators.
		PRE-PREPARED -> PREPARED:
			Validator receives 2F + 1 of valid PREPARE messages to enter PREPARED state. Valid messages conform to the following conditions:
				Matched sequence and round.
				Matched block hash.
				Messages are from known validators.
			Validator broadcasts COMMIT message upon entering PREPARED state.
		PREPARED -> COMMITTED:
			Validator receives 2F + 1 of valid COMMIT messages to enter COMMITTED state. Valid messages conform to the following conditions:
				Matched sequence and round.
				Matched block hash.
				Messages are from known validators.
		COMMITTED -> FINAL COMMITTED:
			Validator appends 2F + 1 commitment signatures to extraData and tries to insert the block into the blockchain.
			Validator enters FINAL COMMITTED state when insertion succeeds.
		FINAL COMMITTED -> NEW ROUND:
			Validators pick a new proposer and starts a new round timer.
	 * @author sangwon
	 *
	 */
	public static enum RoundState{
		NEWROUND(0)
		,PREPREPARED(2)
		,PREPARED(3)
		,COMMITTED(4)
		,FINALCOMMITTED(5)
		,ROUNDCHANGE(6);
		private int value; 
        private RoundState(int value) {
                this.value = value;
        }
	} 
	private ConcurrentMap<String, RoundState> consensusRoundStateMap = new ConcurrentHashMap<String,RoundState>();
	
	private static class ConsensusInstance{
		public static Consensus instance = new Consensus();
	}
	private Consensus(){ 
	}
	public static Consensus getInstance(){
		return ConsensusInstance.instance;
	}
	
	public int getConsensusCount(){
		int totalPeerCount = Peer.getPeerIpList().size();
		int consensusCount = 0;
		if( totalPeerCount <=  3){
			consensusCount = 1;
		}else{
			//totalPeerCount-1 ==> Proposer는 합의 Peer에서 제외함
			consensusCount = Math.round(( GlobalConfiguration.CONSENSUS_PBFT_RATIO * (totalPeerCount-1))/100);
		}
		
		if( logger.isDebugEnabled() ){
			logger.debug("consensusCount  : "+consensusCount);
		}
		return consensusCount;
	}
	
	/**
	 * Validator receives 2F + 1 of valid PREPARE messages to enter PREPARED state. Valid messages confirm to the following conditions:
			Matched sequence and round.
			Matched block hash.
			Messages are from known validators.
		Validator broadcasts COMMIT message upon entering PREPARED state.
	 * @param message
	 * @return
	 */
	public boolean consensusPreparedState(PreparedMessage message){
		boolean bResult = false; 
		preparedLock.lock();
		try{
			String roundId = message.roundId;
			
			RoundState roundState = consensusRoundStateMap.get(roundId);
			if( roundState == RoundState.COMMITTED || roundState == RoundState.FINALCOMMITTED ){
				return true;
			}

			/**
			 * prepared timer thread 
			 */
			Thread t = preparedTimerMap.get(roundId);
			if( t == null ){
				t = new Thread(new PreparedTimer(roundId));
				t.setDaemon(true);   
				t.setName("PreparedTimer-"+roundId);
				preparedTimerMap.put(roundId, t);
			}
			
			/**
			 * prepared counting
			 */
			RoundCount roundCount = preparedRoundCountMap.get(roundId);
			if( roundCount ==  null ){
				roundCount = new RoundCount(this.getConsensusCount());
				preparedRoundCountMap.put(roundId, roundCount);
			}
			bResult = roundCount.consensusPreparedMessage(message);  
		}catch(Exception e){ 
			logger.error(ExceptionUtils.getStackTrace(e));
			bResult = false;
		}finally{
			preparedLock.unlock();
		}
		
		return bResult;
	}
	
	public boolean consensusCommittedState(CommittedMessage message){
		boolean bResult = false; 
		committedLock.lock();
		try{
			String roundId = message.roundId;  
			RoundState roundState = consensusRoundStateMap.get(roundId);
			if( roundState == RoundState.FINALCOMMITTED ){
				return true;
			}

			/**
			 * commit  timer thread  
			 */
			Thread t = committedTimerMap.get(roundId);
			if( t == null ){
				t = new Thread(new CommittedTimer(roundId));
				t.setDaemon(true);   
				t.setName("CommittedTimer-"+roundId);
				committedTimerMap.put(roundId, t);
			}
			
			/**
			 * committed counting
			 */
			RoundCount roundCount = committedRoundCountMap.get(roundId);
			if( roundCount ==  null ){
				roundCount = new RoundCount(this.getConsensusCount());
				committedRoundCountMap.put(roundId, roundCount);
			}
			bResult = roundCount.consensusCommittedMessage(message);  
		}catch(Exception e){
			
		}finally{
			committedLock.unlock(); 
		}
		return bResult;
	}
	
	public boolean isCommittedStateCompleted(String roundId){
		boolean bResult = false;
		RoundState state = consensusRoundStateMap.get(roundId);
		if( state == RoundState.FINALCOMMITTED){
			bResult = true;
		}
		return bResult;
	}
	 

	public boolean isPreparedStateCompleted(String roundId){
		boolean bResult = false;
		RoundState state = consensusRoundStateMap.get(roundId);
		if( state == RoundState.COMMITTED || state == RoundState.FINALCOMMITTED){
			bResult = true;
		}
		return bResult;
	}
	
	public CommittedMessage getCompletedCommittedMessage(String roundId){
		RoundCount roundCount = committedRoundCountMap.get(roundId);
		CommittedMessage message = roundCount.completedCommittedMessage();
		return message;
	}
	
	public PreparedMessage getCompletedPreparedMessage(String roundId){
		RoundCount roundCount = preparedRoundCountMap.get(roundId);
		PreparedMessage message = roundCount.completedPreparedMessage();
		return message;
	}
	
	@SuppressWarnings("static-access")
	public void consensusRoundClear(String roundId){
		preparedRoundCountMap.remove(roundId);
		Thread preparedTimer = preparedTimerMap.get(roundId);
		preparedTimer.interrupted();
		committedRoundCountMap.remove(roundId);
		Thread committedTimer = committedTimerMap.get(roundId);
		committedTimer.interrupted();
		
	}
	 
	public void setRoundState(String roundId, RoundState roundState){
		RoundState state = consensusRoundStateMap.putIfAbsent(roundId, roundState); 
		if( state != null && roundState.value > state.value ){
			consensusRoundStateMap.replace(roundId, roundState);
		}
	}
	
	public RoundState getRoundState(String roundId){
		return consensusRoundStateMap.get(roundId);
	}
	
	/**
	 *  
	 * @author sangwon
	 *
	 */
	private class PreparedTimer implements Runnable{
		private Logger _logger = LoggerFactory.getLogger(PreparedTimer.class); 
		private long startTime = 0;
		private String roundId = null;
		public PreparedTimer(String roundId) {
			// TODO Auto-generated constructor stub
			this.startTime = System.currentTimeMillis();
			this.roundId = roundId;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try{
				while(!Thread.currentThread().isInterrupted()){
					long elapsed = System.currentTimeMillis() - this.startTime;
					if( elapsed > preparedStateTimeout){
						break;
					} 
					Thread.sleep(100);
				} 
			}catch(Exception e){
				_logger.error( ExceptionUtils.getStackTrace(e) );
			}finally{ 
				preparedTimerMap.remove(this.roundId);
			}
		} 
	}
	

	private class CommittedTimer implements Runnable{
		private Logger _logger = LoggerFactory.getLogger(CommittedTimer.class); 
		private long startTime = 0;
		private String roundId = null;
		public CommittedTimer(String roundId) {
			// TODO Auto-generated constructor stub
			this.startTime = System.currentTimeMillis();
			this.roundId = roundId;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try{
				while(!Thread.currentThread().isInterrupted()){
					long elapsed = System.currentTimeMillis() - this.startTime;
					if( elapsed > committedStateTimeout){
						break;
					} 
					Thread.sleep(100);
				} 
			}catch(Exception e){
				_logger.error( ExceptionUtils.getStackTrace(e) );
			}finally{ 
				committedTimerMap.remove(this.roundId);
			}
		} 
	}
}
