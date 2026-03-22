package com.homelab.order.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationLoader {
    private static final Map<String, String> properties = new HashMap<>();

    static {
        // Ưu tiên đọc từ đường dẫn file bên ngoài (trong VM/container), có thể cấu hình qua biến môi trường
        String configPath = System.getenv().getOrDefault("CONFIG_FILE_PATH", "/app/config/override-config.xml");
        File configFile = new File(configPath);

        try {
            InputStream sourceStream = configFile.exists()
                    ? new FileInputStream(configFile)
                    : ConfigurationLoader.class.getClassLoader().getResourceAsStream("override-config.xml");

            try (InputStream input = sourceStream) {
            if (input != null) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(input);
                doc.getDocumentElement().normalize();

                NodeList entries = doc.getElementsByTagName("entry");
                for (int i = 0; i < entries.getLength(); i++) {
                    Element entry = (Element) entries.item(i);
                    // Hỗ trợ nhiều cặp key-value trong cùng một thẻ <entry>
                    NodeList keys = entry.getElementsByTagName("key");
                    NodeList values = entry.getElementsByTagName("value");

                    for (int j = 0; j < keys.getLength() && j < values.getLength(); j++) {
                        properties.put(keys.item(j).getTextContent(), values.item(j).getTextContent());
                    }
                }
            } else {
                System.err.println("Warning: config.xml not found in path '" + configPath + "' or in classpath.");
            }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }
}