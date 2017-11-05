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
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import heretical.pointer.path.NestedPointer;
import org.junit.Test;

import static heretical.pointer.path.json.JSONNestedPointerCompiler.COMPILER;
import static org.junit.Assert.*;

/**
 *
 */
public class JSONNestedPointerRemoveTest
  {
  private static ObjectMapper mapper = new ObjectMapper();

  @Test
  public void testRemoveChild() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    NestedPointer<JsonNode, ArrayNode> pointer = COMPILER.nested( "/person/measure/value" );

    JsonNode removed = pointer.remove( from ).get( 0 );

    assertNotNull( removed );
    assertEquals( JsonNodeType.NUMBER, removed.getNodeType() );
    assertEquals( IntNode.class, removed.getClass() );
    assertEquals( 100, removed.intValue() );

    JsonNode result = pointer.at( from );

    assertNull( result );
    }

  @Test
  public void testRemoveChildWild() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    NestedPointer<JsonNode, ArrayNode> pointer = COMPILER.nested( "/person/*/value" );

    JsonNode removed = pointer.remove( from );

    assertNotNull( removed );
    assertEquals( JsonNodeType.ARRAY, removed.getNodeType() );
    assertEquals( 1, removed.size() );
    assertEquals( IntNode.class, removed.get( 0 ).getClass() );
    assertEquals( 100, removed.get( 0 ).intValue() );

    JsonNode result = pointer.allAt( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.ARRAY, result.getNodeType() );
    assertEquals( 0, result.size() );
    }

  @Test
  public void testRemoveChildDescent() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    NestedPointer<JsonNode, ArrayNode> pointer = COMPILER.nested( "/person/**/name" );

    JsonNode removed = pointer.remove( from );

    assertNotNull( removed );
    assertEquals( JsonNodeType.ARRAY, removed.getNodeType() );
    assertEquals( 1, removed.size() );
    assertEquals( "John Doe", removed.get( 0 ).textValue() );
    }

  @Test
  public void testRemoveArrayChildWild() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    NestedPointer<JsonNode, ArrayNode> pointer = COMPILER.nested( "/person/measures/*/value" );

    JsonNode removed = pointer.remove( from );

    assertNotNull( removed );
    assertEquals( JsonNodeType.ARRAY, removed.getNodeType() );
    assertEquals( 2, removed.size() );
    assertEquals( IntNode.class, removed.get( 0 ).getClass() );
    assertEquals( 1000, removed.get( 0 ).intValue() );
    assertEquals( IntNode.class, removed.get( 1 ).getClass() );
    assertEquals( 2000, removed.get( 1 ).intValue() );

    JsonNode result = pointer.allAt( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.ARRAY, result.getNodeType() );
    assertEquals( 0, result.size() );
    }

  @Test
  public void testRemoveArrayChildDescent() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    NestedPointer<JsonNode, ArrayNode> pointer = COMPILER.nested( "/person/**/value" );

    JsonNode removed = pointer.remove( from );

    assertNotNull( removed );
    assertEquals( JsonNodeType.ARRAY, removed.getNodeType() );
    assertEquals( 3, removed.size() );
    assertEquals( IntNode.class, removed.get( 0 ).getClass() );
    assertEquals( 100, removed.get( 0 ).intValue() );
    assertEquals( IntNode.class, removed.get( 1 ).getClass() );
    assertEquals( 1000, removed.get( 1 ).intValue() );
    assertEquals( IntNode.class, removed.get( 2 ).getClass() );
    assertEquals( 2000, removed.get( 2 ).intValue() );

    JsonNode result = pointer.allAt( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.ARRAY, result.getNodeType() );
    assertEquals( 0, result.size() );
    }
  }
