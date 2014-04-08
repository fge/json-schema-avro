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

package com.github.fge.jsonschema2avro.writers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema2avro.AvroWriterProcessor;
import com.google.common.collect.ImmutableMap;
import org.apache.avro.Schema;

import java.util.Map;

public final class SimpleTypeWriter
    extends AvroWriter
{
    private static final Map<NodeType, Schema.Type> TYPEMAP
        = ImmutableMap.<NodeType, Schema.Type>builder()
            .put(NodeType.BOOLEAN, Schema.Type.BOOLEAN)
            .put(NodeType.NULL, Schema.Type.NULL)
            .put(NodeType.STRING, Schema.Type.STRING)
            .put(NodeType.INTEGER, Schema.Type.LONG)
            .put(NodeType.NUMBER, Schema.Type.DOUBLE)
            .build();

    private static final AvroWriter INSTANCE = new SimpleTypeWriter();

    private SimpleTypeWriter()
    {
    }

    public static AvroWriter getInstance()
    {
        return INSTANCE;
    }

    @Override
    protected Schema generate(final AvroWriterProcessor writer,
        final ProcessingReport report, final SchemaTree tree)
    {
        final JsonNode node = tree.getNode();
        final NodeType type = NodeType.fromName(node.get("type").textValue());
        return Schema.create(TYPEMAP.get(type));
    }
}
