package com.github.fge.avro.translators;

import com.github.fge.avro.MutableTree;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.NodeType;
import org.apache.avro.Schema;

final class ByteTranslator
    extends AvroTranslator
{
    private static final String BYTES_PATTERN = "^[\u0000-\u00ff]*$";

    private static final AvroTranslator INSTANCE = new ByteTranslator();

    private ByteTranslator()
    {
    }

    public static AvroTranslator getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void translate(final Schema avroSchema, final MutableTree jsonSchema,
        final ProcessingReport report)
        throws ProcessingException
    {
        jsonSchema.setType(NodeType.STRING);
        jsonSchema.getCurrentNode().put("pattern", BYTES_PATTERN);
    }
}
