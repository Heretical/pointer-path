/*
 * Copyright (c) 2017 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.path;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 *
 */
public class BaseNestedPointer<Node, Result> implements NestedPointer<Node, Result>
  {
  private static Pattern pattern = Pattern.compile( "((?<=/[*]{1,2}+)|(?=/[*]{1,2}+))" );

  final PointerCompiler<Node, Result> compiler;
  final String pointer;

  Resolver<Node, Result> resolver;
  boolean isAbsolute = true;

  protected BaseNestedPointer( PointerCompiler<Node, Result> compiler, String pointer )
    {
    this.compiler = compiler;
    this.pointer = pointer;

    build();
    }

  private void build()
    {
    String[] split = pattern.split( this.pointer );

    resolver = new Resolver<>( compiler );

    Resolver<Node, Result> current = resolver;

    for( String token : split )
      {
      switch( token )
        {
        case "/**":
          current = current.setNext( new DescentResolver<>( compiler ) );
          isAbsolute = false;
          break;

        case "/*":
          current = current.setNext( new WildCardResolver<>( compiler ) );
          isAbsolute = false;
          break;

        default:
          current = current.setNext( new PointerResolver<>( compiler, token ) );
          break;
        }
      }

    current.setNext( new FinalResolver<>( compiler ) );
    }

  @Override
  public boolean isAbsolute()
    {
    return isAbsolute;
    }

  @Override
  public Pointer<Node> asPointer()
    {
    if( !isAbsolute )
      return null;

    return ( (PointerResolver<Node, Result>) resolver.next ).pointer;
    }

  protected Node absoluteAt( Node node )
    {
    return asPointer().at( node );
    }

  @Override
  public Result allAt( Node root )
    {
    if( isAbsolute() )
      return compiler.add( compiler.resultNode(), absoluteAt( root ) );

    return resolver.resolve( resolver, root, compiler.resultNode() );
    }

  @Override
  public Node at( Node root )
    {
    if( root == null )
      return null;

    if( isAbsolute() )
      return absoluteAt( root );

    Result result = resolver.resolve( resolver, root, compiler.resultNode() );

    return compiler.first( result );
    }

  @Override
  public Result remove( Node root )
    {
    if( isAbsolute() )
      return compiler.add( compiler.resultNode(), asPointer().remove( root ) );

    return resolver.remove( resolver, null, null, root );
    }

  @Override
  public void copy( Node from, Node into, Predicate<Node> filter )
    {
    if( isAbsolute() )
      asPointer().copy( from, into, filter );
    else
      resolver.copy( resolver, null, from, from, null, into, filter );
    }

  @Override
  public void apply( Node root, Function<Node, Node> transform )
    {
    if( isAbsolute() )
      asPointer().apply( root, transform );
    else
      resolver.set( resolver, null, null, root, transform );
    }

  @Override
  public String toString()
    {
    return pointer;
    }
  }
