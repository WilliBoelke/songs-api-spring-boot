package htwb.ai.WiMi.Marshalling;

import java.util.List;

/**
 *
 * @param <T>
 */
public interface Marshaller<T>
{

   /**
    *
    * @param object
    * @return
    */
   public String marshal(List<T> object);

   /**
    *
    * @param object
    * @return
    */
   public String marshal(T object);

   /**
    *
    * @param jso
    * @param typen
    * @return
    */
   public T unmarshal(String jso,  Class<T> typen);
}
