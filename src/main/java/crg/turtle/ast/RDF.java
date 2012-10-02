package crg.turtle.ast;
import crg.turtle.NodeBuilder;
import java.util.Map;

public class RDF {

  public static String NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
  public static String PREFIX = "rdf";

  private static final String TYPE = "type";
  private static final String FIRST = "first";
  private static final String REST = "rest";
  private static final String NIL = "nil";

  private static Object type = null;
  private static Object first = null;
  private static Object rest = null;
  private static Object nil = null;
  private static Object typeFull = null;
  private static Object firstFull = null;
  private static Object restFull = null;
  private static Object nilFull = null;
  private static Map<String,String> prefixMap;
  private static NodeBuilder builder;


  public static void init(NodeBuilder builder, Map<String,String> prefixMap) {
    RDF.builder = builder;
    RDF.prefixMap = prefixMap;
  }

  public static Object getType() {
    if (prefixMap.containsKey(PREFIX)) {
      if (type == null) type = builder.newIri(prefixMap, null, PREFIX, TYPE);
      return type;
    } else {
      if (typeFull == null) typeFull = builder.newIri(NS + TYPE);
      return typeFull;
    }
  }

  public static Object getFirst() {
    if (prefixMap.containsKey(PREFIX)) {
      if (first == null) first = builder.newIri(prefixMap, null, PREFIX, FIRST);
      return first;
    } else {
      if (firstFull == null) firstFull = builder.newIri(NS + FIRST);
      return firstFull;
    }
  }

  public static Object getRest() {
    if (prefixMap.containsKey(PREFIX)) {
      if (rest == null) rest = builder.newIri(prefixMap, null, PREFIX, REST);
      return rest;
    } else {
      if (restFull == null) restFull = builder.newIri(NS + REST);
      return restFull;
    }
  }

  public static Object getNil() {
    if (prefixMap.containsKey(PREFIX)) {
      if (nil == null) nil = builder.newIri(prefixMap, null, PREFIX, NIL);
      return nil;
    } else {
      if (nilFull == null) nilFull = builder.newIri(NS + NIL);
      return nilFull;
    }
  }

}

