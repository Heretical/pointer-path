/*
 * Copyright (c) 2017 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.path;

import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 *
 */
class WildCardResolver<Node, Result> extends Resolver<Node, Result>
  {
  public WildCardResolver( PointerCompiler<Node, Result> compiler )
    {
    super( compiler );
    }

  @Override
  Result resolve( Resolver<Node, Result> previous, Node node, Result result )
    {
    compiler.iterable( node )
      .forEach( child -> next.resolve( this, child, result ) );

    return result;
    }

  @Override
  Result remove( Resolver<Node, Result> previous, Node parent, Pointer<Node> pointer, Node node )
    {
    Result result = compiler.resultNode();

    switch( compiler.kind( node ) )
      {
      case Array:
        int i = 0;
        for( Node child : compiler.iterable( node ) )
          {
          Pointer<Node> current = this.next.isFinal() ? get( i ) : null;

          Result removed = this.next.remove( this, node, current, child );

          compiler.addAll( result, removed );

          i++;
          }
        break;

      case Map:
        Iterator<Map.Entry<String, Node>> fields = compiler.entries( node );

        while( fields.hasNext() )
          {
          Map.Entry<String, Node> next = fields.next();
          Pointer<Node> current = this.next.isFinal() ? get( next.getKey() ) : null;

          Result removed = this.next.remove( this, node, current, next.getValue() );

          compiler.addAll( result, removed );
          }
        break;

      case Value:
        break;
      }

    return result;
    }

  @Override
  public void set( Resolver<Node, Result> previous, Node parent, Pointer<Node> pointer, Node node, Function<Node, Node> transform )
    {
    switch( compiler.kind( node ) )
      {
      case Array:
        int i = 0;
        for( Node child : compiler.iterable( node ) )
          {
          Pointer<Node> current = this.next.isFinal() ? get( i ) : null;

          this.next.set( this, node, current, child, transform );

          i++;
          }
        break;

      case Map:
        Iterator<Map.Entry<String, Node>> fields = compiler.entries( node );

        while( fields.hasNext() )
          {
          Map.Entry<String, Node> next = fields.next();
          Pointer<Node> current = this.next.isFinal() ? get( next.getKey() ) : null;

          this.next.set( this, node, current, next.getValue(), transform );
          }
        break;

      case Value:
        break;
      }
    }

  @Override
  public void copy( Resolver<Node, Result> previous, Deque<String> queue, Node root, Node from, Pointer<Node> pointer, Node into, Predicate<Node> filter )
    {
    switch( compiler.kind( from ) )
      {
      case Array:
        int i = 0;
        for( Node child : compiler.iterable( from ) )
          {
          Pointer<Node> current = this.next.isFinal() ? get( i ) : null;

          queue.addLast( "/" + i );

          this.next.copy( this, queue, root, child, current, into, filter );

          queue.removeLast();

          i++;
          }
        break;

      case Map:
        Iterator<Map.Entry<String, Node>> fields = compiler.entries( from );

        while( fields.hasNext() )
          {
          Map.Entry<String, Node> next = fields.next();
          Pointer current = this.next.isFinal() ? get( next.getKey() ) : null;

          queue.addLast( "/" + next.getKey() );

          this.next.copy( this, queue, root, next.getValue(), current, into, filter );

          queue.removeLast();
          }
        break;

      case Value:
        break;
      }
    }
  }
