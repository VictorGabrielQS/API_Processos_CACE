package cace.processos_api.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // No serviço de email, mude o parâmetro para receber a URL completa:
    public void sendResetToken(String toEmail, String resetLink) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("Solicitação de Redefinição de Senha");

        String htmlContent = String.format("""
    <html>
      <body style="font-family: Arial, sans-serif; color: #333;">
        <p>Olá,</p>

        <p>Recebemos uma solicitação para redefinir a sua senha.</p>

        <p>Para prosseguir com a redefinição, clique no link abaixo. Ele estará disponível por <strong>30 minutos</strong>:</p>

        <p><a href="%s" target="_blank" style="color: #2a7ae2;">Redefinir senha</a></p>

        <p>Se você não solicitou esta alteração, por favor ignore este e-mail. Nenhuma ação será realizada.</p>

        <hr style="margin-top: 30px; margin-bottom: 20px;"/>

        <div style="margin-top: 30px; font-family: Arial, sans-serif; font-size: 14px; color: #333;">
          <img src="https://i.ibb.co/TDSCJ4tD/Logo-CACE-TI.png" alt="Logo CACE TI" style="height: 60px; margin-bottom: 10px;" />
          <p style="margin: 4px 0;"><strong>Equipe de Suporte TI CACE</strong></p>
          <p style="margin: 4px 0;">Sistema de Redefinição de Senhas</p>
          <a href="https://www.seusite.com" target="_blank" style="color: #2a7ae2; text-decoration: none;">www.seusite.com</a>
        </div>
      </body>
    </html>
    """, resetLink);

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

}