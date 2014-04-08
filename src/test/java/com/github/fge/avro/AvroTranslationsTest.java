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
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.processors.syntax.SyntaxValidator;
import com.github.fge.jsonschema.report.DevNullProcessingReport;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.JsonTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.tree.SimpleJsonTree;
import com.github.fge.jsonschema.util.ValueHolder;
import com.google.common.collect.Lists;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

public abstract class AvroTranslationsTest
{
    private static final Avro2JsonSchemaProcessor PROCESSOR
        = new Avro2JsonSchemaProcessor();

    private static final SyntaxValidator VALIDATOR
        = new SyntaxValidator(ValidationConfiguration.byDefault());

    private final JsonNode testNode;

    private ProcessingReport report;

    protected AvroTranslationsTest(final String name)
        throws IOException
    {
        testNode = JsonLoader.fromResource("/avro/" + name + ".json");
    }

    @BeforeMethod
    public final void init()
    {
        report = new DevNullProcessingReport();
    }

    @DataProvider
    public final Iterator<Object[]> testData()
    {
        final List<Object[]> list = Lists.newArrayList();

        for (final JsonNode element: testNode)
            list.add(new Object[] {
                element.get("avroSchema"),
                element.get("jsonSchema")
            });

        return list.iterator();
    }

    @Test(
        dataProvider = "testData",
        invocationCount = 10,
        threadPoolSize = 4
    )
    public final void conversionIsCorrectlyPerformed(final JsonNode avroSchema,
        final JsonNode jsonSchema)
        throws ProcessingException
    {
        final ValueHolder<JsonTree> input
            = ValueHolder.<JsonTree>hold(new SimpleJsonTree(avroSchema));

        final ValueHolder<SchemaTree> output = PROCESSOR.process(report, input);
        assertTrue(output.getValue().getBaseNode().equals(jsonSchema));

        assertTrue(VALIDATOR.schemaIsValid(jsonSchema));
    }
}
