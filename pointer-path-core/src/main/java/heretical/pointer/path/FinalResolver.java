/*
 * Copyright (c) 2017 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.path;

import java.util.Deque;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 *
 */
class FinalResolver<Node, Result> extends Resolver<Node, Result>
  {
  public FinalResolver( PointerCompiler<Node, Result> compiler )
    {
    super( compiler );
    }

  @Override
  boolean isFinal()
    {
    return true;
    }

  @Override
  Result resolve( Resolver<Node, Result> previous, Node node, Result result )
    {
    return compiler.add( result, node );
    }

  @Override
  Result remove( Resolver<Node, Result> previous, Node parent, Pointer<Node> pointer, Node child )
    {
    Result result = compiler.resultNode();

    return compiler.add( result, pointer.remove( parent ) );
    }

  @Override
  void set( Resolver<Node, Result> previous, Node parent, Pointer<Node> pointer, Node child, Function<Node, Node> transform )
    {
    pointer.set( parent, child, transform );
    }

  @Override
  public void copy( Resolver<Node, Result> previous, Deque<String> queue, Node root, Node from, Pointer<Node> pointer, Node into, Predicate<Node> filter )
    {
    StringBuilder buffer = new StringBuilder();

    queue.forEach( buffer::append );

    Pointer<Node> queuePointer = compiler.compile( buffer.toString() );

    queuePointer.copy( root, into, filter );
    }
  }
