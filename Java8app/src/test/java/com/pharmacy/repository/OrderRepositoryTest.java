package com.pharmacy.repository;

import com.pharmacy.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class OrderRepositoryTest {

    private OrderRepository repo;

    @BeforeEach
    void setUp() {
        repo = OrderRepository.getInstance();
    }

    private Order buildOrder(String id, String patientId, String prescriptionId, String status) {
        return new Order(id, prescriptionId, "Patient " + patientId, patientId,
                "MED001", "Aspirin", 1, new BigDecimal("5.99"), new Date(),
                status, "CASH", "notes");
    }

    // ── generateId ───────────────────────────────────────────────────────

    @Test
    void generateId_returnsUniqueIncreasingIds() {
        String id1 = repo.generateId();
        String id2 = repo.generateId();
        assertNotEquals(id1, id2);
        assertTrue(id1.startsWith("ORD"));
        assertTrue(id2.startsWith("ORD"));
    }

    // ── addOrder / findById ──────────────────────────────────────────────

    @Test
    void addAndFindById_returnsAddedOrder() {
        Order o = buildOrder("ORDT001", "PAT001", "RX001", "PENDING");
        repo.addOrder(o);

        Order found = repo.findById("ORDT001");
        assertNotNull(found);
        assertEquals("PAT001", found.getPatientId());
    }

    @Test
    void findById_returnsNullForUnknownId() {
        assertNull(repo.findById("DOES_NOT_EXIST"));
    }

    // ── findAll ──────────────────────────────────────────────────────────

    @Test
    void findAll_returnsAllAddedOrders() {
        repo.addOrder(buildOrder("ORDT002", "PAT002", "RX002", "PAID"));
        List<Order> all = repo.findAll();
        assertTrue(all.stream().anyMatch(o -> o.getId().equals("ORDT002")));
    }

    // ── findByPatientId ──────────────────────────────────────────────────

    @Test
    void findByPatientId_returnsMatchingOrders() {
        repo.addOrder(buildOrder("ORDT003", "UNIQUE_PAT", "RX003", "PENDING"));
        repo.addOrder(buildOrder("ORDT004", "UNIQUE_PAT", "RX004", "PAID"));

        List<Order> results = repo.findByPatientId("UNIQUE_PAT");
        assertEquals(2, results.size());
    }

    @Test
    void findByPatientId_returnsEmptyForUnknownPatient() {
        List<Order> results = repo.findByPatientId("GHOST_PATIENT_XYZ");
        assertTrue(results.isEmpty());
    }

    // ── findByStatus ─────────────────────────────────────────────────────

    @Test
    void findByStatus_returnsOnlyMatchingStatus() {
        repo.addOrder(buildOrder("ORDT005", "PAT005", "RX005", "COLLECTED"));

        List<Order> results = repo.findByStatus("COLLECTED");
        assertTrue(results.stream().allMatch(o -> "COLLECTED".equals(o.getStatus())));
        assertTrue(results.stream().anyMatch(o -> o.getId().equals("ORDT005")));
    }

    // ── findByPrescriptionId ─────────────────────────────────────────────

    @Test
    void findByPrescriptionId_returnsMatchingOrders() {
        repo.addOrder(buildOrder("ORDT006", "PAT006", "UNIQUE_RX_XYZ", "PENDING"));

        List<Order> results = repo.findByPrescriptionId("UNIQUE_RX_XYZ");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(o -> "UNIQUE_RX_XYZ".equals(o.getPrescriptionId())));
    }

    // ── updateOrder ──────────────────────────────────────────────────────

    @Test
    void updateOrder_replacesExistingEntry() {
        repo.addOrder(buildOrder("ORDT007", "PAT007", "RX007", "PENDING"));

        Order updated = buildOrder("ORDT007", "PAT007", "RX007", "PAID");
        repo.updateOrder(updated);

        assertEquals("PAID", repo.findById("ORDT007").getStatus());
    }

    // ── deleteOrder ──────────────────────────────────────────────────────

    @Test
    void deleteOrder_removesEntry() {
        repo.addOrder(buildOrder("ORDT008", "PAT008", "RX008", "PENDING"));
        repo.deleteOrder("ORDT008");

        assertNull(repo.findById("ORDT008"));
    }
}
