/*
 * Copyright (c) 2017-2018 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.util;

import java.util.List;

import heretical.pointer.util.PathTree;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class PathTreeTest
  {
  @Test
  public void pathTree()
    {
    PathTree pathTree = new PathTree();

    PathTree.Element first = pathTree.child( "person" ).child( "first" );
    PathTree.Element last = pathTree.child( "person" ).child( "last" );

    Assert.assertEquals( "/person/first", first.pointer() );
    Assert.assertEquals( "/person/last", last.pointer() );

    List<String> pointers = pathTree.depthFirstPointers();

    Assert.assertEquals( 4, pointers.size() );
    Assert.assertEquals( "", pointers.get( 3 ) );

    pointers = pathTree.leafPointers();

    System.out.println( "pointers = " + pointers );
    Assert.assertEquals( 2, pointers.size() );

    PathTree.Element children = pathTree.child( "person" ).child( "children" );

    PathTree.Element firstChild = children.child( 0 ).child( "person" ).child( "age" );
    PathTree.Element secondChild = children.child( 1 ).child( "person" ).child( "age" );

    Assert.assertEquals( "/person/children/0/person/age", firstChild.pointer() );
    Assert.assertEquals( "/person/children/1/person/age", secondChild.pointer() );

    pointers = pathTree.depthFirstPointers();

    Assert.assertEquals( 11, pointers.size() );
    Assert.assertEquals( "", pointers.get( 10 ) );

    pointers = pathTree.leafPointers();

    Assert.assertEquals( 4, pointers.size() );
    }
  }
