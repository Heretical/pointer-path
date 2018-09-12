/*
 * Copyright (c) 2017 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.operation.json.transform;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import heretical.pointer.operation.Transform;

/**
 * Class JSONSetTextTransform is an implementation of {@link Transform} for updating the text in a
 * JSON {@link com.fasterxml.jackson.databind.node.TextNode}
 */
public class JSONSetTextTransform implements Transform<JsonNode>
  {
  String name = null;
  String defaultReplace = null;
  String replace = null;

  /**
   * Constructor JSONSetTextTransform creates a new JSONSetTextTransform instance.
   */
  public JSONSetTextTransform()
    {
    }

  /**
   * Constructor JSONSetTextTransform creates a new JSONSetTextTransform instance.
   *
   * @param replace of String
   */
  public JSONSetTextTransform( String replace )
    {
    this.replace = replace;
    }

  /**
   * Constructor JSONSetTextTransform creates a new JSONSetTextTransform instance.
   *
   * @param name           of String
   * @param defaultReplace of String
   */
  public JSONSetTextTransform( String name, String defaultReplace )
    {
    this.name = name;
    this.defaultReplace = defaultReplace;

    if( name == null || name.isEmpty() )
      throw new IllegalArgumentException( "name may not be null or empty" );
    }

  @Override
  public JsonNode apply( JsonNode node )
    {
    if( node.getNodeType() != JsonNodeType.STRING )
      throw new IllegalStateException( "can only be applied to a TextNode" );

    return JsonNodeFactory.instance.textNode( replace );
    }

  /**
   * Method isResettable returns the resettable of this JSONSetTextTransform object.
   *
   * @return the resettable (type boolean) of this JSONSetTextTransform object.
   */
  @Override
  public boolean isResettable()
    {
    return name != null;
    }

  @Override
  public void reset( Map<String, Object> values )
    {
    replace = asString( values.getOrDefault( name, defaultReplace ) );
    }

  private static String asString( Object object )
    {
    return object == null ? null : object.toString();
    }

  @Override
  public String toString()
    {
    final StringBuilder sb = new StringBuilder( "JSONSetTextTransform{" );
    sb.append( "name='" ).append( name ).append( '\'' );
    sb.append( ", defaultReplace='" ).append( defaultReplace ).append( '\'' );
    sb.append( ", replace='" ).append( replace ).append( '\'' );
    sb.append( ", resettable=" ).append( isResettable() );
    sb.append( '}' );
    return sb.toString();
    }
  }
