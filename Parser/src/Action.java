public class Action {

	private String type;
	private int index;

	Action(String type, int index) {
		this.type = type;
		this.index = index;
	}

	Action(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public int getIndex() {
		return index;
	}
}