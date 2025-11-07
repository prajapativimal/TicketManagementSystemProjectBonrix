package com.example.demo.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Repository.AdminBrandRepository;
import com.example.demo.Repository.AdminRepository;
import com.example.demo.Repository.ComplaintRepository;
import com.example.demo.Repository.MerchantRepository;
import com.example.demo.Repository.SupportAgentRepository;
import com.example.demo.Repository.TicketMessageRepository;
import com.example.demo.dto.ConversationViewResponse;
import com.example.demo.dto.SendMessageRequest;
import com.example.demo.dto.UnseenMessageCountResponse;
import com.example.demo.dto.DashboardUnseenCountResponse;
import com.example.demo.entity.Admin;
import com.example.demo.entity.AdminBrand;
import com.example.demo.entity.Merchant;
import com.example.demo.entity.SenderRole;
import com.example.demo.entity.SupportAgent;
import com.example.demo.entity.TicketMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketChatService {

    private final TicketMessageRepository messageRepository; // ✅ Use the new repository
    private final ComplaintRepository complaintRepository;
    private final MerchantRepository merchantRepository;
    private final SupportAgentRepository supportAgentRepository;
    private final AdminRepository adminRepository;
    private final AdminBrandRepository adminBrandRepository; // ✅ Inject this repository

    
    private final Path rootLocation = Paths.get("uploads/messages");

    // --- Sending Messages (Now calls one simple helper) ---

 // ✅ 1. Update the method signature to accept the full request object
    public void sendMessageByMerchant(String ticketId, SendMessageRequest request, String merchantContact) throws IOException {
        Merchant merchant = merchantRepository.findByContactNumber(merchantContact).orElseThrow();
        
        // ✅ 2. Handle optional attachments
        String fileNames = "";
        if (request.getAttachments() != null && !request.getAttachments()[0].isEmpty()) {
            fileNames = saveAttachmentsIfPresent(request.getAttachments(), ticketId);
        }

        saveMessage(ticketId, request.getContent(), merchant.getId(), merchant.getMerchantName(), SenderRole.MERCHANT, fileNames);
    }
    
    
    public void sendMessageByAdmin(String ticketId, SendMessageRequest request, String adminEmail) throws IOException {
        Admin admin = adminRepository.findByEmail(adminEmail).orElseThrow();
        // Handle optional attachments
        String fileNames = saveAttachmentsIfPresent(request.getAttachments(), ticketId); // ✅ CORRECTED LINE
        // Pass the correct parameters to the helper
        saveMessage(ticketId, request.getContent(), admin.getId(), admin.getName(), SenderRole.ADMIN, fileNames);
    }

    public void sendMessageBySupportAgent(String ticketId, SendMessageRequest request, String agentEmail) throws IOException {
        SupportAgent agent = supportAgentRepository.findByEmail(agentEmail).orElseThrow();
        // Handle optional attachments
        String fileNames = saveAttachmentsIfPresent(request.getAttachments(), ticketId); // ✅ CORRECTED LINE
        // Pass the correct parameters to the helper
        saveMessage(ticketId, request.getContent(), agent.getId(), agent.getName(), SenderRole.SUPPORT_AGENT, fileNames);
    }
    
    // ✅ ADD THIS NEW METHOD FOR THE BRAND_ADMIN
    public void sendMessageByBrandAdmin(String ticketId, SendMessageRequest request, String brandAdminEmail) throws IOException {
        // Find the brand admin by their email (which is their login username)
        AdminBrand brand = adminBrandRepository.findByEmail(brandAdminEmail)
                .orElseThrow(() -> new RuntimeException("Brand Admin user not found."));

        // Handle optional attachments using the existing helper
        String fileNames = saveAttachmentsIfPresent(request.getAttachments(), ticketId);

        // Save the message with the correct sender details
        saveMessage(ticketId, request.getContent(), brand.getId(), brand.getBrandName(), SenderRole.BRAND_ADMIN, fileNames);
    }


    
 // In MessageService.java
    public List<ConversationViewResponse> getMessagesForTicket(String ticketId, Authentication authentication) {
        // ... (Your existing security check logic) ...
        List<TicketMessage> messages = messageRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);

        return messages.stream().map(msg -> {
            // Build the attachment URLs
            List<String> urls = Collections.emptyList();
            String attachments = msg.getAttachments();
            if (attachments != null && !attachments.isEmpty()) {
                urls = Arrays.stream(attachments.split(","))
                        .map(fileName -> "/messages/" + msg.getTicketId() + "/" + fileName.trim())
                        .collect(Collectors.toList());
            }

            return new ConversationViewResponse(
                    msg.getId(),
                    msg.getContent(),
                    msg.getSenderName(),
                    msg.getSenderRole(),
                    msg.getCreatedAt(),
                    urls // Pass the generated URLs
            );
        }).collect(Collectors.toList());
    }

    // Get unseen message count for a specific ticket based on user role
    public UnseenMessageCountResponse getUnseenMessageCount(String ticketId, Authentication authentication) {
        SenderRole currentUserRole = getCurrentUserRole(authentication);
        List<SenderRole> senderRolesToCount = getSenderRolesToCount(currentUserRole);
        
        long unseenCount = messageRepository.countUnseenMessagesBySenderRoles(ticketId, senderRolesToCount);
        
        return UnseenMessageCountResponse.builder()
                .ticketId(ticketId)
                .unseenCount(unseenCount)
                .hasUnseenMessages(unseenCount > 0)
                .build();
    }

    // Mark messages as seen for a specific ticket based on user role
    public void markMessagesAsSeen(String ticketId, Authentication authentication) {
        SenderRole currentUserRole = getCurrentUserRole(authentication);
        List<SenderRole> senderRolesToMark = getSenderRolesToCount(currentUserRole);
        
        messageRepository.markMessagesAsSeenBySenderRoles(ticketId, senderRolesToMark);
    }

    // Get dashboard unseen message count for all tickets
    public DashboardUnseenCountResponse getDashboardUnseenCount(Authentication authentication) {
        SenderRole currentUserRole = getCurrentUserRole(authentication);
        List<SenderRole> senderRolesToCount = getSenderRolesToCount(currentUserRole);
        
        // Get all ticket IDs with unseen messages
        List<String> ticketIdsWithUnseen = messageRepository.findTicketIdsWithUnseenMessagesBySenderRoles(senderRolesToCount);
        
        // Create ticket unseen counts
        List<DashboardUnseenCountResponse.TicketUnseenCount> ticketCounts = ticketIdsWithUnseen.stream()
                .map(ticketId -> {
                    long unseenCount = messageRepository.countUnseenMessagesBySenderRoles(ticketId, senderRolesToCount);
                    return DashboardUnseenCountResponse.TicketUnseenCount.builder()
                            .ticketId(ticketId)
                            .unseenCount(unseenCount)
                            .hasUnseenMessages(unseenCount > 0)
                            .build();
                })
                .collect(Collectors.toList());
        
        // Calculate total unseen messages
        long totalUnseen = messageRepository.countTotalUnseenMessagesBySenderRoles(senderRolesToCount);
        
        return DashboardUnseenCountResponse.builder()
                .totalUnseenMessages(totalUnseen)
                .ticketUnseenCounts(ticketCounts)
                .build();
    }

    // Helper method to determine current user role
    private SenderRole getCurrentUserRole(Authentication authentication) {
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        
        // Remove ROLE_ prefix if present
        if (role.startsWith("ROLE_")) {
            role = role.substring(5);
        }
        
        switch (role) {
            case "MERCHANT":
                return SenderRole.MERCHANT;
            case "ADMIN":
                return SenderRole.ADMIN;
            case "SUPPORT_AGENT":
                return SenderRole.SUPPORT_AGENT;
            case "BRAND_ADMIN":
                return SenderRole.BRAND_ADMIN;
            default:
                throw new RuntimeException("Unknown user role: " + role);
        }
    }

    // Get sender roles to count based on current user role
    private List<SenderRole> getSenderRolesToCount(SenderRole currentUserRole) {
        switch (currentUserRole) {
            case MERCHANT:
                // Merchant sees messages from: ADMIN, BRAND_ADMIN, SUPPORT_AGENT
                return Arrays.asList(SenderRole.ADMIN, SenderRole.BRAND_ADMIN, SenderRole.SUPPORT_AGENT);
                
            case ADMIN:
                // Admin sees messages from: MERCHANT, BRAND_ADMIN, SUPPORT_AGENT
                return Arrays.asList(SenderRole.MERCHANT, SenderRole.BRAND_ADMIN, SenderRole.SUPPORT_AGENT);
                
            case SUPPORT_AGENT:
                // Support Agent sees messages from: MERCHANT, BRAND_ADMIN
                return Arrays.asList(SenderRole.MERCHANT, SenderRole.BRAND_ADMIN);
                
            case BRAND_ADMIN:
                // Brand Admin sees messages from: MERCHANT, ADMIN, SUPPORT_AGENT
                return Arrays.asList(SenderRole.MERCHANT, SenderRole.ADMIN, SenderRole.SUPPORT_AGENT);
                
            default:
                return Collections.emptyList();
        }
    }

    // This helper was missing the "attachments" parameter in your provided code
    private void saveMessage(String ticketId, String content, Long senderId, String senderName, SenderRole role, String attachments) {
        complaintRepository.findByTicketId(ticketId).orElseThrow(() -> new RuntimeException("Ticket not found"));

        TicketMessage message = TicketMessage.builder()
                .ticketId(ticketId)
                .content(content)
                .senderId(senderId)
                .senderName(senderName)
                .senderRole(role)
                .createdAt(LocalDateTime.now())
                .attachments(attachments) // Save the filenames
                .build();
        messageRepository.save(message);
    }

    private String saveAttachmentsIfPresent(MultipartFile[] files, String ticketId) throws IOException {
        if (files == null || files[0].isEmpty()) {
            return null; // No files to save
        }
        
        Path ticketDirectory = rootLocation.resolve(ticketId);
        Files.createDirectories(ticketDirectory);

        return java.util.Arrays.stream(files)
                .map(file -> {
                    String filename = StringUtils.cleanPath(file.getOriginalFilename());
                    try {
                        Path destinationFile = ticketDirectory.resolve(filename).normalize().toAbsolutePath();
                        
                        // ✅ Check if file already exists and delete it for override
                        if (Files.exists(destinationFile)) {
                            Files.delete(destinationFile);
                        }
                        
                        // Save the new file (this will override the previous one)
                        Files.copy(file.getInputStream(), destinationFile);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to store file " + filename, e);
                    }
                    return filename;
                })
                .collect(Collectors.joining(","));
    }
}