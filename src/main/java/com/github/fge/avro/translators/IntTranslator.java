package com.github.fge.avro.translators;

import com.github.fge.avro.MutableTree;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.NodeType;
import org.apache.avro.Schema;

final class IntTranslator
    extends AvroTranslator
{
    private static final AvroTranslator INSTANCE = new IntTranslator();

    private IntTranslator()
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
        jsonSchema.getCurrentNode().put("minimum", Integer.MIN_VALUE)
            .put("maximum", Integer.MAX_VALUE);
    }
}
