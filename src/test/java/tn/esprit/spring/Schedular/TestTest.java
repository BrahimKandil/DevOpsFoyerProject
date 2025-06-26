package tn.esprit.spring.Schedular;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import static org.assertj.core.api.Assertions.assertThat;

class TestTest {

    @Test
    void testAfficheLogsBonjour() {
        test testInstance = new test(); // Ensure this class exists

        // Setup Logback ListAppender
        Logger logger = (Logger) LoggerFactory.getLogger(test.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

//        testInstance.affiche(); // Call the logging method

        // Assert logs
        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(msg -> msg.contains("Bonjour"));

        logger.detachAppender(listAppender);
    }
}