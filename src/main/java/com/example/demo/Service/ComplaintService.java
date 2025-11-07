package com.example.demo.Service;

import java.nio.file.Files;
import java.nio.file.Path;

import com.example.demo.Repository.AdminBrandRepository;
import com.example.demo.Repository.AdminCategoryRepository;
import com.example.demo.Repository.AdminRepository;
import com.example.demo.Repository.ComplaintRepository;
import com.example.demo.Repository.DeviceModelNumberRepository;
import com.example.demo.Repository.IssueRepository;
import com.example.demo.Repository.MerchantRepository;
import com.example.demo.Repository.PincodeRepository;
import com.example.demo.Repository.SupportAgentRepository;
import com.example.demo.dto.ComplaintRequest;
import com.example.demo.entity.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.UUID;
import org.springframework.util.StringUtils; // Import StringUtils for cleaning paths

import org.springframework.util.StringUtils; // Import StringUtils for cleaning paths

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepo;
    private final MerchantRepository merchantRepo;
    // ✅ 1. INJECT THE DEVICE MODEL REPOSITORY
    private final DeviceModelNumberRepository deviceModelNumberRepository;
    private final PincodeRepository pincodeRepository; // ✅ 1. INJECT THE NEW REPOSITORY
    private final AdminRepository adminRepository;
    private final AdminBrandRepository adminbrandRepository; // ✅ Inject BrandRepository

    private final AdminCategoryRepository adminCategoryRepository;
    private final IssueRepository issueRepository; // Assuming this exists
    
  //  private final ComplaintRepository complaintRepository;
    private final SupportAgentRepository supportAgentRepository;
    
    private final DeviceModelNumberRepository repository;
    

    
    // The root directory for all uploads
    private final Path rootLocation = Paths.get("uploads/images");

    // Submit complaint
    public Complaint submitComplaint(String contactNumber, ComplaintRequest request) throws IOException {
        Merchant merchant = merchantRepo.findByContactNumber(contactNumber)
                .orElseThrow(() -> new RuntimeException("Merchant not found"));
        
     // ✅ 2. GET THE BRAND FROM THE MERCHANT
        AdminBrand brand = merchant.getBrand();
        if (brand == null) {
            throw new RuntimeException("Merchant is not associated with any brand.");
        }
        
        // ✅ 2. ADD THE VALIDATION CALL HERE
        validateCategoryAndIssue(request.getCategoryName(), request.getIssues());

        // ✅ 2. ADD VALIDATION FOR MODEL NUMBER
        validateModelNumber(request.getModelNumber());
        
     // ✅ 2. ADD THE PINCODE LOOKUP LOGIC HERE
        // If a pincode is provided in the request...
        if (request.getPincode() != null && !request.getPincode().isEmpty()) {
            // ...find it in the database.
            Pincode pincodeData = pincodeRepository.findById(request.getPincode())
                    .orElseThrow(() -> new RuntimeException("Validation Error: Invalid Pincode '" + request.getPincode() + "' provided."));
            
            // ...and automatically set the city and state from the lookup.
            request.setCity(pincodeData.getCity());
            request.setState(pincodeData.getState());
        }

        // ✅ 1. GENERATE TICKET ID FIRST
        String ticketId = "TICKET-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // ✅ 2. SAVE ATTACHMENTS using the ticketId
        String fileNames = "";
        // Check if attachments are present and not empty
        if (request.getAttachments() != null && !request.getAttachments()[0].isEmpty()) {
            fileNames = saveFiles(request.getAttachments(), ticketId);
        }
        
        //autoassign compiant for support-agent wise bran-name 
        
     // ✅ 2. --- START: AUTO-ASSIGNMENT LOGIC ---
        // Find the support agent assigned to this merchant's brand
        Optional<SupportAgent> agentOpt = supportAgentRepository.findByBrand(brand);
        SupportAgent assignedAgent = agentOpt.orElse(null); // Get agent if present, or null if not

        // Set status based on whether an agent was found
        ComplaintStatus newStatus = (assignedAgent != null) ? ComplaintStatus.ASSIGNED : ComplaintStatus.OPEN;
        // ✅ --- END: AUTO-ASSIGNMENT LOGIC ---
//
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime slaEnd = now.plusHours(72);

        Complaint complaint = Complaint.builder()
                .ticketId(ticketId)
                .merchantId(merchant.getId())
                .merchantName(merchant.getMerchantName())
                .brandName(brand.getBrandName()) // Use the brand object, not the request
                
                .supportAgent(assignedAgent) // ✅ 3. Set the agent
                .status(ComplaintStatus.OPEN) // ✅ 4. Set the new status
                
                .contactNumber(merchant.getContactNumber())
                .deviceOrderId(request.getDeviceOrderId())
                .category(request.getCategory())
                .description(request.getDescription())
                // ✅ CHANGE THIS LINE
                .priority(Priority.NORMAL) // Set the default priority here
                .attachments(fileNames) // Store the comma-separated filenames
                .status(ComplaintStatus.OPEN)
                .createdAt(now)
                .updatedAt(now)
                .slaEndTime(slaEnd)
             // --- Set new fields from the request DTO ---
                .serialNumber(request.getSerialNumber())
                .transactionId(request.getTransactionId())
//                .orderId(request.getOrderId())
                .storeId(request.getStoreId())
                .address(request.getAddress())
                .contactNumber(request.getContactNumber()) // Use contact number from the form
                // ✅ ADD THESE THREE LINES TO MAP THE NEW DATA
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                //
                // ✅ ADD THESE TWO LINES TO MAP THE NEW DATA
                .categoryName(request.getCategoryName())
                .issueName(request.getIssues())
                .modelNumber(request.getModelNumber())


                .build();

        return complaintRepo.save(complaint);
    }

 // ✅ 4. ADD THIS NEW PRIVATE METHOD FOR VALIDATION
    private void validateModelNumber(String modelNumber) {
        // Only validate if a model number was provided
        if (modelNumber != null && !modelNumber.isEmpty()) {
            boolean modelExists = deviceModelNumberRepository.existsByModelNumber(modelNumber);
            if (!modelExists) {
                throw new RuntimeException("Validation Error: Device Model Number '" + modelNumber + "' does not exist.");
            }
        }
    }

 // ✅ 3. ADD THIS NEW PRIVATE METHOD FOR VALIDATION
    private void validateCategoryAndIssue(String categoryName, String issueName) {
        // Only perform validation if a categoryName is provided
        if (categoryName != null && !categoryName.isEmpty()) {
            
            // Find the category by its name. Throw an error if it doesn't exist.
            AdminCategory category = adminCategoryRepository.findByCategoryName(categoryName)
                    .orElseThrow(() -> new RuntimeException("Validation Error: Category '" + categoryName + "' not found."));

            // If an issue name is also provided, validate it
            if (issueName != null && !issueName.isEmpty()) {
                // Check if any issue within this category matches the provided issue name
                boolean issueExistsInCategory = category.getIssues().stream()
                        .anyMatch(issue -> issue.getIssueName().equalsIgnoreCase(issueName));
                
                if (!issueExistsInCategory) {
                    throw new RuntimeException("Validation Error: Issue '" + issueName + "' does not belong to category '" + categoryName + "'.");
                }
            }
        }
	}

	// ✅ 3. REWRITE THE saveFiles METHOD
    private String saveFiles(MultipartFile[] files, String ticketId) throws IOException {
        // Create the main upload directory if it doesn't exist
        if (!Files.exists(rootLocation)) {
            Files.createDirectories(rootLocation);
        }

        // Create a sub-directory for the specific ticket
        Path ticketDirectory = rootLocation.resolve(ticketId);
        Files.createDirectories(ticketDirectory);

        return java.util.Arrays.stream(files)
                .map(file -> {
                    // Sanitize the filename to prevent security issues (e.g., ../../filename)
                    String filename = StringUtils.cleanPath(file.getOriginalFilename());
                    
                    try {
                        if (filename.contains("..")) {
                            throw new IOException("Cannot store file with relative path outside current directory " + filename);
                        }
                        // Copy the file to the ticket-specific directory
                        Path destinationFile = ticketDirectory.resolve(filename).normalize().toAbsolutePath();
                        Files.copy(file.getInputStream(), destinationFile);

                    } catch (IOException e) {
                        throw new RuntimeException("Failed to store file " + filename, e);
                    }
                    return filename; // Return just the filename
                })
                .collect(Collectors.joining(","));
    }
    
  
    // Get all complaints for merchant
    public List<Complaint> getMerchantComplaints(String contactNumber) {
        Merchant merchant = merchantRepo.findByContactNumber(contactNumber)
                .orElseThrow(() -> new RuntimeException("Merchant not found"));

        return complaintRepo.findByMerchantId(merchant.getId());
    }
    //
    public List<Complaint> getComplaintsForAgent(String agentEmail) {
        // Find the agent using their email (which is the username in the JWT)
        SupportAgent agent = supportAgentRepository.findByEmail(agentEmail)
                .orElseThrow(() -> new RuntimeException("Agent not found with email: " + agentEmail));
        
        // You'll need to add this method to your ComplaintRepository
        return complaintRepo.findBySupportAgent(agent);
    }
    
    // ✅ Update complaint status
    public Complaint updateComplaintStatus(Long complaintId, ComplaintStatus newStatus) {
        Complaint complaint = complaintRepo.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        complaint.setStatus(newStatus);
        complaint.setUpdatedAt(LocalDateTime.now());

        return complaintRepo.save(complaint);
    }
    
    
    public List<DeviceModelNumber> getAllModels() {
        return repository.findAll();
    }
    
    // ✅ UPDATE THIS METHOD subadmin
    public List<Complaint> getComplaintsForBrand(String brandEmail) {
        // 1. Find the brand using the EMAIL from the JWT
        AdminBrand brand = adminbrandRepository.findByEmail(brandEmail)
                .orElseThrow(() -> new RuntimeException("Brand not found for the logged-in user."));
        
        // 2. Use the brand's name to find all associated complaints
        return complaintRepo.findByBrandName(brand.getBrandName());
    }
