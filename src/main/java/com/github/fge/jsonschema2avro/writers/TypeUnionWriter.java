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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.ValueHolder;
import com.github.fge.jsonschema2avro.AvroWriterProcessor;
import com.google.common.collect.Lists;
import org.apache.avro.Schema;

import java.util.List;

public final class TypeUnionWriter
    extends AvroWriter
{
    private static final AvroWriter INSTANCE = new TypeUnionWriter();

    private TypeUnionWriter()
    {
    }

    public static AvroWriter getInstance()
    {
        return INSTANCE;
    }

    @Override
    protected Schema generate(final AvroWriterProcessor writer,
        final ProcessingReport report, final SchemaTree tree)
        throws ProcessingException
    {
        // In such a union, there cannot be embedded unions so we need not care
        // here
        final JsonNode node = tree.getNode();
        final List<Schema> schemas = Lists.newArrayList();

        for (final ValueHolder<SchemaTree> holder: expand(node))
            schemas.add(writer.process(report, holder).getValue());

        return Schema.createUnion(schemas);
    }

    private static List<ValueHolder<SchemaTree>> expand(final JsonNode node)
    {
        final ObjectNode common = node.deepCopy();
        final ArrayNode typeNode = (ArrayNode) common.remove("type");

        final List<ValueHolder<SchemaTree>> ret = Lists.newArrayList();

        ObjectNode schema;
        SchemaTree tree;

        for (final JsonNode element: typeNode) {
            schema = common.deepCopy();
            schema.put("type", element);
            tree = new CanonicalSchemaTree(schema);
            ret.add(ValueHolder.hold("schema", tree));
        }

        return ret;
    }
}
