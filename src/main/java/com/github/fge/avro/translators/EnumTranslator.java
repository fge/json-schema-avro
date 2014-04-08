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
import com.github.fge.jsonschema.core.report.ProcessingReport;
import org.apache.avro.Schema;

final class EnumTranslator
    extends NamedAvroTypeTranslator
{
    private static final AvroTranslator INSTANCE = new EnumTranslator();

    private EnumTranslator()
    {
        super(Schema.Type.ENUM);
    }

    public static AvroTranslator getInstance()
    {
        return INSTANCE;
    }

    @Override
    protected void doTranslate(final Schema avroSchema,
        final MutableTree jsonSchema, final ProcessingReport report)
    {
        if (avroSchema.getDoc() != null)
            jsonSchema.getCurrentNode().put("description", avroSchema.getDoc());

        final ArrayNode enumValues = FACTORY.arrayNode();
        for (final String symbol: avroSchema.getEnumSymbols())
            enumValues.add(symbol);
        jsonSchema.getCurrentNode().put("enum", enumValues);
    }
}
