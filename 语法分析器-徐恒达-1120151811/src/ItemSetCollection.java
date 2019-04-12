import java.util.*;

class ItemSetCollection extends ArrayList<ItemSet> {

	private Alphabet alphabet;
	private Grammar grammar;

	ItemSetCollection() {
		alphabet = Alphabet.newInstance();
		grammar = Grammar.newInstance();
	}

	public void init(ItemSet itemSet) {
		super.clear();
		super.add(closure(itemSet));
	}

	public void build() {
		Queue<ItemSet> queue = new LinkedList<>();
		queue.offer(super.iterator().next());
		int i = 0;
		while (!queue.isEmpty()) {
			i++;
//			if (i % 10 == 0)
//              System.out.println(i + ": "
//              + queue.size() + " " + this.size());

			ItemSet itemSet = queue.poll();

			// Find all the symbols to scan
			HashSet<String> symbols = new HashSet<>();
			for (Item item: itemSet) {
				String symbol = item.nextSymbol();
				if (symbol != null) symbols.add(symbol);
			}

			// Build GOTO(I, X) for each symbol X
			for (String symbol: symbols) {

				// Generate ItemSet GOTO(I, X)
				ItemSet set = new ItemSet();
				for (Item item: itemSet) {
					if (symbol.equals(item.nextSymbol())) {
						Production production = item.getProduction();
						int position = item.getPosition() + 1;
						set.add(production, position);
					}
				}
				set = closure(set);

				// Add set to GOTO(I, X)
				if (super.contains(set)) {
					int index = super.indexOf(set);
					set = super.get(index);
					itemSet.addGoto(symbol, set);
				} else {
					super.add(set);
					itemSet.addGoto(symbol, set);
					queue.offer(set);
				}
			}
		}
	}

	public void generateSpreadRelation() {
		for (ItemSet itemSet: this) {
			for (Item tm: itemSet) {
				Item item = new Item(tm.getProduction(), tm.getPosition());
				item.setLASymbol("$");
				ItemSet set = new ItemSet();
				set.add(item);
				set = closureWithLA(set);
				for (Item t: set) {
					String symbol = t.nextSymbol();
					if (symbol == null) continue;
					ItemSet s = itemSet.gotoItemSet(symbol);
					Item tt = new Item(t.getProduction(), t.getPosition() + 1);
					int index = s.indexOf(tt);
					Item it = s.get(index);
					it.addAllLASymbols(t.getLASymbols());
					if (t.getLASymbols().contains("$")) tm.addSpreadItem(it);
				}
			}
		}
	}

	public void spread() {
		boolean update = true;
		while (update) {
			update = false;
			for (ItemSet itemSet: this) {
				for (Item item: itemSet) {
					for (Item t: item.getSpreadList()) {
						Set<String> fromLASymbols = item.getLASymbols();
						Set<String> toLASymbols = t.getLASymbols();
						if (!toLASymbols.containsAll(fromLASymbols)) {
							toLASymbols.addAll(fromLASymbols);
							update = true;
						}
					}
				}
			}
		}
	}

	public LALRTable buildLALRTable() {
		LALRTable lalrTable = new LALRTable();
		for (int i = 0; i < this.size(); i++) {
			ItemSet itemSet = this.get(i);
			for (Item item: itemSet) {
				String symbol = item.nextSymbol();
				if (symbol == null) {
					if (item.getHead().equals(alphabet.getStartSymbol())
							&& item.getPosition() == 1) {
						lalrTable.addAccept(i, "$");
					} else {
						int index = grammar.indexOf(item.getProduction());
						for (String LASymbol : item.getLASymbols())
							lalrTable.addReduction(i, LASymbol, index);
					}
				} else if (alphabet.isTerminal(symbol)) {
					int index = super.indexOf(itemSet.gotoItemSet(symbol));
					lalrTable.addShift(i, symbol, index);
				} else {
					int index = super.indexOf(itemSet.gotoItemSet(symbol));
					lalrTable.addGo(i, symbol, index);
				}
			}
		}
		return lalrTable;
	}

