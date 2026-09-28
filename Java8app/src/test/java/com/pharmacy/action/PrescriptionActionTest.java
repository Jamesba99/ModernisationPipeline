package com.pharmacy.action;

import com.opensymphony.xwork2.Action;
import com.pharmacy.model.Medicine;
import com.pharmacy.model.Prescription;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.repository.PrescriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class PrescriptionActionTest {

    private PrescriptionAction action;
    private PrescriptionRepository prescriptionRepo;
    private MedicineRepository medicineRepo;

    @BeforeEach
    void setUp() throws Exception {
        action = new PrescriptionAction();
        prescriptionRepo = PrescriptionRepository.getInstance();
        medicineRepo = MedicineRepository.getInstance();
        setField(action, "prescriptionRepo", prescriptionRepo);
        setField(action, "medicineRepo", medicineRepo);
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    // ── list() ───────────────────────────────────────────────────────────

    @Test
    void list_returnsSuccessAndPopulatesPrescriptions() {
        String result = action.list();
        assertEquals(Action.SUCCESS, result);
        assertNotNull(action.getPrescriptions());
        assertFalse(action.getPrescriptions().isEmpty());
    }

    // ── view() ───────────────────────────────────────────────────────────

    @Test
    void view_returnsSuccessForNullId() {
        action.setPrescriptionId(null);
        assertEquals(Action.SUCCESS, action.view());
        assertNull(action.getPrescription());
    }

    @Test
    void view_returnsSuccessForKnownPrescription() {
        action.setPrescriptionId("RX001");
        assertEquals(Action.SUCCESS, action.view());
        assertNotNull(action.getPrescription());
    }

    @Test
    void view_returnsErrorForUnknownPrescription() {
        action.setPrescriptionId("RX_DOES_NOT_EXIST");
        assertEquals(Action.ERROR, action.view());
        assertFalse(action.getActionErrors().isEmpty());
    }

    // ── create() ─────────────────────────────────────────────────────────

    @Test
    void create_returnsSuccessAndPopulatesMedicines() {
        assertEquals(Action.SUCCESS, action.create());
        assertNotNull(action.getMedicines());
        assertFalse(action.getMedicines().isEmpty());
    }

    // ── save() ───────────────────────────────────────────────────────────

    @Test
    void save_withValidData_returnsSuccess() {
        action.setPatientName("Alice");
        action.setPatientId("P_SAVE_01");
        action.setDoctorName("Dr. Save");
        action.setMedicineId("MED001"); // seeded medicine
        action.setQuantity(5);
        action.setDosage("twice daily");
        action.setNotes("test note");

        assertEquals(Action.SUCCESS, action.save());
        assertFalse(action.getActionMessages().isEmpty());
    }

    @Test
    void save_withUnknownMedicine_returnsError() {
        action.setMedicineId("UNKNOWN_MED_SAVE");
        assertEquals(Action.ERROR, action.save());
        assertFalse(action.getActionErrors().isEmpty());
    }

    // ── validatePrescription() ────────────────────────────────────────────

    @Test
    void validatePrescription_updatesStatusToValidated() {
        // Add a fresh PENDING prescription so we don't corrupt shared data
        Prescription p = new Prescription("RX_VAL_TST", "Bob", "P_VAL",
                "Dr. V", "MED001", "Aspirin", 10, "once daily",
                new Date(), new Date(), "PENDING", "");
        prescriptionRepo.addPrescription(p);

        action.setPrescriptionId("RX_VAL_TST");
        assertEquals(Action.SUCCESS, action.validatePrescription());
        assertEquals("VALIDATED", prescriptionRepo.findById("RX_VAL_TST").getStatus());
    }

    @Test
    void validatePrescription_returnsErrorForUnknownId() {
        action.setPrescriptionId("RX_GHOST_XYZ");
        assertEquals(Action.ERROR, action.validatePrescription());
        assertFalse(action.getActionErrors().isEmpty());
    }

    @Test
    void validatePrescription_returnsErrorForNullId() {
        action.setPrescriptionId(null);
        assertEquals(Action.ERROR, action.validatePrescription());
    }

    // ── getters/setters ───────────────────────────────────────────────────

    @Test
    void settersAndGetters_roundTrip() {
        action.setPrescriptionId("RX999");
        action.setPatientName("Charlie");
        action.setPatientId("P999");
        action.setDoctorName("Dr. X");
        action.setMedicineId("MED999");
        action.setQuantity(3);
        action.setDosage("daily");
        action.setNotes("note");

        assertEquals("RX999", action.getPrescriptionId());
        assertEquals("Charlie", action.getPatientName());
        assertEquals("P999", action.getPatientId());
        assertEquals("Dr. X", action.getDoctorName());
        assertEquals("MED999", action.getMedicineId());
        assertEquals(3, action.getQuantity());
        assertEquals("daily", action.getDosage());
        assertEquals("note", action.getNotes());
    }
}
