package com.github.fge.jsonschema2avro;

import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.tree.SchemaTree;

public final class AvroPayload
    implements MessageProvider
{
    private final SchemaTree tree;
    private final AvroWriterProcessor writer;

    public AvroPayload(final SchemaTree tree, final AvroWriterProcessor writer)
    {
        this.tree = tree;
        this.writer = writer;
    }

    public SchemaTree getTree()
    {
        return tree;
    }

    public AvroWriterProcessor getWriter()
    {
        return writer;
    }

    @Override
    public ProcessingMessage newMessage()
    {
        return new ProcessingMessage().put("schema", tree);
    }
}
