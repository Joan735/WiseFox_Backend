package WiseFox.Finance.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import WiseFox.Finance.model.Transaction;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String from;

	public void sendVerificationCode(String toEmail, String code) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setFrom(from);
			helper.setTo(toEmail);
			helper.setSubject("WiseFox — Verification code");
			helper.setText(buildEmailHtml(code), true);

			mailSender.send(message);

		} catch (MessagingException e) {
			throw new RuntimeException("Failed to send verification email: " + e.getMessage(), e);
		}
	}

	private String buildEmailHtml(String code) {
		return """
				<div style="font-family: sans-serif; max-width: 480px; margin: 0 auto; padding: 32px;">
				  <h2 style="color: #1a1a1a; margin-bottom: 8px;">Verify your email</h2>
				  <p style="color: #555; margin-bottom: 24px;">
				    Use the code below to complete your WiseFox registration.
				    It expires in <strong>10 minutes</strong>.
				  </p>
				  <div style="background: #f4f4f4; border-radius: 8px; padding: 24px; text-align: center; margin-bottom: 24px;">
				    <span style="font-size: 36px; font-weight: 700; letter-spacing: 10px; color: #1a1a1a;">%s</span>
				  </div>
				  <p style="color: #999; font-size: 13px;">
				    If you didn't request this, you can safely ignore this email.
				  </p>
				</div>
				"""
				.formatted(code);
	}

	public void sendMonthlyReport(String toEmail, String userName, String monthLabel,
			List<MonthlyReportService.LedgerSummary> summaries) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setFrom(from);
			helper.setTo(toEmail);
			helper.setSubject("WiseFox — Your summary for " + monthLabel);
			helper.setText(buildReportHtml(userName, monthLabel, summaries), true);
			mailSender.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException("Failed to send monthly report: " + e.getMessage(), e);
		}
	}

	private String buildReportHtml(String userName, String monthLabel,
			List<MonthlyReportService.LedgerSummary> summaries) {
		StringBuilder sb = new StringBuilder();
		sb.append("""
				<div style="font-family:sans-serif;max-width:560px;margin:0 auto;padding:32px;color:#1a1a1a">
				<h2 style="margin-bottom:4px">Monthly summary</h2>
				<p style="color:#888;margin-top:0">%s · Hi %s</p>
				""".formatted(monthLabel, userName));

		for (var s : summaries) {
			String netColor = s.net().signum() >= 0 ? "#1a7a4a" : "#c0392b";
			sb.append(
					"""
							<div style="background:#f7f7f7;border-radius:10px;padding:20px;margin:20px 0">
							<h3 style="margin:0 0 12px">%s <span style="font-size:13px;color:#888;font-weight:400">(%s)</span></h3>
							<table style="width:100%%;border-collapse:collapse;font-size:14px">
							<tr>
							<td style="padding:4px 0;color:#555">Income</td>
							<td style="text-align:right;color:#1a7a4a;font-weight:500">+ %s %.2f</td>
							</tr>
							<tr>
							<td style="padding:4px 0;color:#555">Expenses</td>
							<td style="text-align:right;color:#c0392b;font-weight:500">− %s %.2f</td>
							</tr>
							<tr style="border-top:1px solid #ddd">
							<td style="padding:8px 0 4px;font-weight:500">Net</td>
							<td style="text-align:right;color:%s;font-weight:700;padding-top:8px">%s %.2f</td>
							</tr>
							</table>
							"""
							.formatted(s.ledgerName(), s.currency(), s.currency(), s.totalIncome(), s.currency(),
									s.totalExpense(), netColor, s.currency(), s.net()));

			if (!s.byCategory().isEmpty()) {
				sb.append("""
						<p style="margin:14px 0 6px;font-size:13px;color:#888;font-weight:500">
						EXPENSES BY CATEGORY</p>
						<table style="width:100%%;font-size:13px;border-collapse:collapse">
						""");
				s.byCategory().entrySet().stream()
						.sorted(Map.Entry.<Transaction.Category, BigDecimal>comparingByValue().reversed())
						.forEach(e -> sb.append("""
								<tr>
								  <td style="padding:3px 0;color:#555">%s</td>
								  <td style="text-align:right;color:#333">%s %.2f</td>
								</tr>""".formatted(capitalize(e.getKey().name()), s.currency(), e.getValue())));
				sb.append("</table>");
			}
			sb.append("</div>");
		}

		sb.append("""
				<p style="color:#bbb;font-size:12px;margin-top:24px">
				You're receiving this because you have a WiseFox account.
				</p>
				</div>""");
		return sb.toString();
	}

	private String capitalize(String s) {
		return s.charAt(0) + s.substring(1).toLowerCase();
	}
}