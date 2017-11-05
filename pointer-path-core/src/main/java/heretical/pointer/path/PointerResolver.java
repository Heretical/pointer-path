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
class PointerResolver<Node, Result> extends Resolver<Node, Result>
  {
  final Pointer<Node> pointer;

  public PointerResolver( PointerCompiler<Node, Result> compiler, String pointer )
    {
    super( compiler );
    this.pointer = compiler.compile( pointer );
    }

  @Override
  Result resolve( Resolver<Node, Result> previous, Node node, Result result )
    {
    Node at = pointer.at( node );

    if( at == null )
      return result;

    next.resolve( this, at, result );

    return result;
    }

  @Override
  Result remove( Resolver<Node, Result> previous, Node parent, Pointer<Node> pointer, Node node )
    {
    Node child = this.pointer.at( node );

    if( child == null )
      return null;

    return next.remove( this, node, this.pointer, child );
    }

  @Override
  public void set( Resolver<Node, Result> previous, Node parent, Pointer<Node> pointer, Node node, Function<Node, Node> transform )
    {
    Node child = this.pointer.at( node );

    if( child == null )
      return;

    next.set( this, node, this.pointer, child, transform );
    }

  @Override
  public void copy( Resolver<Node, Result> previous, Deque<String> queue, Node root, Node from, Pointer<Node> pointer, Node into, Predicate<Node> filter )
    {
    Node child = this.pointer.at( from );

    if( child == null )
      return;

    queue.addLast( this.pointer.toString() );

    next.copy( this, queue, root, child, this.pointer, into, filter );

    queue.removeLast();
    }
  }
