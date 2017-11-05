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
import java.util.LinkedList;
import java.util.List;

/**
 * Class BuildSpec is used to declare how a key value maps into a new or existing nested object type.
 * <p>
 * To map child elements from one nested object to another, see {@link CopySpec} and related operations.
 * <p>
 * When using a BuildSpec, you are declaring that a field value is put into a specific location in a nested object
 * type.
 * <p>
 * When a BuildSpec is created, the target root location of all the values must be declared, or all values will
 * be placed immediately below the root object.
 * <p>
 * For example, you want to put a field named {@code firstName} it into a JSON tree at {@code /person/firstName}
 * there are two ways to use a BuildSpec.
 * <p>
 * Either
 * <p>
 * {@code
 * new BuildSpec().putInto( "firstName", "/person/firstName" );
 * }
 * <p>
 * Or
 * <p>
 * {@code
 * new BuildSpec( "/person" ).putInto( "firstName", "/firstName" );
 * }
 * <p>
 * Note that a field being copied or put into the new object can also be a nested object. In the case of JSON
 * if the object to be copied is a JSON String, the value can be converted to a JSON object on the copy.
 * <p>
 * {@code
 * new BuildSpec().putInto( "person", JSONCoercibleType.TYPE, "/person" );
 * }
 * <p>
 * This example assumes the {@code person} field is a valid JSON String or already a JSON {@code JsonNode} instance.
 *
 * @see CopySpec
 */
