package com.SWP391_02.controller;

import com.SWP391_02.dto.AddPartRequest;
import com.SWP391_02.dto.MessageResponse;
import com.SWP391_02.dto.PartResponse;
import com.SWP391_02.dto.PartUpdateRequest;
import com.SWP391_02.service.TechnicianProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/tech/products")
@RequiredArgsConstructor
public class TechnicianProductController {

    private final TechnicianProductService service;

    // GET all product (tìm kiếm + phân trang + sắp xếp)
    @GetMapping
    public ResponseEntity<Page<PartResponse>> getAll(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "desc") String dir
    ) {
        return ResponseEntity.ok(service.getAll(q, page, size, sortBy, dir));
    }

    // UPDATE product

    @PutMapping("/{id}")
    public ResponseEntity<PartResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid PartUpdateRequest req
    ) {
        return ResponseEntity.ok(service.update(id, req));
    }

    // DELETE product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // POST - Tạo product (Part)
    @PostMapping
    public ResponseEntity<?> addPart(@Valid @RequestBody AddPartRequest request) {
        try {
            PartResponse partResponse = service.create(request);   // <- dùng instance đã inject
            return ResponseEntity.status(HttpStatus.CREATED).body(partResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error: An unexpected error occurred"));
        }
    }
}
