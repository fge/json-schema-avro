package com.github.fge.jsonschema2avro.writers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.processors.data.SchemaHolder;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema2avro.AvroWriterProcessor;
import com.google.common.collect.Lists;
import org.apache.avro.Schema;

import java.util.List;

public final class SimpleUnionWriter
    extends AvroWriter
{
    private static final AvroWriter INSTANCE = new SimpleUnionWriter();

    private SimpleUnionWriter()
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
        final String keyword = node.has("oneOf") ? "oneOf" : "anyOf";
        final int size = node.get(keyword).size();

        JsonPointer ptr;
        SchemaHolder holder;
        Schema subSchema;

        final List<Schema> schemas = Lists.newArrayList();

        for (int index = 0; index < size; index++) {
            ptr = JsonPointer.of(keyword, index);
            holder = new SchemaHolder(tree.append(ptr));
            subSchema = writer.process(report, holder).getValue();
            if (subSchema.getType() == Schema.Type.UNION)
                throw new ProcessingException("union within union is illegal");
            schemas.add(subSchema);
        }

        return Schema.createUnion(schemas);
    }
}
