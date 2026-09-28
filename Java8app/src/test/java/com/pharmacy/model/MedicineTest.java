package com.pharmacy.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class MedicineTest {

    @Test
    void defaultConstructorCreatesEmptyMedicine() {
        Medicine m = new Medicine();
        assertNull(m.getId());
        assertNull(m.getName());
        assertNull(m.getDescription());
        assertNull(m.getPrice());
        assertEquals(0, m.getStockQuantity());
        assertNull(m.getManufacturer());
    }

    @Test
    void fullConstructorSetsAllFields() {
        Medicine m = new Medicine("M1", "Aspirin", "Pain reliever",
                new BigDecimal("5.99"), 100, "BayerCorp");

        assertEquals("M1", m.getId());
        assertEquals("Aspirin", m.getName());
        assertEquals("Pain reliever", m.getDescription());
        assertEquals(new BigDecimal("5.99"), m.getPrice());
        assertEquals(100, m.getStockQuantity());
        assertEquals("BayerCorp", m.getManufacturer());
    }

    @Test
    void settersOverwriteValues() {
        Medicine m = new Medicine();
        m.setId("M2");
        m.setName("Ibuprofen");
        m.setDescription("Anti-inflammatory");
        m.setPrice(new BigDecimal("7.50"));
        m.setStockQuantity(50);
        m.setManufacturer("PharmaX");

        assertEquals("M2", m.getId());
        assertEquals("Ibuprofen", m.getName());
        assertEquals("Anti-inflammatory", m.getDescription());
        assertEquals(new BigDecimal("7.50"), m.getPrice());
        assertEquals(50, m.getStockQuantity());
        assertEquals("PharmaX", m.getManufacturer());
    }

    @Test
    void stockQuantityCanBeSetToZero() {
        Medicine m = new Medicine();
        m.setStockQuantity(0);
        assertEquals(0, m.getStockQuantity());
    }

    @Test
    void priceCanBeZero() {
        Medicine m = new Medicine();
        m.setPrice(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, m.getPrice());
    }
}
