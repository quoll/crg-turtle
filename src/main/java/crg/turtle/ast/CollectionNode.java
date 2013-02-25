package crg.turtle.ast;

import crg.turtle.NodeBuilder;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import beaver.Symbol;

public class CollectionNode extends Symbol implements AstNode {
  private final List<Object> c;
  private Object head;
  private List<Triple> _triples;
  private final NodeBuilder builder;

  public CollectionNode(NodeBuilder builder, Symbol[] objects) {
    this(builder, Arrays.asList(objects));
  }

  public CollectionNode(NodeBuilder builder, List<Symbol> objectList) {
    c = new ArrayList<Object>();
    this.builder = builder;
    if (objectList != null) for (Symbol o: objectList) c.add(o.value);
    _triples = null;
    head = null;
  }

  public List<Triple> addAsObject(List<Triple> triples, Object s, Object p) {
    triples = getTriples(triples);
    triples.add(new Triple(s, p, head));
    return triples;
  }

  public List<Triple> getTriples(List<Triple> triples) {
    if (head != null) throw new IllegalStateException();
    Object nil = RDF.getNil();
    if (c.size() > 0) {
      head = builder.newBlank();
      Object cn = c.get(0);
      Object first = RDF.getFirst();
      Object rest = RDF.getRest();
      if (cn instanceof AstNode) ((AstNode)cn).addAsObject(triples, head, first);
      else triples.add(new Triple(head, first, cn));
      Object last = head;
      for (int i = 1; i < c.size(); i++) {
        Object n = builder.newBlank();
        triples.add(new Triple(last, rest, n));
        Object li = c.get(i);
        if (li instanceof AstNode) ((AstNode)li).addAsObject(triples, n, first);
        else triples.add(new Triple(n, first, li));
        last = n;
      }
      triples.add(new Triple(last, rest, nil));
    } else {
      head = nil;
    }
    return triples;
  }

  public Object getNode() {
    if (head == null) _triples = getTriples(new ArrayList<Triple>());
    return head;
  }

  public String toString() {
    new Exception().printStackTrace();
    return "Collection node: " + head;
  }
}
