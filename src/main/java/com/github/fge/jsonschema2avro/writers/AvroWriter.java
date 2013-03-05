package com.github.fge.jsonschema2avro.writers;

import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.ValueHolder;
import com.github.fge.jsonschema2avro.AvroPayload;
import com.github.fge.jsonschema2avro.AvroWriterProcessor;
import org.apache.avro.Schema;

public abstract class AvroWriter
    implements Processor<AvroPayload, ValueHolder<Schema>>
{
    @Override
    public final ValueHolder<Schema> process(final ProcessingReport report,
        final AvroPayload input)
        throws ProcessingException
    {
        final AvroWriterProcessor writer = input.getWriter();
        final SchemaTree tree = input.getTree();
        final Schema schema = generate(writer, report, tree);
        return ValueHolder.hold(schema);
    }

    protected abstract Schema generate(final AvroWriterProcessor writer,
        final ProcessingReport report, final SchemaTree tree)
        throws ProcessingException;
}
