package com.example.service;

import com.example.domain.Donor;
import com.example.repository.UserRepository;
import io.micronaut.email.Email;
import io.micronaut.email.EmailSender;
import io.micronaut.http.HttpResponse;
import jakarta.inject.Singleton;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Singleton
public class UserService {

    private final EmailSender<?, ?> emailSender;
    private final UserRepository userRepository;

    public UserService(EmailSender<?, ?> emailSender, UserRepository userRepository) {
        this.emailSender = emailSender;
        this.userRepository = userRepository;
    }

    public HttpResponse<Donor> getUserById(Long id) {
        return userRepository.findById(id)
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }
    public HttpResponse<Donor> requestBlood(Long id) {
        Optional<Donor> donor = userRepository.findById(id);
        emailSender.send(Email.builder()
                .to(donor.get().getEmail())
                .subject("Welcome to Pure Blood! "+ LocalDateTime.now())
                .body("your request is accepted"));
        return HttpResponse.ok();
    }

    public HttpResponse<Donor> saveUser(Donor donor) {
        Donor savedDonor = userRepository.save(donor);
        String emailHtml = "Dear  Donor,\n" +
                "Thank you for creating an account on Pure Blood! We are thrilled to have you join our community dedicated to making a difference in the lives of those in need of blood donations. Your participation plays a crucial role in helping us connect donors with recipients efficiently.\n" +
                "If you have any questions or need assistance, feel free to reach out. Together, we can save lives!\n" +
                "Best regards,\n" +
                "The Pure Blood Team";


        emailSender.send(Email.builder()
                .to(donor.getEmail())
                .subject("Welcome to Pure Blood! "+ LocalDateTime.now())
                .body(emailHtml)

        );
        //File pdfFile = generatePdfDocument(savedUser);
        // Upload the PDF to OCI Object Storage
        // UploadResponse<?> response = uploadFileToStorage(savedUser.getId(), pdfFile);
        //Return response with the uploaded document URLreturn HttpResponse.created(savedUser).header("PDF-URL", response.getETag()); } catch (IOException e) { return HttpResponse.serverError("Error generating PDF: " + e.getMessage()); } }
        // Generate a PDF document from the user detailsprivate File generatePdfDocument(UserModel user) throws IOException { String fileName = "profile_" + user.getId() + ".pdf"; File pdfFile = new File(fileName); PDDocument document = new PDDocument(); PDPage page = new PDPage(); document.addPage(page); PDPageContentStream contentStream = new PDPageContentStream(document, page); contentStream.beginText(); contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16); contentStream.newLineAtOffset(50, 750); contentStream.showText("User Profile Document"); contentStream.newLineAtOffset(0, -20); contentStream.setFont(PDType1Font.HELVETICA, 12); contentStream.showText("Username: " + user.getUsername()); contentStream.newLineAtOffset(0, -20); contentStream.showText("Type: " + user.getType()); contentStream.newLineAtOffset(0, -20); contentStream.showText("Location: " + user.getLocation()); contentStream.endText(); contentStream.close(); document.save(pdfFile); document.close(); return pdfFile; }
        // Upload the PDF file to OCI Object Storageprivate UploadResponse<?> uploadFileToStorage(Long userId, File file) throws IOException { try (FileInputStream inputStream = new FileInputStream(file)) { UploadRequest<?> uploadRequest = UploadRequest.fromInputStream(inputStream, file.getName(), file.length()); return objectStorage.upload(uploadRequest);
        return HttpResponse.created(savedDonor);
    }

    public HttpResponse<List<Donor>> getAllUsers() {
        List<Donor> donors = userRepository.findAll();
        return HttpResponse.ok(donors);
    }

    public HttpResponse<Donor> updateUser(Long id, Donor updatedDonor) {
        Optional<Donor> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            Donor donor = existingUser.get();
            donor.setFullName(updatedDonor.getFullName());
            donor.setType(updatedDonor.getType());
            donor.setLocation(updatedDonor.getLocation());
            donor.setPhoneNumber(updatedDonor.getPhoneNumber());
            donor.setEmail(updatedDonor.getEmail());

            Donor savedDonor = userRepository.update(donor);
            return HttpResponse.ok(savedDonor);
        } else {
            return HttpResponse.notFound();
        }
    }

    public HttpResponse<Void> deleteUser(Long id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
            return HttpResponse.noContent();
        } else {
            return HttpResponse.notFound();
        }
    }
    public HttpResponse<List<Donor>> searchUser(String location , String type) {
        if (!type.isEmpty() && !location.isEmpty()) {
            List<Donor> donors = userRepository.findByTypeAndLocation(type, location);
            return HttpResponse.ok(donors);
        } else if (!type.isEmpty()) {
            List<Donor> donors = userRepository.findByType(type);
            return HttpResponse.ok(donors);
        } else if (!location.isEmpty()) {
            List<Donor> donors = userRepository.findByLocation(location);
            return HttpResponse.ok(donors);
        } else {
            List<Donor> allDonors = userRepository.findAll();
            return HttpResponse.ok(allDonors);
        }
    }
}

