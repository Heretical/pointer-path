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
public class JSONNestedPointerAddTest
  {
  @Test
  public void testAdd() throws Exception
    {
    ObjectNode into = JsonNodeFactory.instance.objectNode();

    COMPILER.compile( "/value" ).add( into, JsonNodeFactory.instance.textNode( "foo" ) );

    assertEquals( "foo", into.get( "value" ).get( 0 ).textValue() );

    COMPILER.compile( "/value" ).add( into, JsonNodeFactory.instance.textNode( "foo" ) );

    assertEquals( "foo", into.get( "value" ).get( 1 ).textValue() );

    COMPILER.compile( "/value" ).add( into, JsonNodeFactory.instance.textNode( "bar" ) );

    assertEquals( "bar", into.get( "value" ).get( 2 ).textValue() );

    assertEquals( 3, into.get( "value" ).size() );
    }
  }
