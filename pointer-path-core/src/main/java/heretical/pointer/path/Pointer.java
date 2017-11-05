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
 * The Pointer interface is implemented by specific nested type providers.
 */
public interface Pointer<Node> extends Serializable
  {
  /**
   * Method at returns the object or value referenced by this pointer relative to the
   * give {@code root} node. If no object or value is referenced, {@code null} is returned.
   *
   * @param root of Node
   * @return Node
   */
  Node at( Node root );

  /**
   * Method objectAt returns the object referenced by this pointer relative to the
   * give {@code root} node. If no object is referenced, an attempt to create
   * all the parents is made.
   * <p>
   * If a value is referenced, an {@link UnsupportedOperationException} is thrown.
   *
   * @param root of Node
   * @return Node
   * @throws UnsupportedOperationException if this pointer references a value, and not an object
   */
  Node objectAt( Node root );

  /**
   * Method remove removes the object or value referenced by this pointer relative to the
   * given {@code root} node.
   *
   * @param root of Node
   * @return Node
   */
  Node remove( Node root );

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
   *
   * @param root      of Node
   * @param transform of Function<Node, Node>
   */
  void apply( Node root, Function<Node, Node> transform );

  /**
   * Method set will set the {@code child} into the location referenced by this pointer relative to the
   * given {@code root} node.
   * <p>
   * If the path between the root node and the location this pointer references does
   * not exist, the missing parents will be created.
   *
   * @param root  of Node
   * @param child of Node
   */
  default void set( Node root, Node child )
    {
    set( root, child, Function.identity() );
    }

  /**
   * Method set will set the {@code child} into the location referenced by this pointer relative to the
   * given {@code root} node after applying the {@code transform} to the {@code child} node.
   * <p>
   * If the path between the root node and the location this pointer references does
   * not exist, the missing parents will be created.
   *
   * @param root      of Node
   * @param child     of Node
   * @param transform of Function<Node, Node>
   */
  void set( Node root, Node child, Function<Node, Node> transform );

  /**
   * Method add will add the {@code child} into the location referenced by this pointer relative to the
   * given {@code root} node.
   * <p>
   * This method assumes no index is given in the pointer, that is a pointer with the value {@code /users}
   * will assume the {@code /users} value is an array, and the {@code child} value will be added to the end of it.
   * <p>
   * If the value referenced by this pointer is missing, an array type will be added to the location.
   * <p>
   * If the path between the root node and the location this pointer references does
   * not exist, the missing parents will be created.
   *
   * @param root  of Node
   * @param child of Node
   * @throws IllegalArgumentException if the location parent does not exist
   */
  default void add( Node root, Node child )
    {
    add( root, child, Function.identity() );
    }

  /**
   * Method add will add the {@code child} into the location referenced by this pointer relative to the
   * given {@code root} node after applying the {@code transform} to the {@code child} node.
   * <p>
   * This method assumes no index is given in the pointer, that is a pointer with the value {@code /users}
   * will assume the {@code /users} value is an array, and the {@code child} value will be added to the end of it.
   * <p>
   * If the value referenced by this pointer is missing, an array type will be added to the location.
   * <p>
   * If the path between the root node and the location this pointer references does
   * not exist, the missing parents will be created.
   *
   * @param root      of Node
   * @param child     of Node
   * @param transform of Function<Node, Node>
   */
  void add( Node root, Node child, Function<Node, Node> transform );
  }