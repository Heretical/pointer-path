# Pointer-Path - An API for processing nested data types

## Overview

Pointer-Path is a Java API for building and transforming nested data types like JSON.

The API is generic, but currently only supports JSON operations, with the intent to provide POJO and XML providers.

Releases are currently available on Conjars

* http://conjars.org/io.heretical/pointer-path-core
* http://conjars.org/io.heretical/pointer-path-json

This library requires Java 8 and the JSON functionality is dependent on [Jackson](https://github.com/FasterXML/jackson).

### Pointer Path Syntax

The pointer path syntax is an extended version of the [JSON
Pointer](https://tools.ietf.org/html/draft-ietf-appsawg-json-pointer-03) syntax with the addition of a wildcard operator
(`*`) and a descent operator(`**`).

`/person` references the `person` element in an data structure.

`/person/firstName` references the child `firstName` beneath the `person` element in an data structure.

`/people/*/age` will reference the `age` value a level below the `people` array (e.g. `/people/measures/age`).

`/measures/**/value` will reference all the `value` values at any level below the `measures` root.

### Pointer

The `Pointer` API, relying on the pointer path syntax, provides accessors and mutators for use against a nested data
structure.

The following code snippet will deep copy the tree that matches `/person/measures/*/value` from the `from` node into the
`into` node.

```java
    JSONNestedPointerCompiler.COMPILER.nested( "/person/measures/*/value" ).copy( from, into );
```  

This code will return an array type (specific to the provider) of all the `values` values found in the tree.

```java
    JsonNode from = mapper.readTree( JSONData.nested );

    JsonNode result = COMPILER.nested( "/**/measures/*/value" ).allAt( from );
```

Here we coerce all elements named `value` to `float` types, then retrieve all the values as a JSON array.

```java
    JsonNode from = mapper.readTree( JSONData.nested );

    NestedPointer<JsonNode, ArrayNode> pointer = COMPILER.nested( "/person/**/value" );

    pointer.apply( from, JSONPrimitiveTransforms.TO_FLOAT ); // accepts a Java 8 Function or lambda

    JsonNode result = pointer.allAt( from );
```

### Builder

The `Builder` class allows for new nested objects to be created from a set of `BuildSpec` declarations and a Map of
values. With the intent of declaring the transformation once, and repeatedly generating new objects on new sets of
argument values.

Below a new JSON object is being created from a Map of values.

```java
    Map<Comparable, Object> arguments = new HashMap<>();

    arguments.put( "id", "123-45-6789" );
    arguments.put( "age", 50 );
    arguments.put( "first", "John" );
    arguments.put( "last", "Doe" );
    arguments.put( "child", "Jane" );
    arguments.put( "child-age", 4 );

    BuildSpec spec = new BuildSpec()
      .putInto( "id", "/ssn" )
      .putInto( "age", String.class, "/age" )
      .putInto( "first", "/name/first" )
      .putInto( "last", "/name/last" )
      .addInto( "child", "/children" )
      .addInto( "child-age", Integer.class, "/childAges" );

    JSONBuilder builder = new JSONBuilder( spec );

    ObjectNode value = JsonNodeFactory.instance.objectNode();

    builder.build( ( key, type ) -> arguments.get( key ), value );
```

Note that the values in the `arguments` Map can be any primitive type or supported child type to the parent node,
including another JSON Object or Array. Internally, all primitive values are wrapped by their corresponding node type,
if any.

### Copier

Similarly the `Copier` class allows for a new nested objects to be created from a set of `CopySpec` declarations and a
single input nested data type. Again with the intent of declaring the transformation once, and repeatedly generating new
objects on new sets of argument values.

Below a new JSON object is being created by copying the original object, but excluding specific values.

```java
    JsonNode value = mapper.readTree( JSONData.nested );
    ObjectNode result = JsonNodeFactory.instance.objectNode();

    CopySpec spec = new CopySpec()
      .fromExclude( "/person", "/ssn", "/children" ); // exclude ssn and children from the result

    JSONCopier copier = new JSONCopier( spec );

    copier.copy( value, result );
```

Its worth noting that the Copier and CopySpec declarations attempts to replicate the semantics of copying a directory 
tree from one location to another.

In the example below, we apply a transformation to all the `value` elements in an array into `float` values. 

```java
    JsonNode value = mapper.readTree( JSONData.nested );
    ObjectNode result = JsonNodeFactory.instance.objectNode();

    CopySpec spec = new CopySpec()
      .fromTransform( "/result", "/measures/*/value", JSONPrimitiveTransforms.TO_FLOAT );

    JSONCopier copier = new JSONCopier( spec );

    copier.copy( value, result );
```

We could also have used a descent operator to catch all the child `value` elements in a complex object to help normalize
the data structure. In some object the `value` could be an `int` on one path and `float` on another, which could cause
downstream headaches if a system was tyring to infer data types from observed values (looking at you Elasticsearch).