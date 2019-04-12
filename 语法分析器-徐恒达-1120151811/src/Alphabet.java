import java.util.HashMap;

public class Alphabet {

	private static Alphabet alphabet = null;
	private HashMap<String, Boolean> map;
	private String startSymbol;

	private Alphabet() {
		map = new HashMap<>();
		startSymbol = null;
	}

	public static Alphabet newInstance() {
		if (alphabet == null) alphabet = new Alphabet();
		return alphabet;
	}

	void add(String symbol) {
		if (!map.containsKey(symbol)) map.put(symbol, true);
	}

	void setTerminal(String symbol, boolean flag) {
		if (!map.containsKey(symbol)) return;
		map.put(symbol, flag);
		if (map.size() == 1 && !flag) startSymbol = symbol;
	}

	boolean isTerminal(String symbol) {
		if (!map.containsKey(symbol)) return true;
		return map.get(symbol);
	}

	boolean isStartSymbol(String symbol) {
		return symbol.equals(startSymbol);
	}

	public String getStartSymbol() {
		return startSymbol;
	}

	public int size() {
		return map.size();
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		return s.toString();
	}
}
