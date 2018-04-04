package meetledger.core.block;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import meetledger.core.Sha256Hash;
import meetledger.util.Utils;

/**
 * BlockHeader + Transaction
 * 
 * @author sangwon.ku
 *
 */
public class Block implements Serializable {
	private static final long serialVersionUID = -6359572372756391075L;
	private Sha256Hash blockHash= null;
	private Sha256Hash prevBlockHash = null;
	private Sha256Hash merkleRootHash = null;
	private int blockHeight = 0;
	public LinkedList<Transaction> transactions = null;
	
	

	public Sha256Hash getBlockHash() {
		return blockHash;
	}

	public void setBlockHash(Sha256Hash blockHash) {
		this.blockHash = blockHash;
	}

	public Sha256Hash getPrevBlockHash() {
		return prevBlockHash;
	}

	public void setPrevBlockHash(Sha256Hash prevBlockHash) {
		this.prevBlockHash = prevBlockHash;
	}

	public Sha256Hash getMerkleRootHash() {
		return merkleRootHash;
	}

	public void setMerkleRootHash(Sha256Hash merkleRootHash) {
		this.merkleRootHash = merkleRootHash;
	}

	public int getBlockHeight() {
		return blockHeight;
	}

	public void setBlockHeight(int blockHeight) {
		this.blockHeight = blockHeight;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private Sha256Hash calculateMerkleRoot() {
		List<byte[]> tree = buildMerkleTree();
		return Sha256Hash.wrap(tree.get(tree.size() - 1));
	}

	private List<byte[]> buildMerkleTree() {
		// The Merkle root is based on a tree of hashes calculated from the
		// transactions:
		//
		//     root
		//    /   \
		//   A     B
		//  / \   / \
		// t1 t2 t3 t4
		//
		// The tree is represented as a list: t1,t2,t3,t4,A,B,root where each
		// entry is a hash.
		//
		// The hashing algorithm is double SHA-256. The leaves are a hash of the
		// serialized contents of the transaction.
		// The interior nodes are hashes of the concenation of the two child
		// hashes.
		//
		// This structure allows the creation of proof that a transaction was
		// included into a block without having to
		// provide the full block contents. Instead, you can provide only a
		// Merkle branch. For example to prove tx2 was
		// in a block you can just provide tx2, the hash(tx1) and B. Now the
		// other party has everything they need to
		// derive the root, which can be checked against the block header. These
		// proofs aren't used right now but
		// will be helpful later when we want to download partial block
		// contents.
		//
		// Note that if the number of transactions is not even the last tx is
		// repeated to make it so (see
		// tx3 above). A tree with 5 transactions would look like this:
		//
		// root
		// / \
		// 1 5
		// / \ / \
		// 2 3 4 4
		// / \ / \ / \
		// t1 t2 t3 t4 t5 t5
		ArrayList<byte[]> tree = new ArrayList<>();
		// Start by adding all the hashes of the transactions as leaves of the
		// tree.
		for (Transaction t : transactions) {
			tree.add(t.getHash().getBytes());
		}
		int levelOffset = 0; // Offset in the list where the currently processed
								// level starts.
		// Step through each level, stopping when we reach the root (levelSize
		// == 1).
		for (int levelSize = transactions.size(); levelSize > 1; levelSize = (levelSize + 1) / 2) {
			// For each pair of nodes on that level:
			for (int left = 0; left < levelSize; left += 2) {
				// The right hand node can be the same as the left hand, in the
				// case where we don't have enough
				// transactions.
				int right = Math.min(left + 1, levelSize - 1);
				byte[] leftBytes = Utils.reverseBytes(tree.get(levelOffset + left));
				byte[] rightBytes = Utils.reverseBytes(tree.get(levelOffset + right));
				tree.add(Utils.reverseBytes(Sha256Hash.hashTwice(leftBytes, 0, 32, rightBytes, 0, 32)));
			}
			// Move to the next level.
			levelOffset += levelSize;
		}
		return tree;
	}

}
