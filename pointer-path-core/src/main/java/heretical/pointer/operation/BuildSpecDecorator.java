/*
 * Copyright (c) 2017 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.operation;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import heretical.pointer.path.NestedPointerCompiler;
import heretical.pointer.path.Pointer;

/**
 *
 */
class BuildSpecDecorator<Node> implements Serializable
  {
  public static <Node> BuildSpecDecorator<Node>[] array( NestedPointerCompiler<Node, ?> compiler, BuildSpec... buildSpecs )
    {
    BuildSpecDecorator<Node>[] results = new BuildSpecDecorator[ buildSpecs.length ];

    for( int i = 0; i < buildSpecs.length; i++ )
      results[ i ] = new BuildSpecDecorator<>( buildSpecs[ i ], compiler );

    return results;
    }

  public class LiteralDecorator
    {
    private final BuildSpec.Literal literal;

    private transient Pointer<Node> intoPointer;

    public LiteralDecorator( BuildSpec.Literal literal )
      {
      this.literal = literal;
      }

    public Object getValue()
      {
      return literal.getValue();
      }

    public Pointer<Node> getIntoPointer()
      {
      if( intoPointer == null )
        intoPointer = compiler.compile( literal.getInto() );

      return intoPointer;
      }

    @Override
    public String toString()
      {
      return literal.toString();
      }
    }

  public class CopyDecorator
    {
    private final BuildSpec.Put put;

    private transient Pointer<Node> intoPointer;

    public CopyDecorator( BuildSpec.Put put )
      {
      this.put = put;
      }

    public BuildSpec.Op getMode()
      {
      return put.getOp();
      }

    public Comparable getFromKey()
      {
      return put.getFromKey();
      }

    public Type getAsType()
      {
      return put.getAsType();
      }

    public Pointer<Node> getIntoPointer()
      {
      if( intoPointer == null )
        intoPointer = compiler.compile( put.getInto() );

      return intoPointer;
      }

    @Override
    public String toString()
      {
      return put.toString();
      }
    }

  private final BuildSpec<?> buildSpec;
  private final NestedPointerCompiler<Node, ?> compiler;

  private transient Pointer<Node> intoPointer;
  private transient List<LiteralDecorator> literalList;
  private transient List<CopyDecorator> copyList;

  public BuildSpecDecorator( BuildSpec buildSpec, NestedPointerCompiler<Node, ?> compiler )
    {
    this.buildSpec = buildSpec;
    this.compiler = compiler;
    }

  public void verify()
    {
    try
      {
      getIntoPointer();
      getLiteralList();
      getCopyList();
      }
    catch( RuntimeException exception )
      {
      throw new IllegalArgumentException( "BuildSpec has invalid pointer: " + toString(), exception );
      }
    }

  public List<LiteralDecorator> getLiteralList()
    {
    if( literalList != null )
      return literalList;

    literalList = buildSpec
      .getLiteralList()
      .stream()
      .map( LiteralDecorator::new )
      .collect( Collectors.toList() );

    return literalList;
    }

  public List<CopyDecorator> getCopyList()
    {
    if( copyList != null )
      return copyList;

    copyList = buildSpec
      .putList
      .stream()
      .map( CopyDecorator::new )
      .collect( Collectors.toList() );

    return copyList;
    }

  public Pointer<Node> getIntoPointer()
    {
    if( intoPointer == null )
      intoPointer = compiler.compile( buildSpec.getInto() );

    return intoPointer;
    }
  }
