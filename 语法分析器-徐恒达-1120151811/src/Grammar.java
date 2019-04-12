import java.util.ArrayList;

public class Grammar extends ArrayList<Production> {

	private static Grammar grammar = null;

	private Grammar() {
	}

	public static Grammar newInstance() {
		if (grammar == null) grammar = new Grammar();
		return grammar;
	}

	public ArrayList<Production> getProductionsByHead(String head) {
		ArrayList<Production> productions = new ArrayList<>();
		for (Production p: this)
			if (p.getHead().equals(head))
				productions.add(p);
		return productions;
	}
}
