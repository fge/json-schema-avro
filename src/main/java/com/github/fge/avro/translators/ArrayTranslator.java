package com.github.fge.avro.translators;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.avro.MutableTree;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.NodeType;
import org.apache.avro.Schema;

final class ArrayTranslator
    extends AvroTranslator
{
    private static final AvroTranslator INSTANCE = new ArrayTranslator();

    private ArrayTranslator()
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
        jsonSchema.setType(NodeType.ARRAY);

        final JsonPointer pwd = jsonSchema.getPointer();
        final ObjectNode subSchema = FACTORY.objectNode();
        final Schema valuesSchema = avroSchema.getElementType();

        jsonSchema.getCurrentNode().put("items", subSchema);

        jsonSchema.setPointer(pwd.append("items"));
        AvroTranslators.getTranslator(valuesSchema.getType())
            .translate(valuesSchema, jsonSchema, report);
        jsonSchema.setPointer(pwd);
    }
}
