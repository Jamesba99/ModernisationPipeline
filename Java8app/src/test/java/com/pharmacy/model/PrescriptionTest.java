package com.pharmacy.model;

import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class PrescriptionTest {

    @Test
    void defaultConstructorCreatesEmptyPrescription() {
        Prescription p = new Prescription();
        assertNull(p.getId());
        assertNull(p.getPatientName());
        assertNull(p.getPatientId());
        assertNull(p.getDoctorName());
        assertNull(p.getMedicineId());
        assertNull(p.getMedicineName());
        assertEquals(0, p.getQuantity());
        assertNull(p.getDosage());
        assertNull(p.getPrescriptionDate());
        assertNull(p.getExpiryDate());
        assertNull(p.getStatus());
        assertNull(p.getNotes());
    }

    @Test
    void fullConstructorSetsAllFields() {
        Date prescDate = new Date();
        Date expiryDate = new Date(prescDate.getTime() + 86_400_000L * 30);

        Prescription p = new Prescription("RX001", "Alice", "P100",
                "Dr. Bob", "MED001", "Amoxicillin", 30,
                "1 tablet twice daily", prescDate, expiryDate, "PENDING", "Take with food");

        assertEquals("RX001", p.getId());
        assertEquals("Alice", p.getPatientName());
        assertEquals("P100", p.getPatientId());
        assertEquals("Dr. Bob", p.getDoctorName());
        assertEquals("MED001", p.getMedicineId());
        assertEquals("Amoxicillin", p.getMedicineName());
        assertEquals(30, p.getQuantity());
        assertEquals("1 tablet twice daily", p.getDosage());
        assertEquals(prescDate, p.getPrescriptionDate());
        assertEquals(expiryDate, p.getExpiryDate());
        assertEquals("PENDING", p.getStatus());
        assertEquals("Take with food", p.getNotes());
    }

    @Test
    void settersOverwriteValues() {
        Prescription p = new Prescription();
        Date d1 = new Date();
        Date d2 = new Date(d1.getTime() + 86_400_000L);

        p.setId("RX002");
        p.setPatientName("Bob");
        p.setPatientId("P200");
        p.setDoctorName("Dr. Alice");
        p.setMedicineId("MED002");
        p.setMedicineName("Metformin");
        p.setQuantity(60);
        p.setDosage("twice daily");
        p.setPrescriptionDate(d1);
        p.setExpiryDate(d2);
        p.setStatus("VALIDATED");
        p.setNotes("Monitor glucose");

        assertEquals("RX002", p.getId());
        assertEquals("Bob", p.getPatientName());
        assertEquals("P200", p.getPatientId());
        assertEquals("Dr. Alice", p.getDoctorName());
        assertEquals("MED002", p.getMedicineId());
        assertEquals("Metformin", p.getMedicineName());
        assertEquals(60, p.getQuantity());
        assertEquals("twice daily", p.getDosage());
        assertEquals(d1, p.getPrescriptionDate());
        assertEquals(d2, p.getExpiryDate());
        assertEquals("VALIDATED", p.getStatus());
        assertEquals("Monitor glucose", p.getNotes());
    }
}
