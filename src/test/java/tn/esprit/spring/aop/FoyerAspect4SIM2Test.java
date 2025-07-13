package tn.esprit.spring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(OutputCaptureExtension.class)
class FoyerAspect4SIM2Test {

    // Dummy service in same class
    static class DummyService {
        public String ajouterElement(String input) {
            return "Added: " + input;
        }

        public String doSomething(String input) {
            return "Did: " + input;
        }
    }

    // Custom test aspect with simplified pointcuts
    @Aspect
    static class TestAspect {

        @Before("execution(* tn.esprit.spring.aop.FoyerAspect4SIM2Test.DummyService.ajouter*(..))")
        public void beforeAjouter(org.aspectj.lang.JoinPoint jp) {
            System.out.println("Ranni méthode ajouter" + jp.getSignature().getName());
        }

        @Before("execution(* tn.esprit.spring.aop.FoyerAspect4SIM2Test.DummyService.*(..))")
        public void beforeAll(org.aspectj.lang.JoinPoint jp) {
            System.out.println("ranni d5alt lil méthode " + jp.getSignature().getName());
        }

        @After("execution(* tn.esprit.spring.aop.FoyerAspect4SIM2Test.DummyService.*(..))")
        public void afterAll(org.aspectj.lang.JoinPoint jp) {
            System.out.println("ranni 5rajt mil méthode " + jp.getSignature().getName());
        }

        @Around("execution(* tn.esprit.spring.aop.FoyerAspect4SIM2Test.DummyService.*(..))")
        public Object profile(ProceedingJoinPoint pjp) throws Throwable {
            long start = System.currentTimeMillis();
            Object obj = pjp.proceed();
            long elapsedTime = System.currentTimeMillis() - start;
            System.out.println("Method execution time: " + elapsedTime + " milliseconds.");
            return obj;
        }
    }

    @Test
    void testAspectAdvicesAndLogs(CapturedOutput output) {
        DummyService target = new DummyService();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.addAspect(new TestAspect());

        DummyService proxy = factory.getProxy();

        // Call ajouterElement
        String result1 = proxy.ajouterElement("test");
        assertEquals("Added: test", result1);

        // Call doSomething
        String result2 = proxy.doSomething("foo");
        assertEquals("Did: foo", result2);

        // Now verify logs
        assertThat(output)
                .contains("ranni d5alt lil méthode ajouterElement")
                .contains("Ranni méthode ajouter")
                .contains("ranni 5rajt mil méthode ajouterElement")
                .contains("ranni d5alt lil méthode doSomething")
                .contains("ranni 5rajt mil méthode doSomething")
                .contains("Method execution time:");
    }
    
}
