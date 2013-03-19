package com.github.fge.jsonschema2avro.writers;

import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.ValueHolder;
import com.github.fge.jsonschema2avro.AvroWriterProcessor;
import com.google.common.collect.Lists;
import org.apache.avro.Schema;

import java.util.Collections;
import java.util.List;

public final class RecordWriter
    extends NamedTypeWriter
{
    public RecordWriter()
    {
        super("record");
    }

    @Override
    protected Schema generate(final AvroWriterProcessor writer,
        final ProcessingReport report, final SchemaTree tree)
        throws ProcessingException
    {
        final Schema schema = Schema.createRecord(getName(), null, null, false);
        final List<String> list
            = Lists.newArrayList(tree.getNode().get("properties").fieldNames());
        Collections.sort(list);

        final List<Schema.Field> fields = Lists.newArrayList();

        JsonPointer ptr;
        ValueHolder<SchemaTree> holder;
        Schema fieldSchema;

        for (final String name: list) {
            ptr = JsonPointer.of("properties", name);
            holder = ValueHolder.hold("schema", tree.append(ptr));
            fieldSchema = writer.process(report, holder).getValue();
            fields.add(new Schema.Field(name, fieldSchema, null, null));
        }

        schema.setFields(fields);
        return schema;
    }
}
