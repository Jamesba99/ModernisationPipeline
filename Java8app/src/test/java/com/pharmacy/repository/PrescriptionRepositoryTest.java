package com.pharmacy.repository;

import com.pharmacy.model.Prescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PrescriptionRepositoryTest {

    private PrescriptionRepository repo;

    @BeforeEach
    void setUp() {
        repo = PrescriptionRepository.getInstance();
    }

    private Prescription buildPrescription(String id, String patientId, String status) {
        return new Prescription(id, "Patient " + patientId, patientId,
                "Dr. Test", "MED001", "Aspirin", 10, "once daily",
                new Date(), new Date(), status, "notes");
    }

    // ── generateId ───────────────────────────────────────────────────────

    @Test
    void generateId_returnsUniqueIncreasingIds() {
        String id1 = repo.generateId();
        String id2 = repo.generateId();
        assertNotEquals(id1, id2);
        assertTrue(id1.startsWith("RX"));
        assertTrue(id2.startsWith("RX"));
    }

    // ── addPrescription / findById ────────────────────────────────────────

    @Test
    void addAndFindById_returnsAddedPrescription() {
        Prescription p = buildPrescription("RXTEST001", "PAT001", "PENDING");
        repo.addPrescription(p);

        Prescription found = repo.findById("RXTEST001");
        assertNotNull(found);
        assertEquals("PAT001", found.getPatientId());
    }

    @Test
    void findById_returnsNullForUnknownId() {
        assertNull(repo.findById("RXDOESNOTEXIST"));
    }

    // ── findAll ──────────────────────────────────────────────────────────

    @Test
    void findAll_containsPreloadedSampleData() {
        List<Prescription> all = repo.findAll();
        assertFalse(all.isEmpty());
        // three sample records are seeded in initializeSampleData
        assertTrue(all.size() >= 3);
    }

    // ── findByPatientId ──────────────────────────────────────────────────

    @Test
    void findByPatientId_returnsMatchingPrescriptions() {
        repo.addPrescription(buildPrescription("RXTEST002", "UNIQUE_PT_XYZ", "PENDING"));
        repo.addPrescription(buildPrescription("RXTEST003", "UNIQUE_PT_XYZ", "VALIDATED"));

        List<Prescription> results = repo.findByPatientId("UNIQUE_PT_XYZ");
        assertEquals(2, results.size());
    }

    @Test
    void findByPatientId_returnsEmptyForUnknownPatient() {
        assertTrue(repo.findByPatientId("GHOST_PATIENT_99").isEmpty());
    }

    // ── findByStatus ─────────────────────────────────────────────────────

    @Test
    void findByStatus_returnsOnlyMatchingStatus() {
        repo.addPrescription(buildPrescription("RXTEST004", "PAT004", "EXPIRED"));

        List<Prescription> results = repo.findByStatus("EXPIRED");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(p -> "EXPIRED".equals(p.getStatus())));
    }

    @Test
    void findByStatus_returnsEmptyForUnusedStatus() {
        assertTrue(repo.findByStatus("UNKNOWN_STATUS_XYZ").isEmpty());
    }

    // ── updatePrescription ───────────────────────────────────────────────

    @Test
    void updatePrescription_replacesExistingEntry() {
        repo.addPrescription(buildPrescription("RXTEST005", "PAT005", "PENDING"));

        Prescription updated = buildPrescription("RXTEST005", "PAT005", "FULFILLED");
        repo.updatePrescription(updated);

        assertEquals("FULFILLED", repo.findById("RXTEST005").getStatus());
    }

    // ── deletePrescription ───────────────────────────────────────────────

    @Test
    void deletePrescription_removesEntry() {
        repo.addPrescription(buildPrescription("RXTEST006", "PAT006", "PENDING"));
        repo.deletePrescription("RXTEST006");

        assertNull(repo.findById("RXTEST006"));
    }
}
