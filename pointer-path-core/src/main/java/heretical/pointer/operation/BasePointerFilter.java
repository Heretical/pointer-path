/*
 * Copyright (c) 2017 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.operation;

import java.util.function.Predicate;

import heretical.pointer.path.Pointer;

/**
 *
 */
public abstract class BasePointerFilter<Node> implements Predicate<Node>
  {
  Pointer<Node> pointer;

  public BasePointerFilter( Pointer pointer )
    {
    this.pointer = pointer;
    }

  @Override
  public boolean test( Node node )
    {
    return compares( pointer.at( node ) );
    }

  protected abstract boolean compares( Node at );
  }
