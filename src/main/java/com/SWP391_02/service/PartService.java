package com.SWP391_02.service;

import com.SWP391_02.entity.Part;
import com.SWP391_02.repository.PartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartService {
    private final PartRepository partRepository;

    public List<Part> getAllParts() {
        return partRepository.findAll();
    }

    public Optional<Part> getPartById(Long id) {
        return partRepository.findById(id);
    }

    public Optional<Part> getByPartNo(String partNo) {
        return partRepository.findByPartNo(partNo);
    }

    public Part createPart(Part part) {
        if (partRepository.existsByPartNo(part.getPartNo())) {
            throw new IllegalArgumentException("Part number already exists!");
        }
        return partRepository.save(part);
    }

    public Part updatePart(Long id, Part updated) {
        Part existing = partRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Part not found!"));
        existing.setName(updated.getName());
        existing.setCategory(updated.getCategory());
        existing.setDescription(updated.getDescription());
        existing.setUnitPrice(updated.getUnitPrice());
        existing.setStockQuantity(updated.getStockQuantity());
        existing.setUom(updated.getUom());
        existing.setTrackLot(updated.getTrackLot());
        existing.setTrackSerial(updated.getTrackSerial());
        return partRepository.save(existing);
    }

    public void deletePart(Long id) {
        partRepository.deleteById(id);
    }
}
