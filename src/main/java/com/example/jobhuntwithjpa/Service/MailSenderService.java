package com.example.jobhuntwithjpa.Service;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

@Service
public class MailSenderService {

    private JavaMailSender mailSender;

    @Autowired
    private ResourceLoader resourceLoader;


    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    public MailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public String sendEmail(String to, String subject, String body) throws Exception{
        /*SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);*/

        String verificationNum = getVerificationNumber().toString();

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);

        Resource resource = resourceLoader.getResource("classpath:static/acceptEmail.html");
        InputStream inputStream = resource.getInputStream();
        String emailTemplate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        emailTemplate = emailTemplate.replace("{{CertificationNumber}}", verificationNum);

        helper.setText(emailTemplate, true);
        mailSender.send(mimeMessage);

        return verificationNum;
    }

    public Integer getVerificationNumber() {
        // 난수의 범위 111111 ~ 999999 (6자리 난수)
        Random r = new Random();
        Integer checkNum = r.nextInt(888888) + 111111;
        //System.out.println("인증번호 : " + checkNum);

        return checkNum;
    }
}
