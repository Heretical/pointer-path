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
 * Builder is the base class for type specific nested builder implementations.
 * <p>
 * The Builder uses {@link BuildSpec} declarations to build new nested objects.
 */
public class Builder<Node, Result> implements Serializable
  {
  private final NestedPointerCompiler<Node, Result> nestedPointerCompiler;
  private final BuildSpecDecorator<Node>[] buildSpecs;

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

  protected BuildSpecDecorator<Node>[] getBuildSpecs()
    {
    return buildSpecs;
    }

  /**
   * Method build first insert literal into the target {@code resultNode}, then inserts values
   * from the {@code valuesLookup} function into the {@code resultNode}.
   *
   * @param valuesLookup a {@link BiFunction} used to lookup values from the source object
   * @param resultNode the object to insert values into
   */
  public void build( BiFunction<Comparable, Type, Object> valuesLookup, Node resultNode )
    {
    buildLiterals( resultNode );

    buildNodes( valuesLookup, resultNode );
    }

  /**
   * Method buildLiterals inserts literal values declared by the given {@link BuildSpec} instances into
   * the target {@code resultNode}.
   *
   * @param resultNode the object to insert values into
   */
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

  /**
   * Method buildNodes inserts values from the {@code valuesLookup} function into the {@code resultNode}.
   *
   * @param valuesLookup a {@link BiFunction} used to lookup values from the source object
   * @param resultNode the object to insert values into
   */
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
