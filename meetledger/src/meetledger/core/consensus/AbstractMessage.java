package meetledger.core.consensus;

import meetledger.core.block.Block;

/**
 * 
 * @author sangwon
 *
 */
public abstract class AbstractMessage implements Message{   
	private static final long serialVersionUID = -6916382343657570189L;
	public String proposerIp = null;
	public Block block = null;
	
	@Override
	public void process() {
		// TODO Auto-generated method stub
		this.process0(); 
	}
	
	/**
	 * 
	 */
	abstract public void process0();
}
