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

        if (updated.getName() != null)
            existing.setName(updated.getName());

        if (updated.getCategory() != null)
            existing.setCategory(updated.getCategory());

        if (updated.getDescription() != null)
            existing.setDescription(updated.getDescription());

        if (updated.getUnitPrice() != null)
            existing.setUnitPrice(updated.getUnitPrice());

        if (updated.getStockQuantity() != null)
            existing.setStockQuantity(updated.getStockQuantity());

        if (updated.getUom() != null)
            existing.setUom(updated.getUom());

        if (updated.getTrackLot() != null)
            existing.setTrackLot(updated.getTrackLot());

        if (updated.getTrackSerial() != null)
            existing.setTrackSerial(updated.getTrackSerial());

        return partRepository.save(existing);
    }


    public void deletePart(Long id) {
        partRepository.deleteById(id);
    }
}
