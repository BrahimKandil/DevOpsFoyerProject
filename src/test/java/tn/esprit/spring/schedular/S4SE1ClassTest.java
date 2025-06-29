package tn.esprit.spring.schedular;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
 class S4SE1ClassTest {

    @Test
    void testScheduledMethodsLog() {
        S4SE1Class scheduler = new S4SE1Class();

        // Get the logger and attach a ListAppender
        Logger logger = (Logger) LoggerFactory.getLogger(S4SE1Class.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        // Call methods
        scheduler.fixedDelayMethod();
        scheduler.fixedRateMethod();

        // Verify logs
        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(log -> log.contains("Hello fixedDelay"))
                .anyMatch(log -> log.contains("Hello fixedRate"));

        logger.detachAppender(listAppender);
    }
}
