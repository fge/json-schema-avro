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

package com.github.fge.avro;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.avro.translators.AvroTranslators;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.processing.RawProcessor;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.core.tree.JsonTree;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;

public final class Avro2JsonSchemaProcessor
    extends RawProcessor<JsonTree, SchemaTree>
{
    public Avro2JsonSchemaProcessor()
    {
        super("avroSchema", "schema");
    }

    @Override
    public SchemaTree rawProcess(final ProcessingReport report,
        final JsonTree input)
        throws ProcessingException
    {
        final JsonNode node = input.getBaseNode();

        final Schema avroSchema;
        try {
            final String s = node.toString();
            avroSchema = new Schema.Parser().parse(s);
        } catch (AvroRuntimeException e) {
            /*
             * There is a SchemaParseException, but it does not cover all cases.
             *
             * This schema, for instance, throws a AvroRuntimeException:
             *
             * { "type": [ "null", "null" ] }
             *
             */
            throw new IllegalAvroSchemaException(e);
        }

        final MutableTree tree = new MutableTree();
        final Schema.Type avroType = avroSchema.getType();
        AvroTranslators.getTranslator(avroType)
            .translate(avroSchema, tree, report);

        return new CanonicalSchemaTree(tree.getBaseNode());
    }
}
