package meetledger.conf;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
public class LoadConfiguration {
	private static Logger logger = LoggerFactory.getLogger(LoadConfiguration.class);
	private static class LoadConfigurationInstance{
		public static LoadConfiguration instance = new LoadConfiguration();
	}
	public static LoadConfiguration getInstance(){
		return LoadConfigurationInstance.instance;
	}
	private LoadConfiguration(){}
	
	public GlobalConfiguration loadConf(String confPath) throws Exception{
		File f = new File(confPath);
		Properties props = new Properties();
		props.load(new FileInputStream(f));
		GlobalConfiguration conf = new GlobalConfiguration();

		GlobalConfiguration.OPERATION_MODE = props.getProperty("operation.mode").trim();
		GlobalConfiguration.CONSENSUS_PBFT_RATIO = Integer.parseInt( props.getProperty("consensus.pbft.ratio").trim());
		GlobalConfiguration.CONSENSUS_TYPE = props.getProperty("consensus.type").trim();
		GlobalConfiguration.LEDGER_PORT = Integer.parseInt( props.getProperty("ledger.port").trim());
		
		GlobalConfiguration.PRIMARY_PEER_IP =  ( props.getProperty("primary.peer.ip") == null)? "0.0.0.0":  props.getProperty("primary.peer.ip").trim(); 
		GlobalConfiguration.THIS_NODE_IP = ( props.getProperty("this.node.ip") == null)? InetAddress.getLocalHost().getHostAddress():  props.getProperty("this.node.ip").trim(); 
		if( logger.isDebugEnabled() ){ 
			logger.debug("configuration Path : "+confPath);
		}
		
		return conf;
	}
}
