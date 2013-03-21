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
        assertEquals(output.getValue().getBaseNode(), jsonSchema);

        assertTrue(VALIDATOR.schemaIsValid(jsonSchema));
    }
}
