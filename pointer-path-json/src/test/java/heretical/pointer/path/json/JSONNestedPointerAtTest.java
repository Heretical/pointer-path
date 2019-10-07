/*
 * Copyright (c) 2017-2019 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.path.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.junit.Test;

import static heretical.pointer.path.json.JSONNestedPointerCompiler.COMPILER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 */
public class JSONNestedPointerAtTest
  {
  private static ObjectMapper mapper = new ObjectMapper();

  @Test
  public void testGetChild() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    JsonNode result = COMPILER.nested( "/person/measure/value" ).at( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.NUMBER, result.getNodeType() );
    assertEquals( 100, result.intValue() );
    }

  @Test
  public void testGetChildWild() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    JsonNode result = COMPILER.nested( "/person/*/value" ).allAt( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.ARRAY, result.getNodeType() );
    assertEquals( 1, result.size() );
    assertEquals( 100, result.get( 0 ).intValue() );
    }

  @Test
  public void testGetArrayChildWild() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    JsonNode result = COMPILER.nested( "/person/measures/*/value" ).allAt( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.ARRAY, result.getNodeType() );
    assertEquals( 2, result.size() );
    assertEquals( 1000, result.get( 0 ).intValue() );
    assertEquals( 2000, result.get( 1 ).intValue() );
    }

  @Test
  public void testGetChildArray() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    JsonNode result = COMPILER.nested( "/person/measured" ).at( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.ARRAY, result.getNodeType() );
    assertEquals( 2, result.size() );
    assertEquals( 1000, result.get( 0 ).intValue() );
    assertEquals( 2000, result.get( 1 ).intValue() );
    }

  @Test
  public void testGetChildWildArray() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    JsonNode result = COMPILER.nested( "/person/measured/*" ).allAt( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.ARRAY, result.getNodeType() );
    assertEquals( 2, result.size() );
    assertEquals( 1000, result.get( 0 ).intValue() );
    assertEquals( 2000, result.get( 1 ).intValue() );
    }

  @Test
  public void testGetArrayChildWildWild() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    JsonNode result = COMPILER.nested( "/person/*/*/value" ).allAt( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.ARRAY, result.getNodeType() );
    assertEquals( 2, result.size() );
    assertEquals( 1000, result.get( 0 ).intValue() );
    assertEquals( 2000, result.get( 1 ).intValue() );
    }

  @Test
  public void testGetArrayChildWildWild2() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    JsonNode result = COMPILER.nested( "/person/measures/*/*" ).allAt( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.ARRAY, result.getNodeType() );
    assertEquals( 2, result.size() );
    assertEquals( 1000, result.get( 0 ).intValue() );
    assertEquals( 2000, result.get( 1 ).intValue() );
    }

  @Test
  public void testGetArrayChildDescentWild() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    JsonNode result = COMPILER.nested( "/**/measures/*/value" ).allAt( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.ARRAY, result.getNodeType() );
    assertEquals( 2, result.size() );
    assertEquals( 1000, result.get( 0 ).intValue() );
    assertEquals( 2000, result.get( 1 ).intValue() );
    }

  @Test
  public void testGetArrayChildDescentWild2() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    JsonNode result = COMPILER.nested( "/**/*/value" ).allAt( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.ARRAY, result.getNodeType() );
    assertEquals( 3, result.size() );
    assertEquals( 100, result.get( 0 ).intValue() );
    assertEquals( 1000, result.get( 1 ).intValue() );
    assertEquals( 2000, result.get( 2 ).intValue() );
    }

  @Test
  public void testGetChildDirectDescent() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    JsonNode result = COMPILER.nested( "/person/**/name" ).allAt( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.ARRAY, result.getNodeType() );
    assertEquals( 1, result.size() );
    assertEquals( "John Doe", result.get( 0 ).textValue() );
    }

  @Test
  public void testGetChildDescent() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    JsonNode result = COMPILER.nested( "/person/**/value" ).allAt( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.ARRAY, result.getNodeType() );
    assertEquals( 3, result.size() );
    assertEquals( 100, result.get( 0 ).intValue() );
    assertEquals( 1000, result.get( 1 ).intValue() );
    assertEquals( 2000, result.get( 2 ).intValue() );
    }

  @Test
  public void testGetChildDescent2() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    JsonNode result = COMPILER.nested( "/**/value" ).allAt( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.ARRAY, result.getNodeType() );
    assertEquals( 3, result.size() );
    assertEquals( 100, result.get( 0 ).intValue() );
    assertEquals( 1000, result.get( 1 ).intValue() );
    assertEquals( 2000, result.get( 2 ).intValue() );
    }

  @Test
  public void testObjectAt() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    JsonNode result = COMPILER.compile( "/person/measure" ).objectAt( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.OBJECT, result.getNodeType() );
    }

  @Test
  public void testObjectAtMissing() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    JsonNode result = COMPILER.compile( "/person/object/noValue" ).objectAt( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.OBJECT, result.getNodeType() );
    assertEquals( JsonNodeType.OBJECT, from.at( "/person/object" ).getNodeType() );
    }

  @Test
  public void testObjectAtArrayMissing() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );

    JsonNode result = COMPILER.compile( "/person/array/10/noValue" ).objectAt( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.OBJECT, result.getNodeType() );
    assertEquals( JsonNodeType.ARRAY, from.at( "/person/array" ).getNodeType() );
    }

  @Test
  public void testArrayAt() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nestedArray );

    JsonNode result = COMPILER.nested( "/0/annotations/1/name" ).at( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.STRING, result.getNodeType() );
    assertEquals( "end", result.asText() );
    }

  @Test
  public void testArrayWildAt() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nestedArray );

    JsonNode result = COMPILER.nested( "/0/annotations/*/name" ).allAt( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.ARRAY, result.getNodeType() );
    assertEquals( 2, result.size() );
    assertEquals( "begin", result.get( 0 ).asText() );
    assertEquals( "end", result.get( 1 ).asText() );
    }

  @Test
  public void testArrayDescentAt() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nestedArray );

    JsonNode result = COMPILER.nested( "/0/annotations/**/name" ).allAt( from );

    assertNotNull( result );
    assertEquals( JsonNodeType.ARRAY, result.getNodeType() );
    assertEquals( 2, result.size() );
    assertEquals( "begin", result.get( 0 ).asText() );
    assertEquals( "end", result.get( 1 ).asText() );
    }
  }
