import cn.hutool.core.io.file.FileWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

/**
 * @author chris
 * @create 2022/1/22
 */
public class ToolTest {
    @Test
    public void huTool() {
        String path = "file.txt";
        FileWriter writer = new FileWriter(path);
        writer.write("test", true);
    }
}