public class BuildSpec<T extends BuildSpec> implements Serializable
  {
  private static final String ROOT = "";

  enum Op
    {
      put,
      add
    }

  protected static class Literal implements Serializable
    {
    final Object value;
    final String into;

    protected Literal( Object value, String into )
      {
      this.value = value;
      this.into = into;
      }

    public Object getValue()
      {
      return value;
      }

    public String getInto()
      {
      return into;
      }

    @Override
    public String toString()
      {
      final StringBuilder sb = new StringBuilder( "Literal{" );
      sb.append( "value=" ).append( value );
      sb.append( ", into='" ).append( into ).append( '\'' );
      sb.append( '}' );
      return sb.toString();
      }
    }

  protected class Put implements Serializable
    {
    final Op op;
    final Comparable from;
    final Type asType;
    final String into;

    public Put( Op op, Comparable from, Type asType, String into )
      {
      this.op = op;
      this.from = from;
      this.asType = asType;
      this.into = into;
      }

    public Op getOp()
      {
      return op;
      }

    public Comparable getFromKey()
      {
      return from;
      }

    public Type getAsType()
      {
      return asType;
      }

    public String getInto()
      {
      return into;
      }

    @Override
    public String toString()
      {
      final StringBuilder sb = new StringBuilder( "Copy{" );
      sb.append( "op=" ).append( op );
      sb.append( ", from='" ).append( from ).append( '\'' );
      sb.append( ", asType=" ).append( asType );
      sb.append( ", into='" ).append( into ).append( '\'' );
      sb.append( '}' );
      return sb.toString();
      }
    }

  String into = ROOT;
  Type defaultType = String.class;
  List<Literal> literalList = new LinkedList<>();
  List<Put> putList = new LinkedList<>();

  /**
   * Constructor BuildSpec creates a new BuildSpec instance that places values into the
   * root of the target nested object.
   */
  public BuildSpec()
    {
    }

  /**
   * Constructor BuildSpec creates a new BuildSpec instance that coerces all values, by default,
   * to the given {@code defaultType}.
   *
   * @param defaultType of Type
   */
  public BuildSpec( Type defaultType )
    {
    this.defaultType = defaultType;
    }

  /**
   * Constructor BuildSpec creates a new BuildSpec instance that places values into the
   * {@code intoPointer} location of the target nested object.
   *
   * @param intoPointer of String
   */
  public BuildSpec( String intoPointer )
    {
    this.into = intoPointer;
    }

  /**
   * Constructor BuildSpec creates a new BuildSpec instance that places values into the
   * {@code intoPointer} location of the target nested object and coerces all values, by default,
   * to the given {@code defaultType}.
   *
   * @param intoPointer of String
   * @param defaultType of Type
   */
  public BuildSpec( String intoPointer, Type defaultType )
    {
    this.into = intoPointer;
    this.defaultType = defaultType;
    }

  /**
   * Method getInto returns the intoPointer targetted by this BuildSpec object.
   *
   * @return the into (type String) of this BuildSpec object.
   */
  public String getInto()
    {
    return into;
    }

  /**
   * Method withDefaultType set the {@code defaultType} value on this instance.
   *
   * @param defaultType of Type
   * @return BuildSpec
   */
  public T withDefaultType( Type defaultType )
    {
    this.defaultType = defaultType;

    return (T) this;
    }

  protected List<Literal> getLiteralList()
    {
    return literalList;
    }

  protected List<Put> getPutList()
    {
    return putList;
    }

  /**
   * Method putInto sets the given literal value into the given pointer location.
   *
   * @param value       of Object
   * @param intoPointer of String
   * @return BuildSpec
   */
  public T putInto( Object value, String intoPointer )
    {
    literalList.add( new Literal( value, intoPointer ) );

    return (T) this;
    }

  /**
   * Method putInto copies the value from the given field position into the given pointer location after
   * coercing the original value into the default {@link Type}.
   *
   * @param from        of String
   * @param intoPointer of String
   * @return BuildSpec
   */
  public T putInto( String from, String intoPointer )
    {
    putList.add( new Put( Op.put, from, defaultType, intoPointer ) );

    return (T) this;
    }

  /**
   * Method putInto copies the value from the given field position into the given pointer location after
   * coercing the original value into the given {@link Type}.
   *
   * @param from        of String
   * @param asType      of Type
   * @param intoPointer of String
   * @return BuildSpec
   */
  public T putInto( String from, Type asType, String intoPointer )
    {
    putList.add( new Put( Op.put, from, asType, intoPointer ) );

    return (T) this;
    }

  /**
   * Method putInto copies the value from the given field position into the given pointer location after
   * coercing the original value into the default {@link Type}.
   *
   * @param from        of Fields
   * @param intoPointer of String
   * @return BuildSpec
   */
  public T putInto( Comparable from, String intoPointer )
    {
    putList.add( new Put( Op.put, from, defaultType, intoPointer ) );

    return (T) this;
    }

  /**
   * Method putInto copies the value from the given field position into the given pointer location after
   * coercing the original value into the given {@link Type}.
   *
   * @param from        of Fields
   * @param asType      of Type
   * @param intoPointer of String
   * @return BuildSpec
   */
  public T putInto( Comparable from, Type asType, String intoPointer )
    {
    putList.add( new Put( Op.put, from, asType, intoPointer ) );

    return (T) this;
    }

  /**
   * Method addInto copies the value from the given field position into the given pointer location after
   * coercing the original value into the default {@link Type}.
   * <p>
   * This call assumes the pointer location is an array, and will append the value.
   *
   * @param from        of String
   * @param intoPointer of String
   * @return BuildSpec
   */
  public T addInto( String from, String intoPointer )
    {
    putList.add( new Put( Op.add, from, defaultType, intoPointer ) );

    return (T) this;
    }

  /**
   * Method addInto copies the value from the given field position into the given pointer location after
   * coercing the original value into the given {@link Type}.
   * <p>
   * This call assumes the pointer location is an array, and will append the value.
   *
   * @param from        of String
   * @param asType      of Type
   * @param intoPointer of String
   * @return BuildSpec
   */
  public T addInto( String from, Type asType, String intoPointer )
    {
    putList.add( new Put( Op.add, from, asType, intoPointer ) );

    return (T) this;
    }

  /**
   * Method addInto copies the value from the given field position into the given pointer location after
   * coercing the original value into the default {@link Type}.
   * <p>
   * This call assumes the pointer location is an array, and will append the value.
   *
   * @param from        of Fields
   * @param intoPointer of String
   * @return BuildSpec
   */
  public T addInto( Comparable from, String intoPointer )
    {
    putList.add( new Put( Op.add, from, defaultType, intoPointer ) );

    return (T) this;
    }

  /**
   * Method addInto copies the value from the given field position into the given pointer location after
   * coercing the original value into the given {@link Type}.
   * <p>
   * This call assumes the pointer location is an array, and will append the value.
   *
   * @param from        of Fields
   * @param asType      of Type
   * @param intoPointer of String
   * @return BuildSpec
   */
  public T addInto( Comparable from, Type asType, String intoPointer )
    {
    putList.add( new Put( Op.add, from, asType, intoPointer ) );

    return (T) this;
    }

  @Override
  public String toString()
    {
    final StringBuilder sb = new StringBuilder( "BuildSpec{" );
    sb.append( "into='" ).append( into ).append( '\'' );
    sb.append( ", defaultType=" ).append( defaultType );
    sb.append( ", literalList=" ).append( literalList );
    sb.append( ", putList=" ).append( putList );
    sb.append( '}' );
    return sb.toString();
    }
  }
