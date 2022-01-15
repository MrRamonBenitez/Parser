import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.String.valueOf;


public class Main {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {

        String fileName = "data.csv";
        String[] employee1 = "1,John,Smith,USA,25".split(",");
        makeCSV(employee1, fileName);
        String[] employee2 = "2,Inav,Petrov,RU,23".split(",");
        makeCSV(employee2, fileName);

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        List<Employee> list1 = parseCSV(columnMapping, fileName);
        String json = listToJson(list1);
        fileName = "data.json";
        writeString(json, fileName);

        fileName = "data.xml";
        try {
            makeXMLEmployee(fileName);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        List<Employee> list2 = parseXML("data.xml");
        json = listToJson(list2);
        fileName = "data2.json";
        writeString(json, fileName);

        File fileJson = new File(fileName);
        fileName = "new_data.json";
        File newJson = new File(fileName);
        fileJson.renameTo(newJson);

        json = readString(fileName);

        List<Employee> list3 = jsonToList(json);

        for (Employee record : list3) {
            System.out.println(record);
        }
    }

    private static void makeCSV(String[] employee, String name) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(name, true))) {
            writer.writeNext(employee);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Employee> parseCSV(String[] colMap, String name) {
        List<Employee> list = null;
        try (CSVReader reader = new CSVReader(new FileReader(name))) {
            ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(colMap);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            list = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    private static List<Employee> jsonToList(String json) {
        List<Employee> list = new ArrayList<>();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        JsonArray jsonArray = new JsonParser().parse(json).getAsJsonArray();
        Iterator jsonItr = jsonArray.iterator();
        while (jsonItr.hasNext()) {
            JsonObject object = (JsonObject) jsonItr.next();
            Employee record = gson.fromJson(object, Employee.class);
            list.add(record);
        }
        return list;
    }

    private static void writeString(String json, String fileName) {
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write(valueOf(json));
            writer.append('\n');
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static String readString(String fileName) {
        String json;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader buffReader = new BufferedReader(new FileReader(fileName))) {
            while ((json = buffReader.readLine()) != null) {
                sb.append(json);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return sb.toString();
    }

    private static void makeXMLEmployee(String fileName) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        Element staff = document.createElement("staff");
        document.appendChild(staff);

        Element record1 = document.createElement("employee");
        staff.appendChild(record1);

        Element id1 = document.createElement("id");
        id1.appendChild(document.createTextNode("1"));
        record1.appendChild(id1);
        Element firstName1 = document.createElement("firstName");
        firstName1.appendChild(document.createTextNode("John"));
        record1.appendChild(firstName1);
        Element lastName1 = document.createElement("lastName");
        lastName1.appendChild(document.createTextNode("Smith"));
        record1.appendChild(lastName1);
        Element country1 = document.createElement("country");
        country1.appendChild(document.createTextNode("USA"));
        record1.appendChild(country1);
        Element age1 = document.createElement("age");
        age1.appendChild(document.createTextNode("25"));
        record1.appendChild(age1);

        Element record2 = document.createElement("employee");
        staff.appendChild(record2);

        Element id2 = document.createElement("id");
        id2.appendChild(document.createTextNode("2"));
        record2.appendChild(id2);
        Element firstName2 = document.createElement("firstName");
        firstName2.appendChild(document.createTextNode("Inav"));
        record2.appendChild(firstName2);
        Element lastName2 = document.createElement("lastName");
        lastName2.appendChild(document.createTextNode("Petrov"));
        record2.appendChild(lastName2);
        Element country2 = document.createElement("country");
        country2.appendChild(document.createTextNode("RU"));
        record2.appendChild(country2);
        Element age2 = document.createElement("age");
        age2.appendChild(document.createTextNode("23"));
        record2.appendChild(age2);

        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File(fileName));
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, streamResult);
    }

    private static List<Employee> parseXML(String name) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> list = null;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(name));
        Node root = doc.getDocumentElement();
        list = read(root);
        return list;
    }

    private static List<Employee> read(Node node) {
        List<Employee> list = new ArrayList<>();
        Long id = null;
        String firstName = null;
        String lastName = null;
        String country = null;
        int age = 0;
        NodeList nodeList = node.getChildNodes();
        for (int x = 0; x < nodeList.getLength(); x++) {
            Node node_ = nodeList.item(x);
            NodeList node_List = node_.getChildNodes();
            for (int y = 0; y < node_List.getLength(); y++) {
                Node nodeRecord = node_List.item(y);
                if (Node.ELEMENT_NODE == nodeRecord.getNodeType()) {
                    Element element = (Element) nodeRecord;
                    switch (element.getTagName()) {
                        case "id": {
                            id = Long.parseLong(element.getTextContent());
                            break;
                        }
                        case "firstName": {
                            firstName = element.getTextContent();
                            break;
                        }
                        case "lastName": {
                            lastName = element.getTextContent();
                            break;
                        }
                        case "country": {
                            country = element.getTextContent();
                            break;
                        }
                        case "age": {
                            age = Integer.parseInt(element.getTextContent());
                            break;
                        }
                    }
                }
            }
            Employee record = new Employee(id, firstName, lastName, country, age);
            list.add(record);
        }
        return list;
    }
}