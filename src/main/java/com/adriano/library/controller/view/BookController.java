package com.adriano.library.controller.view;

import com.adriano.library.business.domain.entity.Book;
import com.adriano.library.business.logic.service.BookService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/books")
@PreAuthorize("hasRole('ADMIN')")
public class BookController extends BaseController<Book> {

    public BookController(BookService service) {
        super(service, "books");
    }

    private static String getString(MultipartFile file, String contentType, Set<String> allowedTypes) throws IOException {
        if (contentType == null || !allowedTypes.contains(contentType.toLowerCase())) {
            throw new IOException("Unsupported file type: " + contentType);
        }
        String original = Objects.requireNonNullElse(file.getOriginalFilename(), "");
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0 && dot < original.length() - 1) {
            String candidate = original.substring(dot + 1).toLowerCase();
            // map jpg/jpeg normalization
            if (candidate.equals("jpg") || candidate.equals("jpeg")) candidate = "jpg";
            if (candidate.matches("[a-z0-9]{1,10}") && Set.of("png", "jpg", "webp", "gif").contains(candidate)) {
                ext = "." + candidate;
            }
        }
        if (ext.isEmpty()) {
            // fallback based on content type
            if ("image/png".equals(contentType)) ext = ".png";
            else if ("image/jpeg".equals(contentType) || "image/jpg".equals(contentType)) ext = ".jpg";
            else if ("image/webp".equals(contentType)) ext = ".webp";
            else if ("image/gif".equals(contentType)) ext = ".gif";
        }
        return ext;
    }

    @Override
    protected Book newInstance() {
        return new Book();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String save(@ModelAttribute("item") Book book,
                       @RequestParam(name = "imageFile", required = false) MultipartFile file) throws IOException {
        Path rootLocation = Paths.get("src/main/uploads/books").toAbsolutePath().normalize();
        if (!Files.exists(rootLocation)) {
            Files.createDirectories(rootLocation);
        }

        boolean hasNewFile = file != null && !file.isEmpty();

        // If editing and no new file provided, preserve existing imagePath
        if (book.getId() != null && !hasNewFile) {
            Book existing = service.findById(book.getId()).orElse(null);
            if (existing != null) {
                book.setImagePath(existing.getImagePath());
            }
        }

        if (hasNewFile) {
            // Validate content type and extension
            String contentType = file.getContentType();
            Set<String> allowedTypes = Set.of("image/png", "image/jpeg", "image/jpg", "image/webp", "image/gif");
            String ext = getString(file, contentType, allowedTypes);

            // If replacing an existing image, try to delete the old file
            if (book.getId() != null) {
                Book existing = service.findById(book.getId()).orElse(null);
                if (existing != null && existing.getImagePath() != null) {
                    String oldName = existing.getImagePath().replaceFirst("^/uploads/books/", "");
                    Path oldPath = rootLocation.resolve(oldName).normalize();
                    if (oldPath.startsWith(rootLocation)) {
                        try {
                            Files.deleteIfExists(oldPath);
                        } catch (IOException ignored) {
                        }
                    }
                }
            }

            String filename = UUID.randomUUID() + ext; // discard user filename entirely
            Path destination = rootLocation.resolve(filename).normalize();
            if (!destination.startsWith(rootLocation)) {
                throw new IOException("Invalid file path");
            }
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            book.setImagePath("/uploads/books/" + filename);
        }

        return super.save(book);
    }
}
