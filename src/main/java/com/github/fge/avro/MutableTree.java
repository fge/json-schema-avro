/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.avro;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.util.AsJson;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public final class MutableTree
    implements AsJson
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

    private final ObjectNode baseNode = FACTORY.objectNode();

    private JsonPointer pointer = JsonPointer.empty();

    private ObjectNode currentNode = baseNode;

    public void setType(final NodeType type)
    {
        currentNode.put("type", type.toString());
    }

    public ObjectNode getBaseNode()
    {
        return baseNode;
    }

    public ObjectNode getCurrentNode()
    {
        return currentNode;
    }

    public JsonPointer getPointer()
    {
        return pointer;
    }

    public void setPointer(final JsonPointer pointer)
    {
        this.pointer = pointer;
        currentNode = (ObjectNode) pointer.get(baseNode);
    }

    public boolean hasDefinition(final String name)
    {
        boolean ret = true;

        if (!baseNode.has("definitions")) {
            ret = false;
            baseNode.put("definitions", FACTORY.objectNode());
        }

        final ObjectNode definitions = (ObjectNode) baseNode.get("definitions");

        if (!definitions.has(name)) {
            ret = false;
            definitions.put(name, FACTORY.objectNode());
        }

        return ret;
    }

    @Override
    public JsonNode asJson()
    {
        return FACTORY.objectNode().put("pointer", pointer.toString());
    }
}

