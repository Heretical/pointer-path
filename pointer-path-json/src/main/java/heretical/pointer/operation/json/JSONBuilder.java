/*
 * Copyright (c) 2017 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.operation.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import heretical.pointer.operation.BuildSpec;
import heretical.pointer.operation.Builder;
import heretical.pointer.path.json.JSONNestedPointerCompiler;

/**
 * JSONBuilder provides the a means to copy values from a lookup function into a JSON object.
 * <p>
 * The {@link BuildSpec} build declaration relies on {@link heretical.pointer.path.json.JSONNestedPointer} Strings to
 * define the insertion paths.
 */
public class JSONBuilder extends Builder<JsonNode, ArrayNode>
  {
  /**
   * Creates a new JSONBuilder instance.
   *
   * @param buildSpecs the build declaration
   */
  public JSONBuilder( BuildSpec... buildSpecs )
    {
    super( JSONNestedPointerCompiler.COMPILER, buildSpecs );
    }
  }
