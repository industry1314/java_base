package com.practice.java_base.email;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.sun.mail.util.MailSSLSocketFactory;

/**
 * 邮件发送工具类
 * 
 * create by 马伟 on 2019年12月15日
 */
public class SendEmailUtil {

	/**
	 * 登录用户名和授权码
	 */
	private static final String USER_NAME = "1107827920@qq.com";
	private static final String PASSWORD = "orcnhjkrewphjbdd";

	/**
	 * 使用JavaMail发送邮件的5个步骤
	 */
	public static void sendTextEmail(EmailType emailType) throws Exception {
		// 1、创建定义整个应用程序所需的环境信息的 Session 对象
		Session session = createSession();

		// 2、通过session得到transport对象
		Transport ts = session.getTransport();

		// 3、使用邮箱的用户名和授权码连上邮件服务器
		ts.connect("smtp.qq.com", USER_NAME, PASSWORD);

		// 4、创建邮件 可用模板+策略设计模式
		MimeMessage message = null;
		if (emailType == EmailType.COMMON) {
			message = createEmail(session);
		} else if (emailType == EmailType.WITH_ATTACH) {
			message = createAttachEmail(session);
		} else {
			throw new IllegalArgumentException("邮件类型无效");
		}

		// 5、发送邮件
		ts.sendMessage(message, message.getAllRecipients());

		ts.close();
	}

	/**
	 * 创建文本邮件
	 */
	private static MimeMessage createEmail(Session session) throws Exception {
		// 创建邮件对象
		MimeMessage message = new MimeMessage(session);
		// 指明邮件的发件人
		message.setFrom(new InternetAddress(USER_NAME));
		// 指明邮件的收件人（可以是自己）
		message.setRecipient(Message.RecipientType.TO, new InternetAddress("3079170340@qq.com"));
		// 邮件的标题
		message.setSubject("开发测试邮件");
		// 邮件的文本内容
		message.setContent("当时明月在，曾照彩云归。明日难再醉，彩云不复回", "text/html;charset=UTF-8");
		return message;
	}

	/**
	 * 创建带附件邮件
	 */
	private static MimeMessage createAttachEmail(Session session) throws Exception {
		// 创建邮件对象
		MimeMessage message = new MimeMessage(session);
		// 指明邮件的发件人
		message.setFrom(new InternetAddress(USER_NAME));
		// 指明邮件的收件人（可以是自己）
		// Message.RecipientType.TO-收件人；Message.RecipientType.BCC-密送；Message.RecipientType.CC-抄送
		// message.setRecipients指定多个收件人
		message.setRecipient(Message.RecipientType.TO, new InternetAddress("aa1107827920@163.com"));
		// 邮件的标题
		message.setSubject("开发测试邮件-附件");

		// 设置发送日期
		// message.setSentDate(new Date());

		// 准备图片数据
		MimeBodyPart image = getAttachBodyPart("C:/java_develop_tools/test_files/girl.jpg");
		MimeBodyPart image2 = getAttachBodyPart("C:/java_develop_tools/test_files/竹影.jpg");
		MimeBodyPart excel = getAttachBodyPart("C:/java_develop_tools/test_files/训练表.xlsx");
		MimeBodyPart javaFile = getAttachBodyPart("C:/java_develop_tools/test_files/SendEmailUtil.java");

		// 准备正文数据
		MimeBodyPart text = new MimeBodyPart();
		text.setContent("当时明月在，曾照彩云归。明日难再醉，彩云不复回", "text/html;charset=UTF-8");

		// 描述数据关系
		MimeMultipart mimeMultipart = new MimeMultipart();
		mimeMultipart.addBodyPart(text);
		mimeMultipart.addBodyPart(image);
		mimeMultipart.addBodyPart(image); // 重复添加
		mimeMultipart.addBodyPart(image2);
		mimeMultipart.addBodyPart(excel);
		mimeMultipart.addBodyPart(javaFile);
		mimeMultipart.setSubType("related");

		// 设置到消息中，保存修改
		message.setContent(mimeMultipart);
		message.saveChanges();

		return message;
	}

	/**
	 * 设置附件
	 */
	private static MimeBodyPart getAttachBodyPart(String filePath) throws MessagingException, UnsupportedEncodingException {
		MimeBodyPart image = new MimeBodyPart();
		FileDataSource fileDataSource = new FileDataSource(filePath);
		DataHandler dh = new DataHandler(fileDataSource);
		image.setDataHandler(dh);
		// 设置附件名称,MimeUtility.encodeText可以处理乱码问题
		image.setFileName(MimeUtility.encodeText(fileDataSource.getName()));
		return image;
	}

	private static Session createSession() throws GeneralSecurityException {
		Properties prop = new Properties();
		prop.setProperty("mail.host", "smtp.qq.com"); // 设置QQ邮件服务器
		prop.setProperty("mail.transport.protocol", "smtp"); // 邮件发送协议
		prop.setProperty("mail.smtp.auth", "true"); // 需要验证用户名密码

		// 关于QQ邮箱，还要设置SSL加密
		MailSSLSocketFactory sf = new MailSSLSocketFactory();
		sf.setTrustAllHosts(true);
		prop.put("mail.smtp.ssl.enable", "true");
		prop.put("mail.smtp.ssl.socketFactory", sf);

		// 创建定义整个应用程序所需的环境信息的 Session 对象
		Session session = Session.getDefaultInstance(prop, new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				// 发件人邮件用户名、授权码
				return new PasswordAuthentication(USER_NAME, PASSWORD);
			}
		});

		// 开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
		session.setDebug(true);
		return session;
	}

}
