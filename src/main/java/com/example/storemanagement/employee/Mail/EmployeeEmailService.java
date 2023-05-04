package com.example.storemanagement.employee.Mail;

import com.example.storemanagement.employee.Employee;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmployeeEmailService {

    private JavaMailSender mailSender;
    String companyName = "My Store"; // TODO Considering having this value globally. Maybe in an env file

    public EmployeeEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendRegistrationEmail(Employee employee, String activationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("Registration Confirmation");
        message.setTo(employee.getEmail());
        message.setText("Dear " + employee.getFirstName() + ",\n\n"
                + "Thank you for registering with us. Please click on the following link to activate your account:\n\n"
                + activationLink + "\n\n"
                + "Best regards,\n"
                + companyName);

        mailSender.send(message);
    }
}


