package meetledger.core.block;

import java.io.Serializable;

import meetledger.core.Sha256Hash;
 

/**
 * 
 * 
 * @author sangwon
 *
 */
public class Transaction implements Serializable{ 
	
	private static final long serialVersionUID = -4008405246355197316L;
	/**
	 * 트랜잭션 서명(인증키)
	 */
	private String signature = null;
	/**
	 * 트랜잭션Id
	 */
	 private Sha256Hash hash;
	 
	/**
	 * 트랜잭션 Data
	 * Custom Data이므로 byte[]로 받음
	 */
	private byte[] txData = null;
	
	 /**
	 * Returns the transaction hash (aka txid) as you see them in block explorers. It is used as a reference by
	 * transaction inputs via outpoints.
	 */
	public Sha256Hash getHash() {
        if (hash == null) {
            //hash = Sha256Hash.wrapReversed(Sha256Hash.hashTwice(unsafeBitcoinSerialize()));
        }
        return hash;
    }

	
}
