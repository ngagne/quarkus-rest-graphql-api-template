package com.example.api.downstream;

import com.example.api.model.ProductExposure;
import java.util.List;

public interface ExposureGateway {

    List<ProductExposure> fetchExposures(String customerId);
}

