/*
 * Copyright (c) 2017 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.path.json;

import org.junit.Test;

import static heretical.pointer.path.json.JSONNestedPointerCompiler.COMPILER;

/**
 *
 */
public class JSONNestedPointerParseTest
  {

  @Test
  public void testParse() throws Exception
    {
    COMPILER.nested( "/*" );
    COMPILER.nested( "/*/foo" );
    COMPILER.nested( "/foo" );
    COMPILER.nested( "/foo/bar" );
    COMPILER.nested( "/foo/bar/2" );
    COMPILER.nested( "/foo/bar/*/baz" );
    COMPILER.nested( "/foo/bar/**/baz" );
    COMPILER.nested( "/foo/bar/*/*/baz" );
    COMPILER.nested( "/foo/bar/**/*/baz" );
    COMPILER.nested( "/foo/bar/**/**/baz" );
    COMPILER.nested( "/foo/bar/*/**/baz" );
    }
  }
