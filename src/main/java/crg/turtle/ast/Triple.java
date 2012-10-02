package crg.turtle.ast;

import beaver.Symbol;

public class Triple extends Symbol {

  private Object s;

  private Object p;

  private Object o;

  public Triple(Object s, Object p, Object o) {
    this.s = s;
    this.p = p;
    this.o = o;
  }

  public Object getSubject() { return s; }
  public Object getPredicate() { return p; }
  public Object getObject() { return o; }
}

