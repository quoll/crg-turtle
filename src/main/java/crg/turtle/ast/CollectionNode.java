package crg.turtle.ast;

import crg.turtle.TripleSink;
import crg.turtle.NodeBuilder;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import beaver.Symbol;

public class CollectionNode extends Symbol implements AstNode {
  private final List<Object> c;
  private Object head;
  private List<Triple> triples;
  private final NodeBuilder builder;

  public CollectionNode(NodeBuilder builder, Symbol[] objects) {
    this(builder, Arrays.asList(objects));
  }

  public CollectionNode(NodeBuilder builder, List<Symbol> objectList) {
    c = new ArrayList<Object>();
    this.builder = builder;
    if (objectList != null) for (Symbol o: objectList) c.add(o.value);
    triples = null;
    head = null;
  }

  public List<Object> getCollection() { return c; }

  public void addAsObject(TripleSink sink, Object s, Object p) {
    drain(sink);
    sink.triple(s, p, head);
  }

  public void drain(TripleSink sink) {
    if (head != null) throw new IllegalStateException();
    Object nil = RDF.getNil();
    if (c.size() > 0) {
      head = builder.newBlank();
      Object cn = c.get(0);
      Object first = RDF.getFirst();
      Object rest = RDF.getRest();
      if (cn instanceof AstNode) ((AstNode)cn).addAsObject(sink, head, first);
      else sink.triple(head, first, cn);
      Object last = head;
      for (int i = 1; i < c.size(); i++) {
        Object n = builder.newBlank();
        sink.triple(last, rest, n); 
        Object li = c.get(i);
        if (li instanceof AstNode) ((AstNode)li).addAsObject(sink, n, first);
        else sink.triple(n, first, li);
        last = n;
      }
      sink.triple(last, rest, nil);
    } else {
      head = nil;
    }
  }

  public Object getNode() {
    if (head == null) populateTriples();
    return head;
  }

  private void populateTriples() {
    triples = new ArrayList<Triple>();
    drain(new TripleSink() {
      public void triple(Object s, Object p, Object o) {
        triples.add(new Triple(s, p, o));
      }
    });
  }

  public String toString() {
    new Exception().printStackTrace();
    return "Collection node: " + head;
  }
}
