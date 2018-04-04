package meetledger.core.consensus.pbft;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 

import java.util.Set;

/**
 * 합의 처리
 * @author sangwon
 *
 */
public class RoundCount {
	private static Logger logger = LoggerFactory.getLogger(RoundCount.class);
	private Map<PreparedMessage, Integer> preparedMessageCountMap = new HashMap<PreparedMessage, Integer>();
	private Map<CommittedMessage, Integer> committedMessageCountMap = new HashMap<CommittedMessage, Integer>();
	
	public enum state{
		START
		,STOP
	} 
	
	private state preparedRoundState = null;
	private state committedRoundState = null;
	
	/**
	 * 2f+1 합의 개수
	 */
	private int counsensusCount = 0;
	
	public RoundCount(int counsensusCount){
		this.counsensusCount = counsensusCount; 
	}
	
	@SuppressWarnings({ "unchecked",  "rawtypes" })
	private boolean checkCount(Map messageMap, PbftMessage message){
		Set<Entry<?,?>> s = messageMap.entrySet(); 
		int chksum = 0;
		boolean bResult = false;
		for( Entry entry : s){
			PbftMessage pbftMessage = (PbftMessage)entry.getKey();
			Integer count = (Integer)entry.getValue();
			if( pbftMessage.proposerIp.equals(message.proposerIp) && pbftMessage.block.getBlockHash().equals(message.block.getBlockHash())
					&& pbftMessage.roundId.equals(message.roundId) &&  pbftMessage.block.getPrevBlockHash().equals(message.block.getPrevBlockHash()) ){
				count = count+1;
				messageMap.replace(pbftMessage, count); 
				
				if( logger.isDebugEnabled() ){
					logger.debug("current count : "+count+", consensusCount : "+counsensusCount);
				}
				/**
				 *  합의가 완료 되었을때 더이상 메시지 처리를 하지 않기 위함
				 */
				if( count >= counsensusCount ){ 
					if( message instanceof PreparedMessage ){
						preparedRoundState = state.STOP;
					}else{
						committedRoundState = state.STOP;
					}
					bResult = true;
				}
				chksum++;
			}
		}   
		
		if( chksum == 0 ){
			messageMap.put(message, 1); 
			if( message instanceof PreparedMessage ){
				preparedRoundState = state.START;
			}else{
				committedRoundState = state.START;
			}
			/**
			 *  합의가 완료 되었을때 더이상 메시지 처리를 하지 않기 위함
			 */
			if( 1 == counsensusCount ){ 
				if( message instanceof PreparedMessage ){
					preparedRoundState = state.STOP;
				}else{
					committedRoundState = state.STOP;
				}
				bResult = true;
			}
		}
		return bResult;
	}
	
	public boolean consensusPreparedMessage(PreparedMessage message){
		boolean bResult = false; 
		if( preparedRoundState != state.STOP){
			bResult = checkCount(preparedMessageCountMap, message);
		}else{
			bResult = true;
		}
		return bResult;
	} 

	public boolean consensusCommittedMessage(CommittedMessage message){
		boolean bResult = false; 
		if( committedRoundState != state.STOP ){
			bResult = checkCount(committedMessageCountMap, message);
		}else{
			bResult = true;
		}
		return bResult;
	} 
	
	public boolean isPreparedStopped(){
		return preparedRoundState == state.STOP;
	}
	
	public boolean isCommittedStopped(){
		return committedRoundState == state.STOP;
	}
	
	public CommittedMessage completedCommittedMessage(){
		Set<Entry<CommittedMessage, Integer>> s = committedMessageCountMap.entrySet();
		int currentCount = 0;
		CommittedMessage rtnMessage = null;
		for( Entry<CommittedMessage, Integer> entry: s){
			if(currentCount < entry.getValue() ){
				currentCount = entry.getValue();
				rtnMessage = entry.getKey();
			}
		}
		return rtnMessage;
		 
	}
	
	public PreparedMessage completedPreparedMessage(){
		Set<Entry<PreparedMessage, Integer>> s = preparedMessageCountMap.entrySet();
		int currentCount = 0;
		PreparedMessage rtnMessage = null;
		for( Entry<PreparedMessage, Integer> entry: s){
			if(currentCount < entry.getValue() ){
				currentCount = entry.getValue();
				rtnMessage = entry.getKey();
			}
		}
		return rtnMessage;
		 
	}
}
