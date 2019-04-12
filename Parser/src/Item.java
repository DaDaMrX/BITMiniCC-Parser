import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Item {

    private Production production;
    private int position;
    private Set<String> LASymbols;
    private List<Item> spread;

    Item(Production production, int position) {
        this.production = production;
        this.position = position;
        LASymbols = new HashSet<>();
        spread = new ArrayList<>();
    }

    Item(Production production, int position, Set<String> LASymbols) {
	    this.production = production;
	    this.position = position;
	    this.LASymbols = LASymbols;
	    spread = new ArrayList<>();
    }

	Item(Production production, int position, String LASymbol) {
		this.production = production;
		this.position = position;
		this.LASymbols = new HashSet<>();
		this.LASymbols.add(LASymbol);
		spread = new ArrayList<>();
	}

    public void addAllLASymbols(Set<String> set) {
    	LASymbols.addAll(set);
    }

	public void addLASymbol(String symbol) {
    	LASymbols.add(symbol);
	}

	public void setLASymbol(String symbol) {
    	LASymbols.clear();
    	LASymbols.add(symbol);
	}

	public Set<String> getLASymbols() {
		return LASymbols;
	}

	public void addSpreadItem(Item item) {
    	spread.add(item);
	}

	public List<Item> getSpreadList() {
    	return spread;
	}

	public Production getProduction() {
		return production;
	}

    int getPosition() {
        return position;
    }

    public String nextSymbol() {
		return production.getSymbol(position);
    }

	public String getHead() {
    	return production.getHead();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof Item) {
			Item anotherItem = (Item)obj;
			return production.equals(anotherItem.production) &&
					position == anotherItem.position;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return production.hashCode() + position;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(production.getHead());
		s.append(" ->");
		for (int i = 0; i < position; i++)
			s.append(" ").append(production.getSymbol(i));
		s.append(" .");
		for (int i = position; i < production.bodySize(); i++)
			s.append(" ").append(production.getSymbol(i));
		if (LASymbols.size() > 0) {
			s.append(":");
			for (String a: LASymbols) s.append(" ").append(a);
		}
		return s.toString();
	}

	public List<String> otherSymbols() {
    	List<String> body = production.getBody();
    	List<String> subList = body.subList(position + 1, body.size());
		return new ArrayList<>(subList);
	}
}
