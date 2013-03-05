package com.github.fge.avro;

import com.github.fge.jsonschema.exceptions.ProcessingException;

public final class IllegalAvroSchemaException
    extends ProcessingException
{
    private static final String ILLEGAL = "illegal Avro schema";

    public IllegalAvroSchemaException(final Throwable e)
    {
        super(ILLEGAL, e);
    }
}
