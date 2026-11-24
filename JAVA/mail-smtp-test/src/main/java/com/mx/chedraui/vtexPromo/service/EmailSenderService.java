package com.mx.chedraui.vtexPromo.service;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

public interface EmailSenderService {

	public void sendEmailWithAttachments(String subject, String message, String[] attachFiles)
			throws AddressException, MessagingException;

	public void sendEmailUsersWithAttachments(String mailTo, String subject, String message, String[] attachFiles)
			throws AddressException, MessagingException;
}
