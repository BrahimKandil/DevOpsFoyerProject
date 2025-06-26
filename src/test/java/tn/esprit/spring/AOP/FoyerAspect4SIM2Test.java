package tn.esprit.spring.AOP;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
@SpringBootTest

public class FoyerAspect4SIM2Test {

    // Dummy service in target package to trigger aspects
    public static class DummyService {
        public String ajouterElement(String input) {
            return "Added: " + input;
        }

        public String doSomething(String input) {
            return "Did: " + input;
        }
    }

    @Test
    void testAspectAdvices() throws Throwable {
        // Create aspect instance, but override the profile() to call proceed()
        FoyerAspect4SIM2 aspect = new FoyerAspect4SIM2() {
            @Override
            public Object profile(ProceedingJoinPoint pjp) throws Throwable {
                long start = System.currentTimeMillis();
                Object obj = pjp.proceed(); // call the actual method
                long elapsedTime = System.currentTimeMillis() - start;
                // You can add log capture here if needed
                return obj;
            }
        };

        DummyService target = new DummyService();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.addAspect(aspect);
        DummyService proxy = factory.getProxy();

        // Call method that matches @Before("ajouter*") advice
        String addResult = proxy.ajouterElement("test");
        assertEquals("Added: test", addResult);

        // Call method that does not match ajouter* but matches others
        String doResult = proxy.doSomething("foo");
        assertEquals("Did: foo", doResult);
    }
}

