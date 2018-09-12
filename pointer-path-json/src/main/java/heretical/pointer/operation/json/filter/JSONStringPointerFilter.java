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
 * Class JSONStringPointerFilter filters on a string value.
 */
public class JSONStringPointerFilter extends JSONBasePointerFilter
  {
  private String value;
  private boolean ignoreCase = false;

  /**
   * Constructor JSONStringPointerFilter creates a new JSONStringPointerFilter instance.
   *
   * @param value of String
   */
  public JSONStringPointerFilter( String value )
    {
    this.value = value;
    }

  /**
   * Constructor JSONStringPointerFilter creates a new JSONStringPointerFilter instance.
   *
   * @param value      of String
   * @param ignoreCase of boolean
   */
  public JSONStringPointerFilter( String value, boolean ignoreCase )
    {
    this.value = value;
    this.ignoreCase = ignoreCase;
    }

  /**
   * Constructor JSONStringPointerFilter creates a new JSONStringPointerFilter instance.
   *
   * @param pointer of String
   * @param value   of String
   */
  public JSONStringPointerFilter( String pointer, String value )
    {
    super( pointer );

    this.value = value;
    }

  /**
   * Constructor JSONStringPointerFilter creates a new JSONStringPointerFilter instance.
   *
   * @param pointer    of String
   * @param value      of String
   * @param ignoreCase of boolean
   */
  public JSONStringPointerFilter( String pointer, String value, boolean ignoreCase )
    {
    super( pointer );

    this.value = value;
    this.ignoreCase = ignoreCase;
    }

  /**
   * Constructor JSONStringPointerFilter creates a new JSONStringPointerFilter instance.
   *
   * @param pointer    of Pointer
   * @param value      of String
   */
  public JSONStringPointerFilter( Pointer pointer, String value )
    {
    super( pointer );
    this.value = value;
    }

  /**
   * Constructor JSONStringPointerFilter creates a new JSONStringPointerFilter instance.
   *
   * @param pointer    of Pointer
   * @param value      of String
   * @param ignoreCase of boolean
   */
  public JSONStringPointerFilter( Pointer pointer, String value, boolean ignoreCase )
    {
    super( pointer );
    this.value = value;
    this.ignoreCase = ignoreCase;
    }

  @Override
  protected boolean compares( JsonNode at )
    {
    boolean atIsNull = at == null || at.isNull();

    if( value == null && atIsNull )
      return true;

    if( atIsNull )
      return false;

    if( ignoreCase )
      return at.asText().equalsIgnoreCase( value );

    return at.asText().equals( value );
    }
  }
