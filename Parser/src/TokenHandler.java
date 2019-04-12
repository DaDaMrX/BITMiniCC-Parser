import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class TokenHandler extends DefaultHandler {

	private StringBuilder value;
	private boolean isToken;
	private StringBuilder type;
	private boolean isType;
	private List<Token> tokens;
	private Token token;

	TokenHandler(List<Token> tokens) {
		this.tokens = tokens;
	}

	@Override
	public void startDocument() {
		value = new StringBuilder();
		type = new StringBuilder();
	}

	@Override
	public void startElement(String uri,
	                         String localName,
	                         String qName,
	                         Attributes attributes) {
		switch (qName) {
			case "token":
				token = new Token();
				break;
			case "value":
				isToken = true;
				value.setLength(0);
				break;
			case "type":
				isType = true;
				type.setLength(0);
				break;
		}
	}

	@Override
	public void endElement(String uri,
	                       String localName,
	                       String qName) {
		switch (qName) {
			case "token":
				tokens.add(token);
				break;
			case "value":
				isToken = false;
				token.setValue(value.toString());
				break;
			case "type":
				isType = false;
				token.setType(type.toString());
				break;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		if (isToken) value.append(new String(ch, start, length));
		else if (isType) type.append(new String(ch, start, length));
	}
}
