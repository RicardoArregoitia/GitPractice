package com.mx.chedraui.vtexPromo.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mx.chedraui.vtexPromo.repository.sql.ConfigurationPropertiesRepository;
import com.mx.chedraui.vtexPromo.request.MailSendRequest;
import com.mx.chedraui.vtexPromo.service.EmailSenderService;
import com.mx.chedraui.vtexPromo.utils.DateUtil;

@Controller
@RequestMapping(path = { "/api/xml/" })
public class MailController {

	@Autowired
	EmailSenderService emailSenderService;

	@Autowired
	ConfigurationPropertiesRepository configurationPropertiesRepository;

	@RequestMapping(path = "sendMailStatus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> sendMailStatus(HttpServletRequest hsr) {
		System.out.println(DateUtil.getActuallyDate("dd/MM/yyyy HH:mm:ss") + "Validando Promos.............");
		String mailTo = configurationPropertiesRepository.getByName("mail.logger").get(0).getDescription();
		String body = "<p>Prueba SMTP desde el POC Spring, mismo flujo de EmailSenderServiceImpl.</p>";
		return send(mailTo, "Total de Promociones Procesadas", body, new String[0]);
	}

	@RequestMapping(path = "sendMail", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> sendMail(@RequestBody MailSendRequest request, HttpServletRequest hsr) {
		System.out.println(DateUtil.getActuallyDate("dd/MM/yyyy HH:mm:ss") + "Envia correo a destinatario indicado.............");
		Map<String, String> missing = new LinkedHashMap<String, String>();
		if (request == null || isBlank(request.getTo()) || isBlank(request.getHeader()) || isBlank(request.getBody())) {
			missing.put("RESULTADO", "ERROR");
			missing.put("detalle", "Faltan to, header o body");
			return missing;
		}
		return send(request.getTo().trim(), request.getHeader(), request.getBody(), new String[0]);
	}

	@RequestMapping(path = "sendMailFile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> sendMailFile(@RequestParam(name = "fileName") String fileName,
			HttpServletRequest hsr) {
		System.out.println(DateUtil.getActuallyDate("dd/MM/yyyy HH:mm:ss")
				+ "Envia correo por falta de Archivo diario.............");
		String mailTo = configurationPropertiesRepository.getByName("mail.logger").get(0).getDescription();
		String body = String.format("Este fue el ultimo archivo procesado: <b>%s</b> <br>", fileName);
		return send(mailTo, "Ultimo Archivo de PROMOCIONES", body, new String[0]);
	}

	private Map<String, String> send(String mailTo, String subject, String message, String[] attachFiles) {
		Map<String, String> result = new LinkedHashMap<String, String>();
		try {
			emailSenderService.sendEmailUsersWithAttachments(mailTo, subject, message, attachFiles);
			System.out.println(DateUtil.getActuallyDate("dd/MM/yyyy HH:mm:ss") + "Proceso concluido.............");
			result.put("RESULTADO", "OK");
			result.put("detalle", "Correo enviado a " + mailTo);
		} catch (Exception ex) {
			String detalle = detalle(ex);
			System.out.println(DateUtil.getActuallyDate("dd/MM/yyyy HH:mm:ss") + "error--> " + detalle);
			result.put("RESULTADO", "ERROR");
			result.put("tipo", ex.getClass().getName());
			result.put("detalle", detalle);
		}
		return result;
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	private String detalle(Exception ex) {
		StringBuilder text = new StringBuilder();
		text.append(ex.getClass().getName());
		if (ex.getMessage() != null) {
			text.append(": ").append(ex.getMessage());
		}
		Throwable cause = ex.getCause();
		while (cause != null) {
			text.append(" | cause=").append(cause.getClass().getName());
			if (cause.getMessage() != null) {
				text.append(": ").append(cause.getMessage());
			}
			cause = cause.getCause();
		}
		return text.toString();
	}

}
