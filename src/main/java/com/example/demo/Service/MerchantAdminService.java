package com.example.demo.Service;
import org.apache.poi.ss.usermodel.*; // Import Apache POI classes

import com.example.demo.Repository.AdminBrandRepository;
import com.example.demo.Repository.MerchantRepository;
import com.example.demo.dto.CreateMerchantBySubAdminBrandRequest;
import com.example.demo.dto.CreateMerchantRequest;
import com.example.demo.dto.SubAdminSummaryDto;
import com.example.demo.entity.AdminBrand;
import com.example.demo.entity.Merchant;
import com.example.demo.entity.Role;
import lombok.RequiredArgsConstructor;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException; // Import for security
@Service
@RequiredArgsConstructor
public class MerchantAdminService {

    private final MerchantRepository merchantRepository;
    private final AdminBrandRepository brandRepository;

    public Merchant createMerchant(CreateMerchantRequest request) {
        // 1. Find the brand by the provided name
        AdminBrand brand = brandRepository.findByBrandName(request.getBrandName())
                .orElseThrow(() -> new RuntimeException("Brand '" + request.getBrandName() + "' not found."));

        // 2. Create the new merchant object
        Merchant newMerchant = new Merchant();
        newMerchant.setMerchantName(request.getMerchantName());
        newMerchant.setContactNumber(request.getContactNumber());
        newMerchant.setBusinessName(request.getBusinessName());
        newMerchant.setRole(Role.MERCHANT); // Set the role
        newMerchant.setBrand(brand); // Change from setId() to setBrand()

        // 3. Save the new merchant
        return merchantRepository.save(newMerchant);
    }

    //active or deactive account merchant logic admin site 
    
 // ✅ ADD THIS METHOD
    public Merchant updateMerchantStatus(String contactNumber, boolean isActive) {
        // 1. Find the merchant by their contact number
        Merchant merchant = merchantRepository.findByContactNumber(contactNumber)
                .orElseThrow(() -> new RuntimeException("Merchant not found with contact number: " + contactNumber));
        
        // 2. Update their active status
        merchant.setActive(isActive);
        
        // 3. Save and return the updated merchant
        return merchantRepository.save(merchant);
    }
   

    public void deleteMerchant(Long id) {
        merchantRepository.deleteById(id);
    }
    //bulk 
 // ✅ ADD THIS NEW METHOD FOR BULK UPLOAD admin will be create 
    public List<String> createMerchantsFromExcel(MultipartFile file) {
        List<String> results = new ArrayList<>();
        List<Merchant> merchantsToSave = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0); // Assuming data is on the first sheet
            Iterator<Row> rows = sheet.iterator();

            // Skip header row
            if (rows.hasNext()) {
                rows.next();
            }

            int rowNumber = 1; // Start counting from the first data row
            while (rows.hasNext()) {
                rowNumber++;
                Row currentRow = rows.next();
                try {
                    String merchantName = getCellValueAsString(currentRow.getCell(0)); // Column A
                    String contactNumber = getCellValueAsString(currentRow.getCell(1)); // Column B
                    String businessName = getCellValueAsString(currentRow.getCell(2)); // Column C
                    String brandName = getCellValueAsString(currentRow.getCell(3)); // Column D

                    // Basic validation
                    if (merchantName.isEmpty() || contactNumber.isEmpty() || brandName.isEmpty()) {
                        results.add("Row " + rowNumber + ": Skipped - Missing required data (Merchant Name, Contact Number, Brand Name).");
                        continue;
                    }

                    // Check if merchant already exists by contact number
                    if (merchantRepository.findByContactNumber(contactNumber).isPresent()) {
                         results.add("Row " + rowNumber + ": Skipped - Merchant with contact number " + contactNumber + " already exists.");
                         continue;
                    }

                    // Find the brand
                    AdminBrand brand = brandRepository.findByBrandName(brandName)
                            .orElseThrow(() -> new RuntimeException("Brand '" + brandName + "' not found."));

                    Merchant newMerchant = new Merchant();
                    newMerchant.setMerchantName(merchantName);
                    newMerchant.setContactNumber(contactNumber);
                    newMerchant.setBusinessName(businessName);
                    newMerchant.setRole(Role.MERCHANT);
                    newMerchant.setBrand(brand);
                    newMerchant.setActive(true); // Default to active

                    merchantsToSave.add(newMerchant);
                    results.add("Row " + rowNumber + ": Prepared '" + merchantName + "' for creation.");

                } catch (Exception e) {
                    results.add("Row " + rowNumber + ": Error - " + e.getMessage());
                }
            }

