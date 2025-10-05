package com.example.emob.config;

import com.example.emob.entity.Inventory;
import com.example.emob.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {
    @Autowired
    InventoryRepository inventoryRepository;

    @Override
    public void run(String... args) throws Exception {
        seedCompanyInventory();
    }
    private void seedCompanyInventory() {
        if (inventoryRepository.countByIsCompanyTrue() == 0) {
            Inventory companyInventory = Inventory.builder()
                    .totalQuantity(0)
                    .isCompany(true)
                    .dealer(null)
                    .build();
            inventoryRepository.save(companyInventory);
            System.out.println("Seeded company inventory!");
        }
    }
}
