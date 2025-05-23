package cace.processos_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetToken(String toEmail, String token) {
        String resetLink = "https://api-processos-cace.onrender.com/redefinir-senha?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Redefinição de senha");
        message.setText("""
        Olá,
        
        Você solicitou a redefinição de senha. 
        Clique no link abaixo (válido por 30 minutos):
        """ + resetLink + """
        
        Se você não solicitou, ignore este e-mail.
        """);

        mailSender.send(message);
    }

}