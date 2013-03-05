package com.github.fge.jsonschema2avro.writers;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class NamedTypeWriter
    extends AvroWriter
{
    protected static final String NAMESPACE
        = "com.github.fge.jsonschema2avro";

    private final String typeName;
    private final AtomicInteger nameIndex = new AtomicInteger(0);

    protected NamedTypeWriter(final String typeName)
    {
        this.typeName = typeName;
    }

    protected final String getName()
    {
        return typeName + nameIndex.getAndIncrement();
    }
}
