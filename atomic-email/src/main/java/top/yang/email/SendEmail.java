package top.yang.email;

import java.io.File;
import java.util.Objects;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author PrideYang
 */
@Component
public class SendEmail {

    private static final Logger logger = LoggerFactory.getLogger(SendEmail.class);
    private final JavaMailSender mailSender;

    public SendEmail(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean commonEmail(String from, String subject, String content, String... tos) {
        //创建简单邮件消息
        SimpleMailMessage message = new SimpleMailMessage();
        //谁发的
        message.setFrom(from);
        //谁要接收
        message.setTo(tos);
        //邮件标题
        message.setSubject(subject);
        //邮件内容
        message.setText(content);
        try {
            mailSender.send(message);
            return true;
        } catch (MailException e) {
            e.printStackTrace();
            logger.error("发送邮件失败 错误信息:{}", e.getMessage());
            return false;
        }
    }

    public boolean htmlEmail(String from, String subject, String content, String... tos) {
        //创建一个MINE消息
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper minehelper = null;
        try {
            minehelper = new MimeMessageHelper(message, true);
            //谁发
            minehelper.setFrom(from);
            //谁要接收
            minehelper.setTo(tos);
            //邮件主题
            minehelper.setSubject(subject);
            //邮件内容   true 表示带有附件或html
            minehelper.setText(content, true);
            mailSender.send(message);
            return true;
        } catch (MessagingException | MailException e) {
            e.printStackTrace();
            logger.error("发送HTML邮件失败 错误信息:{}", e.getMessage());
            return false;
        }

    }

    public boolean staticEmail(File multipartFile, String from, String subject, String content, String... tos) {
        //创建一个MINE消息
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            //谁发
            helper.setFrom(from);
            //谁接收
            helper.setTo(tos);
            //邮件主题
            helper.setSubject(subject);
            //邮件内容   true 表示带有附件或html
            //邮件内容拼接
            helper.setText(content, true);
            FileSystemResource res = new FileSystemResource(multipartFile);
            //添加内联资源，一个id对应一个资源，最终通过id来找到该资源
            helper.addInline(Objects.requireNonNull(res.getFilename()), res);
            mailSender.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            logger.error("嵌入静态资源的邮件发送失败 错误信息：{}", e.getMessage());
            return false;
        }
    }

    public boolean enclosureEmail(File file, String from, String subject, String content, String... tos) {
        //创建一个MINE消息
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            //谁发
            helper.setFrom(from);
            //谁接收
            helper.setTo(tos);
            //邮件主题
            helper.setSubject(subject);
            //邮件内容   true 表示带有附件或html
            helper.setText(content, true);
            FileSystemResource fileSystemResource = new FileSystemResource(file);
            //添加附件
            helper.addAttachment(Objects.requireNonNull(fileSystemResource.getFilename()), file);
            mailSender.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            logger.error("附件邮件发送失败 错误信息：{}", e.getMessage());
            return false;
        }
    }
}
