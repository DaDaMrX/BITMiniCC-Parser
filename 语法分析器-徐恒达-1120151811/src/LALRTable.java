import java.util.HashMap;
import java.util.Map;

public class LALRTable {

	private Map<Integer, Map<String, Action>> action;
	private Map<Integer, Map<String, Integer>> go;
	private Grammar grammar;

	LALRTable() {
		action = new HashMap<>();
		go = new HashMap<>();
		grammar = Grammar.newInstance();
	}

	public String getActionType(int state, String symbol) {
		if (!action.containsKey(state)) return "ERROR";
		Map<String, Action> map = action.get(state);
		if (!map.containsKey(symbol)) return "ERROR";
		return map.get(symbol).getType();
	}

	public int getActionIndex(int state, String symbol) {
		if (!action.containsKey(state)) return -1;
		Map<String, Action> map = action.get(state);
		if (!map.containsKey(symbol)) return -1;
		return map.get(symbol).getIndex();
	}

	public int getGoto(int state, String symbol) {
		if (!go.containsKey(state)) return -1;
		Map<String, Integer> map = go.get(state);
		if (!map.containsKey(symbol)) return -1;
		return map.get(symbol);
	}

	public void addShift(int fromStatus, String symbol, int toStatus) {
		if (!action.containsKey(fromStatus)) action.put(fromStatus, new HashMap<>());
		Map<String, Action> map = action.get(fromStatus);
		Action a = new Action("SHIFT", toStatus);
		map.put(symbol, a);
	}

	public void addReduction(int fromStatus, String symbol, int index) {
		if (!action.containsKey(fromStatus)) action.put(fromStatus, new HashMap<>());
		Map<String, Action> map = action.get(fromStatus);
		Action a = new Action("REDUCTION", index);
		map.put(symbol, a);
	}

	public void addAccept(int fromStatus, String symbol) {
		if (!action.containsKey(fromStatus)) action.put(fromStatus, new HashMap<>());
		Map<String, Action> map = action.get(fromStatus);
		Action a = new Action("ACCEPT");
		map.put(symbol, a);
	}

	public void addGo(int fromStatus, String symbol, int toStatus) {
		if (!go.containsKey(fromStatus)) go.put(fromStatus, new HashMap<>());
		Map<String, Integer> map = go.get(fromStatus);
		map.put(symbol, toStatus);
	}
}



//enum ACTION {SHIFT, REDUCTION, ACCEPT}
