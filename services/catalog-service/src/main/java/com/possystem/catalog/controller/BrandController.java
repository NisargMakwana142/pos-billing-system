package com.possystem.catalog.controller;

import com.possystem.catalog.dto.BrandDto;
import com.possystem.catalog.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public List<BrandDto> getAllBrands(){
        return brandService.getAllBrands();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BrandDto createBrand(@Valid @RequestBody BrandDto brandDto){
        return brandService.createBrand(brandDto);
    }

}
