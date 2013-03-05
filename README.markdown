<h1>Read me first</h1>

<p>The license of this project is LGPLv3 or later. See file src/main/resources/LICENSE for the full
text.</p>

<h1>What this is</h1>

<p>This package contains two processors (see <a
href="https://github.com/fge/json-schema-core">json-schema-core</a>) to convert Avro schemas to JSON
Schemas, and the reverse.</p>

<h1>Status</h1>

<h2>Avro schemas to JSON Schema</h2>

<p>This processor can transform <b>all</b> Avro schemas you can think of, as long as said schemas
are self contained. The generated JSON Schemas can accurately validate JSON representations of Avro
data with two exceptions:</p>

* as JSON has no notion of order, the `order` property of Avro records is not enforced;
* Avro's `float` and `double` are validated as JSON numbers, with no minimum or maximum (note
  however that `int` and `long`'s limits _are_ enforced).

<p>Note that this processor is demoed online <a
href="http://json-schema-validator.herokuapp.com/avro.jsp">here</a>.</p>

<h2>JSON Schema to Avro schemas</h2>

<p>This processor is not complete yet. It is _much_ more difficult to write than the reverse for two
reasons:</p>

* JSON Schema can describe a much broader set of data than Avro (Avro can only have strings in
  enums, for instance, while enums in JSON Schema can have any JSON value);
* but Avro has notions which are not available in JSON (property order in records, binary types).

<p>The generated Avro schemas are however reasonably good, and cover a very large subset of JSON
Schema usages.</p>

<p>This processor is not available online yet; it will soon be.</p>

<h1>Maven artifact</h1>

<p><i>Coming soon; for now, no version is published</i></p>

<p>Replace <tt>your-version-here</tt> with the appropriate version:</p>

```xml
<dependency>
    <groupId>com.github.fge</groupId>
    <artifactId>json-schema-avro</artifactId>
    <version>your-version-here</version>
</dependency>
```

