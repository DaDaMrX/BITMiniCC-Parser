import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.List;
import java.util.Stack;

public class LALR {

	private LALRTable table;
	private Grammar grammar;
	private Alphabet alphabet;

	LALR(LALRTable table) {
		this.table = table;
		grammar = Grammar.newInstance();
		alphabet = Alphabet.newInstance();
	}

	private void snapshot(Stack<Integer> stack, List<Token> list) {
		for (int s: stack) System.out.print(s + " ");
		System.out.print("     ");
		for (Token s: list) System.out.print(s.getValue() + " ");
		System.out.println("$");
	}

	public Document parse(List<Token> list) throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();

		Stack<Integer> stack = new Stack<>();
		stack.push(0);
		String startSymbol = alphabet.getStartSymbol();
		Stack<Node> nodeStack = new Stack<>();
		Element startSymbolElement = document.createElement(startSymbol);
		nodeStack.push(startSymbolElement);
		snapshot(stack, list);

		int i = 0;
		Token token = list.get(i);
		String value = token.getValue();
		String ttype = token.getType();
		String symbol;
		if (ttype.equals("keyword") ||
				ttype.equals("delimiter") ||
				ttype.equals("operator")) {
			symbol = value;
		} else symbol = ttype;
		boolean running = true;
		boolean successful = false;
		while (running) {
			int state = stack.peek();
			String type = table.getActionType(state, symbol);
			switch (type) {
				case "SHIFT":
					state = table.getActionIndex(state, symbol);
					stack.push(state);

					if (ttype.equals("keyword") ||
							ttype.equals("delimiter") ||
							ttype.equals("operator")) {
						Text text = document.createTextNode(value);
						nodeStack.push(text);
					}
					else
					{
						Text text = document.createTextNode(value);
						Element element = document.createElement(ttype);
						element.appendChild(text);
						nodeStack.push(element);
					}

					i++;
					if (i < list.size()) {
						token = list.get(i);
						value = token.getValue();
						ttype = token.getType();
						if (ttype.equals("keyword") ||
								ttype.equals("delimiter") ||
								ttype.equals("operator")) {
							symbol = value;
						} else symbol = ttype;
					} else symbol = "$";
					snapshot(stack, list.subList(i, list.size()));
					break;
				case "REDUCTION":
					int index = table.getActionIndex(state, symbol);
					Production p = grammar.get(index);
					int num = p.bodySize();
					for (int j = 0; j < num; j++) stack.pop();
					state = stack.peek();
					state = table.getGoto(state, p.getHead());
					stack.push(state);

					Stack<Node> s = new Stack<>();
					for (int j = 0; j < num; j++) s.push(nodeStack.pop());
					Element headElement = document.createElement(p.getHead());
					for (int j = 0; j < num; j++) headElement.appendChild(s.pop());
					nodeStack.push(headElement);

					snapshot(stack, list.subList(i, list.size()));
					break;
				default:
					System.out.println(type);
					running = false;
					if (type.equals("ACCEPT")) successful = true;
					break;
			}
		}
		if (successful)
		{
			Node e = nodeStack.pop();
			Node n = nodeStack.pop();
			n.appendChild(e);
			document.appendChild(n);
		}
		return  document;
	}
}
