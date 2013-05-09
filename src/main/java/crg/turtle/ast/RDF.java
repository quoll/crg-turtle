package crg.turtle.ast;
import crg.turtle.nodes.NodeBuilder;
import java.util.Map;
import clojure.lang.IPersistentMap;

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
  private static NodeBuilder builder;


  public static void init(NodeBuilder builder) {
    RDF.builder = builder;
  }

  public static Object getType(IPersistentMap prefixMap) {
    if (prefixMap.containsKey(PREFIX)) {
      if (type == null) type = builder.new_iri(prefixMap, null, PREFIX, TYPE);
      return type;
    } else {
      if (typeFull == null) typeFull = builder.new_iri(NS + TYPE);
      return typeFull;
    }
  }

  public static Object getFirst(IPersistentMap prefixMap) {
    if (prefixMap.containsKey(PREFIX)) {
      if (first == null) first = builder.new_iri(prefixMap, null, PREFIX, FIRST);
      return first;
    } else {
      if (firstFull == null) firstFull = builder.new_iri(NS + FIRST);
      return firstFull;
    }
  }

  public static Object getRest(IPersistentMap prefixMap) {
    if (prefixMap.containsKey(PREFIX)) {
      if (rest == null) rest = builder.new_iri(prefixMap, null, PREFIX, REST);
      return rest;
    } else {
      if (restFull == null) restFull = builder.new_iri(NS + REST);
      return restFull;
    }
  }

  public static Object getNil(IPersistentMap prefixMap) {
    if (prefixMap.containsKey(PREFIX)) {
      if (nil == null) nil = builder.new_iri(prefixMap, null, PREFIX, NIL);
      return nil;
    } else {
      if (nilFull == null) nilFull = builder.new_iri(NS + NIL);
      return nilFull;
    }
  }

}

