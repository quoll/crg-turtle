package crg.turtle;

import java.util.Map;

/**
 * A callback interface for receiving triples that are parsed from a TTL file.
 * This interface is informed explicitly what the running prefix map is.
 */
public interface PrefixedTripleSink extends TripleSink {

  /**
   * Sets the running prefix map. This map will be updated while parsing, so the sink
   * needs to be aware that the contents may change from one triple to the next.
   * Will only be called by the parser on initialization.
   * @param prefixMap The map the parser uses.
   */
  void setPrefixMap(Map<String,String> prefixMap);

  /**
   * Called by the parser whenever a new triple has been read.
   * @param s a subject node.
   * @param p a predicate node.
   * @param o an object node.
   */
  void triple(Object s, Object p, Object o);
}
