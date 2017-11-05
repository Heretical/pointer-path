/*
 * Copyright (c) 2017 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.path.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import heretical.pointer.path.NestedPointer;
import org.junit.Test;

import static heretical.pointer.path.json.JSONNestedPointerCompiler.COMPILER;
import static org.junit.Assert.*;

/**
 *
 */
public class JSONNestedPointerTransformTest
  {
  private static ObjectMapper mapper = new ObjectMapper();

  @Test
  public void testTransformChild() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    NestedPointer<JsonNode, ArrayNode> pointer = COMPILER.nested( "/person/measure/value" );

    pointer.apply( from, JSONPrimitiveTransforms.TO_FLOAT );

    JsonNode result = pointer.at( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.NUMBER, result.getNodeType() );
    assertEquals( FloatNode.class, result.getClass() );
    assertEquals( 100.0F, result.floatValue(), 0 );
    }

  @Test
  public void testTransformChildMissing() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    NestedPointer<JsonNode, ArrayNode> pointer = COMPILER.nested( "/person/measure/foobar" );

    pointer.apply( from, JSONPrimitiveTransforms.TO_FLOAT );

    JsonNode result = COMPILER.nested( "/person/measure" ).at( from );

    assertNotNull( result );
    assertFalse( result.has( "foobar" ) );
    }

  @Test
  public void testTransformChildWild() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    NestedPointer<JsonNode, ArrayNode> pointer = COMPILER.nested( "/person/*/value" );

    pointer.apply( from, JSONPrimitiveTransforms.TO_FLOAT );

    JsonNode result = pointer.allAt( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.ARRAY, result.getNodeType() );
    assertEquals( 1, result.size() );
    assertEquals( FloatNode.class, result.get( 0 ).getClass() );
    assertEquals( 100.0F, result.get( 0 ).floatValue(), 0 );
    }

  @Test
  public void testTransformChildWildMissing() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    NestedPointer<JsonNode, ArrayNode> pointer = COMPILER.nested( "/person/*/foobar" );

    pointer.apply( from, JSONPrimitiveTransforms.TO_FLOAT );

    JsonNode result = COMPILER.nested( "/person/measure" ).at( from );

    assertNotNull( result );
    assertFalse( result.has( "foobar" ) );
    }

  @Test
  public void testTransformArrayChildWild() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    NestedPointer<JsonNode, ArrayNode> pointer = COMPILER.nested( "/person/measures/*/value" );

    pointer.apply( from, JSONPrimitiveTransforms.TO_FLOAT );

    JsonNode result = pointer.allAt( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.ARRAY, result.getNodeType() );
    assertEquals( 2, result.size() );
    assertEquals( FloatNode.class, result.get( 0 ).getClass() );
    assertEquals( 1000.0F, result.get( 0 ).floatValue(), 0 );
    assertEquals( FloatNode.class, result.get( 1 ).getClass() );
    assertEquals( 2000.0F, result.get( 1 ).floatValue(), 0 );
    }

  @Test
  public void testTransformArrayChildDescent() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    NestedPointer<JsonNode, ArrayNode> pointer = COMPILER.nested( "/person/**/value" );

    pointer.apply( from, JSONPrimitiveTransforms.TO_FLOAT );

    JsonNode result = pointer.allAt( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.ARRAY, result.getNodeType() );
    assertEquals( 3, result.size() );
    assertEquals( FloatNode.class, result.get( 0 ).getClass() );
    assertEquals( 100.0F, result.get( 0 ).floatValue(), 0 );
    assertEquals( FloatNode.class, result.get( 1 ).getClass() );
    assertEquals( 1000.0F, result.get( 1 ).floatValue(), 0 );
    assertEquals( FloatNode.class, result.get( 2 ).getClass() );
    assertEquals( 2000.0F, result.get( 2 ).floatValue(), 0 );
    }

  @Test
  public void testTransformDirectChildDescent() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    NestedPointer<JsonNode, ArrayNode> pointer = COMPILER.nested( "/person/**/name" );

    pointer.apply( from, v -> JsonNodeFactory.instance.textNode( "Jane Doe" ) );

    JsonNode result = pointer.allAt( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.ARRAY, result.getNodeType() );
    assertEquals( 1, result.size() );
    assertEquals( JsonNodeType.STRING, result.get( 0 ).getNodeType() );
    assertEquals( "Jane Doe", result.get( 0 ).textValue() );
    }
  }
