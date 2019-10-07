/*
 * Copyright (c) 2017-2019 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.path.json;

import java.lang.reflect.Type;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import heretical.pointer.path.PointerCompiler;

/**
 * Class JSONPointerCompiler is an implementation of {@link PointerCompiler} for use with JSON objects.
 * <p>
 * Note the {@link #node(Object)} method will convert any known Java type to the appropriate JsonNode class.
 * <p>
 * Recognized Java types are:
 * <ul>
 * <li>String.class</li>
 * <li>Integer.class</li>
 * <li>Integer.Type</li>
 * <li>Long.class</li>
 * <li>Long.Type</li>
 * <li>Float.class</li>
 * <li>Float.Type</li>
 * <li>Double.class</li>
 * <li>Double.Type</li>
 * <li>Boolean.class</li>
 * <li>Boolean.Type</li>
 * </ul>
 * <p>
 * Any other type will be wrapped in a {@link com.fasterxml.jackson.databind.node.POJONode}.
 *
 * @see PointerCompiler for more details.
 */
public class JSONPointerCompiler implements PointerCompiler<JsonNode, ArrayNode>
  {
  static private Map<Type, Function<Object, JsonNode>> convert = new IdentityHashMap<>();

  static
    {
    convert.put( String.class, value -> JsonNodeFactory.instance.textNode( (String) value ) );

    // numeric and boolean
    convert.put( Integer.class, value -> JsonNodeFactory.instance.numberNode( (Integer) value ) );
    convert.put( Integer.TYPE, value -> JsonNodeFactory.instance.numberNode( (Integer) value ) );
    convert.put( Long.class, value -> JsonNodeFactory.instance.numberNode( (Long) value ) );
    convert.put( Long.TYPE, value -> JsonNodeFactory.instance.numberNode( (Long) value ) );
    convert.put( Float.class, value -> JsonNodeFactory.instance.numberNode( (Float) value ) );
    convert.put( Float.TYPE, value -> JsonNodeFactory.instance.numberNode( (Float) value ) );
    convert.put( Double.class, value -> JsonNodeFactory.instance.numberNode( (Double) value ) );
    convert.put( Double.TYPE, value -> JsonNodeFactory.instance.numberNode( (Double) value ) );
    convert.put( Boolean.class, value -> JsonNodeFactory.instance.booleanNode( (Boolean) value ) );
    convert.put( Boolean.TYPE, value -> JsonNodeFactory.instance.booleanNode( (Boolean) value ) );
    }

  @Override
  public JSONPointer compile( String path )
    {
    return new JSONPointer( path );
    }

  @Override
  public Kind kind( JsonNode node )
    {
    if( node == null || node.isMissingNode() )
      return null;

    if( node.isArray() )
      return Kind.Array;
    if( node.isObject() )
      return Kind.Map;
    if( node.isValueNode() )
      return Kind.Value;

    throw new IllegalArgumentException( "unknown node type: " + node.getNodeType() );
    }

  @Override
  public ArrayNode add( ArrayNode result, JsonNode node )
    {
    if( node != null )
      result.add( node );

    return result;
    }

  @Override
  public ArrayNode addAll( ArrayNode into, ArrayNode from )
    {
    if( from == null )
      return into;

    return into.addAll( from );
    }

  @Override
  public JsonNode first( ArrayNode node )
    {
    if( node == null )
      return null;

    return node.get( 0 );
    }

  @Override
  public boolean isContainer( JsonNode node )
    {
    return node != null && node.isContainerNode();
    }

  @Override
  public Iterator<Map.Entry<String, JsonNode>> entries( JsonNode node )
    {
    return node.fields();
    }

  @Override
  public Iterable<JsonNode> iterable( JsonNode node )
    {
    return node;
    }

  @Override
  public int size( JsonNode jsonNode )
    {
    if( jsonNode == null )
      throw new IllegalArgumentException( "jsonNode may not be null" );

    return jsonNode.size();
    }

  @Override
  public ArrayNode resultNode()
    {
    return JsonNodeFactory.instance.arrayNode();
    }

  @Override
  public JsonNode node( Object value )
    {
    if( value == null )
      return JsonNodeFactory.instance.nullNode();

    Class from = value.getClass();

    if( JsonNode.class.isAssignableFrom( from ) )
      return (JsonNode) value;

    return convert.getOrDefault( from, JsonNodeFactory.instance::pojoNode ).apply( value );
    }
  }
