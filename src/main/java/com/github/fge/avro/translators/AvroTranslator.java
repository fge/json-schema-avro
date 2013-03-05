package com.github.fge.avro.translators;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.fge.avro.MutableTree;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.JacksonUtils;
import org.apache.avro.Schema;

public abstract class AvroTranslator
{
    protected static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

    public abstract void translate(final Schema avroSchema,
        final MutableTree jsonSchema, final ProcessingReport report)
        throws ProcessingException;

    protected static final <T> ProcessingMessage newMsg(final MutableTree tree,
        final T message)
    {
        return new ProcessingMessage().message(message).put("tree", tree);
    }
}
