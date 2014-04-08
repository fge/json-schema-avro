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

import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.util.ValueHolder;
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
