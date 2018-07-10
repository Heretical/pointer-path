/*
 * Copyright (c) 2017-2018 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
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

import heretical.pointer.util.PathTree;

/**
 *
 */
class DescentResolver<Node, Result> extends Resolver<Node, Result>
  {
  public DescentResolver( PointerCompiler<Node, Result> compiler )
    {
    super( compiler );
    }

  @Override
  boolean isDescent()
    {
    return true;
    }

  @Override
  Result resolve( Resolver<Node, Result> previous, Node node, Result result )
    {
    if( node == null )
      return null;

    next.resolve( this, node, result );

    return recursiveAdd( compiler.iterable( node ), result );
    }

  private Result recursiveAdd( Iterable<Node> node, Result result )
    {
    for( Node child : node )
      {
      next.resolve( this, child, result );

      if( compiler.isContainer( child ) )
        recursiveAdd( compiler.iterable( child ), result );
      }

    return result;
    }

  @Override
  Result remove( Resolver<Node, Result> previous, Node parent, Pointer<Node> pointer, Node child )
    {
    if( child == null )
      return null;

    Result result = compiler.resultNode();

    Result remove = next.remove( this, parent, pointer, child );

    compiler.addAll( result, remove );

    return recursiveRemove( result, child );
    }

  private Result recursiveRemove( Result result, Node node )
    {
    switch( compiler.kind( node ) )
      {
      case Array:
        int i = 0;
        for( Node child : compiler.iterable( node ) )
          {
          Result removed = next.remove( this, node, compiler.compile( "/" + i ), child );

          compiler.addAll( result, removed );

          recursiveRemove( result, child );
          }
        break;

      case Map:
        Iterator<Map.Entry<String, Node>> fields = compiler.entries( node );

        while( fields.hasNext() )
          {
          Map.Entry<String, Node> next = fields.next();
          String key = next.getKey();
          Node child = next.getValue();

          Result removed = this.next.remove( this, node, compiler.compile( "/" + key ), child );

          compiler.addAll( result, removed );

          recursiveRemove( result, child );
          }
        break;

      case Value:
        break;
      }

    return result;
    }

  @Override
  public void set( Resolver<Node, Result> previous, Node parent, Pointer<Node> pointer, Node child, Function<Node, Node> transform )
    {
    if( child == null )
      return;

    next.set( this, parent, pointer, child, transform );

    recursiveSet( child, transform );
    }

  private void recursiveSet( Node node, Function<Node, Node> transform )
    {
    switch( compiler.kind( node ) )
      {
      case Array:
        int i = 0;
        for( Node child : compiler.iterable( node ) )
          {
          next.set( this, node, compiler.compile( "/" + i ), child, transform );

          i++;

          recursiveSet( child, transform );
          }
        break;

      case Map:
        Iterator<Map.Entry<String, Node>> fields = compiler.entries( node );

        while( fields.hasNext() )
          {
          Map.Entry<String, Node> next = fields.next();
          String key = next.getKey();
          Node child = next.getValue();

          this.next.set( this, node, compiler.compile( "/" + key ), child, transform );

          recursiveSet( child, transform );
          }
        break;

      case Value:
        break;
      }
    }

  @Override
  public void copy( Resolver<Node, Result> previous, Deque<String> queue, Node root, Node from, Pointer<Node> pointer, Node into, Predicate<Node> filter )
    {
    copyNew( queue, root, from, pointer, into, filter );
    }

  private void copyNew( Deque<String> queue, Node root, Node from, Pointer<Node> pointer, Node into, Predicate<Node> filter )
    {
    if( from == null )
      return;

    if( this.next.isFinal() && filter == null )
      {
      this.next.copy( this, queue, root, from, pointer, into, filter );
      return;
      }

    PathTree pathTree = new PathTree();
    buildTree( pathTree.root(), from );

    for( String path : pathTree.depthFirstPointers() )
      {
      queue.addLast( path );

      Pointer<Node> childPointer = compiler.compile( path );
      Node child = childPointer.at( from );

      try
        {
        this.next.copy( this, queue, root, child, childPointer, into, filter );
        }
      finally
        {
        queue.removeLast();
        }
      }
    }

  private void buildTree( PathTree.Element current, Node from )
    {
    switch( compiler.kind( from ) )
      {
      case Array:
        int i = 0;
        for( Node child : compiler.iterable( from ) )
          buildTree( current.child( i++ ), child );
        break;

      case Map:
        Iterator<Map.Entry<String, Node>> fields = compiler.entries( from );

        while( fields.hasNext() )
          {
          Map.Entry<String, Node> next = fields.next();
          String key = next.getKey();
          Node child = next.getValue();

          buildTree( current.child( key ), child );
          }
        break;

      case Value:
        break;
      }
    }
  }
