package com.github.fge.avro;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.avro.translators.AvroTranslators;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.processing.RawProcessor;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.JsonTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;

public final class Avro2JsonSchemaProcessor
    extends RawProcessor<JsonTree, SchemaTree>
{
    public Avro2JsonSchemaProcessor()
    {
        super("avroSchema", "schema");
    }

    @Override
    public SchemaTree rawProcess(final ProcessingReport report,
        final JsonTree input)
        throws ProcessingException
    {
        final JsonNode node = input.getBaseNode();

        final Schema avroSchema;
        try {
            final String s = node.toString();
            avroSchema = new Schema.Parser().parse(s);
        } catch (AvroRuntimeException e) {
            /*
             * There is a SchemaParseException, but it does not cover all cases.
             *
             * This schema, for instance, throws a AvroRuntimeException:
             *
             * { "type": [ "null", "null" ] }
             *
             */
            throw new IllegalAvroSchemaException(e);
        }

        final MutableTree tree = new MutableTree();
        final Schema.Type avroType = avroSchema.getType();
        AvroTranslators.getTranslator(avroType)
            .translate(avroSchema, tree, report);

        return new CanonicalSchemaTree(tree.getBaseNode());
    }
}
