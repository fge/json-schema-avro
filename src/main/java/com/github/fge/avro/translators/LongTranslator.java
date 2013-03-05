package com.github.fge.avro.translators;

import com.github.fge.avro.MutableTree;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.NodeType;
import org.apache.avro.Schema;

final class LongTranslator
    extends AvroTranslator
{
    private static final AvroTranslator INSTANCE = new LongTranslator();

    private LongTranslator()
    {
    }

    static AvroTranslator getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void translate(final Schema avroSchema, final MutableTree jsonSchema,
        final ProcessingReport report)
        throws ProcessingException
    {
        jsonSchema.setType(NodeType.INTEGER);
        jsonSchema.getCurrentNode().put("minimum", Long.MIN_VALUE)
            .put("maximum", Long.MAX_VALUE);
    }
}
