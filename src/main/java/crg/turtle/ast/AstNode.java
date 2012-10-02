package crg.turtle.ast;

import java.util.List;
import crg.turtle.TripleSink;
import crg.turtle.NodeBuilder;

public interface AstNode {
  /**
   * Push all triples into a sink. This is associated with getMore()
   * but the data goes directly to the sink instead of a data structure.
   */
  void drain(TripleSink sink);

  /**
   * If this node represents more, then get the simple node that this structure
   * is built around.
   * @return A single blank node.
   */
  Object getNode();

  /**
   * Tells a node to add itself as the object of a triple, along with any
   * associated triples described by the node.
   */
  void addAsObject(TripleSink sink, Object s, Object p);

}
