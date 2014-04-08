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

import com.github.fge.jackson.NodeType;
import com.google.common.collect.ImmutableMap;
import org.apache.avro.Schema;

import java.util.Map;

public final class AvroTranslators
{
    private static final Map<Schema.Type, AvroTranslator> TRANSLATORS;

    private AvroTranslators()
    {
    }

    static {
        final ImmutableMap.Builder<Schema.Type, AvroTranslator> builder
            = ImmutableMap.builder();

        Schema.Type avroType;
        AvroTranslator translator;

        avroType = Schema.Type.NULL;
        translator = new SimpleTypeTranslator(NodeType.NULL);
        builder.put(avroType, translator);

        avroType = Schema.Type.BOOLEAN;
        translator = new SimpleTypeTranslator(NodeType.BOOLEAN);
        builder.put(avroType, translator);

        avroType = Schema.Type.STRING;
        translator = new SimpleTypeTranslator(NodeType.STRING);
        builder.put(avroType, translator);

        // Reuse for "bytes"
        avroType = Schema.Type.BYTES;
        translator = ByteTranslator.getInstance();
        builder.put(avroType, translator);

        avroType = Schema.Type.INT;
        translator = IntTranslator.getInstance();
        builder.put(avroType, translator);

        avroType = Schema.Type.LONG;
        translator = LongTranslator.getInstance();
        builder.put(avroType, translator);

        avroType = Schema.Type.FLOAT;
        translator = new SimpleTypeTranslator(NodeType.NUMBER);
        builder.put(avroType, translator);

        // Reuse for "double"
        avroType = Schema.Type.DOUBLE;
        builder.put(avroType, translator);

        avroType = Schema.Type.MAP;
        translator = MapTranslator.getInstance();
        builder.put(avroType, translator);

        avroType = Schema.Type.FIXED;
        translator = FixedTranslator.getInstance();
        builder.put(avroType, translator);

        avroType = Schema.Type.ENUM;
        translator = EnumTranslator.getInstance();
        builder.put(avroType, translator);

        avroType = Schema.Type.ARRAY;
        translator = ArrayTranslator.getInstance();
        builder.put(avroType, translator);

        avroType = Schema.Type.UNION;
        translator = UnionTranslator.getInstance();
        builder.put(avroType, translator);

        avroType = Schema.Type.RECORD;
        translator = RecordTranslator.getInstance();
        builder.put(avroType, translator);

        TRANSLATORS = builder.build();
    }

    public static AvroTranslator getTranslator(final Schema.Type avroType)
    {
        return TRANSLATORS.get(avroType);
    }
}
