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

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.ValueHolder;
import com.google.common.collect.Lists;
import org.apache.avro.Schema;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public abstract class AvroWriterProcessorTest
{
    private final JsonNode testNode;

    private AvroWriterProcessor processor;
    private ProcessingReport report;

    protected AvroWriterProcessorTest(final String prefix)
        throws IOException
    {
        testNode = JsonLoader.fromResource("/jsonschema2avro/" + prefix
            + ".json");
    }

    @BeforeMethod
    public final void init()
    {
        processor = new AvroWriterProcessor();
        report = mock(ProcessingReport.class);
    }

    @DataProvider
    public final Iterator<Object[]> testData()
    {
        final List<Object[]> list = Lists.newArrayList();

        for (final JsonNode element: testNode)
            list.add(new Object[] {
                element.get("jsonSchema"),
                element.get("avroSchema")
            });

        return list.iterator();
    }

    @Test(dataProvider = "testData")
    public final void JsonSchemasAreCorrectlyTranslated(final JsonNode schema,
        final JsonNode avro)
        throws ProcessingException
    {
        final SchemaTree tree = new CanonicalSchemaTree(schema);
        final ValueHolder<SchemaTree> input = ValueHolder.hold("schema", tree);
        final Schema expected = new Schema.Parser().parse(avro.toString());

        final Schema actual = processor.process(report, input).getValue();
        assertEquals(expected, actual);
    }
}
