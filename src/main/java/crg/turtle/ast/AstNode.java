package crg.turtle.ast;

import java.util.List;
import crg.turtle.NodeBuilder;

public interface AstNode {
  /**
   * Retrieves the triples associated with a node. This naturally recurses into the various
   * structures associated with different node types.
   * @param triples A list of triples to add to. This list, or a superset of it, should be returned.
   * @return a list containing everything from triples plus the new triples associated with this node.
   */
  List<Triple> getTriples(List<Triple> triples);

  /**
   * If this node represents more, then get the simple node that this structure
   * is built around.
   * @return A single blank node.
   */
  Object getNode();

  /**
   * Tells a node to add itself as the object of a triple, along with any
   * associated triples described by the node.
   * @param triples The existing triples to add to.
   * @param s The subject of the triple that this is an object of.
   * @param p The predicate of the triple that this is an object of.
   * @return A list containing all of triples, plus the triples associated with this object.
   */
  List<Triple> addAsObject(List<Triple> triples, Object s, Object p);

}
