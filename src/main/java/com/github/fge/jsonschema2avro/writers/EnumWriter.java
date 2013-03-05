package com.github.fge.jsonschema2avro.writers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema2avro.AvroWriterProcessor;
import com.google.common.collect.Lists;
import org.apache.avro.Schema;

import java.util.List;

public final class EnumWriter
    extends NamedTypeWriter
{
    public EnumWriter()
    {
        super("enum");
    }

    @Override
    protected Schema generate(final AvroWriterProcessor writer,
        final ProcessingReport report, final SchemaTree tree)
        throws ProcessingException
    {
        final JsonNode enumNode = tree.getNode().get("enum");
        final List<String> values = Lists.newArrayList();
        for (final JsonNode element: enumNode)
            values.add(element.textValue());

        return Schema.createEnum(getName(), null, null, values);
    }
}
