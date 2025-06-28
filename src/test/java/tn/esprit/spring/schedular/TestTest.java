package tn.esprit.spring.schedular;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;
import static org.assertj.core.api.Assertions.assertThat;

class TestTest {

    @org.junit.jupiter.api.Test
    void testAfficheLogsBonjour() {
        Test testInstance = new Test(); // Ensure this class exists

        // Setup Logback ListAppender
        Logger logger = (Logger) LoggerFactory.getLogger(Test.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        testInstance.affiche(); // Call the logging method

        // Assert logs
        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(msg -> msg.contains("Bonjour"));

        logger.detachAppender(listAppender);
    }
}