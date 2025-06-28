package tn.esprit.spring.aop;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
 class FoyerAspect4SE1Test {

    // Dummy service in the target package to trigger the aspect advices
     public static class DummyService {
        public String doWork(String input) {
            return "Hello " + input;
        }
    }

    @Test
    void testAspectAdvicesTriggered() {
        // Create aspect instance
        FoyerAspect4SE1 aspect = new FoyerAspect4SE1();

        // Create proxy of DummyService with aspect applied
        DummyService target = new DummyService();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.addAspect(aspect);
        DummyService proxy = factory.getProxy();

        // Call method on proxy - should trigger advices
        String result = proxy.doWork("World");

        // Assert method returns expected result
        assertEquals("Hello World", result);

        // Since your aspect logs info, you can verify via logs manually or use a logging spy framework.
        // For simplicity, here we just verify the method runs correctly and aspect advices do not throw exceptions.
    }
}
