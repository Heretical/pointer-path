/*
 * Copyright (c) 2017 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.operation.json.filter;

import com.fasterxml.jackson.databind.JsonNode;
import heretical.pointer.operation.BasePointerFilter;
import heretical.pointer.path.Pointer;
import heretical.pointer.path.json.JSONNestedPointerCompiler;

public abstract class JSONBasePointerFilter extends BasePointerFilter<JsonNode>
  {
  public JSONBasePointerFilter()
    {
    this( "" );
    }

  public JSONBasePointerFilter( String pointer )
    {
    super( JSONNestedPointerCompiler.COMPILER.compile( pointer ) );
    }

  public JSONBasePointerFilter( Pointer pointer )
    {
    super( pointer );
    }
  }
