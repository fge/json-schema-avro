package com.github.fge.jsonschema2avro;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.processors.data.SchemaHolder;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.JsonLoader;
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
        final SchemaHolder input = new SchemaHolder(tree);
        final Schema expected = new Schema.Parser().parse(avro.toString());

        final Schema actual = processor.process(report, input).getValue();
        assertEquals(expected, actual);
    }
}
