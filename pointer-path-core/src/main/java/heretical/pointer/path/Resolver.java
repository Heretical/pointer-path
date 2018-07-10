/*
 * Copyright (c) 2017-2018 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.path;

import java.io.Serializable;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 *
 */
class Resolver<Node, Result> implements Serializable
  {
  static final Map<Object, Pointer> cache = Collections.synchronizedMap( new LinkedHashMap<Object, Pointer>()
    {
    @Override
    protected boolean removeEldestEntry( Map.Entry<Object, Pointer> eldest )
      {
      return size() > 200;
      }
    } );

  final PointerCompiler<Node, Result> compiler;
  Resolver<Node, Result> next;

  public Resolver( PointerCompiler<Node, Result> compiler )
    {
    this.compiler = compiler;
    }

  Pointer get( Object pointer )
    {
    return cache.computeIfAbsent( pointer, k -> compiler.compile( "/" + pointer ) );
    }

  public Resolver<Node, Result> setNext( Resolver<Node, Result> next )
    {
    this.next = next;

    return next;
    }

  boolean isFinal()
    {
    return false;
    }

  boolean isDescent()
    {
    return false;
    }

  boolean isWildCard()
    {
    return false;
    }

  Result resolve( Resolver<Node, Result> previous, Node node, Result result )
    {
    return next.resolve( this, node, result );
    }

  Result remove( Resolver<Node, Result> previous, Node parent, Pointer<Node> pointer, Node child )
    {
    return next.remove( previous, parent, pointer, child );
    }

  void set( Resolver<Node, Result> previous, Node parent, Pointer<Node> pointer, Node child, Function<Node, Node> transform )
    {
    next.set( previous, parent, pointer, child, transform );
    }

  public void copy( Resolver<Node, Result> previous, Deque<String> queue, Node root, Node from, Pointer<Node> pointer, Node into, Predicate<Node> filter )
    {
    next.copy( previous, new LinkedList<>(), root, from, pointer, into, filter );
    }
  }
