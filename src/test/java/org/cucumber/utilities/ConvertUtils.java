package org.cucumber.utilities;


import com.google.gson.Gson;
import io.restassured.path.json.JsonPath;
import org.bson.Document;
import org.bson.types.Decimal128;

import java.math.BigDecimal;
import java.util.Map;

public class ConvertUtils {

    public static JsonPath mapToJsonpath(Map<String, Object> map) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(map);

        return JsonPath.from(jsonString);
    }

    public static Double getDecimal128(Map<String,Object> map, String key) {
        Decimal128 decimal128 = (Decimal128) map.get(key);
        BigDecimal bigDecimal = decimal128.bigDecimalValue();
        Double doubleValue = bigDecimal.doubleValue();
//        System.out.println("doubleValue = " + doubleValue);

        return doubleValue;
    }

    public static Double getDecimal128(Document document, String key) {
        Decimal128 decimal128 = document.get(key, Decimal128.class);
        BigDecimal bigDecimal = decimal128.bigDecimalValue();
        Double doubleValue = bigDecimal.doubleValue();
//        System.out.println("doubleValue = " + doubleValue);

        return doubleValue;
    }

    public static int convertDecimal128ToInt(Object value) {
        if (value instanceof Decimal128) {
            return ((Decimal128) value).bigDecimalValue().intValue();
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else {
            throw new IllegalArgumentException("Unsupported type: " + value.getClass().getName());
        }
    }

    public static BigDecimal convertDecimal128ToBigDecimal(Object value) {
        if (value instanceof Decimal128) {
            return ((Decimal128) value).bigDecimalValue();
        } else if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        } else {
            throw new IllegalArgumentException("Unsupported type: " + value.getClass().getName());
        }
    }

}





