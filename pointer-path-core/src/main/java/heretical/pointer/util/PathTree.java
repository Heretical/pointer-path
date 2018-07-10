/*
 * Copyright (c) 2017-2018 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.util;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
public class PathTree
  {
  public class Element
    {
    PathTree tree = PathTree.this;
    Element parent = null;
    String path = "";
    Map<Comparable, Element> children = new LinkedHashMap<>();

    private Element()
      {
      }

    Element( Element parent, Comparable path )
      {
      this.parent = parent;
      this.path = path.toString();
      }

    public PathTree tree()
      {
      return tree;
      }

    public Element child( String path )
      {
      return children.computeIfAbsent( path, p -> new Element( this, p ) );
      }

    public Element child( int ordinal )
      {
      return children.computeIfAbsent( ordinal, o -> new Element( this, o ) );
      }

    public String pointer()
      {
      LinkedList<String> paths = new LinkedList<>();

      addPath( paths, this );

      return paths.stream().collect( Collectors.joining( "/" ) );
      }

    private void addPath( LinkedList<String> paths, Element element )
      {
      if( element == null )
        return;

      paths.addFirst( element.path );

      addPath( paths, element.parent );
      }
    }

  private Element root = new Element();

  public PathTree()
    {
    }

  public Element root()
    {
    return root;
    }

  public Element child( String path )
    {
    return root.child( path );
    }

  public List<String> leafPointers()
    {
    List<String> pointers = new LinkedList<>();

    addPointer( pointers, root, true );

    return pointers;
    }

  public List<String> depthFirstPointers()
    {
    List<String> pointers = new LinkedList<>();

    addPointer( pointers, root, false );

    return pointers;
    }

  private void addPointer( List<String> pointers, Element element, boolean leavesOnly )
    {
    element.children.values().forEach( e -> addPointer( pointers, e, leavesOnly ) );

    if( !leavesOnly || element.children.isEmpty() )
      pointers.add( element.pointer() );
    }

  @Override
  public String toString()
    {
    final StringBuilder sb = new StringBuilder( "PathTree{" );
    sb.append( leafPointers() );
    sb.append( '}' );
    return sb.toString();
    }
  }
