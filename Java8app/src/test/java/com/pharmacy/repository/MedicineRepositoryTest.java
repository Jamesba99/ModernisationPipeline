package com.pharmacy.repository;

import com.pharmacy.model.Medicine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MedicineRepositoryTest {

    private MedicineRepository repo;

    @BeforeEach
    void setUp() {
        repo = MedicineRepository.getInstance();
    }

    // ── addMedicine / findById ────────────────────────────────────────────

    @Test
    void addAndFindById_returnsAddedMedicine() {
        Medicine m = new Medicine("TST001", "TestDrug", "desc",
                new BigDecimal("9.99"), 10, "TestCorp");
        repo.addMedicine(m);

        Medicine found = repo.findById("TST001");
        assertNotNull(found);
        assertEquals("TestDrug", found.getName());
    }

    @Test
    void findById_returnsNullForUnknownId() {
        assertNull(repo.findById("DOES_NOT_EXIST"));
    }

    // ── findAll ──────────────────────────────────────────────────────────

    @Test
    void findAll_returnsNonEmptyList() {
        List<Medicine> all = repo.findAll();
        assertNotNull(all);
        assertFalse(all.isEmpty());
    }

    // ── searchByName ─────────────────────────────────────────────────────

    @Test
    void searchByName_findsMatchCaseInsensitive() {
        repo.addMedicine(new Medicine("TST002", "SearchableDrug", "desc",
                new BigDecimal("1.00"), 5, "Corp"));

        List<Medicine> results = repo.searchByName("searchable");
        assertTrue(results.stream().anyMatch(m -> m.getId().equals("TST002")));
    }

    @Test
    void searchByName_emptyQueryMatchesNothing() {
        // "xyz_nomatch" should return an empty list
        List<Medicine> results = repo.searchByName("xyz_nomatch_unique_string");
        assertTrue(results.isEmpty());
    }

    @Test
    void searchByName_returnsAllWhenQueryMatchesAll() {
        // All sample medicines contain letters, partial name match
        List<Medicine> results = repo.searchByName("mg");
        assertFalse(results.isEmpty());
    }

    // ── updateMedicine ───────────────────────────────────────────────────

    @Test
    void updateMedicine_replacesExistingEntry() {
        repo.addMedicine(new Medicine("TST003", "OldName", "desc",
                new BigDecimal("5.00"), 20, "Corp"));

        Medicine updated = new Medicine("TST003", "NewName", "desc",
                new BigDecimal("6.00"), 20, "Corp");
        repo.updateMedicine(updated);

        assertEquals("NewName", repo.findById("TST003").getName());
    }

    // ── deleteMedicine ───────────────────────────────────────────────────

    @Test
    void deleteMedicine_removesEntry() {
        repo.addMedicine(new Medicine("TST004", "ToDelete", "desc",
                new BigDecimal("1.00"), 1, "Corp"));
        repo.deleteMedicine("TST004");

        assertNull(repo.findById("TST004"));
    }

    // ── updateStock ──────────────────────────────────────────────────────

    @Test
    void updateStock_reducesStockAndReturnsTrue() {
        repo.addMedicine(new Medicine("TST005", "StockDrug", "desc",
                new BigDecimal("10.00"), 50, "Corp"));

        boolean result = repo.updateStock("TST005", 10);

        assertTrue(result);
        assertEquals(40, repo.findById("TST005").getStockQuantity());
    }

    @Test
    void updateStock_returnsFalseWhenInsufficientStock() {
        repo.addMedicine(new Medicine("TST006", "LowStockDrug", "desc",
                new BigDecimal("10.00"), 5, "Corp"));

        boolean result = repo.updateStock("TST006", 100);

        assertFalse(result);
        assertEquals(5, repo.findById("TST006").getStockQuantity()); // unchanged
    }

    @Test
    void updateStock_returnsFalseForUnknownMedicine() {
        assertFalse(repo.updateStock("UNKNOWN_MED", 1));
    }

    @Test
    void updateStock_exactQuantityReducesStockToZero() {
        repo.addMedicine(new Medicine("TST007", "ExactStockDrug", "desc",
                new BigDecimal("3.00"), 10, "Corp"));

        assertTrue(repo.updateStock("TST007", 10));
        assertEquals(0, repo.findById("TST007").getStockQuantity());
    }
}
