package meetledger.util;

/**
 * 
 * @author sangwon
 *
 */
public class OperatingMode {
	private static class OperatingModeInstance{
		public static OperatingMode instance = new OperatingMode();
	}
	public static OperatingMode getInstance(){
		return OperatingModeInstance.instance;
	}
	private OperatingMode(){ 
		currentMode = dev;
	}
	private final byte dev = 0x01;
	private final byte opr = 0x02;
	private byte currentMode = 0x02;
	
	public boolean isDev(){
		return ( currentMode == dev )? true : false;
	}
	
	public void setDevMode(){
		currentMode = dev;
	}
	
	public void setOprMode(){
		currentMode = opr;
	}
}
