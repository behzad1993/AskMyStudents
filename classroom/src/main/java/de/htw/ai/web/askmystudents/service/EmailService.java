package de.htw.ai.web.askmystudents.service;

import com.neverbounce.api.client.NeverbounceClient;
import com.neverbounce.api.model.Result;
import com.neverbounce.api.model.SingleCheckResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    JavaMailSender mailSender;

    NeverbounceClient neverbounceClient;

    @Async
    public void sendEmail(final SimpleMailMessage email) {
        this.mailSender.send(email);
    }

    @Autowired
    public void setJavaMailSender(final JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Autowired
    public void setNeverbounceClient(final NeverbounceClient neverbounceClient) {
        this.neverbounceClient = neverbounceClient;
    }


    public Result checkEmail(final String email) {
        final SingleCheckResponse singleCheckResponse = this.neverbounceClient
                .prepareSingleCheckRequest()
                .withEmail(email) // address to verify
                .withAddressInfo(true)  // return address info with response
                .withCreditsInfo(true)  // return account credits info with response
                .withTimeout(20)  // only wait on slow email servers for 20 seconds max
                .build()
                .execute();
        final Result result = singleCheckResponse.getResult();
        return result;
    }
}
