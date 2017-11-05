/*
 * Copyright (c) 2017 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.path.json;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import static heretical.pointer.path.json.JSONNestedPointerCompiler.COMPILER;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class JSONNestedPointerSetTest
  {
  @Test
  public void testSetValue() throws Exception
    {
    ObjectNode into = JsonNodeFactory.instance.objectNode();

    COMPILER.compile( "/value" ).set( into, JsonNodeFactory.instance.textNode( "foo" ) );

    assertEquals( "foo", into.get( "value" ).textValue() );

    COMPILER.compile( "/value" ).set( into, JsonNodeFactory.instance.textNode( "bar" ) );

    assertEquals( "bar", into.get( "value" ).textValue() );
    }

  @Test
  public void testSetDeepValue() throws Exception
    {
    ObjectNode into = JsonNodeFactory.instance.objectNode();

    COMPILER.compile( "/value/nested/deep" ).set( into, JsonNodeFactory.instance.textNode( "foo" ) );

    assertEquals( "foo", into.findPath( "value" ).findPath( "nested" ).findPath( "deep" ).textValue() );

    COMPILER.compile( "/value/nested/deep" ).set( into, JsonNodeFactory.instance.textNode( "bar" ) );

    assertEquals( "bar", into.findPath( "value" ).findPath( "nested" ).findPath( "deep" ).textValue() );
    }

  @Test(expected = IllegalArgumentException.class)
  public void testSetDeepValueFail() throws Exception
    {
    ObjectNode into = JsonNodeFactory.instance.objectNode();

    COMPILER.compile( "/value/nested" ).set( into, JsonNodeFactory.instance.textNode( "foo" ) );

    COMPILER.compile( "/value/nested/deep" ).set( into, JsonNodeFactory.instance.textNode( "bar" ) );
    }

  @Test
  public void testSetArrayValue() throws Exception
    {
    ObjectNode into = JsonNodeFactory.instance.objectNode();

    COMPILER.compile( "/value/0" ).set( into, JsonNodeFactory.instance.textNode( "foo" ) );

    assertEquals( "foo", into.get( "value" ).get( 0 ).textValue() );

    COMPILER.compile( "/value/1" ).set( into, JsonNodeFactory.instance.textNode( "foo" ) );

    assertEquals( "foo", into.get( "value" ).get( 1 ).textValue() );

    COMPILER.compile( "/value/0" ).set( into, JsonNodeFactory.instance.textNode( "bar" ) );

    assertEquals( "bar", into.get( "value" ).get( 0 ).textValue() );

    assertEquals( 2, into.get( "value" ).size() );
    }

  @Test
  public void testSetDeepArrayValue() throws Exception
    {
    ObjectNode into = JsonNodeFactory.instance.objectNode();

    COMPILER.compile( "/value/0/nested/deep" ).set( into, JsonNodeFactory.instance.textNode( "foo" ) );

    assertEquals( "foo", into.findPath( "value" ).get( 0 ).findPath( "nested" ).findPath( "deep" ).textValue() );

    COMPILER.compile( "/value/0/nested/deep" ).set( into, JsonNodeFactory.instance.textNode( "bar" ) );

    assertEquals( "bar", into.findPath( "value" ).get( 0 ).findPath( "nested" ).findPath( "deep" ).textValue() );
    }
  }
