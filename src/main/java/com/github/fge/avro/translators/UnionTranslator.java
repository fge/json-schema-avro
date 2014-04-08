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

package com.github.fge.avro.translators;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.fge.avro.MutableTree;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.report.ProcessingReport;
import org.apache.avro.Schema;

import java.util.List;

final class UnionTranslator
    extends AvroTranslator
{
    private static final AvroTranslator INSTANCE = new UnionTranslator();

    private UnionTranslator()
    {
    }

    public static AvroTranslator getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void translate(final Schema avroSchema, final MutableTree jsonSchema,
        final ProcessingReport report)
        throws ProcessingException
    {
        final JsonPointer pwd = jsonSchema.getPointer();
        final ArrayNode schemas = FACTORY.arrayNode();
        jsonSchema.getCurrentNode().put("oneOf", schemas);

        Schema schema;
        Schema.Type type;
        AvroTranslator translator;
        JsonPointer ptr;

        final List<Schema> types = avroSchema.getTypes();
        final int size = types.size();

        for (int index = 0; index < size; index++) {
            schema = types.get(index);
            type = schema.getType();
            translator = AvroTranslators.getTranslator(type);
            ptr = JsonPointer.of("oneOf", index);
            schemas.add(FACTORY.objectNode());
            jsonSchema.setPointer(pwd.append(ptr));
            translator.translate(schema, jsonSchema, report);
            jsonSchema.setPointer(pwd);
        }
    }
}
