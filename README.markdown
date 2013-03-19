<h1>Read me first</h1>

<p>The license of this project is LGPLv3 or later. See file src/main/resources/LICENSE for the full
text.</p>

<p>The current version is <b>0.1.1</b>.</p>

<h1>What this is</h1>

<p>This package contains two processors (see <a
href="https://github.com/fge/json-schema-core">json-schema-core</a>) to convert Avro schemas to JSON
Schemas, and the reverse.</p>

<h1>Status</h1>

<h2>Avro schemas to JSON Schema</h2>

<p>This processor can transform <b>all</b> Avro schemas you can think of, as long as said schemas
are self contained. The generated JSON Schemas can accurately validate JSON representations of Avro
data with two exceptions:</p>

* as JSON has no notion of order, the <span style="font-family: monospace;">order</span> property of
  Avro records is not enforced;
* Avro's <span style="font-family: monospace;">float</span> and <span
  style="font-family: monospace;">double</span> are validated as JSON numbers, with no minimum or
  maximum, see below as to why. Note however that <span style="font-family: monospace;">int</span>
  and <span style="font-family: monospace;">long</span>'s limits _are_ enforced.

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

<h1>Why limits are not enforced on Avro's <span style="font-family: monospace;">float</span> and
<span style="font-family: monospace;">double</span></h1>

<p>While JSON Schema has <span style="font-family: monospace;">minimum</span> and <span
style="font-family: monospace;">maximum</span> to enforce the minimum and maximum values of a JSON
number, JSON numbers (<a href="http://tools.ietf.org/html/rfc4627">RFC 4627, section 2.4</a>) do not
define any limit to the scale and/or precision of numbers.</p>

<p>That is a first reason, but then one should ask why then, are there limits for <span
style="font-family: monospace;">int</span> and <span style="font-family: monospace;">long</span>.
There are two reasons for this:</p>

* JSON Schema defines integer (as a number with no fractional and/or exponent part); integer being a
  discrete domain, such limits can therefore be defined without room for error;
* but Avro's <span style="font-family: monospace;">float</span> and <span
  style="font-family: monospace;">double</span> are IEEE 754 floating point numbers; they do have minimums and
  maximums, but 0.1, for instance, cannot even be represented exactly in a double.

<p>Defining limits would therefore not ensure that the JSON number being validated can indeed fit
into the corresponding Avro type.</p>

<h1>Maven artifact</h1>

<p>Replace <tt>your-version-here</tt> with the appropriate version:</p>

```xml
<dependency>
    <groupId>com.github.fge</groupId>
    <artifactId>json-schema-avro</artifactId>
    <version>your-version-here</version>
</dependency>
```

