package meetledger.thrift.service;

import java.util.List;

import org.apache.thrift.TException;

import meetledger.core.Peer;
import meetledger.core.consensus.MessageBroker;
import meetledger.thrift.meta.TMessage;
import meetledger.thrift.meta.TPeer;

public class LedgerServiceImpl implements LedgerService.Iface{

	@Override
	public void sendMessage(TMessage message) throws TException {
		// TODO Auto-generated method stub
		try {
			MessageBroker.getInstance().putMessage(message.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new TException(e.getMessage());
		} 
	}
 
	@Override
	public List<String> syncPeer(TPeer peer) throws TException {
		// TODO Auto-generated method stub
		Peer.addPeer(peer.ip);
		return Peer.getPeerIpList();
	}

}
