package com.github.fge.jsonschema2avro.writers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.processors.data.SchemaHolder;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema2avro.AvroWriterProcessor;
import org.apache.avro.Schema;

public final class ArrayWriter
    extends AvroWriter
{
    private static final AvroWriter INSTANCE = new ArrayWriter();

    private ArrayWriter()
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
        final JsonNode node = tree.getNode();
        final JsonPointer ptr = node.has("items") ? JsonPointer.of("items")
            : JsonPointer.of("additionalItems");

        final SchemaTree subTree = tree.append(ptr);
        final SchemaHolder input = new SchemaHolder(subTree);
        final Schema itemsSchema = writer.process(report, input).getValue();
        return Schema.createArray(itemsSchema);
    }
}
