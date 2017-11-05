/*
 * Copyright (c) 2017 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.path.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import heretical.pointer.path.BaseNestedPointer;
import heretical.pointer.path.PointerCompiler;

/**
 *
 */
public class JSONNestedPointer extends BaseNestedPointer<JsonNode, ArrayNode>
  {
  public JSONNestedPointer( PointerCompiler<JsonNode, ArrayNode> compiler, String pointer )
    {
    super( compiler, pointer );
    }
  }
