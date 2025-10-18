/* EMOB-2025 */
package com.example.emob.config;

import com.example.emob.constant.AccountStatus;
import com.example.emob.constant.Role;
import com.example.emob.entity.Account;
import com.example.emob.entity.Inventory;
import com.example.emob.repository.AccountRepository;
import com.example.emob.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {
  @Autowired InventoryRepository inventoryRepository;
  @Autowired AccountRepository accountRepository;
  @Autowired @Lazy PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) throws Exception {
    seedCompanyInventory();
    seedCreateAdminUser();
  }

  private void seedCompanyInventory() {
    if (inventoryRepository.countByIsCompanyTrue() == 0) {
      Inventory companyInventory =
          Inventory.builder().quantity(0).isCompany(true).dealer(null).build();
      inventoryRepository.save(companyInventory);
      System.out.println("Seeded company inventory!");
    }
  }

  private void seedCreateAdminUser() {
    // Implement logic to create an admin user if not exists
    if (accountRepository.findAccountByEmail("admin@gmail.com") != null) {
      return;
    }
    Account admin =
        Account.builder()
            .email("admin@gmail.com")
            .password(passwordEncoder.encode("Admin@123"))
            .role(Role.ADMIN)
            .status(AccountStatus.ACTIVE)
            .build();
    accountRepository.save(admin);
    System.out.println("Seeded admin user!");
  }
}
