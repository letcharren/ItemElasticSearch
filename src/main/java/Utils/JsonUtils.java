package Utils;

import com.google.gson.GsonBuilder;
import spark.ResponseTransformer;

public class JsonUtils {

     public static String toJson(Object object) {
         return new GsonBuilder().create().toJson(object);
     }


     public static ResponseTransformer json() {
         return JsonUtils::toJson;
     }

}

