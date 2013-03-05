package com.github.fge.avro.translators;

import com.github.fge.avro.MutableTree;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.NodeType;
import org.apache.avro.Schema;

final class SimpleTypeTranslator
    extends AvroTranslator
{
    private final NodeType type ;

    SimpleTypeTranslator(final NodeType type)
    {
        this.type = type;
    }

    @Override
    public void translate(final Schema avroSchema, final MutableTree jsonSchema,
        final ProcessingReport report)
        throws ProcessingException
    {
        jsonSchema.setType(type);
    }
}
