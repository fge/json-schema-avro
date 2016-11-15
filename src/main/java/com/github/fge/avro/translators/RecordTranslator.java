/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.avro.translators;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.avro.MutableTree;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import org.apache.avro.Schema;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

final class RecordTranslator
        extends NamedAvroTypeTranslator {
    private static final ObjectMapper OLD_MAPPER = new ObjectMapper();

    private static final AvroTranslator INSTANCE = new RecordTranslator();

    private RecordTranslator() {
        super(Schema.Type.RECORD);
    }

    public static AvroTranslator getInstance() {
        return INSTANCE;
    }

    @Override
    protected void doTranslate(final Schema avroSchema,
                               final MutableTree jsonSchema, final ProcessingReport report)
            throws ProcessingException {
        final List<Schema.Field> fields = avroSchema.getFields();

        if (fields.isEmpty()) {
            final ArrayNode node = FACTORY.arrayNode();
            node.add(FACTORY.objectNode());
            jsonSchema.getCurrentNode().put("enum", node);
            return;
        }

        final JsonPointer pwd = jsonSchema.getPointer();

        if (avroSchema.getDoc() != null) {
            jsonSchema.getCurrentNode().put("description", avroSchema.getDoc());
        }

        jsonSchema.setType(NodeType.OBJECT);

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
        final List<String> requiredFields = new LinkedList<String>();
        for (final Schema.Field field : fields) {
            fieldName = field.name();
            fieldSchema = field.schema();
            fieldType = fieldSchema.getType();
            translator = AvroTranslators.getTranslator(fieldType);
            if (field.defaultValue() == null) {
                requiredFields.add(fieldName);
            }
            ptr = JsonPointer.of("properties", fieldName);
            propertyNode = FACTORY.objectNode();
            properties.put(fieldName, propertyNode);
            injectDefault(propertyNode, field);
            jsonSchema.setPointer(pwd.append(ptr));
            translator.translate(fieldSchema, jsonSchema, report);
            jsonSchema.setPointer(pwd);
        }

        if (requiredFields.size() > 0) {
            final ArrayNode required = FACTORY.arrayNode();
            for (String requiredFieldName : requiredFields) {
                required.add(requiredFieldName);
            }
            jsonSchema.getCurrentNode().put("required", required);
        }
    }

    private static void injectDefault(final ObjectNode propertyNode,
                                      final Schema.Field field) {
        final JsonNode value = field.defaultValue();
        if (value == null) {
            return;
        }

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
