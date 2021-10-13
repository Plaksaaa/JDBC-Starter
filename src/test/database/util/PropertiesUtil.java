package test.database.util;

import java.io.IOException;
import java.util.Properties;

public final class PropertiesUtil {

    private static final Properties PROPERTIES = new Properties();

    //    cтатический блок срабатывает один раз
    static {
        loadProperties();
    }

    private PropertiesUtil() {
    }

    //    чтобы получать значения пароли и тд
    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    //    всегда сможем достучаться до файла если он лежит в срц а он лежит ибо мы его пометили как проп
    private static void loadProperties() {
        try (var inputStream = PropertiesUtil.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
