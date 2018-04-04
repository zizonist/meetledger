package meetledger.conf;

/**
 * read from ledger.properties
 * @author sangwon
 *
 */
public class GlobalConfiguration {
	public static String CONSENSUS_TYPE = "pbft";
	public static int CONSENSUS_PBFT_RATIO = 67;
	public static int LEDGER_PORT = 11250;
	public static String PRIMARY_PEER_IP = "0.0.0.0";
	public static String THIS_NODE_IP="0.0.0.0";
	public static String OPERATION_MODE = "dev";
	
}
