import org.w3c.dom.Document;
import org.xml.sax.XMLReader;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
        	System.out.println("missing file.");
        	return;
        }

        Scanner scanner = new Scanner(true);
        scanner.run("test.c", "test.token.xml");

		Parser parser = new Parser();
        parser.run(args[0], args[1]);
    }

    private LALR lalr;

	public Parser() throws Exception {
		Generator generator = new Generator();
		LALRTable table = generator.generate("grammar.txt");
		lalr = new LALR(table);
	}

	public void run(String inputFile, String outputFile) throws Exception {
	    List<Token> tokens = readXML(inputFile);
	    Document document = lalr.parse(tokens);
	    writeXML(document, outputFile);
    }

	private List<Token> readXML(String lex) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		XMLReader xmlReader = saxParser.getXMLReader();
		List<Token> tokens = new ArrayList<>();
		TokenHandler tokenHandler = new TokenHandler(tokens);
		xmlReader.setContentHandler(tokenHandler);
		xmlReader.parse(lex);
		return tokens;
	}

	private void writeXML(Document document, String outputFile) throws Exception {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformerFactory.setAttribute("indent-number", 2);

		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		DOMSource domSource = new DOMSource(document);
		FileWriter fileWriter = new FileWriter(outputFile);
		StreamResult streamResult = new StreamResult(fileWriter);
		transformer.transform(domSource, streamResult);
	}
}
