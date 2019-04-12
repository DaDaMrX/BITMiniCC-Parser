import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemSet extends ArrayList<Item> {

	private Map<String, ItemSet> map;

	ItemSet() {
		map = new HashMap<>();
	}

	public void add(Production production, int position) {
		super.add(new Item(production, position));
	}

	public void addGoto(String symbol, ItemSet itemSet) {
		map.put(symbol, itemSet);
	}

	public ItemSet gotoItemSet(String symbol) {
		return map.getOrDefault(symbol, null);
	}

	public Map<String, ItemSet> getGoto() {
		return map;
	}

	public void setMap(Map<String, ItemSet> map) {
		this.map = map;
	}
}
