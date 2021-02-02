package htwb.ai.WiMi.Marshalling;

import com.fasterxml.jackson.xml.XmlMapper;
import htwb.ai.WiMi.logger.Log;

import java.io.IOException;
import java.util.List;


/**
 *  Implementation of the  {@link Marshaller} interface, as used
 *  in {@link htwb.ai.WiMi.controller.SongsController}
 * @param <T>
 */
public class XmlMapperWrapper<T> implements Marshaller<T>
{
    private final String TAG = getClass().getSimpleName();

    @Override
    public String marshal(List<T> list)
    {
        StringBuilder sb = new StringBuilder();

        for (T object : list)
        {
            sb.append(this.marshal(object));
        }
        return sb.toString();
    }


    @Override
    public String marshal(T object)
    {
        XmlMapper xmlMapper = new XmlMapper();
        String xml = "";
        try
        {
            xml = xmlMapper.writeValueAsString(object);
            Log.deb(TAG, "marshal ", "xml = " + xml);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.err(TAG, "marshal", "xml = " + xml);
        }

        return xml;
    }

    @Override
    public T unmarshal(String jso, Class<T> typen)
    {
        return null;
    }
}
