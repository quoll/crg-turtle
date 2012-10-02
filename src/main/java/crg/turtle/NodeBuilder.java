package crg.turtle;
import java.util.Map;

/**
 * A callback interface for building graph nodes
 */
public interface NodeBuilder {

  /**
   * Generates a new blank node.
   * @return a unique blank node.
   */
  Object newBlank();

  /**
   * Returns a blank node with a given ID. If the ID has been seen before, then the same
   * blank node is being represented as on the previous occasion.
   * @param id The ID for the blank node. Used to determine equivalency between blank nodes.
   * @param A blank node for the system that compares as equal to other blank nodes with this ID.
   */
  Object newBlank(String id);

  /**
   * Create an IRI. This will only be called for fully qualified absolute IRIs
   * that are effectively constants for the system, such as:
   *   http://www.w3.org/1999/02/22-rdf-syntax-ns#type
   * @param iri a string containing the IRI value.
   * @return an IRI of the correct type for the system.
   */
  Object newIri(String iri);

  /**
   * Creates an IRI given a string. If the IRI is relative then it should use the base.
   * @param iri The IRI to be created.
   * @param base The current document base to use if the IRI is relative.
   * @return an IRI of the correct type for the system.
   */
  Object newIri(String iri, String base);

  /**
   * Creates an IRI from a prefix/namespace using a particular context.
   * @param prefixMap The current state of prefixes mapped to namespaces.
   * @param base The current base of the system. May be null meaning no base has been set.
   * @param prefix A prefix for the IRI. This should be expected in the prefixMap.
   * @param local The local part of the IRI.
   * @return an IRI of the correct type for the system.
   */
  Object newIri(Map<String,String> prefixMap, String base, String prefix, String local);


  /**
   * Creates a literal that may be plain (string - possibly with language tag),
   * simple (string - no language tag), or typed.
   * @param lexical The string form of the literal.
   * @param type an IRI of the type. This will be null if the literal is plain.
   * @param lang The language code for the literal. This will be null if the literal is
   *             typed or simple.
   * @return a Literal of the correct type for the system.
   */
  Object newLiteral(String lexical, Object type, String lang);

}
