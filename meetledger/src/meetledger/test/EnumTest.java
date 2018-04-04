package meetledger.test;

public class EnumTest {
	public enum State{
		NEWROUND(0)  ,PREPARED(1);
		private int value; 
        private State(int value) {
                this.value = value;
        }
	}
	public static void main(String[] args) {
		System.out.println(State.NEWROUND.value  );
		System.out.println(State.PREPARED.value);
	}
}
