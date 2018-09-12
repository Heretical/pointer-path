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
import heretical.pointer.operation.Copier;
import heretical.pointer.operation.CopySpec;
import heretical.pointer.path.json.JSONNestedPointerCompiler;

/**
 * JSONCopier provides the a means to copy values from one JSON object into a another JSON object.
 *
 * The {@link CopySpec} copy declaration relies on {@link heretical.pointer.path.json.JSONNestedPointer} Strings to
 * define the copy from and to copy paths.
 */
public class JSONCopier extends Copier<JsonNode, ArrayNode>
  {
  /**
   * Creates a new JSONCopier instance.
   *
   * @param copySpecs the copy declaration
   */
  public JSONCopier( CopySpec... copySpecs )
    {
    super( JSONNestedPointerCompiler.COMPILER, copySpecs );
    }
  }
