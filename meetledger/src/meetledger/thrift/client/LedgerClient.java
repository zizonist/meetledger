package meetledger.thrift.client;

import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import meetledger.conf.GlobalConfiguration;
import meetledger.thrift.ThriftServiceName;
import meetledger.thrift.meta.TMessage;
import meetledger.thrift.meta.TPeer;
import meetledger.thrift.service.LedgerService;

/**
 * connection pool이 필요함
 * Message Client
 * @author sangwon
 *
 */
public class LedgerClient {
	private static class TMessageClientInstance{
		public static LedgerClient instance = new LedgerClient();
	}
	private LedgerClient(){
		
	}
	public static LedgerClient getInstance(){
		return TMessageClientInstance.instance;
	}
	
	public void sendMessage(String ip, TMessage message){
		TTransport transport = null;
        try {
            transport = new TSocket(ip, GlobalConfiguration.LEDGER_PORT);
            transport.open();
 
            TProtocol protocol = new TBinaryProtocol(transport);
            TMultiplexedProtocol mp = new TMultiplexedProtocol(protocol, ThriftServiceName._LEDGERSERVICE_);
            LedgerService.Client client = new LedgerService.Client(mp);
            client.sendMessage(message);
        } catch (TTransportException e) {
        	e.printStackTrace();
        } catch (TException e) {
        	e.printStackTrace();
        } finally{
        	if ( transport != null ){
        		transport.close();
        	}
        }
	}
	
	public List<String> syncPeer(TPeer peer){
		TTransport transport = null;
		List<String> peerList = null;
        try {
            transport = new TSocket(GlobalConfiguration.PRIMARY_PEER_IP, GlobalConfiguration.LEDGER_PORT);
            transport.open();
 
            TProtocol protocol = new TBinaryProtocol(transport);
            TMultiplexedProtocol mp = new TMultiplexedProtocol(protocol, ThriftServiceName._LEDGERSERVICE_);
            LedgerService.Client client = new LedgerService.Client(mp);
            peerList = client.syncPeer(peer);
        } catch (TTransportException e) {
           // e.printStackTrace();
        } catch (TException e) {
           //e.printStackTrace();
        } finally{
        	if ( transport != null ){
        		transport.close();
        	}
        }
        return peerList;
	}
	
}
