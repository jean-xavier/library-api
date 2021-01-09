package br.com.libraryapi.api.services;

import java.util.List;

public interface EmailService {
    void sendMails(String message, List<String> mails);
}
