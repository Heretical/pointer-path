/*
 * Copyright (c) 2017 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.path;

import java.io.Serializable;

/**
 *
 */
public interface NestedPointerCompiler<Node, Result> extends Serializable
  {
  Pointer<Node> compile( String path );

  NestedPointer<Node, Result> nested( String path );

  Iterable<Node> iterable( Result node );

  Node node( Object value );
  }
