package com.github.fge.jsonschema2avro.writers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.processors.data.SchemaHolder;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.SchemaTree;
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

        for (final SchemaHolder holder: expand(node))
            schemas.add(writer.process(report, holder).getValue());

        return Schema.createUnion(schemas);
    }

    private static List<SchemaHolder> expand(final JsonNode node)
    {
        final ObjectNode common = node.deepCopy();
        final ArrayNode typeNode = (ArrayNode) common.remove("type");

        final List<SchemaHolder> ret = Lists.newArrayList();

        ObjectNode schema;
        SchemaTree tree;

        for (final JsonNode element: typeNode) {
            schema = common.deepCopy();
            schema.put("type", element);
            tree = new CanonicalSchemaTree(schema);
            ret.add(new SchemaHolder(tree));
        }

        return ret;
    }
}
