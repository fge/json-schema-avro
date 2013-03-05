package com.github.fge.jsonschema2avro;

import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ProcessorSelector;
import com.github.fge.jsonschema.processors.data.SchemaHolder;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.ValueHolder;
import com.github.fge.jsonschema2avro.writers.ArrayWriter;
import com.github.fge.jsonschema2avro.writers.EnumWriter;
import com.github.fge.jsonschema2avro.writers.MapWriter;
import com.github.fge.jsonschema2avro.writers.RecordWriter;
import com.github.fge.jsonschema2avro.writers.SimpleTypeWriter;
import com.github.fge.jsonschema2avro.writers.SimpleUnionWriter;
import com.github.fge.jsonschema2avro.writers.TypeUnionWriter;
import org.apache.avro.Schema;

import static com.github.fge.jsonschema2avro.predicates.AvroPredicates.*;

public final class AvroWriterProcessor
    implements Processor<SchemaHolder, ValueHolder<Schema>>
{
    private final Processor<AvroPayload, ValueHolder<Schema>> processor;

    public AvroWriterProcessor()
    {
        processor = new ProcessorSelector<AvroPayload, ValueHolder<Schema>>()
            .when(simpleType()).then(SimpleTypeWriter.getInstance())
            .when(array()).then(ArrayWriter.getInstance())
            .when(map()).then(MapWriter.getInstance())
            .when(isEnum()).then(new EnumWriter())
            .when(record()).then(new RecordWriter())
            .when(simpleUnion()).then(SimpleUnionWriter.getInstance())
            .when(typeUnion()).then(TypeUnionWriter.getInstance())
            .getProcessor();
    }

    @Override
    public ValueHolder<Schema> process(final ProcessingReport report,
        final SchemaHolder input)
        throws ProcessingException
    {
        final AvroPayload payload = new AvroPayload(input.getValue(), this);
        return processor.process(report, payload);
    }
}
