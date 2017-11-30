/*
 * Copyright (c) 2017 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.operation;

import java.io.Serializable;
import java.util.Map;
import java.util.function.Function;

/**
 *
 */
public interface Transform<Node> extends Function<Node, Node>, Serializable
  {
  default boolean isResettable()
    {
    return false;
    }

  default void reset( Map<String, Object> values )
    {
    }
  }
