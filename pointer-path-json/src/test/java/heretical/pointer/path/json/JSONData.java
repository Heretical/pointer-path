/*
 * Copyright (c) 2017-2019 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package heretical.pointer.path.json;

/**
 *
 */
public interface JSONData
  {
  String simple = "{ \"existing\":\"value\" }";

  String nested = "{" +
    "\"person\":{" +
    "\"name\":\"John Doe\"," +
    "\"firstName\":\"John\"," +
    "\"lastName\":\"Doe\"," +
    "\"age\":50," +
    "\"human\":true," +
    "\"city\":\"Houston\"," +
    "\"ssn\":\"123-45-6789\"," +
    "\"measure\": { \"value\":100 }," +
    "\"measures\":[ { \"value\":1000 }, { \"value\":2000 } ]," +
    "\"measured\":[ 1000, 2000 ]," +
    "\"children\":[" +
    "\"Jane\"," +
    "\"June\"," +
    "\"Josh\"" +
    "]," +
    "\"arrays\":[" +
    "[" +
    "\"Jane1\"," +
    "\"June1\"," +
    "\"Josh1\"" +
    "]," +
    "[" +
    "\"Jane2\"," +
    "\"June2\"," +
    "\"Josh2\"" +
    "]" +
    "]," +
    "\"zero\": { \"zeroValue\":0 }," +
    "\"empty\": null" +
    "}," +
    "\"empty\": \"i lied\"" +
    "}";

  String people = "{" +
    "\"people\":" +
    "[ " +
    "{" +
    "\"person\":" +
    "{" +
    "\"name\":\"John Doe\"," +
    "\"firstName\":\"John\"," +
    "\"lastName\":\"Doe\"," +
    "\"age\":50," +
    "\"female\":false," +
    "\"city\":\"Houston\"," +
    "\"ssn\":\"123-45-6789\"" +
    "}}," +
    "{" +
    "\"person\":" +
    "{" +
    "\"name\":\"Jane Doe\"," +
    "\"firstName\":\"Jane\"," +
    "\"lastName\":\"Doe\"," +
    "\"age\":49," +
    "\"female\":true," +
    "\"city\":\"Houston\"," +
    "\"ssn\":\"123-45-6789\"" +
    "}}" +
    "]" +
    "}";

  String nestedArray = "[\n" +
    "{\n" +
    "\"annotations\": [\n" +
    "{\n" +
    "\"name\": \"begin\",\n" +
    "\"value\": 1570476797161000\n" +
    "},\n" +
    "{\n" +
    "\"name\": \"end\",\n" +
    "\"value\": 1570476797161001\n" +
    "}\n" +
    "]\n" +
    "}\n" +
    "]";

  // values w/ no spaces to simplify test
  String[] objects = new String[]{
    "{ \"name\":\"John\", \"age\":50, \"car\":null }",
    "{\n\"person\":{ \"name\":\"John\", \"age\":50, \"city\":\"Houston\" }\n}",
    "{ \"age\":50 }",
    "{ \"sale\":true }"
  };

  String[] arrays = new String[]{
    "[ \"Ford\", \"BMW\", \"Fiat\" ]",
    "[ [ \"Ford\", \"BMW\", \"Fiat\" ], [ \"Ford\", \"BMW\", \"Fiat\" ], [ \"Ford\", \"BMW\", \"Fiat\" ] ]"
  };
  }