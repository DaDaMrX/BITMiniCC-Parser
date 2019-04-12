import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Generator {

	private Alphabet alphabet;
	private Grammar grammar;

	Generator() {
		alphabet = Alphabet.newInstance();
		grammar = Grammar.newInstance();
	}

	LALRTable generate(String path) throws Exception {
		FileReader fileReader = new FileReader(path);
		BufferedReader reader = new BufferedReader(fileReader);
		String line;
		while ((line = reader.readLine()) != null) {
			String head;
			if (!line.contains("::=")) {
				head = grammar.get(grammar.size() - 1).getHead();
			} else {
				String[] split = line.split("::=");
				head = split[0].trim();
				alphabet.add(head);
				alphabet.setTerminal(head, false);
				line = split[1];
			}

			String[] bodies = line.trim().split("\\|");
			for (String body: bodies) {
				Production production = new Production(head);
				String[] symbols = body.trim().split(" ");
				for (String symbol: symbols) {
					if (symbol.equals("\\eps")) symbol = "\0";
					if (symbol.equals("<DoubleVerticalBar>")) symbol = "||";
					if (symbol.equals("<VerticalBar>")) symbol = "|";
					alphabet.add(symbol);
					production.addSymbol(symbol);
				}
				grammar.add(production);
			}
		}

		ItemSet itemSet = new ItemSet();
		Item item = new Item(grammar.get(0), 0, "$");
		itemSet.add(item);
		ItemSetCollection collection = new ItemSetCollection();
		collection.init(itemSet);
		collection.build();
		collection.extractKernel();
		collection.generateSpreadRelation();
		collection.spread();
		collection.buildLAClosure();
		return collection.buildLALRTable();
	}
}
