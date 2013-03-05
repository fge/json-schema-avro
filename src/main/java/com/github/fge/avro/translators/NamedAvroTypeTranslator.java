package com.github.fge.avro.translators;

import com.github.fge.avro.MutableTree;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.exceptions.unchecked.ProcessingError;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import org.apache.avro.Schema;

import java.net.URI;
import java.net.URISyntaxException;

abstract class NamedAvroTypeTranslator
    extends AvroTranslator
{
    protected final String typeName;

    protected NamedAvroTypeTranslator(final Schema.Type type)
    {
        typeName = type.toString().toLowerCase();
    }

    @Override
    public final void translate(final Schema avroSchema,
        final MutableTree jsonSchema, final ProcessingReport report)
        throws ProcessingException
    {
        final JsonPointer pwd = jsonSchema.getPointer();
        final String avroName = avroSchema.getFullName();
        final String fullName = typeName + ':' + avroName;
        final JsonPointer ptr = JsonPointer.of("definitions", fullName);
        if (!jsonSchema.hasDefinition(fullName)) {
            jsonSchema.setPointer(ptr);
            doTranslate(avroSchema, jsonSchema, report);
            jsonSchema.setPointer(pwd);
        }
        jsonSchema.getCurrentNode().put("$ref", createRef(ptr));
    }

    protected abstract void doTranslate(final Schema avroSchema,
        final MutableTree jsonSchema, final ProcessingReport report)
        throws ProcessingException;

    private static String createRef(final JsonPointer pointer)
    {
        try {
            return new URI(null, null, pointer.toString()).toString();
        } catch (URISyntaxException ignored) {
            throw new ProcessingError(new ProcessingMessage()
                .message("How on earth did I get there???"));
        }
    }
}
