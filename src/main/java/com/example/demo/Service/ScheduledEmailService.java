package com.example.demo.Service;

import com.example.demo.Repository.AdminSmtpConfigRepository;
import com.example.demo.Repository.ComplaintRepository;
import com.example.demo.Repository.SupportAgentRepository;
import com.example.demo.entity.AdminSmtpConfig;
import com.example.demo.entity.Complaint;
import com.example.demo.entity.ComplaintStatus;
import com.example.demo.entity.SupportAgent;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledEmailService {

    private final ComplaintRepository complaintRepository;
    private final SupportAgentRepository supportAgentRepository;
    private final AdminSmtpConfigRepository smtpConfigRepository;

    // Runs every 2 minutes (120000 milliseconds)
    @Scheduled(fixedDelay = 60000) // check every 1 minute
    public void dynamicScheduler() {
        try {
            // ✅ Fetch only the first active SMTP config
            AdminSmtpConfig smtpConfig = smtpConfigRepository.findFirstByActiveTrueOrderByIdAsc().orElse(null);

            if (smtpConfig == null) {
                log.warn("⏸️ No active SMTP configuration found in DB. Skipping email sending this cycle.");
                return;
            }

            int timeInMinutes = smtpConfig.getTime();
            long intervalMillis = timeInMinutes * 60 * 1000L;

            if (lastExecutionTime == null || System.currentTimeMillis() - lastExecutionTime >= intervalMillis) {
                lastExecutionTime = System.currentTimeMillis();
                sendTicketSummaryEmailsInternal(smtpConfig);
            }

        } catch (Exception e) {
            log.error("Error while running dynamic scheduler: {}", e.getMessage(), e);
        }
    }


    private static Long lastExecutionTime = null;

    // Main logic — now accepts smtpConfig as argument
    private void sendTicketSummaryEmailsInternal(AdminSmtpConfig smtpConfig) {
        log.info("=== Running email scheduler dynamically based on DB time ===");
        try {
            // 🔹 Double-check that the SMTP config is still active
            if (!smtpConfig.isActive()) {
                log.info("⚠️ SMTP config became inactive just now. Skipping this cycle.");
                return;
            }

            JavaMailSenderImpl mailSender = configureMailSender(smtpConfig);

            List<SupportAgent> activeAgents = supportAgentRepository.findByActiveTrue();
            if (activeAgents.isEmpty()) {
                log.warn("No active support agents found");
                return;
            }

            int successCount = 0, skipCount = 0, errorCount = 0;
            for (SupportAgent agent : activeAgents) {
                try {
                    List<Complaint> assignedComplaints = complaintRepository.findBySupportAgent(agent);
                    if (assignedComplaints.isEmpty()) {
                        skipCount++;
                        continue;
                    }

                    String emailBody = buildHtmlEmailBody(agent, assignedComplaints);
                    String subject = "Your Assigned Ticket Summary - " +
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                    sendEmail(mailSender, smtpConfig.getEmail(), agent.getEmail(), subject, emailBody);
                    successCount++;

                } catch (Exception e) {
                    log.error("❌ Failed to send email to {}: {}", agent.getEmail(), e.getMessage());
                    errorCount++;
                }
            }

            log.info("=== Finished scheduled task - Success: {}, Skipped: {}, Errors: {} ===",
                    successCount, skipCount, errorCount);

        } catch (Exception e) {
            log.error("❌ Critical error in scheduled task: {}", e.getMessage(), e);
        }
    }


    private JavaMailSenderImpl configureMailSender(AdminSmtpConfig smtpConfig) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(smtpConfig.getHost());
        mailSender.setPort(smtpConfig.getPort());
        mailSender.setUsername(smtpConfig.getEmail());
        mailSender.setPassword(smtpConfig.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");
        props.put("mail.debug", "true"); // Set to false in production

        return mailSender;
    }
    
    private String buildHtmlEmailBody(SupportAgent agent, List<Complaint> complaints) {
        // Group complaints by status
        Map<ComplaintStatus, Long> countsByStatus = complaints.stream()
                .filter(c -> c.getStatus() != null)
                .collect(Collectors.groupingBy(Complaint::getStatus, Collectors.counting()));

        // Total ticket count
        long totalTickets = complaints.size();

        StringBuilder body = new StringBuilder();
        body.append("<!DOCTYPE html><html><body style='font-family: Arial, sans-serif; padding: 20px;'>");
        body.append("<div style='max-width: 800px; margin: 0 auto;'>");

        body.append("<h2 style='color: #333;'>Hi ").append(agent.getName()).append(",</h2>");
        body.append("<p style='font-size: 16px;'>Here is the summary of your tickets by status:</p>");

        // 🔹 Total ticket count box
        body.append("<div style='background-color: #f0f8ff; padding: 15px; border-radius: 5px; margin: 20px 0;'>");
        body.append("<p style='font-size: 18px; margin: 0;'><b>Total Tickets: ")
            .append(totalTickets)
            .append("</b></p>");
        body.append("</div>");

        // 🔹 Status Breakdown Table (ASSIGNED removed)
        body.append("<table border='1' style='border-collapse: collapse; width: 60%; font-size: 14px;'>");
        body.append("<thead style='background-color: #007BFF; color: white;'>");
        body.append("<tr><th style='padding: 10px;'>Status</th><th style='padding: 10px;'>Count</th></tr>");
        body.append("</thead><tbody>");

        for (ComplaintStatus status : ComplaintStatus.values()) {
            if (status == ComplaintStatus.ASSIGNED) continue; // ✅ skip ASSIGNED
            long count = countsByStatus.getOrDefault(status, 0L);
            body.append("<tr>")
                .append("<td style='padding: 8px;'>").append(status.name()).append("</td>")
                .append("<td style='padding: 8px;'>").append(count).append("</td>")
                .append("</tr>");
        }

        body.append("</tbody></table>");

        // 🔹 Ticket Details Table
        body.append("<h3 style='color: #333; margin-top: 30px;'>Ticket Details:</h3>");
        body.append("<table border='1' style='border-collapse: collapse; width: 100%; font-size: 14px;'>");
        body.append("<thead style='background-color: #4CAF50; color: white;'>");
        body.append("<tr><th style='padding: 12px;'>Ticket ID</th>");
        body.append("<th style='padding: 12px;'>Description</th>");
        body.append("<th style='padding: 12px;'>Status</th>");
        body.append("<th style='padding: 12px;'>BrandName</th>");

        body.append("<th style='padding: 12px;'>Priority</th></tr></thead><tbody>");

        for (Complaint c : complaints) {
            if (c.getStatus() == ComplaintStatus.ASSIGNED) continue; // ✅ Skip ASSIGNED tickets in details too
            String bgColor = switch (c.getStatus()) {
                case OPEN -> "#fff3cd";
                case IN_PROGRESS -> "#cfe2ff";
                case RESOLVED -> "#d1e7dd";
                case CLOSED -> "#f8d7da";
                default -> "#ffffff";
            };
            body.append("<tr style='background-color: ").append(bgColor).append(";'>")
                .append("<td style='padding: 10px;'>").append(c.getTicketId()).append("</td>")
                .append("<td style='padding: 10px;'>").append(c.getDescription()).append("</td>")
                .append("<td style='padding: 10px;'>").append(c.getStatus().name()).append("</td>")
                .append("<td style='padding: 10px;'>").append(c.getBrandName()).append("</td>")

                .append("<td style='padding: 10px;'>").append(c.getPriority()).append("</td>")
                .append("</tr>");
        }

        body.append("</tbody></table>");
        body.append("<p style='margin-top: 30px; font-size: 14px; color: #666;'>Please review these tickets in the support system.</p>");
        body.append("<hr style='margin: 30px 0; border-top: 1px solid #ddd;'>");
        body.append("<p style='font-size: 12px; color: #999;'>Automated report generated at ")
            .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            .append("</p>");
        body.append("</div></body></html>");

        return body.toString();
    }



  /*  private String buildHtmlEmailBody(SupportAgent agent, List<Complaint> complaints) {
        Map<ComplaintStatus, Long> countsByStatus = complaints.stream()
                .filter(c -> c.getStatus() != null)
                .collect(Collectors.groupingBy(Complaint::getStatus, Collectors.counting()));

        StringBuilder body = new StringBuilder();
        body.append("<!DOCTYPE html><html><body style='font-family: Arial, sans-serif; padding: 20px;'>");
        body.append("<div style='max-width: 800px; margin: 0 auto;'>");
        body.append("<h2 style='color: #333;'>Hi ").append(agent.getName()).append(",</h2>");
        body.append("<p style='font-size: 16px;'>Here is your summary of currently assigned tickets requiring action:</p>");
        body.append("<div style='background-color: #f0f8ff; padding: 15px; border-radius: 5px; margin: 20px 0;'>");
        body.append("<p style='font-size: 18px; margin: 0;'><b>Total Active Tickets: ").append(complaints.size()).append("</b></p>");
        body.append("</div>");

        if (!countsByStatus.isEmpty()) {
            body.append("<h3 style='color: #333;'>Status Breakdown:</h3>");
            body.append("<ul style='font-size: 16px;'>");
            countsByStatus.forEach((status, count) ->
                    body.append("<li><b>").append(status.name()).append(":</b> ").append(count).append("</li>")
            );
            body.append("</ul>");
        }

        body.append("<h3 style='color: #333;'>Ticket Details:</h3>");
        body.append("<table border='1' style='border-collapse: collapse; width: 100%; font-size: 14px;'>");
        body.append("<thead style='background-color: #4CAF50; color: white;'>");
        body.append("<tr><th style='padding: 12px; text-align: left;'>Ticket ID</th>");
        body.append("<th style='padding: 12px; text-align: left;'>Status</th></tr></thead>");
        body.append("<tbody>");
        
        complaints.forEach(c -> {
            String bgColor = c.getStatus() == ComplaintStatus.OPEN ? "#fff3cd" : "#ffffff";
            body.append("<tr style='background-color: ").append(bgColor).append(";'>");
            body.append("<td style='padding: 10px; border-bottom: 1px solid #ddd;'>").append(c.getTicketId()).append("</td>");
            body.append("<td style='padding: 10px; border-bottom: 1px solid #ddd;'>")
                .append(c.getStatus() != null ? c.getStatus().name() : "N/A").append("</td>");
            body.append("</tr>");
        });
        
        body.append("</tbody></table>");
        body.append("<p style='margin-top: 30px; font-size: 14px; color: #666;'>");
        body.append("Please review these tickets in the support system at your earliest convenience.</p>");
        body.append("<hr style='margin: 30px 0; border: none; border-top: 1px solid #ddd;'>");
        body.append("<p style='font-size: 12px; color: #999;'>This is an automated message. Generated at ");
        body.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("</p>");
        body.append("</div></body></html>");
        
        return body.toString();
    }
*/
    private void sendEmail(JavaMailSenderImpl mailSender, String from, String to, 
                          String subject, String body) throws Exception {
        log.info("    Preparing email - From: {}, To: {}, Subject: {}", from, to, subject);
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
        
        log.info("    Sending email...");
        mailSender.send(message);
        log.info("    Email sent successfully!");
    }
}