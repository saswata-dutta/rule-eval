package rule.eval;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.immutables.value.Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class JexlPoc {

    @Value.Immutable
    @Value.Style(strictBuilder = true)
    public interface Person {
        String getName();

        long getSalary();

        String getAddress();
    }

    @Value.Immutable
    @Value.Style(strictBuilder = true)
    public interface Threshold {
        long getLow();

        long getHigh();
    }


    @Test
    @DisplayName("POC of JEXL engine and context to evaluate rules")
    void testJexlEval() {
        final Person sas = ImmutablePerson.builder()
                .name("sas")
                .salary(123L)
                .address("WB, IN")
                .build();

        final Threshold salaryRange = ImmutableThreshold.builder().low(100L).high(1000L).build();

        String rule = " person.address =~ '.* IN.*' and person.salary > threshold.low ";

        // Create or retrieve an engine
        JexlEngine jexl = new JexlBuilder().
                strict(true).silent(false).create();

        // compile rule
        JexlExpression expr = jexl.createExpression(rule);

        // populate the context
        JexlContext context = new MapContext();
        context.set("person", sas);
        context.set("threshold", salaryRange);

        // work it out
        Boolean result = (Boolean) expr.evaluate(context);

        assertTrue(result, "'sas' is eligible");

    }
}
