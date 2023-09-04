package com.xiaoxiao.server.service;

import com.xiaoxiao.model.dto.MailDto;
import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

/**
 * 邮件服务
 */
@Service
@Slf4j
@EnableAsync
public class MailService {

    @Resource
    private JavaMailSender mailSender;

    @Resource
    private Environment env;

    @Async
    public void sendSimpleEmail(final MailDto dto) {

        try {

            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(env.getProperty("mail.send.from"));
            simpleMailMessage.setTo(dto.getTos());
            simpleMailMessage.setSubject(dto.getSubject());
            simpleMailMessage.setText(dto.getContent());

            mailSender.send(simpleMailMessage);

            log.info("发送简单文本文件-发生成功！");

        } catch (Exception e) {
            log.error("发送简单文本文件-发生异常：", e.fillInStackTrace());
        }
    }

    @Async
    public void sendHTMLMail(final MailDto dto) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            mimeMessageHelper.setFrom(env.getProperty("mail.send.from"));
            mimeMessageHelper.setTo(dto.getTos());
            mimeMessageHelper.setSubject(dto.getSubject());
            mimeMessageHelper.setText(dto.getContent(), true);

            mailSender.send(mimeMessage);
            log.info("发送HTML文件-发生成功！");

        } catch (Exception e) {
            log.error("发送HTML文件-发生异常：", e.fillInStackTrace());
        }
    }
}
