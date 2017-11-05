/*
 * Copyright (c) 2017 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.path.json;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import heretical.pointer.path.PointerCompiler;

/**
 * Class JSONPointerCompiler is an implementation of {@link PointerCompiler} for use with JSON objects.
 *
 * @see PointerCompiler for more details.
 */
public class JSONPointerCompiler implements PointerCompiler<JsonNode, ArrayNode>
  {
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

    if( from == String.class )
      return JsonNodeFactory.instance.textNode( (String) value );

    if( from == Integer.class || from == Integer.TYPE )
      return JsonNodeFactory.instance.numberNode( (Integer) value );

    if( from == Long.class || from == Long.TYPE )
      return JsonNodeFactory.instance.numberNode( (Long) value );

    if( from == Float.class || from == Float.TYPE )
      return JsonNodeFactory.instance.numberNode( (Float) value );

    if( from == Double.class || from == Double.TYPE )
      return JsonNodeFactory.instance.numberNode( (Double) value );

    if( from == Boolean.class || from == Boolean.TYPE )
      return JsonNodeFactory.instance.booleanNode( (Boolean) value );

    throw new IllegalArgumentException( "unknown type coercion requested from: " + getTypeName( from ) );
    }

  private static String getTypeName( Type type )
    {
    if( type == null )
      return null;

    return type instanceof Class ? ( (Class) type ).getCanonicalName() : type.toString();
    }
  }
