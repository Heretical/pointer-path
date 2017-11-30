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
import java.util.function.BiFunction;

import heretical.pointer.path.NestedPointerCompiler;

/**
 *
 */
public class Builder<Node, Result> implements Serializable
  {
  private final NestedPointerCompiler<Node, Result> nestedPointerCompiler;
  protected BuildSpecDecorator<Node>[] buildSpecs;

  public Builder( NestedPointerCompiler<Node, Result> nestedPointerCompiler, BuildSpec... buildSpecs )
    {
    this.nestedPointerCompiler = nestedPointerCompiler;
    this.buildSpecs = BuildSpecDecorator.array( this.nestedPointerCompiler, buildSpecs );

    verify();
    }

  protected void verify()
    {
    for( BuildSpecDecorator<Node> copySpec : this.buildSpecs )
      copySpec.verify();
    }

  public void build( BiFunction<Comparable, Type, Object> valuesLookup, Node resultNode )
    {
    buildLiterals( resultNode );

    buildNodes( valuesLookup, resultNode );
    }

  public void buildLiterals( Node resultNode )
    {
    for( BuildSpecDecorator<Node> buildSpec : buildSpecs )
      {
      if( buildSpec.getLiteralList().isEmpty() )
        continue;

      Node node = buildSpec.getIntoPointer().objectAt( resultNode );

      for( BuildSpecDecorator<Node>.LiteralDecorator literal : buildSpec.getLiteralList() )
        {
        Node canonical = nestedPointerCompiler.node( literal.getValue() );

        literal.getIntoPointer().set( node, canonical );
        }
      }
    }

  public void buildNodes( BiFunction<Comparable, Type, Object> valuesLookup, Node resultNode )
    {
    for( BuildSpecDecorator<Node> buildSpec : buildSpecs )
      {
      Node node = buildSpec.getIntoPointer().objectAt( resultNode );

      for( BuildSpecDecorator<Node>.CopyDecorator copy : buildSpec.getCopyList() )
        {
        Object value = valuesLookup.apply( copy.getFromKey(), copy.getAsType() );

        if( value == null )
          continue;

        Node canonical = nestedPointerCompiler.node( value );

        switch( copy.getMode() )
          {
          case put:
            copy.getIntoPointer().set( node, canonical );
            break;
          case add:
            copy.getIntoPointer().add( node, canonical );
            break;
          }
        }
      }
    }

  }
