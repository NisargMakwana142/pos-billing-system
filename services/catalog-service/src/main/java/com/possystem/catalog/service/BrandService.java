package com.possystem.catalog.service;

import com.possystem.catalog.dto.BrandDto;
import com.possystem.catalog.entity.Brand;
import com.possystem.catalog.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    public List<BrandDto> getAllBrands(){
        return brandRepository.findAll().stream()
                .map(brand -> new BrandDto(brand.getId(), brand.getName()))
                .toList();
    }

    public BrandDto createBrand(BrandDto dto){
        Brand brand = Brand.builder()
                .name(dto.name())
                .build();

        Brand saved = brandRepository.save(brand);
        return new BrandDto(saved.getId(), saved.getName());
    }
}
