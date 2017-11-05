/*
 * Copyright (c) 2017 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.operation.json;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import heretical.pointer.operation.CopySpec;
import heretical.pointer.operation.json.filter.JSONBooleanPointerFilter;
import heretical.pointer.operation.json.filter.JSONStringPointerFilter;
import heretical.pointer.operation.json.transform.JSONSetTextTransform;
import heretical.pointer.path.json.JSONData;
import heretical.pointer.path.json.JSONPrimitiveTransforms;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class CopyTest
  {
  ObjectMapper mapper = new ObjectMapper();

  @Test
  public void testCopy() throws Exception
    {
    JsonNode value = mapper.readTree( JSONData.nested );
    ObjectNode result = JsonNodeFactory.instance.objectNode();

    CopySpec spec = new CopySpec()
      .from( "/person" );

    JSONCopier copier = new JSONCopier( spec );

    copier.copy( value, result );

    assertNotNull( result );
    assertEquals( "John Doe", result.get( "name" ).textValue() );
    assertEquals( 50, result.get( "age" ).intValue() );
    assertEquals( "123-45-6789", result.get( "ssn" ).textValue() );
    }

  @Test
  public void testCopyPredicate() throws Exception
    {
    JsonNode value = mapper.readTree( JSONData.nested );
    ObjectNode result = JsonNodeFactory.instance.objectNode();

    CopySpec spec = new CopySpec()
      .from( "/person", new JSONStringPointerFilter( "/name", "John Doe" ) );

    JSONCopier copier = new JSONCopier( spec );

    copier.copy( value, result );

    assertNotNull( result );
    assertEquals( "John Doe", result.get( "name" ).textValue() );
    assertEquals( 50, result.get( "age" ).intValue() );
    assertEquals( "123-45-6789", result.get( "ssn" ).textValue() );
    }

  @Test
  public void testCopyPredicateNegate() throws Exception
    {
    JsonNode value = mapper.readTree( JSONData.nested );
    ObjectNode result = JsonNodeFactory.instance.objectNode();

    CopySpec spec = new CopySpec()
      .from( "/person", new JSONStringPointerFilter( "/name", "John Doe" ).negate() );

    JSONCopier copier = new JSONCopier( spec );

    copier.copy( value, result );

    assertNotNull( value );
    assertNull( value.get( "name" ) );
    assertNull( value.get( "age" ) );
    assertNull( value.get( "ssn" ) );
    }

  @Test
  public void testCopyPredicateBoolean() throws Exception
    {
    JsonNode value = mapper.readTree( JSONData.nested );
    ObjectNode result = (ObjectNode) mapper.readTree( JSONData.simple );

    CopySpec spec = new CopySpec()
      .from( "/person", new JSONBooleanPointerFilter( "/human", true ) );

    JSONCopier copier = new JSONCopier( spec );

    copier.copy( value, result );

    assertNotNull( result );
    assertEquals( "value", result.get( "existing" ).textValue() ); // confirm we put data into an existing object
    assertEquals( "John Doe", result.get( "name" ).textValue() );
    assertEquals( true, result.get( "human" ).booleanValue() );
    assertEquals( 50, result.get( "age" ).intValue() );
    assertEquals( "123-45-6789", result.get( "ssn" ).textValue() );
    }

  @Test
  public void testCopyPredicateBooleanNegate() throws Exception
    {
    JsonNode value = mapper.readTree( JSONData.nested );
    ObjectNode result = (ObjectNode) mapper.readTree( JSONData.simple );

    CopySpec spec = new CopySpec()
      .from( "/person", new JSONBooleanPointerFilter( "/human", true ).negate() );

    JSONCopier copier = new JSONCopier( spec );

    copier.copy( value, result );

    assertNotNull( result );
    assertEquals( "value", result.get( "existing" ).textValue() ); // confirm we put data into an existing object
    assertNull( result.get( "name" ) );
    assertNull( result.get( "human" ) );
    assertNull( result.get( "age" ) );
    assertNull( result.get( "ssn" ) );
    }

  @Test
  public void testCopyAsArrayPredicate() throws Exception
    {
    JsonNode value = mapper.readTree( JSONData.people );
    ObjectNode result = JsonNodeFactory.instance.objectNode();

    CopySpec spec = new CopySpec()
      .from( "/people/*", new JSONStringPointerFilter( "/person/name", "John Doe" ) );

    JSONCopier copier = new JSONCopier( spec );

    copier.copy( value, result );

    assertNotNull( value );
    assertEquals( "John Doe", result.get( "person" ).get( "name" ).textValue() );
    assertEquals( 50, result.get( "person" ).get( "age" ).intValue() );
    assertEquals( "123-45-6789", result.get( "person" ).get( "ssn" ).textValue() );
    }

  @Test
  public void testCopyAsArrayPredicateNegate() throws Exception
    {
    JsonNode value = mapper.readTree( JSONData.people );
    ObjectNode result = JsonNodeFactory.instance.objectNode();

    CopySpec spec = new CopySpec()
      .from( "/people/*", new JSONStringPointerFilter( "/person/name", "John Doe" ).negate() );

    JSONCopier copier = new JSONCopier( spec );

    copier.copy( value, result );

    assertNotNull( result );
    assertEquals( "Jane Doe", result.get( "person" ).get( "name" ).textValue() );
    assertEquals( 49, result.get( "person" ).get( "age" ).intValue() );
    assertEquals( "123-45-6789", result.get( "person" ).get( "ssn" ).textValue() );
    }

  @Test
  public void testCopyInto() throws Exception
    {
    JsonNode value = mapper.readTree( JSONData.people );
    ObjectNode result = JsonNodeFactory.instance.objectNode();

    CopySpec spec = new CopySpec( "/people" )
      .from( "/people/0" );

    JSONCopier copier = new JSONCopier( spec );

    copier.copy( value, result );

    assertNotNull( result );

    result = (ObjectNode) result.get( "people" ).get( "person" );

    assertEquals( "John Doe", result.get( "name" ).textValue() );
    assertEquals( 50, result.get( "age" ).intValue() );
    assertEquals( "123-45-6789", result.get( "ssn" ).textValue() );
    }

  @Test
  public void testCopyIncludeFrom() throws Exception
    {
    JsonNode value = mapper.readTree( JSONData.nested );
    ObjectNode result = JsonNodeFactory.instance.objectNode();

    CopySpec spec = new CopySpec()
      .fromInclude( "/person", "/firstName" )
      .fromInclude( "/person", "/age" );

    JSONCopier copier = new JSONCopier( spec );

    copier.copy( value, result );

    assertNotNull( result );
    assertEquals( "John", result.get( "firstName" ).textValue() );
    assertEquals( 50, result.get( "age" ).intValue() );
    assertEquals( null, result.get( "ssn" ) );
    }

  @Test
  public void testCopyIncludeFromPredicate() throws Exception
    {
    JsonNode value = mapper.readTree( JSONData.nested );
    ObjectNode result = JsonNodeFactory.instance.objectNode();

    CopySpec spec = new CopySpec()
      .fromInclude( "/person", "/firstName", new JSONStringPointerFilter( "John" ).negate() )
      .fromInclude( "/person", "/age" );

    JSONCopier copier = new JSONCopier( spec );

    copier.copy( value, result );

    assertNotNull( result );
    assertNull( result.get( "firstName" ) );
    assertEquals( 50, result.get( "age" ).intValue() );
    assertEquals( null, result.get( "ssn" ) );
    }

  @Test
  public void testCopyInclude() throws Exception
    {
    JsonNode value = mapper.readTree( JSONData.nested );
    ObjectNode result = JsonNodeFactory.instance.objectNode();

    CopySpec spec = new CopySpec()
      .include( "/person/firstName" )
      .include( "/person/age" );

    JSONCopier copier = new JSONCopier( spec );

    copier.copy( value, result );

    assertNotNull( result );
    assertEquals( "John", result.findPath( "firstName" ).textValue() );
    assertEquals( 50, result.findPath( "age" ).intValue() );
    assertEquals( null, result.findValue( "ssn" ) );
    }

  @Test
  public void testCopyIncludeWild() throws Exception
    {
    JsonNode value = mapper.readTree( JSONData.nested );
    ObjectNode result = JsonNodeFactory.instance.objectNode();

    CopySpec spec = new CopySpec()
      .include( "/person/firstName" )
      .include( "/*/age" );

    JSONCopier copier = new JSONCopier( spec );

    copier.copy( value, result );

    assertNotNull( result );
    assertEquals( "John", result.findPath( "firstName" ).textValue() );
    assertEquals( 50, result.findPath( "age" ).intValue() );
    assertEquals( null, result.findValue( "ssn" ) );
    }

  @Test
  public void testCopyIncludeDescent() throws Exception
    {
    JsonNode value = mapper.readTree( JSONData.nested );
    ObjectNode result = JsonNodeFactory.instance.objectNode();

    CopySpec spec = new CopySpec()
      .include( "/person/firstName" )
      .include( "/**/age" );

    JSONCopier copier = new JSONCopier( spec );

    copier.copy( value, result );

    assertNotNull( result );
    assertEquals( "John", result.findPath( "firstName" ).textValue() );
    assertEquals( 50, result.findPath( "age" ).intValue() );
    assertEquals( null, result.findValue( "ssn" ) );
    }

  @Test
  public void testCopyIncludeFrom2() throws Exception
    {
    JsonNode value = mapper.readTree( JSONData.nested );
    ObjectNode result = JsonNodeFactory.instance.objectNode();

    CopySpec spec = new CopySpec()
      .fromInclude( "/person", "/firstName", "/age" );

    JSONCopier copier = new JSONCopier( spec );

    copier.copy( value, result );

    assertNotNull( result );
    assertEquals( "John", result.get( "firstName" ).textValue() );
    assertEquals( 50, result.get( "age" ).intValue() );
    assertEquals( null, result.get( "ssn" ) );
    }

  @Test
  public void testCopyExcludeFrom() throws Exception
    {
    JsonNode value = mapper.readTree( JSONData.nested );
    ObjectNode result = JsonNodeFactory.instance.objectNode();

    CopySpec spec = new CopySpec()
      .fromExclude( "/person", "/ssn", "/children" );

    JSONCopier copier = new JSONCopier( spec );

    copier.copy( value, result );

    assertNotNull( result );
    assertEquals( "John Doe", result.get( "name" ).textValue() );
    assertEquals( 50, result.get( "age" ).intValue() );
    assertEquals( null, result.get( "ssn" ) );
    assertEquals( null, result.get( "children" ) );
    }

  @Test
  public void testCopyExclude() throws Exception
    {
    JsonNode value = mapper.readTree( JSONData.nested );
    ObjectNode result = JsonNodeFactory.instance.objectNode();

    CopySpec spec = new CopySpec()
      .exclude( "/person/ssn", "/person/children" );

    JSONCopier copier = new JSONCopier( spec );

    copier.copy( value, result );

    assertNotNull( result );
    assertEquals( "John Doe", result.findPath( "name" ).textValue() );
    assertEquals( 50, result.findPath( "age" ).intValue() );
    assertEquals( null, result.findValue( "ssn" ) );
    assertEquals( null, result.findValue( "children" ) );
    }

  @Test
  public void testCopyExcludeDescent() throws Exception
    {
    JsonNode value = mapper.readTree( JSONData.nested );
    ObjectNode result = JsonNodeFactory.instance.objectNode();

    CopySpec spec = new CopySpec()
      .exclude( "/**/value" );

    JSONCopier copier = new JSONCopier( spec );

    copier.copy( value, result );

    assertNotNull( result );
    assertNotNull( result.get( "person" ) );

    result = (ObjectNode) result.get( "person" );
    assertNull( result.get( "measure" ).get( "value" ) );
    }

  @Test
  public void testCoerce() throws Exception
    {
    JsonNode value = mapper.readTree( JSONData.nested );
    ObjectNode result = JsonNodeFactory.instance.objectNode();

    CopySpec spec = new CopySpec()
      .fromTransform( "/person/measure", "/value", JSONPrimitiveTransforms.TO_FLOAT );

    JSONCopier copier = new JSONCopier( spec );

    copier.copy( value, result );

    assertNotNull( result );
    assertEquals( JsonNodeType.NUMBER, result.get( "value" ).getNodeType() );
    assertEquals( FloatNode.class, result.get( "value" ).getClass() );
    assertEquals( 100.0F, result.get( "value" ).floatValue(), .001 );
    }

  @Test
  public void testCoerceZero() throws Exception
    {
    JsonNode value = mapper.readTree( JSONData.nested );
    ObjectNode result = JsonNodeFactory.instance.objectNode();

    CopySpec spec = new CopySpec()
      .fromTransform( "/person/zero", "/zeroValue", JSONPrimitiveTransforms.TO_FLOAT );

    JSONCopier copier = new JSONCopier( spec );

    copier.copy( value, result );

    assertNotNull( result );
    assertEquals( JsonNodeType.NUMBER, result.get( "zeroValue" ).getNodeType() );
    assertEquals( FloatNode.class, result.get( "zeroValue" ).getClass() );
    assertEquals( 0.0F, result.get( "zeroValue" ).floatValue(), .001 );
    assertEquals( "0.0", result.get( "zeroValue" ).asText() );
    }

  @Test
  public void testCoerceArray() throws Exception
    {
    JsonNode value = mapper.readTree( JSONData.nested );
    ObjectNode result = JsonNodeFactory.instance.objectNode();

    CopySpec spec = new CopySpec()
      .fromTransform( "/person", "/measures/*/value", JSONPrimitiveTransforms.TO_FLOAT );

    JSONCopier copier = new JSONCopier( spec );

    copier.copy( value, result );

    assertNotNull( result );
    assertEquals( JsonNodeType.ARRAY, result.get( "measures" ).getNodeType() );
    assertEquals( FloatNode.class, result.get( "measures" ).get( 0 ).get( "value" ).getClass() );
    assertEquals( 1000.0F, result.get( "measures" ).get( 0 ).get( "value" ).floatValue(), .001 );
    assertEquals( FloatNode.class, result.get( "measures" ).get( 1 ).get( "value" ).getClass() );
    assertEquals( 2000.0F, result.get( "measures" ).get( 1 ).get( "value" ).floatValue(), .001 );
    }

  @Test
  public void testResettableTransform() throws Exception
    {
    JsonNode value = mapper.readTree( JSONData.nested );
    ObjectNode result = JsonNodeFactory.instance.objectNode();

    CopySpec spec = new CopySpec()
      .fromTransform( "/person", "/name", new JSONSetTextTransform( "set-text", "value1" ) );

    Map<Comparable, Object> arguments = Collections.singletonMap( "set-text", "value2" );

    JSONCopier copier = new JSONCopier( spec );

    copier.copy( arguments, value, result );

    assertNotNull( result );
    assertEquals( "value2", result.get( "name" ).textValue() );

    arguments = Collections.singletonMap( "set-text", "value3" );

    copier.copy( arguments, value, result );

    assertNotNull( result );
    assertEquals( "value3", result.get( "name" ).textValue() );
    }
  }
