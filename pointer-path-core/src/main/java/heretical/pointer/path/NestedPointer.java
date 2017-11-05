/*
 * Copyright (c) 2017 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.path;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A NestedPointer provides the ability to address multiple values in a nested object tree through a simple
 * pointer path expression.
 * <p>
 * A nested pointer path expression can be simply an absolute reference like {@code /person/name}, where
 * the {@code name} attribute of the {@code person} object is being referenced.
 * <p>
 * Calling {@code nestedPointer.at( "/person/name" )} would return the value referenced.
 * <p>
 * Pointer path expression also support wildcards ({@code *}) and descent ({@code ** }) elements.
 * <p>
 * The wildcard element denotes all values at the given level.
 * <p>
 * For example, {@code nestedPointer.allAt( "/employees/*}{@code /name" )} will return all the values
 * named {@code name} found one level past {@code employees}.
 * <p>
 * The descent element denotes all values at any level.
 * <p>
 * For example, {@code nestedPointer.allAt( "/data/**}{@code /length" )} will return all the values
 * named {@code length} found anywhere below the {@code data} object.
 */
public interface NestedPointer<Node, Result> extends Serializable
  {
  /**
   * Method isAbsolute returns {@code true} if this pointer is a direct reference
   * without any wildcard or descent elements.
   *
   * @return the absolute (type boolean) of this NestedPointer object.
   */
  boolean isAbsolute();

  /**
   * Method asPointer returns the underlying {@link Pointer} instance if {@link #isAbsolute()} returns
   * true on this instance, otherwise {@code null} is returned.
   *
   * @return Pointer<Node>
   */
  Pointer<Node> asPointer();

  /**
   * Method allAt returns all the values referenced by this pointer relative to the given root node.
   *
   * @param root of Node
   * @return Result
   */
  Result allAt( Node root );

  /**
   * Method at returns the first value referenced by this pointer relative to the given root node.
   * <p>
   * This is a convenience method around {@link #allAt(Object)} that returns the first element
   * in the result node, or null if no values are found.
   *
   * @param root of Node
   * @return Node
   */
  Node at( Node root );

  /**
   * Method remove will remove all values referenced by this instance relative to the given root node.
   *
   * @param root of Node
   * @return Result
   */
  Result remove( Node root );

  /**
   * Method copy duplicates the {@code from} node and places it into the location
   * referenced by this pointer relative to the {@code into} node .
   *
   * @param from of Node
   * @param into of Node
   */
  default void copy( Node from, Node into )
    {
    copy( from, into, null );
    }

  /**
   * Method copy duplicates the {@code from} node and places it into the location
   * referenced by this pointer relative to the {@code into} node if the {@code filter}
   * returns true when given the {@code from} node.
   *
   * @param from   of Node
   * @param into   of Node
   * @param filter of Predicate<Node>
   */
  void copy( Node from, Node into, Predicate<Node> filter );

  /**
   * Method apply will pass the object or value referenced by this pointer relative to the
   * given {@code root} node to the given {@code transform} and replace the original value with the
   * result of the transform.
   * <p>
   * Note the root node is not copied before being modified.
   *
   * @param root      of Node
   * @param transform of Function<Node, Node>
   */
  void apply( Node root, Function<Node, Node> transform );
  }