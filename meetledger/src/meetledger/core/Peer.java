package meetledger.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import meetledger.conf.GlobalConfiguration;
import meetledger.thrift.client.LedgerClient;
import meetledger.thrift.meta.TPeer;
 

public class Peer {
	private static Logger logger = LoggerFactory.getLogger(Peer.class);
	private static List<String> peerList = new ArrayList<String>(); 
	public static void addPeer(String peerIp){
		if( !peerList.contains(peerIp) ){
			if( logger.isDebugEnabled() ){
				logger.debug("addPeer : "+peerIp);
			}
			peerList.add(peerIp);
		}
	}
	public static List<String> getPeerIpList(){ 
		return Collections.unmodifiableList(peerList);
	}
	public static int getPeerCount(){
		return peerList.size();
	}
	
	/**
	 * Primary에 등록된 Peer IP/port를 체크하여
	 * 통신이 불가능한 경우 Peer에서 삭제
	 * @author sangwon
	 *
	 */
	public static class PeerSocketChecker implements Runnable{ 
		private static Logger logger = LoggerFactory.getLogger(PeerSocketChecker.class);
		private long sleeptime_after_checking = 10000L; 
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if( logger.isDebugEnabled() ){
				logger.debug("PeerSocketChecker starting... ");
			}
			while( !Thread.currentThread().isInterrupted() ){
				
				try {
					for(String s : peerList){
						
					}
					
					Thread.sleep(sleeptime_after_checking);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	/**
	 * 
	 * @author sangwon
	 *
	 */
	public static class PeerSynchronizer implements Runnable{
		private static Logger logger = LoggerFactory.getLogger(PeerSynchronizer.class);
		private long sync_period = 10000L; 
		private TPeer peer = null;
		
		public PeerSynchronizer(){
			this.peer = new TPeer();
			this.peer.ip = GlobalConfiguration.THIS_NODE_IP;
			this.peer.port = GlobalConfiguration.LEDGER_PORT;
			
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if( logger.isDebugEnabled() ){
				logger.debug("PeerSynchronizer starting... ");
			}
			while(!Thread.currentThread().isInterrupted()){ 
				try {
					List<String> newPeerList = LedgerClient.getInstance().syncPeer(this.peer); 
					 
					peerList = newPeerList;
					Thread.sleep(sync_period);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					logger.error( ExceptionUtils.getStackTrace(e) );
				}
			}
		}
		
	}
}