	private ItemSet closureWithLA(ItemSet itemSet) {

		// Avoid duplication item by map
		Map<Item, Set<String>> map = new HashMap<>();
		for (Item item: itemSet)
			map.put(item, item.getLASymbols());

		// Add all the item which its nextSymbol is nonterminal to queue
		Queue<Item> queue = new LinkedList<>();
		for (Item item: itemSet) {
			String symbol = item.nextSymbol();
			if (symbol != null &&
					!alphabet.isTerminal(symbol))
				queue.offer(item);
		}

		// BFS
		while (!queue.isEmpty()) {
			Item item = queue.poll();
			Set<String> firstSet = new HashSet<>();
			List<String> otherSymbols = item.otherSymbols();
			for (String s: item.getLASymbols()) {
				otherSymbols.add(s);
				Set<String> set = first(otherSymbols, new HashSet<>());
				firstSet.addAll(set);
				otherSymbols.remove(otherSymbols.size() - 1);
			}
//			Set<String> firstSet = first(item.otherSymbols());

			String symbol = item.nextSymbol();
			List<Production> list = grammar.getProductionsByHead(symbol);
			for (Production production: list) {
				Item t = new Item(production, 0, firstSet);
				if (map.containsKey(t) &&
						map.get(t).containsAll(firstSet))
					continue;
				if (!map.containsKey(t)) {
					map.put(t, new HashSet<>(firstSet));
				} else {
					Set<String> s = map.get(t);
					s.addAll(firstSet);
					map.put(t, s);
				}
				String s = t.nextSymbol();
				if (s != null && !alphabet.isTerminal(s))
					queue.offer(t);
			}
		}
		ItemSet closureSet = new ItemSet();
		for (Item item: map.keySet()) {
			Set<String> s = map.get(item);
			item.addAllLASymbols(s);
			closureSet.add(item);
		}
		return closureSet;
	}

	private ItemSet closure(ItemSet itemSet) {
		// Init closure set
		ItemSet closureSet = new ItemSet();
		closureSet.addAll(itemSet);

		// Add symbols need to be expanded (root)
		Queue<String> queue = new LinkedList<>();
		for (Item item: itemSet) {
			String symbol = item.nextSymbol();
			if (symbol != null &&
					!alphabet.isTerminal(symbol))
				queue.offer(symbol);
		}

		// Avoid duplicates
		HashSet<String> expanded = new HashSet<>();

		// BFS
		while (!queue.isEmpty()) {
			String symbol = queue.poll();
			expanded.add(symbol);
			ArrayList<Production> list = grammar.getProductionsByHead(symbol);
			for (Production production: list) {
				Item item = new Item(production, 0);
				closureSet.add(item);
				String s = item.nextSymbol();
				if (!alphabet.isTerminal(s) &&
						!expanded.contains(s)) {
					queue.offer(s);
					expanded.add(s);
				}
			}
		}
		return closureSet;
	}

	public void extractKernel() {
		for (ItemSet itemSet: this)
			itemSet.removeIf(item -> item.getPosition() == 0 &&
					!alphabet.isStartSymbol(item.getHead()));
	}

	private Set<String> first(List<String> list, Set<String> used) {
		HashSet<String> set = new HashSet<>();
		boolean eps = false;
		for (String symbol: list) {
			if (symbol == null) continue;
			if (alphabet.isTerminal(symbol)) {
				set.add(symbol);
			}
			else {
				if (used.contains(symbol)) continue;
				ArrayList<Production> a = grammar.getProductionsByHead(symbol);
				for (Production p: a) {
					boolean added = used.contains(symbol);
					used.add(symbol);
					set.addAll(first(p.getBody(), used));
					if (!added) used.remove(symbol);
				}
			}
			if (!set.contains("\0")) {
				eps = false;
				break;
			}
			eps = true;
			set.remove("\0");
		}
		if (eps) set.add("\0");
		return set;
	}

	public void buildLAClosure() {
		List<ItemSet> list = new ArrayList<>();
		for (ItemSet itemSet: this) {
			ItemSet set = closureWithLA(itemSet);
			itemSet.clear();
			itemSet.addAll(set);
//			list.add(set);
		}
//		super.clear();
//		super.addAll(list);
	}
}
