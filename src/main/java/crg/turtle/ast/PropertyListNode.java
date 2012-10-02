package crg.turtle.ast;

import crg.turtle.TripleSink;
import crg.turtle.NodeBuilder;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import beaver.Symbol;

public class PropertyListNode extends Symbol implements AstNode {
  private final Object node;
  private final List<PredicateObjects> properties;
  private List<Triple> triples;

  public PropertyListNode(Object node, List<PredicateObjects> properties) {
    this.node = node;
    this.properties = properties;
    triples = null;
  }

  public List<PredicateObjects> getProperties() { return properties; }

  public Object getNode() {
    return node;
  }

  public void drain(TripleSink s) {
    for (PredicateObjects pos: properties) {
      Object p = pos.getPredicate();
      for (Object o: pos.getObjects()) {
        if (o instanceof AstNode) ((AstNode)o).addAsObject(s, node, p);
        else s.triple(node, p, o);
      }
    }
  }

  public void addAsObject(TripleSink sink, Object s, Object p) {
    sink.triple(s, p, node);
    drain(sink);
  }

}
