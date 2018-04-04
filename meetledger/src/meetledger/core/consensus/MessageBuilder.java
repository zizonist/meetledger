package meetledger.core.consensus;

import meetledger.conf.GlobalConfiguration;
import meetledger.core.block.Block;
import meetledger.core.consensus.pbft.NewRoundMessage;

/**
 * 
 * @author sangwon
 *
 */
public class MessageBuilder { 
	private static class MessageBuilderInstance{
		public static MessageBuilder instance = new MessageBuilder();
	}
	private MessageBuilder(){
	}
	public static MessageBuilder getInstance(){
		return MessageBuilderInstance.instance;
	}
	/**
	 * Dynamic Generator로 변경해야됨
	 * @return
	 */
	public Message build(String proposalIp, Block block){
		Message message = null;
		if( GlobalConfiguration.CONSENSUS_TYPE.equals( ConsensusType._PBFT_ )  ){
			message = new NewRoundMessage(proposalIp, block); 
		}
		return message;
	}
}
