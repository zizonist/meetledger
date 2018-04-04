package meetledger;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import meetledger.conf.GlobalConfiguration;
import meetledger.conf.LoadConfiguration;
import meetledger.core.InitializeLedger;
import meetledger.core.Peer;
import meetledger.core.Primary;
import meetledger.thrift.MeetLedgerServer;
import meetledger.util.OperatingMode;
 
/**
 * 
 *
 * import com.sds.meetledger.ui
 * @author sangwon.ku
 *
 */
public class MeetLedger { 
	private static Logger logger = LoggerFactory.getLogger(MeetLedger.class);
	public static void main(String[] args) throws Exception{
		if( logger.isDebugEnabled() ){
			logger.debug("================= let's get it!! =================="); 
		}
		
		/**
		 * load configuration 
		 */
		String ledgerConf = System.getProperty("ledger.conf");
		LoadConfiguration.getInstance().loadConf(ledgerConf);
		
		if(GlobalConfiguration.OPERATION_MODE.equals("dev")){
			OperatingMode.getInstance().setDevMode();
		}else{
			OperatingMode.getInstance().setOprMode();
		}
		
		/** 
		 */ 
		if( logger.isDebugEnabled() ){
			logger.debug("GlobalConfiguration.THIS_NODE_IP : "+GlobalConfiguration.THIS_NODE_IP);
		} 
		
		/**
		 * 실행시 현재 노드가 primary인지 체크,  Peer Sync,Check Thread 실행
		 */
		if( GlobalConfiguration.PRIMARY_PEER_IP.equals("0.0.0.0") || GlobalConfiguration.PRIMARY_PEER_IP == null 
				|| GlobalConfiguration.PRIMARY_PEER_IP.equals(GlobalConfiguration.THIS_NODE_IP)){
			Primary.isPrimary = true;  
			if( logger.isDebugEnabled() ){
				logger.debug("GlobalConfiguration.PRIMARY_PEER_IP  : "+ GlobalConfiguration.PRIMARY_PEER_IP );
			}
			
			Thread checkThread = new Thread(new Peer.PeerSocketChecker());
			checkThread.setDaemon(true);
			checkThread.setName("PeerSocketChecker");
			checkThread.start();
			
			Peer.addPeer(GlobalConfiguration.THIS_NODE_IP);
		} else {
			Thread peerSyncThread = new Thread(new Peer.PeerSynchronizer());
			peerSyncThread.setDaemon(true);
			peerSyncThread.setName("PeerSynchronizer");
			peerSyncThread.start();
		}
		  
		/**
		 * 
		 */
		InitializeLedger.getInstance().init();
		
		
		/**
		 * thrift server startup
		 */ 
		MeetLedgerServer meetLedgerServer = new MeetLedgerServer(GlobalConfiguration.LEDGER_PORT);
		meetLedgerServer.start();
		
		
	}
}
