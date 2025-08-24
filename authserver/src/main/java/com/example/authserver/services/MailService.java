package com.example.authserver.services;

/**
 * Service interface for sending emails related to user account operations.
 * <p>
 * Typically used to send verification emails, password reset emails,
 * or other notification emails to users.
 * </p>
 */
public interface MailService {

    /**
     * Sends a verification email to the specified email address.
     * <p>
     * The email should contain a link with the verification token
     * that allows the user to verify their account.
     * </p>
     *
     * @param email the recipient's email address
     * @param token the verification token to include in the email link
     */
    void sendVerificationMail(String email, String token);
}
