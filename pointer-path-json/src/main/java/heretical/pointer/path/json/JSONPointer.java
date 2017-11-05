/*
 * Copyright (c) 2017 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.path.json;

import java.util.function.Function;
import java.util.function.Predicate;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import heretical.pointer.path.Pointer;

/**
 * Class JSONPointer is an implementation of the {@link Pointer} interface that encapsulates
 * the FasterXML {@link JsonPointer} class.
 */
public class JSONPointer implements Pointer<JsonNode>
  {
  private static final JsonNodeFactory INSTANCE = JsonNodeFactory.instance;
  private final JsonPointer pointer;

  public JSONPointer( String path )
    {
    this.pointer = JsonPointer.compile( path );
    }

  @Override
  public JsonNode at( JsonNode root )
    {
    JsonNode at = root.at( this.pointer );

    if( at == null || at.isMissingNode() )
      return null;

    return at;
    }

  @Override
  public JsonNode objectAt( JsonNode root )
    {
    if( pointer.matches() )
      return root;

    return safeWith( parents( root, this.pointer ), this.pointer.last() );
    }

  @Override
  public JsonNode remove( JsonNode root )
    {
    JsonPointer head = this.pointer.head();
    JsonNode parent = !head.matches() ? root.at( head ) : root;
    JsonPointer last = !this.pointer.last().matches() ? this.pointer.last() : this.pointer;

    if( parent.isObject() )
      return ( (ObjectNode) parent ).remove( last.getMatchingProperty() );
    else if( parent.isArray() )
      return ( (ArrayNode) parent ).remove( last.getMatchingIndex() );
    else
      throw new IllegalStateException( "parent node is of unknown object type: " + parent.getNodeType() );
    }

  @Override
  public void copy( JsonNode from, JsonNode into, Predicate<JsonNode> filter )
    {
    JsonPointer currentPointer = this.pointer;
    JsonNode value = from.at( currentPointer );

    if( value == null || value.isMissingNode() )
      return;

    if( filter != null && !filter.test( value ) )
      return;

    value = value.deepCopy();

    if( currentPointer.tail() == null )
      {
      ( (ObjectNode) into ).setAll( (ObjectNode) value );
      return;
      }

    JsonNode currentNode = into;

    while( true )
      {
      if( currentPointer.tail() == null )
        break;

      int matchingIndex = currentPointer.getMatchingIndex();

      if( !currentPointer.tail().matches() )
        {
        currentNode = safeWith( currentNode, currentPointer );

        currentPointer = currentPointer.tail();

        continue;
        }

      if( matchingIndex == -1 )
        setOnObject( (ObjectNode) currentNode, currentPointer, value, Function.identity() );
      else
        addOnArray( (ArrayNode) currentNode, value, Function.identity() );

      break;
      }
    }

  protected JsonNode safeWith( JsonNode node, JsonPointer pointer )
    {
    if( node.isObject() )
      {
      if( pointer.tail().getMatchingIndex() == -1 )
        node = node.with( pointer.getMatchingProperty() );
      else
        node = node.withArray( pointer.getMatchingProperty() );
      }
    else if( node.isArray() )
      {
      if( pointer.tail().getMatchingIndex() == -1 )
        node = ( (ArrayNode) node ).addObject();
      else
        node = ( (ArrayNode) node ).addArray();
      }
    else
      {
      throw new IllegalStateException( "parent node is of unknown object type: " + node.getNodeType() );
      }

    return node;
    }

  @SuppressWarnings("ConstantConditions")
  @Override
  public void apply( JsonNode root, Function<JsonNode, JsonNode> transform )
    {
    JsonPointer head = this.pointer.head();
    JsonNode parent = !head.matches() ? root.at( head ) : (ObjectNode) root;

    if( parent == null || parent.isMissingNode() )
      throw new IllegalArgumentException( "parent is missing" );

    JsonPointer last = !this.pointer.last().matches() ? this.pointer.last() : this.pointer;

    if( parent.isObject() && parent.has( last.getMatchingProperty() ) )
      setOnObject( (ObjectNode) parent, last, parent.get( last.getMatchingProperty() ), transform );
    else if( parent.isArray() && parent.has( last.getMatchingIndex() ) )
      setOnArray( (ArrayNode) parent, last, parent.get( last.getMatchingIndex() ), transform );
    else if( !parent.isObject() && !parent.isArray() )
      throw new IllegalStateException( "parent node is of unknown object type: " + parent.getNodeType() );
    }

  @Override
  public void set( JsonNode root, JsonNode child, Function<JsonNode, JsonNode> transform )
    {
    JsonNode parent = parents( root, this.pointer );
    JsonPointer last = !this.pointer.last().matches() ? this.pointer.last() : this.pointer;

    update( parent, last, child, transform, true );
    }

  @Override
  public void add( JsonNode root, JsonNode child, Function<JsonNode, JsonNode> transform )
    {
    JsonNode parent = parents( root, this.pointer );
    JsonPointer last = !this.pointer.last().matches() ? this.pointer.last() : this.pointer;

    update( parent, last, child, transform, false );
    }

  protected ContainerNode parents( JsonNode root, JsonPointer pointer )
    {
    JsonPointer head = pointer.head();
    JsonNode parent = !head.matches() ? root.at( head ) : root;

    if( !parent.isContainerNode() && !parent.isMissingNode() )
      throw new IllegalArgumentException( "parent node at:" + head + ", is not a container node" );

    if( !parent.isMissingNode() )
      return (ContainerNode) parent;

    parent = pointer
      .last()
      .mayMatchElement() ? INSTANCE.arrayNode() : INSTANCE.objectNode();

    ContainerNode ancestor = parents( root, head );

    update( ancestor, head.last(), parent, Function.identity(), true );

    return (ContainerNode) parent;
    }

  protected void update( JsonNode parent, JsonPointer location, JsonNode child, Function<JsonNode, JsonNode> transform, boolean isSet )
    {
    if( !parent.isContainerNode() )
      throw new IllegalArgumentException( "parent node must be a container node, got: " + parent.getNodeType() );

    if( !isSet )
      {
      addTo( parent, location, child );
      return;
      }

    if( parent.isObject() )
      setOnObject( (ObjectNode) parent, location, child, transform );
    else if( parent.isArray() )
      setOnArray( (ArrayNode) parent, location, child, transform );
    else
      throw new IllegalStateException( "parent node is of unknown object type: " + parent.getNodeType() );
    }

  @SuppressWarnings("ConstantConditions")
  protected void addTo( JsonNode parent, JsonPointer last, JsonNode child )
    {
    JsonNode container = null;

    if( parent instanceof ObjectNode )
      container = parent.get( last.getMatchingProperty() );
    else if( parent instanceof ArrayNode )
      container = parent.get( last.getMatchingIndex() );

    if( container == null || container.isMissingNode() )
      {
      container = INSTANCE.arrayNode();

      if( parent.isObject() )
        setOnObject( (ObjectNode) parent, last, container, Function.identity() );
      else if( parent.isArray() )
        addOnArray( (ArrayNode) parent, container, Function.identity() );
      }

    if( !container.isArray() )
      throw new IllegalStateException( "node referenced by pointer is not an array: " + pointer );

    addOnArray( (ArrayNode) container, child, Function.identity() );
    }

  protected void setOnArray( ArrayNode parent, JsonPointer location, JsonNode child, Function<JsonNode, JsonNode> transform )
    {
    int size = parent.size();
    int matchingIndex = location.getMatchingIndex();

    if( size - 1 < matchingIndex )
      {
      for( int i = 0; i < ( 1 + matchingIndex - size ); i++ )
        parent.addNull();
      }

    parent.set( matchingIndex, transform.apply( child ) );
    }

  protected void setOnObject( ObjectNode parent, JsonPointer location, JsonNode child, Function<JsonNode, JsonNode> transform )
    {
    parent.set( location.getMatchingProperty(), transform.apply( child ) );
    }

  protected void addOnArray( ArrayNode parent, JsonNode child, Function<JsonNode, JsonNode> transform )
    {
    parent.add( transform.apply( child ) );
    }

  @Override
  public String toString()
    {
    return pointer.toString();
    }
  }
