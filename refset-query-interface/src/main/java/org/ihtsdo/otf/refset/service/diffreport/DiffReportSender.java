/**
* Copyright 2014 IHTSDO
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.ihtsdo.otf.refset.service.diffreport;

import java.util.Date;
import java.util.Properties;

import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 *
 */
@Component
public class DiffReportSender {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DiffReportSender.class);
	
	
	@Value("${diffreport.mail.host}")
	private String host;

	
	@Value("${diffreport.mail.from}")
	private String from;
	
	@Value("${diffreport.mail.subject}")
	private String subject;
	
	//optional
	@Value("${diffreport.mail.user}")
	private String user;
	
	@Value("${diffreport.mail.password}")
	private String password;
	
	@Value("${diffreport.mail.port}")
	private Integer port;
	
	@Value("${diffreport.mail.auth}")
	private Boolean auth;

	@Value("${diffreport.mail.starttls.enable}")
	private Boolean starttls;

	@Autowired
	private MailSender sender;
	
	@Autowired
	private SimpleMailMessage msg;
	
	
	@Bean
	private MailSender getMailSender() {
			
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setHost(host);
		if (!StringUtils.isEmpty(port)) {
			
			sender.setPort(port);

		}
		if (!StringUtils.isEmpty(password)) {

			sender.setPassword(password);
			
			if (!StringUtils.isEmpty(user)) {
				sender.setUsername(user);

			}
		}
		
		sender.setProtocol("smtp");
		
		Properties javaMailProperties = new Properties();

		if (starttls != null) {
			
			javaMailProperties.put("mail.smtp.starttls.enable", starttls);

		}
		
		if (auth != null) {
			
			javaMailProperties.put("mail.smtp.auth", auth);

		}
		
		sender.setJavaMailProperties(javaMailProperties);
		
		return sender;
	}
	
	@Bean
	private SimpleMailMessage getMessage() {
		
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setSubject(subject);
        msg.setFrom(from);
        msg.setSentDate(new Date());
        msg.setReplyTo(from);
        
        return msg;
	}
	
	/**
	 * @param body
	 * @param email
	 * @throws RefsetServiceException 
	 */
	public void send(String body, String email) throws RefsetServiceException {
		
		msg.setTo(email);
		msg.setText(body);
		
		LOGGER.debug("Sending message to {} with message as {}", email, msg.toString());
		try {
			
			sender.send(msg);

			LOGGER.debug("Message sent successfully");

		} catch (MailException e) {
			
			LOGGER.error("Error in sending email ", e);

			throw new RefsetServiceException("Error while sending email", e);
		}
		
	}
}
