package com.github.fge.avro;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.avro.translators.AvroTranslators;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.JsonTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.ValueHolder;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;

public final class Avro2JsonSchemaProcessor
    implements Processor<ValueHolder<JsonTree>, ValueHolder<SchemaTree>>
{
    @Override
    public ValueHolder<SchemaTree> process(final ProcessingReport report,
        final ValueHolder<JsonTree> input)
        throws ProcessingException
    {
        final JsonNode node = input.getValue().getBaseNode();

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

        final SchemaTree schemaTree
            = new CanonicalSchemaTree(tree.getBaseNode());
        return ValueHolder.hold("schema", schemaTree);
    }
}
