/*
 * Copyright (c) 2017 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.operation.json;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import heretical.pointer.operation.BuildSpec;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 */
public class BuildTest
  {
  @Test
  public void build() throws Exception
    {
    Map<Comparable, Object> arguments = new HashMap<>();

    arguments.put( "id", "123-45-6789" );
    arguments.put( "age", 50 );
    arguments.put( "first", "John" );
    arguments.put( "last", "Doe" );
    arguments.put( "child", "Jane" );
    arguments.put( "child-age", 4 );

    BuildSpec spec = new BuildSpec()
      .putInto( "id", "/ssn" )
      .putInto( "age", String.class, "/age" )
      .putInto( "first", "/name/first" )
      .putInto( "last", "/name/last" )
      .addInto( "child", "/children" )
      .addInto( "child-age", Integer.class, "/childAges" );

    JSONBuilder builder = new JSONBuilder( spec );

    ObjectNode value = JsonNodeFactory.instance.objectNode();

    builder.build( ( key, type ) -> arguments.get( key ), value );

    assertNotNull( value );
    assertEquals( "John", value.findPath( "name" ).findPath( "first" ).textValue() );
    assertEquals( JsonNodeType.NUMBER, value.findPath( "age" ).getNodeType() );
    assertEquals( 50, value.findPath( "age" ).numberValue() );
    assertEquals( "123-45-6789", value.findValue( "ssn" ).textValue() );
    assertEquals( JsonNodeType.ARRAY, value.findPath( "childAges" ).getNodeType() );
    assertEquals( 1, value.findPath( "childAges" ).size() );
    assertEquals( JsonNodeType.NUMBER, value.findPath( "childAges" ).get( 0 ).getNodeType() );
    }
  }
