package org.techpulse.cicdpipeline.product;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return repository.findAll().stream().map(ProductResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return ProductResponse.from(findProduct(id));
    }

    public ProductResponse create(ProductRequest request) {
        Product product = new Product(request.name(), request.description(), request.price());
        return ProductResponse.from(repository.save(product));
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findProduct(id);
        product.update(request.name(), request.description(), request.price());
        return ProductResponse.from(repository.save(product));
    }

    public void delete(Long id) {
        repository.delete(findProduct(id));
    }

    private Product findProduct(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }
}
