/*
 * Copyright (c) 2017 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.operation;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import heretical.pointer.path.NestedPointerCompiler;

/**
 * Copier is a base class that provides object to object copy functionality declared through a set
 * of {@link CopySpec} instances.
 */
public class Copier<Node, Result> implements Serializable
  {
  private final NestedPointerCompiler<Node, Result> nestedPointerCompiler;
  private final CopySpecDecorator<Node>[] copySpecs;

  public Copier( NestedPointerCompiler<Node, Result> nestedPointerCompiler, CopySpec... copySpecs )
    {
    this.nestedPointerCompiler = nestedPointerCompiler;
    this.copySpecs = CopySpecDecorator.array( this.nestedPointerCompiler, copySpecs );

    verify();
    }

  protected void verify()
    {
    for( CopySpecDecorator copySpec : this.copySpecs )
      copySpec.verify();
    }

  protected CopySpecDecorator<Node>[] getCopySpecs()
    {
    return copySpecs;
    }

  /**
   * Method copy first resets all {@link CopySpec} transforms with the Map of {@code arguments}, then applies
   * all the CopySpec instances to perform a copy and transform.
   *
   * @param arguments  a Map of transform arguments, may be null
   * @param fromNode   the object to copy and possibly transform values from
   * @param resultNode the object to copy value to
   */
  public void copy( Map<Comparable, Object> arguments, Node fromNode, Node resultNode )
    {
    resetTransforms( arguments );

    copy( fromNode, resultNode );
    }

  /**
   * Method copy applies all the CopySpec instances to perform a copy and transform.
   *
   * @param fromNode   the object to copy and possibly transform values from
   * @param resultNode the object to copy value to
   */
  public void copy( Node fromNode, Node resultNode )
    {
    for( CopySpecDecorator<Node> copySpec : copySpecs )
      {
      Node intoNode = copySpec.getIntoPointer().objectAt( resultNode );

      for( CopySpecDecorator<Node>.FromDecorator fromSpec : copySpec.getFromSpecs() )
        {
        Result fromResult = (Result) fromSpec.getFromPointer().allAt( fromNode );
        Iterable<Node> iterable = iterable( fromResult );

        for( Node fromValue : iterable )
          {
          fromSpec.getIncludePointers()
            .forEach( ( pointer, filter ) -> pointer.copy( fromValue, intoNode, filter ) );

          fromSpec.getExcludePointers()
            .forEach( pointer -> pointer.remove( intoNode ) );

          fromSpec.getTransformPointers()
            .forEach( ( pointer, transform ) -> pointer.apply( intoNode, transform ) );
          }
        }
      }
    }

  /**
   * Method resetTransforms resets all {@link CopySpec} transforms.
   *
   * @param arguments a Map of transform arguments, may be null
   */
  public void resetTransforms( Map<Comparable, Object> arguments )
    {
    if( arguments == null )
      arguments = Collections.emptyMap();

    for( CopySpecDecorator<Node> copySpec : copySpecs )
      copySpec.resetTransforms( arguments );

    }

  protected Iterable<Node> iterable( Result node )
    {
    return nestedPointerCompiler.iterable( node );
    }
  }
