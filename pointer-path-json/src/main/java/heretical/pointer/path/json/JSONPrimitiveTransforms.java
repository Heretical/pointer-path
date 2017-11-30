/*
 * Copyright (c) 2017 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.path.json;

import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.TextNode;
import heretical.pointer.operation.Transform;

/**
 * Interface JSONPrimitiveTransforms provides a set of convenience {@link Function} helpers.
 */
public interface JSONPrimitiveTransforms
  {
  Transform<JsonNode> TO_STRING = (Transform<JsonNode>) node -> node == null || node.isTextual() ? node : TextNode.valueOf( node.asText() );
  Transform<JsonNode> TO_DOUBLE = (Transform<JsonNode>) node -> node == null || node.isDouble() ? node : DoubleNode.valueOf( node.asDouble() );
  Transform<JsonNode> TO_FLOAT = (Transform<JsonNode>) node -> node == null || node.isFloat() ? node : FloatNode.valueOf( (float) node.asDouble() );
  Transform<JsonNode> TO_LONG = (Transform<JsonNode>) node -> node == null || node.isLong() ? node : LongNode.valueOf( node.asLong() );
  Transform<JsonNode> TO_INT = (Transform<JsonNode>) node -> node == null || node.isInt() ? node : IntNode.valueOf( node.asInt() );
  }
