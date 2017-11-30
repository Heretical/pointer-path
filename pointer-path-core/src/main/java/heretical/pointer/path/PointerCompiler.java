/*
 * Copyright (c) 2017 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.path;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

/**
 * Interface PointerCompiler is implemented by specific nested type providers.
 */
public interface PointerCompiler<Node, Result> extends Serializable
  {
  enum Kind
    {
      Array, Map, Value
    }

  /**
   * Method compile creates a new {@link Pointer} instance for the given path.
   *
   * @param path of String
   * @return Pointer<Node>
   */
  Pointer<Node> compile( String path );

  /**
   * Method kind returns the element type of the given node.
   * <p>
   * An element {@link Kind} is either an Array, Map, or Value.
   *
   * @param node of Node
   * @return Kind
   */
  Kind kind( Node node );

  /**
   * Method add will add the given node value to the end of the into object.
   *
   * @param into of Result
   * @param node of Node
   * @return Result
   */
  Result add( Result into, Node node );

  /**
   * Method addAll will add all values in the from object to the into object.
   *
   * @param into of Result
   * @param from of Result
   * @return Result
   */
  Result addAll( Result into, Result from );

  /**
   * Method first will return the first element in the node.
   *
   * @param node of Result
   * @return Node
   */
  Node first( Result node );

  /**
   * Method isContainer will return true if the node is either a Map or Array {@link Kind}.
   *
   * @param node of Node
   * @return boolean
   */
  boolean isContainer( Node node );

  /**
   * Method entries returns all the {@link Map.Entry} values in the given Map node.
   *
   * @param node of Node
   * @return Iterator<Entry<String, Node>>
   */
  Iterator<Map.Entry<String, Node>> entries( Node node );

  /**
   * Method iterable returns an {@link Iterable} over all the values in the given node.
   *
   * @param node of Node
   * @return Iterable<Node>
   */
  Iterable<Node> iterable( Node node );

  /**
   * Method resultNode creates a new 'result' node.
   * <p>
   * A result node must return {@code Kind.Array} when {@link #kind(Object)} is called.
   *
   * @return Result
   */
  Result resultNode();

  Node node( Object value );
  }