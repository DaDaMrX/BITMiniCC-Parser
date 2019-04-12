import java.util.ArrayList;

class Production {

    private String head;
    private ArrayList<String> body;

    Production(String head) {
        this.head = head;
        body = new ArrayList<>();
    }

    void addSymbol(String symbol) {
        body.add(symbol);
    }

    String getHead() {
    	return head;
    }

    String getSymbol(int index) {
    	return index < body.size() ? body.get(index) : null;
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof Production) {
			Production another = (Production)obj;
			return head.equals(another.head) &&
					body.equals(another.body);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return head.hashCode() + body.hashCode();
	}

	@Override
	public String toString() {
    	StringBuilder s = new StringBuilder(head + " ->");
    	for (String symbol: body)
    		s.append(" ").append(symbol);
		return s.toString();
	}

	public int bodySize() {
    	return body.size();
	}

	public ArrayList<String> getBody() {
    	return body;
	}
}