//status will be changes open to inprocess 
    
    public String updateStatusByTicketId(String email, String ticketId) {
        // 1️⃣ Try to find Support Agent first
        Optional<SupportAgent> supportAgentOpt = supportAgentRepository.findByEmail(email);

        // 2️⃣ Try to find Admin if not found as Support Agent
        Optional<Admin> adminOpt = adminRepository.findByEmail(email);

        if (supportAgentOpt.isEmpty() && adminOpt.isEmpty()) {
            throw new RuntimeException("User not found with email: " + email);
        }

        // 3️⃣ Find complaint by ticket ID
        Complaint complaint = complaintRepo.findByTicketId(ticketId)
                .orElseThrow(() -> new RuntimeException("Complaint not found with ticket ID: " + ticketId));

        // 4️⃣ Only update if status is OPEN
        if (complaint.getStatus() == ComplaintStatus.OPEN) {
            complaint.setStatus(ComplaintStatus.IN_PROGRESS);
            complaint.setUpdatedAt(LocalDateTime.now());
            complaintRepo.save(complaint);

            return "Complaint " + ticketId + " successfully updated from OPEN to IN_PROGRESS.";
        } else {
            return "Complaint " + ticketId + " is already " + complaint.getStatus() + ".";
        }
    }

    
    //sub-admin can be changes status for closed or resolved 
    public String updateComplaintStatusByBrandAdmin(String ticketId, String newStatus) {
        Complaint complaint = complaintRepo.findByTicketId(ticketId)
                .orElseThrow(() -> new RuntimeException("Complaint not found for Ticket ID: " + ticketId));

        // ✅ Only allow updates if current status is CLOSED or RESOLVED
        if (complaint.getStatus() != ComplaintStatus.CLOSED &&
            complaint.getStatus() != ComplaintStatus.RESOLVED) {
            return "Status cannot be changed. Only CLOSED or RESOLVED complaints can be updated by Brand Admin.";
        }

        // ✅ Only allow VERIFIED or REOPENED
        if (!newStatus.equalsIgnoreCase("VERIFIED") &&
            !newStatus.equalsIgnoreCase("REOPENED")) {
            return "Invalid status. Allowed statuses are: VERIFIED or REOPENED.";
        }

        // ✅ Update status
        complaint.setStatus(ComplaintStatus.valueOf(newStatus.toUpperCase()));
        complaint.setUpdatedAt(LocalDateTime.now());
        complaintRepo.save(complaint);

        return "Complaint " + ticketId + " successfully updated to " + newStatus.toUpperCase() + ".";
    }
    
    
    //reopen for only closed status other status does not changes  merchant 
    public String reopenComplaintByMerchant(String contactNumber, String ticketId) {
        // 1️⃣ Find merchant by contact number or email
        Merchant merchant = merchantRepo.findByContactNumber(contactNumber)
                .orElseThrow(() -> new RuntimeException("Merchant not found"));

        // 2️⃣ Find complaint by ticket ID and verify ownership
        Complaint complaint = complaintRepo.findByTicketId(ticketId)
                .orElseThrow(() -> new RuntimeException("Complaint not found with ticket ID: " + ticketId));

        if (!complaint.getMerchantId().equals(merchant.getId())) {
            return "You are not authorized to modify this complaint.";
        }

        // 3️⃣ Allow reopen only if status = CLOSED
        if (complaint.getStatus() == ComplaintStatus.CLOSED) {
            complaint.setStatus(ComplaintStatus.REOPENED);
            complaint.setUpdatedAt(LocalDateTime.now());
            complaintRepo.save(complaint);
            return "Complaint " + ticketId + " successfully reopened.";
        }

        // 4️⃣ Block other statuses
        return "Complaint " + ticketId + " cannot be reopened. Only CLOSED complaints can be reopened.";
    }

    
    
}
