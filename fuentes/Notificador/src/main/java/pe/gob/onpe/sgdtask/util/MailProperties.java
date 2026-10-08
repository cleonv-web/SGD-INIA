package pe.gob.onpe.sgdtask.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: crosales
 * Date: 27/04/12
 * Time: 02:47 PM
 * To change this template use File | Settings | File Templates.
 */
@Component("mailProperties")
public class MailProperties {
    @Autowired @Qualifier(value = "mailProps")
    private Properties properties;

    public String getUserName() {
        return properties.getProperty("USER_NAME");
    }

    public String getUserPassword() {
        return properties.getProperty("USER_PASSWORD");
    }

    public String getServerIP() {
        return properties.getProperty("SERVER_IP");
    }
    
    public String getServerPort() {
        return properties.getProperty("SERVER_PORT");
    }

    public String getEmailFrom() {
        return properties.getProperty("EMAIL_FROM");
    }     
    public String getSmtpAuth() {
        return properties.getProperty("SMTP_AUTH");
    }
    public String getSmtpStartTLS() {
        return properties.getProperty("SMTP_STARTTLS");
    }
}
