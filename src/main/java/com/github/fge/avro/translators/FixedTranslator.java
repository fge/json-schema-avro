package com.github.fge.avro.translators;

import com.github.fge.avro.MutableTree;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.NodeType;
import org.apache.avro.Schema;

final class FixedTranslator
    extends NamedAvroTypeTranslator
{
    private static final String BYTES_PATTERN = "^[\u0000-\u00ff]*$";

    private static final AvroTranslator INSTANCE = new FixedTranslator();

    private FixedTranslator()
    {
        super(Schema.Type.FIXED);
    }

    public static AvroTranslator getInstance()
    {
        return INSTANCE;
    }

    @Override
    protected void doTranslate(final Schema avroSchema,
        final MutableTree jsonSchema, final ProcessingReport report)
    {
        final int size = avroSchema.getFixedSize();
        jsonSchema.setType(NodeType.STRING);
        jsonSchema.getCurrentNode()
            .put("pattern", BYTES_PATTERN)
            .put("minLength", size)
            .put("maxLength", size);
    }
}
