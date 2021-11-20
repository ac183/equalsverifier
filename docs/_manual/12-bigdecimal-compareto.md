---
title: "`BigDecimal` equality using `compareTo(BigDecimal val)`"
permalink: /manual/bigdecimal-compareto/
---
The `Comparable` interface strongly recommends but does not require that implementations consider two objects equal using `compareTo` whenever they are equal using `equals` and vice versa. `BigDecimal` is a class where this is not applied.

{% highlight java %}
BigDecimal zero = new BigDecimal("0");
BigDecimal alsoZero = new BigDecimal("0.0");

// prints true - zero is the same as zero
System.out.println(zero.compareTo(alsoZero) == 0);
// prints false - zero is not the same as zero
System.out.println(zero.equals(alsoZero));
{% endhighlight %}

This is because `BigDecimal` can have multiple representations of the same value. It uses an unscaled value and a scale so, for example, the value of 1 can be represented as unscaled value 1 with scale of 0 (the number of places after the decimal point) or as unscaled value 10 with scale of 1 resolving to 1.0. Its `equals` and `hashCode` methods use both of these attributes in their calculation rather than the resolved value.

If your class contains any `BigDecimal` fields, and you would like comparably equal values to be considered the same, then the `equals` method must use `compareTo` for the check and the `hashCode` calculation must derive the same value for all `BigDecimal` instances that are equal using `compareTo` (taking care if it is a nullable field).

EqualsVerifier can check this by adding `usingBigDecimalCompareTo()`:

{% highlight java %}
EqualsVerifier.forClass(FooWithComparablyEqualBigDecimalFields.class)
    .usingBigDecimalCompareTo()
    .verify();
{% endhighlight %}

There is more information on `compareTo` and `equals` in the `Comparable` Javadoc and Effective Java's chapter on implementing `Comparable`.
There is more information on `BigDecimal` in its Javadoc (and its representation can be seen by printing `unscaledValue()` and `scale()`).
