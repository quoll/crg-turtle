package crg.turtle.ast;

import beaver.Symbol;
import java.util.List;

public class PredicateObjects extends Symbol {

  private Object p;

  private List<Object> o;

  public PredicateObjects(Object p, List<Object> o) {
    this.p = p;
    this.o = o;
  }

  public Object getPredicate() { return p; }
  public List<Object> getObjects() { return o; }
}

