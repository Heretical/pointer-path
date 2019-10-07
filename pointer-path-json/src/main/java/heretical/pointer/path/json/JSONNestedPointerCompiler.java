/*
 * Copyright (c) 2017-2019 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.path.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import heretical.pointer.path.NestedPointer;
import heretical.pointer.path.NestedPointerCompiler;
import heretical.pointer.path.Pointer;

/**
 * Class JSONNestedPointerCompiler is an implementation of {@link NestedPointerCompiler} for use with JSON objects.
 *
 * @see NestedPointerCompiler for more details.
 */
public class JSONNestedPointerCompiler implements NestedPointerCompiler<JsonNode, ArrayNode>
  {
  public static final JSONNestedPointerCompiler COMPILER = new JSONNestedPointerCompiler();

  private JSONPointerCompiler compiler = new JSONPointerCompiler();

  @Override
  public Pointer<JsonNode> compile( String path )
    {
    return compiler.compile( path );
    }

  @Override
  public NestedPointer<JsonNode, ArrayNode> nested( String path )
    {
    return new JSONNestedPointer( compiler, path );
    }

  @Override
  public Iterable<JsonNode> iterable( ArrayNode node )
    {
    return compiler.iterable( node );
    }

  @Override
  public int size( ArrayNode node )
    {
    return node.size();
    }

  @Override
  public JsonNode node( Object value )
    {
    return compiler.node( value );
    }
  }
