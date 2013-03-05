package com.github.fge.avro.translators;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.avro.MutableTree;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.JsonLoader;
import com.github.fge.jsonschema.util.NodeType;
import org.apache.avro.Schema;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;

final class RecordTranslator
    extends NamedAvroTypeTranslator
{
    private static final ObjectMapper OLD_MAPPER = new ObjectMapper();

    private static final AvroTranslator INSTANCE = new RecordTranslator();

    private RecordTranslator()
    {
        super(Schema.Type.RECORD);
    }

    public static AvroTranslator getInstance()
    {
        return INSTANCE;
    }

    @Override
    protected void doTranslate(final Schema avroSchema,
        final MutableTree jsonSchema, final ProcessingReport report)
        throws ProcessingException
    {
        final List<Schema.Field> fields = avroSchema.getFields();

        if (fields.isEmpty()) {
            final ArrayNode node = FACTORY.arrayNode();
            node.add(FACTORY.objectNode());
            jsonSchema.getCurrentNode().put("enum", node);
            return;
        }

        final JsonPointer pwd = jsonSchema.getPointer();

        if (avroSchema.getDoc() != null)
            jsonSchema.getCurrentNode().put("description", avroSchema.getDoc());

        jsonSchema.setType(NodeType.OBJECT);

        final ArrayNode required = FACTORY.arrayNode();
        jsonSchema.getCurrentNode().put("required", required);

        jsonSchema.getCurrentNode().put("additionalProperties", false);

        final ObjectNode properties = FACTORY.objectNode();
        jsonSchema.getCurrentNode().put("properties", properties);

        String fieldName;
        Schema fieldSchema;
        Schema.Type fieldType;
        AvroTranslator translator;
        JsonPointer ptr;
        ObjectNode propertyNode;
        String s;

        /*
         * FIXME: "default" and readers'/writers' schema? Here, even with a
         * default value, the record field is marked as required.
         */
        for (final Schema.Field field: fields) {
            fieldName = field.name();
            fieldSchema = field.schema();
            fieldType = fieldSchema.getType();
            translator = AvroTranslators.getTranslator(fieldType);
            required.add(fieldName);
            ptr = JsonPointer.of("properties", fieldName);
            propertyNode = FACTORY.objectNode();
            properties.put(fieldName, propertyNode);
            injectDefault(propertyNode, field);
            jsonSchema.setPointer(pwd.append(ptr));
            translator.translate(fieldSchema, jsonSchema, report);
            jsonSchema.setPointer(pwd);
        }
    }

    private static void injectDefault(final ObjectNode propertyNode,
        final Schema.Field field)
    {
        final JsonNode value = field.defaultValue();
        if (value == null)
            return;

        /*
         * Write the value to a string using a 1.8 writer, and read it from that
         * string using a 2.1 reader... Did you say "hack"?
         */
        try {
            final String s = OLD_MAPPER.writeValueAsString(value);
            propertyNode.put("default", JsonLoader.fromString(s));
        } catch (IOException ignored) {
            // cannot happen
        }
    }
}
