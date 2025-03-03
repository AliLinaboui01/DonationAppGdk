package com.example.controller;

import io.micronaut.email.Email;
import io.micronaut.email.EmailException;
import io.micronaut.email.EmailSender;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Named;

import static io.micronaut.email.BodyType.HTML;

@ExecuteOn(TaskExecutors.IO)
@Controller("/email")
public class EmailController {


    private final EmailSender<?, ?> emailSender;
    EmailController(EmailSender<?, ?> emailSender) {
        this.emailSender = emailSender;
    }
    @Post("/send")
    public HttpResponse<?> send(@Body("to") String to) {
        try {
            emailSender.send(Email.builder()
                    .to(to)
                    .subject("Sending email with JavaMail is Fun")
                    .body("and <em>easy</em> to do anywhere with <strong>Micronaut Email</strong>", HTML));
        } catch (EmailException ignored) {
            return HttpResponse.unprocessableEntity();
        }
        return HttpResponse.accepted();
    }
}
