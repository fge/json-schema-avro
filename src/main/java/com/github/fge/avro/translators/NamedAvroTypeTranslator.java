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

import com.github.fge.avro.MutableTree;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.report.ProcessingReport;
import org.apache.avro.Schema;

import java.net.URI;
import java.net.URISyntaxException;

abstract class NamedAvroTypeTranslator
    extends AvroTranslator
{
    protected final String typeName;

    protected NamedAvroTypeTranslator(final Schema.Type type)
    {
        typeName = type.toString().toLowerCase();
    }

    @Override
    public final void translate(final Schema avroSchema,
        final MutableTree jsonSchema, final ProcessingReport report)
        throws ProcessingException
    {
        final JsonPointer pwd = jsonSchema.getPointer();
        final String avroName = avroSchema.getFullName();
        final String fullName = typeName + ':' + avroName;
        final JsonPointer ptr = JsonPointer.of("definitions", fullName);
        if (!jsonSchema.hasDefinition(fullName)) {
            jsonSchema.setPointer(ptr);
            doTranslate(avroSchema, jsonSchema, report);
            jsonSchema.setPointer(pwd);
        }
        jsonSchema.getCurrentNode().put("$ref", createRef(ptr));
    }

    protected abstract void doTranslate(final Schema avroSchema,
        final MutableTree jsonSchema, final ProcessingReport report)
        throws ProcessingException;

    private static String createRef(final JsonPointer pointer)
    {
        try {
            return new URI(null, null, pointer.toString()).toString();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("How did I get there??", e);
        }
    }
}
