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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import static heretical.pointer.path.json.JSONNestedPointerCompiler.COMPILER;
import static org.junit.Assert.*;

/**
 *
 */
public class JSONNestedPointerCopyTest
  {
  private static ObjectMapper mapper = new ObjectMapper();

  @Test
  public void testCopyObject() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );
    ObjectNode into = JsonNodeFactory.instance.objectNode();

    COMPILER.nested( "/person" ).copy( from, into );

    assertNotNull( into.get( "person" ) );
    assertEquals( JsonNodeType.OBJECT, into.get( "person" ).getNodeType() );
    assertNotNull( into.get( "person" ).get( "age" ) );
    assertEquals( JsonNodeType.NUMBER, into.get( "person" ).get( "age" ).getNodeType() );
    }

  @Test
  public void testCopyChild() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );
    ObjectNode into = JsonNodeFactory.instance.objectNode();

    COMPILER.nested( "/person/age" ).copy( from, into );

    assertNotNull( into.get( "person" ) );
    assertEquals( JsonNodeType.OBJECT, into.get( "person" ).getNodeType() );
    assertNotNull( into.get( "person" ).get( "age" ) );
    assertEquals( JsonNodeType.NUMBER, into.get( "person" ).get( "age" ).getNodeType() );
    assertNull( into.get( "person" ).get( "name" ) );
    }

  @Test
  public void testCopyValuePredicate() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );
    ObjectNode into = JsonNodeFactory.instance.objectNode();

    COMPILER.nested( "/person/age" ).copy( from, into, node -> node.numberValue().intValue() > 60 );

    assertNull( into.get( "person" ) );
    }

  @Test
  public void testCopyArray() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );
    ObjectNode into = JsonNodeFactory.instance.objectNode();

    COMPILER.nested( "/person/children" ).copy( from, into );

    assertNotNull( into.get( "person" ) );
    assertEquals( JsonNodeType.OBJECT, into.get( "person" ).getNodeType() );
    assertNotNull( into.get( "person" ).get( "children" ) );
    assertEquals( JsonNodeType.ARRAY, into.get( "person" ).get( "children" ).getNodeType() );
    assertEquals( 3, into.get( "person" ).get( "children" ).size() );
    }

  @Test
  public void testCopyArrayValue() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );
    ObjectNode into = JsonNodeFactory.instance.objectNode();

    COMPILER.nested( "/person/children/2" ).copy( from, into );

    assertNotNull( into.get( "person" ) );
    assertEquals( JsonNodeType.OBJECT, into.get( "person" ).getNodeType() );
    assertNotNull( into.get( "person" ).get( "children" ) );
    assertEquals( JsonNodeType.ARRAY, into.get( "person" ).get( "children" ).getNodeType() );
    assertEquals( 1, into.get( "person" ).get( "children" ).size() );
    assertEquals( "Josh", into.get( "person" ).get( "children" ).get( 0 ).textValue() );
    }

  @Test
  public void testCopyArrayArrayValue() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );
    ObjectNode into = JsonNodeFactory.instance.objectNode();

    COMPILER.nested( "/person/arrays/1/2" ).copy( from, into );

    assertNotNull( into.get( "person" ) );
    assertEquals( JsonNodeType.OBJECT, into.get( "person" ).getNodeType() );
    assertNotNull( into.get( "person" ).get( "arrays" ) );
    assertEquals( 1, into.get( "person" ).get( "arrays" ).size() );
    assertEquals( JsonNodeType.ARRAY, into.get( "person" ).get( "arrays" ).getNodeType() );
    assertEquals( 1, into.get( "person" ).get( "arrays" ).size() );
    assertNotNull( into.get( "person" ).get( "arrays" ).get( 0 ) );
    assertEquals( JsonNodeType.ARRAY, into.get( "person" ).get( "arrays" ).get( 0 ).getNodeType() );
    assertEquals( 1, into.get( "person" ).get( "arrays" ).get( 0 ).size() );
    assertNotNull( into.get( "person" ).get( "arrays" ).get( 0 ).get( 0 ) );
    assertEquals( "Josh2", into.get( "person" ).get( "arrays" ).get( 0 ).get( 0 ).textValue() );
    }

  @Test
  public void testCopyChildWild() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );
    ObjectNode into = JsonNodeFactory.instance.objectNode();

    COMPILER.nested( "/person/*/value" ).copy( from, into );

    assertNotNull( into.get( "person" ) );
    assertEquals( JsonNodeType.OBJECT, into.get( "person" ).getNodeType() );
    assertNotNull( into.get( "person" ).get( "measure" ) );
    assertEquals( JsonNodeType.OBJECT, into.get( "person" ).get( "measure" ).getNodeType() );
    assertNotNull( into.get( "person" ).get( "measure" ).get( "value" ) );
    assertEquals( JsonNodeType.NUMBER, into.get( "person" ).get( "measure" ).get( "value" ).getNodeType() );
    assertEquals( 100, into.get( "person" ).get( "measure" ).get( "value" ).intValue() );
    }

  @Test
  public void testCopyArrayChildWild() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );
    ObjectNode into = JsonNodeFactory.instance.objectNode();

    COMPILER.nested( "/person/measures/*/value" ).copy( from, into );

    assertNotNull( into.get( "person" ) );
    assertEquals( JsonNodeType.OBJECT, into.get( "person" ).getNodeType() );
    assertNotNull( into.get( "person" ).get( "measures" ) );
    assertEquals( JsonNodeType.ARRAY, into.get( "person" ).get( "measures" ).getNodeType() );
    assertEquals( 2, into.get( "person" ).get( "measures" ).size() );
    assertNotNull( into.get( "person" ).get( "measures" ).get( 0 ).get( "value" ) );
    assertEquals( JsonNodeType.NUMBER, into.get( "person" ).get( "measures" ).get( 0 ).get( "value" ).getNodeType() );
    assertEquals( 1000, into.get( "person" ).get( "measures" ).get( 0 ).get( "value" ).intValue() );
    assertNotNull( into.get( "person" ).get( "measures" ).get( 1 ).get( "value" ) );
    assertEquals( JsonNodeType.NUMBER, into.get( "person" ).get( "measures" ).get( 1 ).get( "value" ).getNodeType() );
    assertEquals( 2000, into.get( "person" ).get( "measures" ).get( 1 ).get( "value" ).intValue() );
    }

  @Test
  public void testCopyChildWildArray() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );
    ObjectNode into = JsonNodeFactory.instance.objectNode();

    COMPILER.nested( "/person/measured/*" ).copy( from, into );

    assertNotNull( into.get( "person" ) );
    assertEquals( JsonNodeType.OBJECT, into.get( "person" ).getNodeType() );
    assertNotNull( into.get( "person" ).get( "measured" ) );
    assertEquals( JsonNodeType.ARRAY, into.get( "person" ).get( "measured" ).getNodeType() );
    assertEquals( 2, into.get( "person" ).get( "measured" ).size() );
    assertNotNull( into.get( "person" ).get( "measured" ).get( 0 ) );
    assertEquals( JsonNodeType.NUMBER, into.get( "person" ).get( "measured" ).get( 0 ).getNodeType() );
    assertEquals( 1000, into.get( "person" ).get( "measured" ).get( 0 ).intValue() );
    assertNotNull( into.get( "person" ).get( "measured" ).get( 1 ) );
    assertEquals( JsonNodeType.NUMBER, into.get( "person" ).get( "measured" ).get( 1 ).getNodeType() );
    assertEquals( 2000, into.get( "person" ).get( "measured" ).get( 1 ).intValue() );
    }

  @Test
  public void testCopyChildDescent() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );
    ObjectNode into = JsonNodeFactory.instance.objectNode();

    COMPILER.nested( "/person/**/value" ).copy( from, into );

    assertNotNull( into.get( "person" ) );
    assertEquals( JsonNodeType.OBJECT, into.get( "person" ).getNodeType() );
    assertNotNull( into.get( "person" ).get( "measure" ) );
    assertEquals( JsonNodeType.OBJECT, into.get( "person" ).get( "measure" ).getNodeType() );
    assertNotNull( into.get( "person" ).get( "measure" ).get( "value" ) );
    assertEquals( JsonNodeType.NUMBER, into.get( "person" ).get( "measure" ).get( "value" ).getNodeType() );
    assertEquals( 100, into.get( "person" ).get( "measure" ).get( "value" ).intValue() );
    assertNotNull( into.get( "person" ).get( "measures" ) );
    assertEquals( JsonNodeType.ARRAY, into.get( "person" ).get( "measures" ).getNodeType() );
    assertEquals( 2, into.get( "person" ).get( "measures" ).size() );
    assertNotNull( into.get( "person" ).get( "measures" ).get( 0 ).get( "value" ) );
    assertEquals( JsonNodeType.NUMBER, into.get( "person" ).get( "measures" ).get( 0 ).get( "value" ).getNodeType() );
    assertEquals( 1000, into.get( "person" ).get( "measures" ).get( 0 ).get( "value" ).intValue() );
    assertNotNull( into.get( "person" ).get( "measures" ).get( 1 ).get( "value" ) );
    assertEquals( JsonNodeType.NUMBER, into.get( "person" ).get( "measures" ).get( 1 ).get( "value" ).getNodeType() );
    assertEquals( 2000, into.get( "person" ).get( "measures" ).get( 1 ).get( "value" ).intValue() );
    }

  @Test
  public void testCopyDirectChildDescent() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );
    ObjectNode into = JsonNodeFactory.instance.objectNode();

    COMPILER.nested( "/person/**/name" ).copy( from, into );

    assertNotNull( into.get( "person" ) );
    assertEquals( JsonNodeType.OBJECT, into.get( "person" ).getNodeType() );
    assertNotNull( into.get( "person" ).get( "name" ) );
    assertEquals( JsonNodeType.STRING, into.get( "person" ).get( "name" ).getNodeType() );
    assertEquals( "John Doe", into.get( "person" ).get( "name" ).textValue() );
    }

  @Test
  public void testCopyDirectChildDescent2() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );
    ObjectNode into = JsonNodeFactory.instance.objectNode();

    COMPILER.nested( "/**/person" ).copy( from, into );

    assertNotNull( into.get( "person" ) );
    assertEquals( JsonNodeType.OBJECT, into.get( "person" ).getNodeType() );
    assertNotNull( into.get( "person" ).get( "name" ) );
    assertEquals( JsonNodeType.STRING, into.get( "person" ).get( "name" ).getNodeType() );
    assertEquals( "John Doe", into.get( "person" ).get( "name" ).textValue() );
    }

  @Test
  public void testCopyChildDescent2() throws Exception
    {
    JsonNode from = mapper.readTree( JSONData.nested );
    ObjectNode into = JsonNodeFactory.instance.objectNode();

    COMPILER.nested( "/**/value" ).copy( from, into );

    assertNotNull( into.get( "person" ) );
    assertEquals( JsonNodeType.OBJECT, into.get( "person" ).getNodeType() );
    assertNotNull( into.get( "person" ).get( "measure" ) );
    assertEquals( JsonNodeType.OBJECT, into.get( "person" ).get( "measure" ).getNodeType() );
    assertNotNull( into.get( "person" ).get( "measure" ).get( "value" ) );
    assertEquals( JsonNodeType.NUMBER, into.get( "person" ).get( "measure" ).get( "value" ).getNodeType() );
    assertEquals( 100, into.get( "person" ).get( "measure" ).get( "value" ).intValue() );
    assertNotNull( into.get( "person" ).get( "measures" ) );
    assertEquals( JsonNodeType.ARRAY, into.get( "person" ).get( "measures" ).getNodeType() );
    assertEquals( 2, into.get( "person" ).get( "measures" ).size() );
    assertNotNull( into.get( "person" ).get( "measures" ).get( 0 ).get( "value" ) );
    assertEquals( JsonNodeType.NUMBER, into.get( "person" ).get( "measures" ).get( 0 ).get( "value" ).getNodeType() );
    assertEquals( 1000, into.get( "person" ).get( "measures" ).get( 0 ).get( "value" ).intValue() );
    assertNotNull( into.get( "person" ).get( "measures" ).get( 1 ).get( "value" ) );
    assertEquals( JsonNodeType.NUMBER, into.get( "person" ).get( "measures" ).get( 1 ).get( "value" ).getNodeType() );
    assertEquals( 2000, into.get( "person" ).get( "measures" ).get( 1 ).get( "value" ).intValue() );
    }
  }
