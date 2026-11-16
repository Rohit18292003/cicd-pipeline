package org.techpulse.cicdpipeline.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTests {

    @Mock
    private ProductRepository repository;

    private ProductService service;

    @BeforeEach
    void setUp() {
        service = new ProductService(repository);
    }

    @Test
    void listsProducts() {
        when(repository.findAll()).thenReturn(List.of(product(1L, "Keyboard")));

        assertEquals("Keyboard", service.findAll().get(0).name());
    }

    @Test
    void returnsProductById() {
        when(repository.findById(1L)).thenReturn(Optional.of(product(1L, "Keyboard")));

        assertEquals(1L, service.findById(1L).id());
    }

    @Test
    void createsProduct() {
        when(repository.save(any(Product.class))).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            return product(1L, saved.getName());
        });

        ProductResponse response = service.create(request("Keyboard"));

        assertEquals("Keyboard", response.name());
        verify(repository).save(any(Product.class));
    }

    @Test
    void updatesProduct() {
        Product existing = product(1L, "Old name");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        ProductResponse response = service.update(1L, request("New name"));

        assertEquals("New name", response.name());
    }

    @Test
    void deletesProduct() {
        Product existing = product(1L, "Keyboard");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        service.delete(1L);

        verify(repository).delete(existing);
    }

    @Test
    void throwsNotFoundWhenProductDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> service.findById(99L));
    }

    private Product product(Long id, String name) {
        return new Product(id, name, "Description", new BigDecimal("19.99"));
    }

    private ProductRequest request(String name) {
        return new ProductRequest(name, "Description", new BigDecimal("19.99"));
    }
}
