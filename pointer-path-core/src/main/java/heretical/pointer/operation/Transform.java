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
 * A Transform is a {@link Function} used to transform a value into a new value during a copy operation.
 * <p>
 * Transforms can be reset in order to re-initialize any value before the transform is invoked.
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
