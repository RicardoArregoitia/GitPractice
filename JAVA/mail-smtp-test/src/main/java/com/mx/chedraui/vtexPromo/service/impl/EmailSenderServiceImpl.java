package com.mx.chedraui.vtexPromo.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mx.chedraui.vtexPromo.entity.sql.ConfigurationProperties;
import com.mx.chedraui.vtexPromo.repository.sql.ConfigurationPropertiesRepository;
import com.mx.chedraui.vtexPromo.service.EmailSenderService;
import com.mx.chedraui.vtexPromo.service.OAuthTokenService;
import com.mx.chedraui.vtexPromo.utils.DateUtil;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

	@Autowired
	ConfigurationPropertiesRepository configurationPropertiesRepository;

	@Autowired
	OAuthTokenService oauthTokenService;

	/**
	 * {@inheritDoc}}
	 */
	public void sendEmailWithAttachments(String subject, String message, String[] attachFiles)
			throws AddressException, MessagingException {

		List<ConfigurationProperties> itemUser = configurationPropertiesRepository.getByName("mail.user");
		List<ConfigurationProperties> itemfrom = configurationPropertiesRepository.getByName("mail.from");

		System.out.println(
				DateUtil.getActuallyDate("dd/MM/yyyy HH:mm:ss") + "Obteniendo informacion de corre de base.........");
		Session session = openSession(itemUser.get(0).getDescription());

		Message msg = new MimeMessage(session);

		msg.setFrom(new InternetAddress(itemUser.get(0).getDescription()));

		final InternetAddress[] toAddresses = InternetAddress.parse(itemfrom.get(0).getDescription(), false);
		msg.setRecipients(Message.RecipientType.TO, toAddresses);
		msg.setSubject(subject);
		msg.setSentDate(new Date());

		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(message, "text/html");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);

		if (attachFiles != null && attachFiles.length > 0) {
			for (String filePath : attachFiles) {
				MimeBodyPart attachPart = new MimeBodyPart();

				try {
					attachPart.attachFile(filePath);
				} catch (IOException ex) {
					System.out.println(DateUtil.getActuallyDate("dd/MM/yyyy HH:mm:ss")
							+ "Ocurrio un error al obtener archivo.........");
					ex.printStackTrace();
				}

				multipart.addBodyPart(attachPart);
				System.out.println(
						DateUtil.getActuallyDate("dd/MM/yyyy HH:mm:ss") + "Agregando attachments : " + filePath);
			}
		}

		msg.setContent(multipart);

		Transport.send(msg);
		System.out.println(DateUtil.getActuallyDate("dd/MM/yyyy HH:mm:ss") + "Enviando Correos correctamente ---> "
				+ itemfrom.get(0).getDescription());
	}

	@Override
	public void sendEmailUsersWithAttachments(String mailTo, String subject, String message, String[] attachFiles)
			throws AddressException, MessagingException {
		List<ConfigurationProperties> itemUser = configurationPropertiesRepository.getByName("mail.user");

		System.out.println(
				DateUtil.getActuallyDate("dd/MM/yyyy HH:mm:ss") + "Obteniendo informacion de corre de base.........");
		Session session = openSession(itemUser.get(0).getDescription());

		Message msg = new MimeMessage(session);

		msg.setFrom(new InternetAddress(itemUser.get(0).getDescription()));

		final InternetAddress[] toAddresses = InternetAddress.parse(mailTo, false);
		msg.setRecipients(Message.RecipientType.TO, toAddresses);
		msg.setSubject(subject);
		msg.setSentDate(new Date());

		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(message, "text/html");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);

		if (attachFiles != null && attachFiles.length > 0) {
			for (String filePath : attachFiles) {
				MimeBodyPart attachPart = new MimeBodyPart();

				try {
					attachPart.attachFile(filePath);
				} catch (IOException ex) {
					System.out.println(DateUtil.getActuallyDate("dd/MM/yyyy HH:mm:ss")
							+ "Ocurrio un error al obtener archivo.........");
					ex.printStackTrace();
				}

				multipart.addBodyPart(attachPart);
				System.out.println(
						DateUtil.getActuallyDate("dd/MM/yyyy HH:mm:ss") + "Agregando attachments : " + filePath);
			}
		}

		msg.setContent(multipart);

		Transport.send(msg);
		System.out.println(
				DateUtil.getActuallyDate("dd/MM/yyyy HH:mm:ss") + "Enviando Correos correctamente ---> " + mailTo);

	}

	/**
	 * password: el mismo Basic Auth de vtex promo.
	 * oauth2: token de Entra ID y SMTP AUTH XOAUTH2. El usuario SMTP es mail.user.
	 */
	private Session openSession(final String user) throws MessagingException {
		String host = config("mail.host");
		String port = config("mail.port");
		String authMode = config("mail.azure.tenant.id").isEmpty() ? "password" : "oauth2";

		Properties properties = new Properties();
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", port);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.connectiontimeout", "30000");
		properties.put("mail.smtp.timeout", "30000");
		properties.put("mail.smtp.writetimeout", "30000");

		final String secret;
		if ("oauth2".equalsIgnoreCase(authMode)) {
			secret = oauthTokenService.accessToken();
			properties.put("mail.smtp.auth.mechanisms", "XOAUTH2");
			properties.put("mail.smtp.auth.login.disable", "true");
			properties.put("mail.smtp.auth.plain.disable", "true");
			System.out.println(DateUtil.getActuallyDate("dd/MM/yyyy HH:mm:ss") + "auth_mode=oauth2 user=" + user
					+ " host=" + host + " port=" + port);
		} else {
			secret = config("mail.password");
			properties.put("mail.user", user);
			properties.put("mail.password", secret);
			System.out.println(DateUtil.getActuallyDate("dd/MM/yyyy HH:mm:ss") + "auth_mode=password user=" + user
					+ " host=" + host + " port=" + port);
		}

		Authenticator auth = new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, secret);
			}
		};
		return Session.getInstance(properties, auth);
	}

	private String config(String key) {
		List<ConfigurationProperties> items = configurationPropertiesRepository.getByName(key);
		if (items == null || items.isEmpty() || items.get(0).getDescription() == null) {
			return "";
		}
		return items.get(0).getDescription().trim();
	}

}
