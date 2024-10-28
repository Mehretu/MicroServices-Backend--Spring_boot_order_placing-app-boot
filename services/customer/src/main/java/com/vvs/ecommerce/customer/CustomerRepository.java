package com.vvs.ecommerce.customer;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


public interface CustomerRepository extends MongoRepository<Customer, String> {
}
