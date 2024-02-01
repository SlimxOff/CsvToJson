package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;

public class Main {

    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "/Users/ivangoyda/Documents/JavaProject/CsvToJson/src/main/java/org/example/data.csv";

        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");

        System.out.println("Conversion completed successfully!");

        List<Employee> xmlList = parseXML("/Users/ivangoyda/Documents/JavaProject/CsvToJson/src/main/java/org/example/data.xml");
        String xmlJson = listToJson(xmlList);
        writeString(xmlJson, "data2.json");

        System.out.println("XML to JSON conversion completed successfully!");
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> list = null;
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            list = csvToBean.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static String listToJson(List<Employee> list) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type listType = new com.google.gson.reflect.TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(list, listType);
    }

    private static void writeString(String json, String fileName) {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Employee> parseXML(String fileName) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(fileName));

            NodeList nodeList = document.getDocumentElement().getChildNodes();
            List<Employee> employeeList = new ArrayList<>();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    long id = Long.parseLong(getNodeValue(element, "id"));
                    String firstName = getNodeValue(element, "firstName");
                    String lastName = getNodeValue(element, "lastName");
                    String country = getNodeValue(element, "country");
                    int age = Integer.parseInt(getNodeValue(element, "age"));

                    employeeList.add(new Employee(id, firstName, lastName, country, age));
                }
            }

            return employeeList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getNodeValue(Element element, String nodeName) {
        NodeList nodeList = element.getElementsByTagName(nodeName).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

}
