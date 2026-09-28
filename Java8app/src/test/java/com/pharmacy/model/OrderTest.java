package com.pharmacy.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void defaultConstructorCreatesEmptyOrder() {
        Order o = new Order();
        assertNull(o.getId());
        assertNull(o.getPrescriptionId());
        assertNull(o.getPatientName());
        assertNull(o.getPatientId());
        assertNull(o.getMedicineId());
        assertNull(o.getMedicineName());
        assertEquals(0, o.getQuantity());
        assertNull(o.getTotalAmount());
        assertNull(o.getOrderDate());
        assertNull(o.getStatus());
        assertNull(o.getPaymentMethod());
        assertNull(o.getPharmacistNotes());
    }

    @Test
    void fullConstructorSetsAllFields() {
        Date now = new Date();
        Order o = new Order("ORD001", "RX001", "Jane Doe", "P001",
                "MED001", "Aspirin", 2, new BigDecimal("11.98"),
                now, "PENDING", "CASH", "No notes");

        assertEquals("ORD001", o.getId());
        assertEquals("RX001", o.getPrescriptionId());
        assertEquals("Jane Doe", o.getPatientName());
        assertEquals("P001", o.getPatientId());
        assertEquals("MED001", o.getMedicineId());
        assertEquals("Aspirin", o.getMedicineName());
        assertEquals(2, o.getQuantity());
        assertEquals(new BigDecimal("11.98"), o.getTotalAmount());
        assertEquals(now, o.getOrderDate());
        assertEquals("PENDING", o.getStatus());
        assertEquals("CASH", o.getPaymentMethod());
        assertEquals("No notes", o.getPharmacistNotes());
    }

    @Test
    void settersOverwriteValues() {
        Order o = new Order();
        Date d = new Date();
        o.setId("ORD002");
        o.setPrescriptionId("RX002");
        o.setPatientName("John");
        o.setPatientId("P002");
        o.setMedicineId("MED002");
        o.setMedicineName("Paracetamol");
        o.setQuantity(5);
        o.setTotalAmount(new BigDecimal("25.00"));
        o.setOrderDate(d);
        o.setStatus("PAID");
        o.setPaymentMethod("CREDIT_CARD");
        o.setPharmacistNotes("Dispense carefully");

        assertEquals("ORD002", o.getId());
        assertEquals("RX002", o.getPrescriptionId());
        assertEquals("John", o.getPatientName());
        assertEquals("P002", o.getPatientId());
        assertEquals("MED002", o.getMedicineId());
        assertEquals("Paracetamol", o.getMedicineName());
        assertEquals(5, o.getQuantity());
        assertEquals(new BigDecimal("25.00"), o.getTotalAmount());
        assertEquals(d, o.getOrderDate());
        assertEquals("PAID", o.getStatus());
        assertEquals("CREDIT_CARD", o.getPaymentMethod());
        assertEquals("Dispense carefully", o.getPharmacistNotes());
    }
}
