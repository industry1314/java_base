package com.practice.java_base.email;

import org.junit.Test;

public class EmailTest {

	@Test
	public void sendEmail() throws Exception {
		SendEmailUtil.sendTextEmail(EmailType.WITH_ATTACH);
	}

}
