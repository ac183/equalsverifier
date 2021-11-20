package nl.jqno.equalsverifier.integration.extra_features;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

import java.math.BigDecimal;
import java.util.Objects;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

public class BigDecimalTest {

    @Test
    public void succeed_whenBigDecimalsComparedUsingEquals() {
        EqualsVerifier.forClass(BigDecimalEquals.class).verify();
    }

    @Test
    public void succeed_whenBigDecimalsComparedUsingCompareTo() {
        EqualsVerifier.forClass(BigDecimalCompareTo.class).verify();
    }

    @Test
    public void fail_whenBigDecimalsComparedUsingEquals_givenUsingBigDecimalCompareToIsUsed() {
        ExpectedException
            .when(() ->
                EqualsVerifier.forClass(BigDecimalEquals.class).usingBigDecimalCompareTo().verify()
            )
            .assertFailure()
            .assertMessageContains(
                "BigDecimal",
                "equals",
                "compareTo",
                "bd",
                "usingBigDecimalCompareTo()"
            );
    }

    @Test
    public void succeed_whenBigDecimalsComparedUsingCompareTo_givenUsingBigDecimalCompareToIsUsed() {
        EqualsVerifier.forClass(BigDecimalCompareTo.class).usingBigDecimalCompareTo().verify();
    }

    @Test
    public void fail_whenBigDecimalsComparedUsingCompareToWithInconsistentHashCode_givenUsingBigDecimalCompareToIsUsed() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(BigDecimalInconsistentHashCode.class)
                    .usingBigDecimalCompareTo()
                    .verify()
            )
            .assertFailure()
            .assertMessageContains(
                "BigDecimal",
                "hashCode",
                "compareTo",
                "bd",
                "usingBigDecimalCompareTo()"
            );
    }

    /**
     * Uses standard equals and hashCode for objects.
     * 0 and 0.0 are not equal.
     */
    private static final class BigDecimalEquals {

        private final BigDecimal bd;

        public BigDecimalEquals(BigDecimal bd) {
            this.bd = bd;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof BigDecimalEquals)) {
                return false;
            }
            BigDecimalEquals other = (BigDecimalEquals) obj;
            return Objects.equals(bd, other.bd);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    /**
     * Uses compareTo for BigDecimal equality and ensures hashCode is equal for equal BigDecimal instances.
     * 0 and 0.0 are equal and produce the same hashCode.
     */
    private static final class BigDecimalCompareTo {

        private final BigDecimal bd;

        public BigDecimalCompareTo(BigDecimal bd) {
            this.bd = bd;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof BigDecimalCompareTo)) {
                return false;
            }
            BigDecimalCompareTo other = (BigDecimalCompareTo) obj;
            return comparablyEquals(bd, other.bd);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(comparablyHashed(bd));
        }
    }

    /**
     * Uses compareTo for BigDecimal equality but has hashCode that may be inconsistent.
     * 0 and 0.0 are equal but may produce a different hashCode.
     */
    private static final class BigDecimalInconsistentHashCode {

        private final BigDecimal bd;

        public BigDecimalInconsistentHashCode(BigDecimal bd) {
            this.bd = bd;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof BigDecimalInconsistentHashCode)) {
                return false;
            }
            BigDecimalInconsistentHashCode other = (BigDecimalInconsistentHashCode) obj;
            return comparablyEquals(bd, other.bd);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    /**
     * Checks equality using compareTo rather than equals.
     */
    private static boolean comparablyEquals(BigDecimal left, BigDecimal right) {
        boolean bothNull = left == null && right == null;
        boolean bothNonNullAndEqual = left != null && right != null && left.compareTo(right) == 0;
        return bothNull || bothNonNullAndEqual;
    }

    /**
     * Returns a instance (or null) that produces the same hashCode as any other instance that is equal using compareTo.
     */
    private static BigDecimal comparablyHashed(BigDecimal bd) {
        return bd != null ? bd.stripTrailingZeros() : null;
    }
}
