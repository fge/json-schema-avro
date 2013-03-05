package com.github.fge.jsonschema2avro.writers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.NodeType;
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