            // Save all valid merchants in one go
            if (!merchantsToSave.isEmpty()) {
                merchantRepository.saveAll(merchantsToSave);
                results.add("Successfully created " + merchantsToSave.size() + " new merchants.");
            } else {
                 results.add("No new merchants were created.");
            }

        } catch (Exception e) {
            results.add("Failed to process Excel file: " + e.getMessage());
            e.printStackTrace(); // Log the full error
        }
        return results;
    }

    // Helper method to safely get cell value as String
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                // Handle numeric cells (like contact numbers) correctly
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                 // Handle formulas if necessary, here we just get the cached value
                return cell.getCachedFormulaResultType() == CellType.STRING ? cell.getStringCellValue().trim() : "";
            default:
                return "";
        }
    }
    
    //sub_admin will be create merchant 
    public Merchant createMerchantByBrandAdmin(CreateMerchantBySubAdminBrandRequest request, String brandAdminEmail) {
        // 1. Find the logged-in Brand Admin's Brand by their email
        AdminBrand brand = brandRepository.findByEmail(brandAdminEmail)
                .orElseThrow(() -> new RuntimeException("Brand Admin user not found or not associated with a brand."));

        // 2. Check if merchant contact number already exists
        if(merchantRepository.findByContactNumber(request.getContactNumber()).isPresent()){
            throw new RuntimeException("Merchant contact number already exists.");
        }

        // 3. Create the new merchant object
        Merchant newMerchant = new Merchant();
        newMerchant.setMerchantName(request.getMerchantName());
        newMerchant.setContactNumber(request.getContactNumber());
        newMerchant.setBusinessName(request.getBusinessName());
        newMerchant.setRole(Role.MERCHANT); // Set the role
        newMerchant.setBrand(brand); // ✅ Automatically associate with the logged-in brand admin's brand
        newMerchant.setActive(true); // Default to active

        // 4. Save the new merchant
        return merchantRepository.save(newMerchant);
    }
    //
 // ✅ ADD THIS NEW METHOD FOR BRAND ADMIN BULK UPLOAD merchant 
    public List<String> createMerchantsFromExcelForBrandAdmin(MultipartFile file, String brandAdminEmail) {
        List<String> results = new ArrayList<>();
        List<Merchant> merchantsToSave = new ArrayList<>();

        // 1. Find the brand associated with the logged-in Brand Admin
        AdminBrand brand = brandRepository.findByEmail(brandAdminEmail)
                .orElseThrow(() -> new RuntimeException("Brand Admin user not found or not associated with a brand."));

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Skip header row
            if (rows.hasNext()) {
                rows.next();
            }

            int rowNumber = 1;
            while (rows.hasNext()) {
                rowNumber++;
                Row currentRow = rows.next();
                try {
                    // 2. Read data (Brand Name column is NOT needed here)
                    String merchantName = getCellValueAsString(currentRow.getCell(0)); // Column A
                    String contactNumber = getCellValueAsString(currentRow.getCell(1)); // Column B
                    String businessName = getCellValueAsString(currentRow.getCell(2)); // Column C

                    // Basic validation
                    if (merchantName.isEmpty() || contactNumber.isEmpty()) {
                        results.add("Row " + rowNumber + ": Skipped - Missing Merchant Name or Contact Number.");
                        continue;
                    }

                    // Check if merchant contact number already exists
                    if (merchantRepository.findByContactNumber(contactNumber).isPresent()) {
                         results.add("Row " + rowNumber + ": Skipped - Merchant contact number " + contactNumber + " already exists.");
                         continue;
                    }

                    Merchant newMerchant = new Merchant();
                    newMerchant.setMerchantName(merchantName);
                    newMerchant.setContactNumber(contactNumber);
                    newMerchant.setBusinessName(businessName);
                    newMerchant.setRole(Role.MERCHANT);
                    newMerchant.setBrand(brand); // ✅ Automatically set the brand
                    newMerchant.setActive(true);

                    merchantsToSave.add(newMerchant);
                    results.add("Row " + rowNumber + ": Prepared '" + merchantName + "' for creation under brand '" + brand.getBrandName() + "'.");

                } catch (Exception e) {
                    results.add("Row " + rowNumber + ": Error - " + e.getMessage());
                }
            }

            // Save valid merchants
            if (!merchantsToSave.isEmpty()) {
                merchantRepository.saveAll(merchantsToSave);
                results.add("Successfully created " + merchantsToSave.size() + " new merchants for brand '" + brand.getBrandName() + "'.");
            } else {
                 results.add("No new merchants were created.");
            }

        } catch (Exception e) {
            results.add("Failed to process Excel file: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }
    
    //sub_admin willl be get merchant will be brand name wise 
 // ✅ ADD THIS NEW METHOD
    public List<SubAdminSummaryDto> getMerchantsByBrandAdmin(String brandAdminEmail) {
        // 1. Find the logged-in Brand Admin's Brand by their email
        AdminBrand brand = brandRepository.findByEmail(brandAdminEmail)
                .orElseThrow(() -> new RuntimeException("Brand Admin user not found or not associated with a brand."));

        // 2. Find all merchants associated with this brand
        List<Merchant> merchants = merchantRepository.findByBrand(brand);

        // 3. Map the results to the DTO
        return merchants.stream().map(merchant -> {
        	SubAdminSummaryDto dto = new SubAdminSummaryDto();
            dto.setId(merchant.getId());
            dto.setMerchantName(merchant.getMerchantName());
            dto.setContactNumber(merchant.getContactNumber());
            dto.setBusinessName(merchant.getBusinessName());
            return dto;
        }).collect(Collectors.toList());
    }
   
  //delete merchant
 // ✅ ADD THIS NEW METHOD for deleting a merchant by Brand Admin
    public void deleteMerchantByBrandAdmin(Long merchantId, String brandAdminEmail) {
        // 1. Find the logged-in Brand Admin's Brand
        AdminBrand brand = brandRepository.findByEmail(brandAdminEmail)
                .orElseThrow(() -> new RuntimeException("Brand Admin user not found."));

        // 2. Find the merchant to be deleted
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("Merchant not found with ID: " + merchantId));

        // 3. 🔒 SECURITY CHECK: Ensure the merchant belongs to the correct brand
        if (merchant.getBrand() == null || !merchant.getBrand().getId().equals(brand.getId())) {
            throw new AccessDeniedException("You are not authorized to delete this merchant.");
        }

        // 4. Delete the merchant
        merchantRepository.delete(merchant);
    }
    
}
