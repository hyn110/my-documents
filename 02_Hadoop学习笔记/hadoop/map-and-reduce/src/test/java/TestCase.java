import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCase {
    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(TestCase.class);
        String a = "haha";
        logger.debug("Hello world.{}",a);

    }
    
}
