package meetledger.core;

import meetledger.core.consensus.pbft.Pbft;

/**
 * 
 * @author sangwon
 *
 */
public class InitializeLedger {
	private static class InitializeLedgerInstance{
		public static InitializeLedger instance = new InitializeLedger();
	}
	public static InitializeLedger getInstance(){
		return InitializeLedgerInstance.instance;
	}
	private InitializeLedger(){
		
	} 
	public void init(){
		/**
		 * 1.Primary 
		 */
		
		
		/**
		 * 2. 
		 */
		
		/**
		 * dynamic init 
		 * Consensus Init
		 */
		Thread pbft = new Thread(new Pbft());
		pbft.setDaemon(true);
		pbft.setName("Consensus-pbft");
		pbft.start();
		
	}
	
}
