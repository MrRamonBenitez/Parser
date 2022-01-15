import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        String fileName = "data.csv";
        String[] employee1 = "1,John,Smith,USA,25".split(",");
        makeCSV(employee1, fileName);
        String[] employee2 = "2,Inav,Petrov,RU,23".split(",");
        makeCSV(employee2, fileName);

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
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
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    private static void writeString(String json, String fileName) {
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write(String.valueOf(json));
            writer.append('\n');
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void makeXMLEmployee(String fileName) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        Element staff = document.createElement("staff");
        document.appendChild(staff);
        Element record1 = document.createElement("employee");
        record1.setAttribute("id", "1");
        record1.setAttribute("firstName", "John");
        record1.setAttribute("lastName", "Smith");
        record1.setAttribute("country", "USA");
        record1.setAttribute("age", "25");
        staff.appendChild(record1);
        Element record2 = document.createElement("employee");
        record2.setAttribute("id", "2");
        record2.setAttribute("firstName", "Inav");
        record2.setAttribute("lastName", "Petrov");
        record2.setAttribute("country", "RU");
        record2.setAttribute("age", "23");
        staff.appendChild(record2);
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File(fileName));
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, streamResult);
    }

}
