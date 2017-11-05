/*
 * Copyright (c) 2017 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.operation.json.filter;

import com.fasterxml.jackson.databind.JsonNode;
import heretical.pointer.path.Pointer;

/**
 * Class JSONBooleanPointerFilter filters on a boolean value.
 */
public class JSONBooleanPointerFilter extends JSONBasePointerFilter
  {
  private boolean value;

  /**
   * Constructor JSONBooleanPointerFilter creates a new JSONBooleanPointerFilter instance.
   *
   * @param pointer of String
   * @param value   of boolean
   */
  public JSONBooleanPointerFilter( String pointer, boolean value )
    {
    super( pointer );

    this.value = value;
    }

  public JSONBooleanPointerFilter( Pointer pointer, boolean value )
    {
    super( pointer );
    this.value = value;
    }

  @Override
  protected boolean compares( JsonNode at )
    {
    return value == at.booleanValue();
    }
  }
