package com.example.api.downstream;

import com.example.api.model.CustomerCoreProfile;

public interface CustomerCoreGateway {

    CustomerCoreProfile fetchCustomerProfile(String customerId);

    CustomerCoreProfile createCustomerProfile(CustomerCoreProfile profile);

    CustomerCoreProfile updateCustomerProfile(CustomerCoreProfile profile);
}

