import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        String fileName = "data.csv";
        String[] employee1 = "1,John,Smith,USA,25".split(",");
        makeCsv(employee1, fileName);
        String[] employee2 = "2,Inav,Petrov,RU,23".split(",");
        makeCsv(employee2, fileName);

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        fileName = "data.json";
        writeString(json, fileName);
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

    private static void writeString (String json, String fileName) {
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write(String.valueOf(json));
            writer.append('\n');
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void makeCsv(String[] employee, String name) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(name, true))) {
           writer.writeNext(employee);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
