package meetledger.core.consensus;

import java.io.Serializable;

/**
 * 
 * @author sangwon
 *
 */
public interface Message extends Serializable{
	public void process();
}
