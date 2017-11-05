/*
 * Copyright (c) 2017 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.operation;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import heretical.pointer.path.NestedPointer;
import heretical.pointer.path.NestedPointerCompiler;
import heretical.pointer.path.Pointer;

/**
 *
 */
class CopySpecDecorator<Node> implements Serializable
  {
  public static <Node> CopySpecDecorator<Node>[] array( NestedPointerCompiler<Node, ?> compiler, CopySpec... copySpecs )
    {
    CopySpecDecorator<Node>[] results = new CopySpecDecorator[ copySpecs.length ];

    for( int i = 0; i < copySpecs.length; i++ )
      results[ i ] = new CopySpecDecorator<>( copySpecs[ i ], compiler );

    return results;
    }

  public class FromDecorator
    {
    private final CopySpec.From from;

    private transient Map<NestedPointer<Node, ?>, Predicate<Node>> includePointers;
    private transient List<NestedPointer<Node, ?>> excludePointers;
    private transient Map<NestedPointer<Node, ?>, Function<Node, Node>> transformPointers;

    public FromDecorator( CopySpec.From from )
      {
      this.from = from;
      }

    public NestedPointer<Node, ?> getFromPointer()
      {
      return compiler.nested( from.from );
      }

    public Map<NestedPointer<Node, ?>, Predicate<Node>> getIncludePointers()
      {
      if( includePointers != null )
        return includePointers;

      LinkedHashMap<NestedPointer<Node, ?>, Predicate<Node>> map = new LinkedHashMap<>();

      for( Map.Entry<String, Predicate<?>> entry : from.getIncludes().entrySet() )
        map.putIfAbsent( compiler.nested( entry.getKey() ), (Predicate<Node>) entry.getValue() );

      includePointers = map;

      return includePointers;
      }

    public List<NestedPointer<Node, ?>> getExcludePointers()
      {
      if( excludePointers != null )
        return excludePointers;

      excludePointers = from.getExcludes()
        .stream()
        .map( compiler::nested )
        .collect( Collectors.toList() );

      return excludePointers;
      }

    public Predicate<Node> getFilter()
      {
      return (Predicate<Node>) from.getFilter();
      }

    public Map<NestedPointer<Node, ?>, Function<Node, Node>> getTransformPointers()
      {
      if( transformPointers != null )
        return transformPointers;

      LinkedHashMap<NestedPointer<Node, ?>, Function<Node, Node>> map = new LinkedHashMap<>();

      for( Map.Entry<String, Function<?, ?>> entry : from.getTransforms().entrySet() )
        map.putIfAbsent( compiler.nested( entry.getKey() ), (Function<Node, Node>) entry.getValue() );

      transformPointers = map;

      return transformPointers;
      }

    @Override
    public String toString()
      {
      return from.toString();
      }
    }

  private final CopySpec<?> copySpec;
  private final NestedPointerCompiler<Node, ?> compiler;

  private transient List<FromDecorator> fromSpecs;
  private transient Pointer<Node> intoPointer; // never a nested path

  public CopySpecDecorator( CopySpec copySpec, NestedPointerCompiler<Node, ?> compiler )
    {
    this.copySpec = copySpec;
    this.compiler = compiler;
    }

  public void resetTransforms( Map<Comparable, Object> values )
    {
    copySpec.resetTransforms( values );
    }

  public String getInto()
    {
    return copySpec.getInto();
    }

  public List<FromDecorator> getFromSpecs()
    {
    if( fromSpecs != null )
      return fromSpecs;

    fromSpecs = copySpec
      .getFromMap()
      .values()
      .stream()
      .map( FromDecorator::new )
      .collect( Collectors.toList() );

    return fromSpecs;
    }

  public void verify()
    {
    try
      {
      getIntoPointer();

      for( FromDecorator from : getFromSpecs() )
        {
        from.getFromPointer();
        from.getIncludePointers();
        from.getExcludePointers();
        }
      }
    catch( RuntimeException exception )
      {
      throw new IllegalArgumentException( "CopySpec has invalid pointer: " + toString(), exception );
      }
    }

  public Pointer<Node> getIntoPointer()
    {
    if( intoPointer == null )
      intoPointer = compiler.compile( copySpec.getInto() );

    return intoPointer;
    }

  @Override
  public String toString()
    {
    return copySpec.toString();
    }
  }
