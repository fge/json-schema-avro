package com.github.fge.avro.translators;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.fge.avro.MutableTree;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.report.ProcessingReport;
import org.apache.avro.Schema;

import java.util.List;

final class UnionTranslator
    extends AvroTranslator
{
    private static final AvroTranslator INSTANCE = new UnionTranslator();

    private UnionTranslator()
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
        final JsonPointer pwd = jsonSchema.getPointer();
        final ArrayNode schemas = FACTORY.arrayNode();
        jsonSchema.getCurrentNode().put("oneOf", schemas);

        Schema schema;
        Schema.Type type;
        AvroTranslator translator;
        JsonPointer ptr;

        final List<Schema> types = avroSchema.getTypes();
        final int size = types.size();

        for (int index = 0; index < size; index++) {
            schema = types.get(index);
            type = schema.getType();
            translator = AvroTranslators.getTranslator(type);
            ptr = JsonPointer.of("oneOf", index);
            schemas.add(FACTORY.objectNode());
            jsonSchema.setPointer(pwd.append(ptr));
            translator.translate(schema, jsonSchema, report);
            jsonSchema.setPointer(pwd);
        }
    }
}
