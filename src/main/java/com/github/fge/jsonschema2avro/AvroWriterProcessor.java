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

package com.github.fge.jsonschema2avro;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.processing.ProcessorSelector;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.util.ValueHolder;
import com.github.fge.jsonschema2avro.writers.*;
import org.apache.avro.Schema;

import static com.github.fge.jsonschema2avro.predicates.AvroPredicates.*;

public final class AvroWriterProcessor
    implements Processor<ValueHolder<SchemaTree>, ValueHolder<Schema>>
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
            .when(typeRef()).then(RefWriter.getInstance())
            .getProcessor();
    }

    @Override
    public ValueHolder<Schema> process(final ProcessingReport report,
        final ValueHolder<SchemaTree> input)
        throws ProcessingException
    {
        final AvroPayload payload = new AvroPayload(input.getValue(), this);
        return processor.process(report, payload);
    }
}
