package crg.turtle;

/**
 * A callback interface for receiving triples that are parsed from a TTL file.
 */
public interface TripleSink {

  /**
   * Called by the parser whenever a new triple has been read.
   * @param s a subject node.
   * @param p a predicate node.
   * @param o an object node.
   */
  void triple(Object s, Object p, Object o);
}
