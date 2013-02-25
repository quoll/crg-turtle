package crg.turtle.ast;
import crg.turtle.NodeBuilder;
import java.util.Map;

public class Types {

  public static final String XSD_NS = "http://www.w3.org/2001/XMLSchema#";
  public static final String XSD = "xsd";

  public static final String DECIMAL = "decimal";
  public static final String DOUBLE = "double";
  public static final String INTEGER = "integer";
  public static final String BOOLEAN = "boolean";

  public static final String TRUE = "true";
  public static final String FALSE = "false";

  // type IRIs
  private Object xsdBool = null;
  private Object xsdBoolFull = null;
  private Object xsdInteger = null;
  private Object xsdIntegerFull = null;
  private Object xsdDouble = null;
  private Object xsdDoubleFull = null;
  private Object xsdDecimal = null;
  private Object xsdDecimalFull = null;

  private Object xsdTrue = null;
  private Object xsdTrueFull = null;
  private Object xsdFalse = null;
  private Object xsdFalseFull = null;

  private final NodeBuilder factory;

  public Types(NodeBuilder factory) {
    this.factory = factory;
  }

  private final Object getXsdType(Map<String,String> prefixMap, String type) {
    return factory.newIri(prefixMap, null, XSD, type);
  }

  private final Object getXsdTypeFull(String type) {
    return factory.newIri(XSD_NS + type);
  }

  private final Object getXsdBooleanType(Map<String,String> prefixMap) {
    if (xsdBool == null) xsdBool = getXsdType(prefixMap, BOOLEAN);
    return xsdBool;
  }

  private final Object getXsdBooleanTypeFull() {
    if (xsdBoolFull == null) xsdBoolFull = getXsdTypeFull(BOOLEAN);
    return xsdBoolFull;
  }

  public Object getXsdDouble(Map<String,String> prefixMap) {
    if (prefixMap.containsKey(XSD)) {
      if (xsdDouble == null) xsdDouble = getXsdType(prefixMap, DOUBLE);
      return xsdDouble;
    } else {
      if (xsdDoubleFull == null) xsdDoubleFull = getXsdTypeFull(DOUBLE);
      return xsdDoubleFull;
    }
  }

  public Object getXsdDecimal(Map<String,String> prefixMap) {
    if (prefixMap.containsKey(XSD)) {
      if (xsdDecimal == null) xsdDecimal = getXsdType(prefixMap, DECIMAL);
      return xsdDecimal;
    } else {
      if (xsdDecimalFull == null) xsdDecimalFull = getXsdTypeFull(DECIMAL);
      return xsdDecimalFull;
    }
  }

  public Object getXsdInteger(Map<String,String> prefixMap) {
    if (prefixMap.containsKey(XSD)) {
      if (xsdInteger == null) xsdInteger = getXsdType(prefixMap, INTEGER);
      return xsdInteger;
    } else {
      if (xsdIntegerFull == null) xsdIntegerFull = getXsdTypeFull(INTEGER);
      return xsdIntegerFull;
    }
  }

  public Object getXsdBooleanValue(Map<String,String> prefixMap, boolean value) {
    if (prefixMap.containsKey(XSD)) {
      if (value) {
        if (xsdTrue == null) xsdTrue = factory.newLiteral(TRUE, getXsdBooleanType(prefixMap), null);
        return xsdTrue;
      }
      if (xsdFalse == null) xsdFalse = factory.newLiteral(FALSE, getXsdBooleanType(prefixMap), null);
      return xsdFalse;
    } else {
      if (value) {
        if (xsdTrueFull == null) xsdTrueFull = factory.newLiteral(TRUE, getXsdBooleanTypeFull(), null);
        return xsdTrueFull;
      }
      if (xsdFalseFull == null) xsdFalseFull = factory.newLiteral(FALSE, getXsdBooleanTypeFull(), null);
      return xsdFalseFull;
    }
  }
}

