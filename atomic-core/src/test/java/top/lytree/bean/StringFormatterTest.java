package top.lytree.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import top.lytree.text.StringFormatter;

public class StringFormatterTest {

    private final static Logger logger = LoggerFactory.getLogger(StringFormatterTest.class);

    public static void main(String[] args) {
        String format = StringFormatter.format("测试1 {} {} {} {}", 1, 3, 4, 5);
        FormattingTuple formattingTuple = MessageFormatter.arrayFormat("测试2 {} {} {} {}", new Object[]{new byte[10], new byte[20], 4, 5});
        String message = formattingTuple.getMessage();

        System.out.println(message);
    }
}
