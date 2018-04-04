package meetledger.thrift;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import meetledger.MeetLedger;
import meetledger.thrift.service.LedgerService;
import meetledger.thrift.service.LedgerServiceImpl;

/**
 * Thrift server 
 * @author sangwon
 *
 */
public class MeetLedgerServer {
	private int port = 11250;
	private static Logger logger = LoggerFactory.getLogger(MeetLedgerServer.class);
	Thread thriftServer = null;
	
	public MeetLedgerServer(int port){
		this.port = port;
	}
	
	public void start(){
		this.thriftServer = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub

				try {  
		            TServerSocket serverTransport = new TServerSocket(port);
		            
		            LedgerService.Processor ledgerProcessor = new LedgerService.Processor(new LedgerServiceImpl() );
		   
		            
		            TMultiplexedProcessor multiProcessor = new TMultiplexedProcessor();
		            multiProcessor.registerProcessor(ThriftServiceName._LEDGERSERVICE_, ledgerProcessor); 
		            
		            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(multiProcessor));
		            																				  //.processor(hostProcessor));
		            if( logger.isDebugEnabled() ){
		            	logger.debug("Starting MeetLedgerServer on port  ..."+port);
		            }
		            server.serve();
		        } catch (TTransportException e) {
		            e.printStackTrace();
		        }
			}
		}); 
		this.thriftServer.start(); 
	}
}
