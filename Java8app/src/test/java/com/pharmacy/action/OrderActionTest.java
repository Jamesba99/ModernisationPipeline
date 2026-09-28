package com.pharmacy.action;

import com.opensymphony.xwork2.Action;
import com.pharmacy.model.Medicine;
import com.pharmacy.model.Order;
import com.pharmacy.model.Prescription;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.repository.OrderRepository;
import com.pharmacy.repository.PrescriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class OrderActionTest {

    private OrderAction action;
    private OrderRepository orderRepo;
    private PrescriptionRepository prescriptionRepo;
    private MedicineRepository medicineRepo;

    @BeforeEach
    void setUp() throws Exception {
        action = new OrderAction();
        orderRepo = OrderRepository.getInstance();
        prescriptionRepo = PrescriptionRepository.getInstance();
        medicineRepo = MedicineRepository.getInstance();
        setField(action, "orderRepo", orderRepo);
        setField(action, "prescriptionRepo", prescriptionRepo);
        setField(action, "medicineRepo", medicineRepo);
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    private Order addPendingOrder(String orderId) {
        Order o = new Order(orderId, "RX001", "Patient A", "PAT_A",
                "MED001", "Aspirin", 1, new BigDecimal("5.99"),
                new Date(), "PENDING", null, null);
        orderRepo.addOrder(o);
        return o;
    }

    private Order addPaidOrder(String orderId) {
        Order o = new Order(orderId, "RX001", "Patient B", "PAT_B",
                "MED001", "Aspirin", 1, new BigDecimal("5.99"),
                new Date(), "PAID", "CASH", null);
        orderRepo.addOrder(o);
        return o;
    }

    // ── list() ───────────────────────────────────────────────────────────

    @Test
    void list_returnsSuccess() {
        assertEquals(Action.SUCCESS, action.list());
        assertNotNull(action.getOrders());
    }

    // ── view() ───────────────────────────────────────────────────────────

    @Test
    void view_returnsSuccessForNullOrderId() {
        action.setOrderId(null);
        assertEquals(Action.SUCCESS, action.view());
        assertNull(action.getOrder());
    }

    @Test
    void view_returnsSuccessForKnownOrder() {
        addPendingOrder("ORD_VIEW_01");
        action.setOrderId("ORD_VIEW_01");
        assertEquals(Action.SUCCESS, action.view());
        assertNotNull(action.getOrder());
    }

    @Test
    void view_returnsErrorForUnknownOrder() {
        action.setOrderId("ORD_GHOST_XYZ");
        assertEquals(Action.ERROR, action.view());
        assertFalse(action.getActionErrors().isEmpty());
    }

    // ── createFromPrescription() ──────────────────────────────────────────

    @Test
    void createFromPrescription_returnsErrorForNullPrescriptionId() {
        action.setPrescriptionId(null);
        assertEquals(Action.ERROR, action.createFromPrescription());
    }

    @Test
    void createFromPrescription_returnsErrorForUnknownPrescription() {
        action.setPrescriptionId("RX_GHOST");
        assertEquals(Action.ERROR, action.createFromPrescription());
    }

    @Test
    void createFromPrescription_returnsErrorForNonValidatedPrescription() {
        // RX001 is seeded as PENDING
        action.setPrescriptionId("RX001");
        assertEquals(Action.ERROR, action.createFromPrescription());
        assertTrue(action.getActionErrors().stream()
                .anyMatch(e -> e.contains("validated")));
    }

    @Test
    void createFromPrescription_returnsSuccessForValidatedPrescription() {
        // Add a VALIDATED prescription pointing to a seeded medicine with enough stock
        medicineRepo.addMedicine(new Medicine("MED_CFP", "CFP Drug", "desc",
                new BigDecimal("10.00"), 100, "Corp"));

        Prescription p = new Prescription("RX_CFP_01", "Dave", "P_CFP",
                "Dr. CFP", "MED_CFP", "CFP Drug", 5, "daily",
                new Date(), new Date(), "VALIDATED", "");
        prescriptionRepo.addPrescription(p);

        action.setPrescriptionId("RX_CFP_01");
        assertEquals(Action.SUCCESS, action.createFromPrescription());
        assertFalse(action.getActionMessages().isEmpty());
    }

    @Test
    void createFromPrescription_returnsErrorWhenInsufficientStock() {
        medicineRepo.addMedicine(new Medicine("MED_LOW", "LowStock Drug", "desc",
                new BigDecimal("10.00"), 1, "Corp"));

        Prescription p = new Prescription("RX_LOW_01", "Eve", "P_LOW",
                "Dr. Low", "MED_LOW", "LowStock Drug", 100, "daily",
                new Date(), new Date(), "VALIDATED", "");
        prescriptionRepo.addPrescription(p);

        action.setPrescriptionId("RX_LOW_01");
        assertEquals(Action.ERROR, action.createFromPrescription());
        assertTrue(action.getActionErrors().stream()
                .anyMatch(e -> e.contains("Insufficient")));
    }

    // ── processPayment() ──────────────────────────────────────────────────

    @Test
    void processPayment_returnsErrorForNullOrderId() {
        action.setOrderId(null);
        assertEquals(Action.ERROR, action.processPayment());
    }

    @Test
    void processPayment_returnsErrorForUnknownOrder() {
        action.setOrderId("ORD_GHOST_PAY");
        assertEquals(Action.ERROR, action.processPayment());
    }

    @Test
    void processPayment_returnsErrorWhenPaymentMethodBlank() {
        addPendingOrder("ORD_PAY_01");
        action.setOrderId("ORD_PAY_01");
        action.setPaymentMethod("  ");
        assertEquals(Action.ERROR, action.processPayment());
    }

    @Test
    void processPayment_setsStatusToPaidAndReturnsSuccess() {
        addPendingOrder("ORD_PAY_02");
        action.setOrderId("ORD_PAY_02");
        action.setPaymentMethod("CASH");
        action.setPharmacistNotes("Dispense now");

        assertEquals(Action.SUCCESS, action.processPayment());
        assertEquals("PAID", orderRepo.findById("ORD_PAY_02").getStatus());
        assertEquals("CASH", orderRepo.findById("ORD_PAY_02").getPaymentMethod());
    }

    // ── collect() ─────────────────────────────────────────────────────────

    @Test
    void collect_returnsErrorForNullOrderId() {
        action.setOrderId(null);
        assertEquals(Action.ERROR, action.collect());
    }

    @Test
    void collect_returnsErrorForUnknownOrder() {
        action.setOrderId("ORD_GHOST_COL");
        assertEquals(Action.ERROR, action.collect());
    }

    @Test
    void collect_returnsErrorWhenOrderNotPaid() {
        addPendingOrder("ORD_COL_01");
        action.setOrderId("ORD_COL_01");
        assertEquals(Action.ERROR, action.collect());
        assertTrue(action.getActionErrors().stream()
                .anyMatch(e -> e.contains("paid")));
    }

    @Test
    void collect_setsStatusToCollectedAndReturnsSuccess() {
        addPaidOrder("ORD_COL_02");
        action.setOrderId("ORD_COL_02");

        assertEquals(Action.SUCCESS, action.collect());
        assertEquals("COLLECTED", orderRepo.findById("ORD_COL_02").getStatus());
    }

    // ── getters/setters ───────────────────────────────────────────────────

    @Test
    void settersAndGetters_roundTrip() {
        action.setOrderId("ORD999");
        action.setPrescriptionId("RX999");
        action.setPaymentMethod("INSURANCE");
        action.setPharmacistNotes("Handle with care");

        assertEquals("ORD999", action.getOrderId());
        assertEquals("RX999", action.getPrescriptionId());
        assertEquals("INSURANCE", action.getPaymentMethod());
        assertEquals("Handle with care", action.getPharmacistNotes());
    }
}
