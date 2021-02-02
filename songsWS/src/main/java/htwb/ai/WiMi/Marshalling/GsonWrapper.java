package htwb.ai.WiMi.Marshalling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class GsonWrapper<T> implements Marshaller<T>
{

    @Override
    public String marshal(T object)
    {
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(object);
    }

    @Override
    public String marshal(List<T> object)
    {
        Gson gson = new GsonBuilder().serializeNulls().create();
         return gson.toJson(object);
    }

    @Override
    public T unmarshal(String json, Class<T> type)
    {
        // Building a POJO Song from the JSON String
        Gson gson = new GsonBuilder().serializeNulls().create();
        return  gson.fromJson(json, type);
    }

}
