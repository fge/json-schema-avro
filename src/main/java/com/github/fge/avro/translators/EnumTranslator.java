package com.github.fge.avro.translators;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.fge.avro.MutableTree;
import com.github.fge.jsonschema.report.ProcessingReport;
import org.apache.avro.Schema;

final class EnumTranslator
    extends NamedAvroTypeTranslator
{
    private static final AvroTranslator INSTANCE = new EnumTranslator();

    private EnumTranslator()
    {
        super(Schema.Type.ENUM);
    }

    public static AvroTranslator getInstance()
    {
        return INSTANCE;
    }

    @Override
    protected void doTranslate(final Schema avroSchema,
        final MutableTree jsonSchema, final ProcessingReport report)
    {
        if (avroSchema.getDoc() != null)
            jsonSchema.getCurrentNode().put("description", avroSchema.getDoc());

        final ArrayNode enumValues = FACTORY.arrayNode();
        for (final String symbol: avroSchema.getEnumSymbols())
            enumValues.add(symbol);
        jsonSchema.getCurrentNode().put("enum", enumValues);
    }
}
