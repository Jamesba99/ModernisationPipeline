package com.pharmacy.action;

import com.opensymphony.xwork2.Action;
import com.pharmacy.model.Medicine;
import com.pharmacy.repository.MedicineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MedicineActionTest {

    private MedicineAction action;
    private MedicineRepository medicineRepo;

    @BeforeEach
    void setUp() throws Exception {
        action = new MedicineAction();
        medicineRepo = MedicineRepository.getInstance();
        setField(action, "medicineRepo", medicineRepo);
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    // ── list() ───────────────────────────────────────────────────────────

    @Test
    void list_returnsSuccessAndPopulatesMedicines() {
        String result = action.list();
        assertEquals(Action.SUCCESS, result);
        assertNotNull(action.getMedicines());
        assertFalse(action.getMedicines().isEmpty());
    }

    // ── view() ───────────────────────────────────────────────────────────

    @Test
    void view_returnsSuccessForNullMedicineId() {
        action.setMedicineId(null);
        assertEquals(Action.SUCCESS, action.view());
        assertNull(action.getMedicine());
    }

    @Test
    void view_returnsSuccessForKnownMedicine() {
        // MED001 is seeded in MedicineRepository.initializeSampleData()
        action.setMedicineId("MED001");
        String result = action.view();
        assertEquals(Action.SUCCESS, result);
        assertNotNull(action.getMedicine());
        assertEquals("MED001", action.getMedicine().getId());
    }

    @Test
    void view_returnsErrorForUnknownMedicine() {
        action.setMedicineId("UNKNOWN_MED_XYZ");
        String result = action.view();
        assertEquals(Action.ERROR, result);
        assertFalse(action.getActionErrors().isEmpty());
    }

    // ── search() ─────────────────────────────────────────────────────────

    @Test
    void search_withNullQuery_returnsAllMedicines() {
        action.setSearchQuery(null);
        assertEquals(Action.SUCCESS, action.search());
        assertFalse(action.getMedicines().isEmpty());
    }

    @Test
    void search_withBlankQuery_returnsAllMedicines() {
        action.setSearchQuery("   ");
        assertEquals(Action.SUCCESS, action.search());
        assertFalse(action.getMedicines().isEmpty());
    }

    @Test
    void search_withMatchingQuery_returnsFilteredResults() {
        medicineRepo.addMedicine(new Medicine("SRCH001", "UniqueDrugXYZ",
                "desc", new BigDecimal("1.00"), 5, "Corp"));

        action.setSearchQuery("UniqueDrugXYZ");
        assertEquals(Action.SUCCESS, action.search());

        List<Medicine> results = action.getMedicines();
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(m -> m.getId().equals("SRCH001")));
    }

    @Test
    void search_withNonMatchingQuery_returnsEmptyList() {
        action.setSearchQuery("zzz_no_match_unique");
        assertEquals(Action.SUCCESS, action.search());
        assertTrue(action.getMedicines().isEmpty());
    }

    // ── getters/setters ───────────────────────────────────────────────────

    @Test
    void setMedicineId_andGetMedicineId_roundTrip() {
        action.setMedicineId("MED999");
        assertEquals("MED999", action.getMedicineId());
    }

    @Test
    void setSearchQuery_andGetSearchQuery_roundTrip() {
        action.setSearchQuery("aspirin");
        assertEquals("aspirin", action.getSearchQuery());
    }
}
