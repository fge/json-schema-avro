package com.github.fge.avro;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.avro.translators.AvroTranslators;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processors.data.SchemaHolder;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.JsonTree;
import com.github.fge.jsonschema.util.ValueHolder;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;

public final class Avro2JsonSchemaProcessor
    implements Processor<ValueHolder<JsonTree>, SchemaHolder>
{
    @Override
    public SchemaHolder process(final ProcessingReport report,
        final ValueHolder<JsonTree> input)
        throws ProcessingException
    {
        final JsonNode node = input.getValue().getBaseNode();

        final Schema avroSchema;
        try {
            final String s = node.toString();
            avroSchema = new Schema.Parser().parse(s);
        } catch (AvroRuntimeException e) {
            throw new IllegalAvroSchemaException(e);
        }

        final MutableTree tree = new MutableTree();
        final Schema.Type avroType = avroSchema.getType();
        AvroTranslators.getTranslator(avroType)
            .translate(avroSchema, tree, report);

        return new SchemaHolder(new CanonicalSchemaTree(tree.getBaseNode()));
    }
}
