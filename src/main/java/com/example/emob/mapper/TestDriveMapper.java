/* EMOB-2025 */
package com.example.emob.mapper;

import com.example.emob.entity.TestDrive;
import com.example.emob.model.request.TestDriveRequest;
import com.example.emob.model.response.TestDriveResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TestDriveMapper {
  @Mapping(source = "id", target = "testDriveId")
  @Mapping(source = "salesperson.id", target = "salePersonId")
  @Mapping(source = "customer.id", target = "customerId")
  @Mapping(source = "vehicleUnit.id", target = "testDriveVehicleUnitId")
  TestDriveResponse toTestDriveResponse(TestDrive request);

  @Mapping(target = "id", ignore = true)
  TestDrive toTestDrive(TestDriveRequest request);


}
