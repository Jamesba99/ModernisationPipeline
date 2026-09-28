package com.pharmacy.action;

import com.opensymphony.xwork2.Action;
import com.pharmacy.model.Order;
import com.pharmacy.model.Prescription;
import com.pharmacy.repository.OrderRepository;
import com.pharmacy.repository.PrescriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DashboardActionTest {

    private DashboardAction action;
    private PrescriptionRepository prescriptionRepo;
    private OrderRepository orderRepo;

    @BeforeEach
    void setUp() throws Exception {
        action = new DashboardAction();
        prescriptionRepo = PrescriptionRepository.getInstance();
        orderRepo = OrderRepository.getInstance();

        // Inject the real singletons via reflection (same instances the action uses)
        setField(action, "prescriptionRepo", prescriptionRepo);
        setField(action, "orderRepo", orderRepo);
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test
    void execute_returnsSuccess() {
        assertEquals(Action.SUCCESS, action.execute());
    }

    @Test
    void execute_populatesPendingPrescriptions() {
        action.execute();
        assertNotNull(action.getPendingPrescriptions());
        // All returned items must have PENDING status
        action.getPendingPrescriptions()
              .forEach(p -> assertEquals("PENDING", p.getStatus()));
    }

    @Test
    void execute_populatesPendingOrders() {
        action.execute();
        assertNotNull(action.getPendingOrders());
        action.getPendingOrders()
              .forEach(o -> assertEquals("PENDING", o.getStatus()));
    }

    @Test
    void execute_totalPrescriptionsMatchesRepoSize() {
        action.execute();
        assertEquals(prescriptionRepo.findAll().size(), action.getTotalPrescriptions());
    }

    @Test
    void execute_totalOrdersMatchesRepoSize() {
        action.execute();
        assertEquals(orderRepo.findAll().size(), action.getTotalOrders());
    }

    @Test
    void execute_totalOrdersIncreasesAfterAdd() {
        action.execute();
        int before = action.getTotalOrders();

        orderRepo.addOrder(new Order("DASH_ORD_TST", "RX_DASH", "Patient", "PAT_DASH",
                "MED001", "Aspirin", 1, new BigDecimal("5.00"), new Date(),
                "PENDING", "CASH", null));

        action.execute();
        assertEquals(before + 1, action.getTotalOrders());
    }
}
