import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

/**
 * @author chris
 * @create 2022/1/22
 */
public class LogTest {
    Logger logger = LogManager.getLogger(LogTest.class);

    @Test
    public void log4jTest() {
        logger.debug("Debug Level");
        logger.info("Info Level");
        logger.warn("Warn Level");
        logger.error("Error Level");
    }
}
