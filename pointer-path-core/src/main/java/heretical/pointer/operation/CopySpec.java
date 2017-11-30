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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Class CopySpec is used to declare a mapping of parts of one nested object into a new or existing nested object type.
 * <p>
 * To map fields into a nested object, see {@link BuildSpec} and related operations.
 * <p>
 * When using a CopySpec, you are declaring which sub-trees from an existing nested object are copied into
 * another nested object.
 * <p>
 * When a CopySpec is created, the target root location of all the values must be declared, or all values will
 * be placed immediately below the root object.
 * <p>
 * For example, you want to copy a person named {@code John Doe} into a JSON object.
 * <p>
 * {@code
 * new CopySpec().from( "/people/*", new JSONStringPointerFilter( "/person/name", "John Doe" ) );
 * }
 * <p>
 * This example assumes the {@code people} object is an array of {@code person} objects.
 */
public class CopySpec<T extends CopySpec> implements Serializable
  {
  public static final String ROOT = "";

  protected static class From implements Serializable
    {
    String from;
    Predicate<?> filter;
    Map<String, Predicate<?>> includes = new LinkedHashMap<>();
    List<String> excludes = new LinkedList<>();
    Map<String, Function<?, ?>> transforms = new LinkedHashMap<>();

    protected From( String from, Predicate<?> filter )
      {
      this.from = from;
      this.filter = filter;
      }

    public String getFrom()
      {
      return from;
      }

    public Predicate<?> getFilter()
      {
      return filter;
      }

    public Map<String, Predicate<?>> getIncludes()
      {
      if( includes.isEmpty() )
        return Collections.singletonMap( ROOT, filter );

      return includes;
      }

    public From addInclude( String include, Predicate<?> predicate )
      {
      this.includes.put( include, predicate );

      return this;
      }

    public From addIncludes( String... includes )
      {
      for( String include : includes )
        this.includes.put( include, null );

      return this;
      }

    public List<String> getExcludes()
      {
      return excludes;
      }

    public From addExcludes( String... excludes )
      {
      Collections.addAll( this.excludes, excludes );

      return this;
      }

    public Map<String, Function<?, ?>> getTransforms()
      {
      return transforms;
      }

    public void addTransform( String pointer, Function<?, ?> transform )
      {
      transforms.put( pointer, transform );
      }

    @Override
    public String toString()
      {
      final StringBuilder sb = new StringBuilder( "From{" );
      sb.append( "from='" ).append( from ).append( '\'' );
      sb.append( ", includes=" ).append( includes );
      sb.append( ", excludes=" ).append( excludes );
      sb.append( ", transforms=" ).append( transforms );
      sb.append( '}' );
      return sb.toString();
      }
    }

  String into;
  Map<String, From> fromMap = new LinkedHashMap<>();

  transient List<Transform> resettableTransforms;

  /**
   * Constructor CopySpec creates a new CopySpec instance that places values into the
   * root of the target nested object.
   */
  public CopySpec()
    {
    this.into = ROOT;
    }

  /**
   * Constructor CopySpec creates a new CopySpec instance that places values into the
   * {@code intoPointer} location of the target nested object.
   *
   * @param intoPointer of String
   */
  public CopySpec( String intoPointer )
    {
    this.into = intoPointer;
    }

  protected T self()
    {
    return (T) this;
    }

  /**
   * Method from copies the object referenced by {@code fromPointer} into the
   * location declared by the constructor.
   *
   * @param fromPointer of String
   * @return CopySpec
   */
  public T from( String fromPointer )
    {
    getFrom( fromPointer );

    return self();
    }

  /**
   * Method from copies the object referenced by {@code fromPointer} into the
   * location declared on the constructor if the {@code predicate} returns true when tested
   * on the child referenced by {@code fromPointer}.
   * <p>
   * Note only one predicate may be applied to the value referenced by {@code fromPointer}.
   *
   * @param fromPointer of String
   * @param predicate   of Predicate
   * @return CopySpec
   */
  public T from( String fromPointer, Predicate<?> predicate )
    {
    getFrom( fromPointer, predicate );

    return self();
    }

  /**
   * Method fromInclude copies the children of the object referenced by {@code fromPointer} into the
   * location declared on the constructor if they match the {@code includePointers} paths.
   * <p>
   * This method allows for selective copying of child values from a source object. Instead of copying
   * {@code /person} and all its child values, {@code /first} and {@code /last} can be listed as includes, and only
   * those values will be copied resulting in {@code /person/first} and {@code /person/last}.
   * <p>
   * Note this method may be called any number of times with the same {@code fromPointer} value.
   *
   * @param fromPointer     of String
   * @param includePointers of String...
   * @return CopySpec
   */
  public T fromInclude( String fromPointer, String... includePointers )
    {
    getFrom( fromPointer ).addIncludes( includePointers );

    return self();
    }

  /**
   * Method fromInclude copies the children of the object referenced by {@code fromPointer} into the
   * location declared on the constructor if they match the {@code includePointer} path and the {@code predicate}
   * returns true on the value referenced by {@code includePointer}.
   * <p>
   * This method allows for selective copying of child values from a source object. Instead of copying
   * {@code /person} and all its child values, {@code /first} and {@code /last} can be listed as includes, and only
   * those values will be copied resulting in {@code /person/first} and {@code /person/last}.
   * <p>
   * Note this method may be called any number of times with the same {@code fromPointer} value.
   *
   * @param fromPointer    of String
   * @param includePointer of String
   * @param predicate      of Predicate
   * @return CopySpec
   */
  public T fromInclude( String fromPointer, String includePointer, Predicate<?> predicate )
    {
    getFrom( fromPointer ).addInclude( includePointer, predicate );

    return self();
    }

  /**
   * Method include copies the children of the root object into the
   * location declared on the constructor if they match the {@code includePointers} paths.
   * <p>
   * This method allows for selective copying of child values from a source object. Instead of copying
   * the root object and all its child values, {@code /person/first} and {@code /person/last} can be listed as includes,
   * and only those values will be copied resulting in {@code /person/first} and {@code /person/last}.
   * <p>
   * Note this method may be called any number of times.
   *
   * @param includePointers of String...
   * @return CopySpec
   */
  public T include( String... includePointers )
    {
    getFrom( ROOT ).addIncludes( includePointers );

    return self();
    }

  /**
   * Method include copies the children of the root object into the
   * location declared on the constructor if they match the {@code includePointer} path and the {@code predicate}
   * returns true on the value referenced by {@code includePointer}.
   * <p>
   * This method allows for selective copying of child values from a source object. Instead of copying
   * the root object and all its child values, {@code /person/first} and {@code /person/last} can be listed as includes,
   * and only those values will be copied resulting in {@code /person/first} and {@code /person/last}.
   * <p>
   * Note this method may be called any number of times.
   *
   * @param includePointer of String
   * @param predicate      of Predicate
   * @return CopySpec
   */
  public T include( String includePointer, Predicate predicate )
    {
    getFrom( ROOT ).addInclude( includePointer, predicate );

    return self();
    }

  /**
   * Method fromExclude copies the children of the object referenced by {@code fromPointer} into the
   * location declared on the constructor if they do not match the {@code excludePointers} paths.
   * <p>
   * This method allows for selective copying of child values from a source object. Instead of copying
   * {@code /person} and all its child values, {@code /first} and {@code /last} can be listed as excludes, and neither
   * of those values will be copied resulting in {@code /person/age}.
   * <p>
   * Note this method may be called any number of time with the same {@code fromPointer} value.
   *
   * @param fromPointer     of String
   * @param excludePointers of String...
   * @return CopySpec
   */
  public T fromExclude( String fromPointer, String... excludePointers )
    {
    getFrom( fromPointer ).addExcludes( excludePointers );

    return self();
    }

  /**
   * Method exclude copies the children of the root object into the
   * location declared on the constructor if they do not match the {@code excludePointers} paths.
   * <p>
   * This method allows for selective copying of child values from a source object. Instead of copying
   * the root object and all its child values, {@code /person/first} and {@code /person/last} can be listed as excludes,
   * and neither of those values will be copied resulting in {@code /person/age}.
   * <p>
   * Note this method may be called any number of times.
   *
   * @param excludePointers of String...
   * @return CopySpec
   */
  public T exclude( String... excludePointers )
    {
    getFrom( ROOT ).addExcludes( excludePointers );

    return self();
    }

  /**
   * Method fromTransform copies the object referenced by {@code fromPointer} into the
   * location declared on the constructor, and any child matching the {@code valuePointer}
   * will be applied against the given {@code transform}.
   * <p>
   * This method allows for the normalization of literal values, for example, coercing all
   * values of {@code /**}{@code /weight} to the {@code float} type.
   * <p>
   * Note this method may be called any number of times with the same {@code fromPointer} value.
   *
   * @param fromPointer  of String
   * @param valuePointer of String
   * @param transform    of Transform
   * @return CopySpec
   */
  public T fromTransform( String fromPointer, String valuePointer, Function<?, ?> transform )
    {
    getFrom( fromPointer ).addTransform( valuePointer, transform );

    return self();
    }

  /**
   * Method transform copies the root object into the
   * location declared on the constructor, and any child matching the {@code valuePointer}
   * will be applied against the given {@code transform}.
   * <p>
   * This method allows for the normalization of literal values, for example, coercing all
   * values of {@code /**}{@code /weight} to the {@code float} type.
   * <p>
   * Note this method may be called any number of times.
   *
   * @param valuePointer of String
   * @param transform    of Transform
   * @return CopySpec
   */
  public T transform( String valuePointer, Function<?, ?> transform )
    {
    getFrom( ROOT ).addTransform( valuePointer, transform );

    return self();
    }

  void resetTransforms( Map<Comparable, Object> values )
    {
    getResettableTransforms().forEach( transform -> transform.reset( values ) );
    }

  protected List<Transform> getResettableTransforms()
    {
    if( resettableTransforms != null )
      return resettableTransforms;

    resettableTransforms = getFromMap()
      .values()
      .stream()
      .flatMap( from -> from.getTransforms().values().stream() )
      .filter( Transform.class::isInstance )
      .map( Transform.class::cast )
      .filter( Transform::isResettable )
      .collect( Collectors.toList() );

    return resettableTransforms;
    }

  protected From getFrom( String from )
    {
    return getFrom( from, null );
    }

  protected From getFrom( String from, Predicate<?> predicate )
    {
    From result = fromMap.get( from );

    if( result != null )
      {
      if( predicate != null && result.filter != null )
        throw new IllegalStateException( "may only have one filter, found: " + result.filter );

      if( predicate != null )
        result.filter = predicate;

      return result;
      }

    result = new From( from, predicate );

    fromMap.put( from, result );

    return result;
    }

  public String getInto()
    {
    return into;
    }

  public Map<String, From> getFromMap()
    {
    return fromMap;
    }

  @Override
  public String toString()
    {
    final StringBuilder sb = new StringBuilder( "CopySpec{" );
    sb.append( "into='" ).append( into ).append( '\'' );
    sb.append( ", fromMap=" ).append( fromMap );
    sb.append( ", resettableTransforms=" ).append( resettableTransforms );
    sb.append( '}' );
    return sb.toString();
    }
  }
