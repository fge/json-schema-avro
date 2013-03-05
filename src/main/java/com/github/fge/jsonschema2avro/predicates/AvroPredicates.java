package com.github.fge.jsonschema2avro.predicates;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.library.DraftV4Library;
import com.github.fge.jsonschema.processors.validation.ArraySchemaDigester;
import com.github.fge.jsonschema.processors.validation.ObjectSchemaDigester;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema2avro.AvroPayload;
import com.google.common.base.CharMatcher;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Set;

public final class AvroPredicates
{
    private static final Set<String> KNOWN_KEYWORDS;
    private static final CharMatcher NAME_CHAR;
    private static final CharMatcher DIGIT;

    static {
        final CharMatcher letterOrUnderscore = CharMatcher.is('_')
            .or(CharMatcher.inRange('a', 'z'))
            .or(CharMatcher.inRange('A', 'Z'));
        final CharMatcher digit = CharMatcher.inRange('0', '9');

        NAME_CHAR = letterOrUnderscore.or(digit).precomputed();
        DIGIT = digit.precomputed();

        /*
         * We don't care about keywords having only syntax checkers, only about
         * keywords having an actual use in validation
         */
        KNOWN_KEYWORDS = ImmutableSet.copyOf(DraftV4Library.get()
            .getDigesters().entries().keySet());
    }

    private AvroPredicates()
    {
    }

    public static Predicate<AvroPayload> simpleType()
    {
        return new Predicate<AvroPayload>()
        {
            @Override
            public boolean apply(final AvroPayload input)
            {
                final JsonNode node = schemaNode(input);
                final NodeType type = getType(node);
                if (type == null)
                    return false;
                return type != NodeType.ARRAY && type != NodeType.OBJECT;
            }
        };
    }

    public static Predicate<AvroPayload> array()
    {
        return new Predicate<AvroPayload>()
        {
            @Override
            public boolean apply(final AvroPayload input)
            {
                final JsonNode node = schemaNode(input);
                final NodeType type = getType(node);
                if (NodeType.ARRAY != type)
                    return false;

                final JsonNode digest
                    = ArraySchemaDigester.getInstance().digest(node);

                // FIXME: I should probably make digests POJOs here
                return digest.get("hasItems").booleanValue()
                    ? !digest.get("itemsIsArray").booleanValue()
                    : digest.get("hasAdditional").booleanValue();
            }
        };
    }

    public static Predicate<AvroPayload> map()
    {
        return new Predicate<AvroPayload>()
        {
            @Override
            public boolean apply(final AvroPayload input)
            {
                final JsonNode node = schemaNode(input);
                final NodeType type = getType(node);
                if (NodeType.OBJECT != type)
                    return false;

                final JsonNode digest = ObjectSchemaDigester.getInstance()
                    .digest(node);

                // FIXME: as for array digester, the result should really be
                // a POJO
                if (!digest.get("hasAdditional").booleanValue())
                    return false;

                return digest.get("properties").size() == 0
                    && digest.get("patternProperties").size() == 0;
            }
        };
    }

    public static Predicate<AvroPayload> isEnum()
    {
        return new Predicate<AvroPayload>()
        {
            @Override
            public boolean apply(final AvroPayload input)
            {
                final JsonNode node = schemaNode(input);
                final Set<String> set = Sets.newHashSet(node.fieldNames());
                set.retainAll(KNOWN_KEYWORDS);
                if (!set.equals(ImmutableSet.of("enum")))
                    return false;

                // Test individual entries: they must be strings, and must be
                // the same "shape" as any Avro name
                for (final JsonNode element: node.get("enum")) {
                    if (!element.isTextual())
                        return false;
                    if (!isValidAvroName(element.textValue()))
                        return false;
                }

                return true;
            }
        };
    }

    public static Predicate<AvroPayload> record()
    {
        return new Predicate<AvroPayload>()
        {
            @Override
            public boolean apply(final AvroPayload input)
            {
                final JsonNode node = schemaNode(input);
                final NodeType type = getType(node);
                if (NodeType.OBJECT != type)
                    return false;

                if (node.path("additionalProperties").asBoolean(true))
                    return false;

                if (node.has("patternProperties"))
                    return false;

                final JsonNode properties = node.path("properties");
                if (!properties.isObject())
                    return false;

                for (final String s: Sets.newHashSet(properties.fieldNames()))
                    if (!isValidAvroName(s))
                        return false;

                return true;
            }
        };
    }

    public static Predicate<AvroPayload> simpleUnion()
    {
        return new Predicate<AvroPayload>()
        {
            @Override
            public boolean apply(final AvroPayload input)
            {
                // NOTE: enums within enums are forbidden. This is tested in
                // writers, not here.
                final JsonNode node = schemaNode(input);
                final Set<String> members = Sets.newHashSet(node.fieldNames());
                members.retainAll(KNOWN_KEYWORDS);
                return members.equals(ImmutableSet.of("anyOf"))
                    || members.equals(ImmutableSet.of("oneOf"));
            }
        };
    }

    public static Predicate<AvroPayload> typeUnion()
    {
        return new Predicate<AvroPayload>()
        {
            @Override
            public boolean apply(final AvroPayload input)
            {
                return schemaNode(input).path("type").isArray();
            }
        };
    }

    private static JsonNode schemaNode(final AvroPayload payload)
    {
        return payload.getTree().getNode();
    }

    private static NodeType getType(final JsonNode node)
    {
        final JsonNode typeNode = node.path("type");
        return typeNode.isTextual() ? NodeType.fromName(typeNode.textValue())
            : null;
    }

    private static boolean isValidAvroName(final String s)
    {
        if (s.isEmpty())
            return false;
        if (!NAME_CHAR.matchesAllOf(s))
            return false;
        return !DIGIT.matches(s.charAt(0));
    }
}
