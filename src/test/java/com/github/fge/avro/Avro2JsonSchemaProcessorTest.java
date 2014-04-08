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
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.DevNullProcessingReport;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.JsonTree;
import com.github.fge.jsonschema.core.tree.SimpleJsonTree;
import com.github.fge.jsonschema.core.util.ValueHolder;
import org.apache.avro.SchemaParseException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

public final class Avro2JsonSchemaProcessorTest
{
    private JsonNode schema;

    @BeforeClass
    public void load()
        throws IOException
    {
        schema = JsonLoader.fromResource("/illegal.json");
    }

    @Test
    public void illegalSchemasAreReportedAsSuch()
        throws ProcessingException
    {
        final JsonTree tree = new SimpleJsonTree(schema);
        final ValueHolder<JsonTree> input = ValueHolder.hold(tree);
        final ProcessingReport report = new DevNullProcessingReport();

        try {
            new Avro2JsonSchemaProcessor().process(report, input);
            fail("No exception thrown!!");
        } catch (IllegalAvroSchemaException e) {
            final ProcessingMessage message = e.getProcessingMessage();
            final JsonNode node = message.asJson();
            assertEquals(node.get("exceptionClass").textValue(),
                SchemaParseException.class.getCanonicalName());
        }
    }
}
