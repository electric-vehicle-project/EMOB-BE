package com.example.emob.service;

import com.example.emob.constant.ContractStatus;
import com.example.emob.constant.DeliveryItemStatus;
import com.example.emob.constant.DeliveryStatus;
import com.example.emob.constant.ErrorCode;
import com.example.emob.entity.*;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.DeliveryItemMapper;
import com.example.emob.mapper.DeliveryMapper;
import com.example.emob.mapper.PageMapper;
import com.example.emob.model.request.delivery.DeliveryItemRequest;
import com.example.emob.model.request.delivery.DeliveryRequest;
import com.example.emob.model.request.delivery.UpdateDeliveryItemRequest;
import com.example.emob.model.request.delivery.UpdateDeliveryRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.DeliveryItemResponse;
import com.example.emob.model.response.DeliveryResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.repository.DeliveryItemRepository;
import com.example.emob.repository.DeliveryRepository;
import com.example.emob.repository.SaleContractRepository;
import com.example.emob.repository.VehicleUnitRepository;
import com.example.emob.service.impl.IDelivery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeliveryService implements IDelivery {
    @Autowired
    PageMapper pageMapper;

    @Autowired
    DeliveryRepository deliveryRepository;

    @Autowired
    SaleContractRepository contractRepository;

    @Autowired
    DeliveryMapper deliveryMapper;

    @Autowired
    DeliveryItemRepository deliveryItemRepository;

    @Autowired
    DeliveryItemMapper deliveryItemMapper;

    @Autowired
    VehicleUnitRepository vehicleUnitRepository;

    @Override
    @Transactional
    public APIResponse<DeliveryResponse> createDelivery(DeliveryRequest request) {
        SaleContract contract = contractRepository.findById(request.getContractId())
                .filter((item) -> item.getStatus().equals(ContractStatus.COMPLETED))
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        System.out.println("vào chưa");
        try {
            // mapper
            Delivery delivery = deliveryMapper.toDelivery(request);
            if (request.getDeliveryItems() != null && !request.getDeliveryItems().isEmpty()) {
                Set<DeliveryItem> items = request.getDeliveryItems().stream()
                        .map(itemReq -> {

                            // tìm xe trong request
                            VehicleUnit vehicleUnit = vehicleUnitRepository.findById(itemReq.getVehicleId())
                                    .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
                            System.out.println(vehicleUnit.getId());
                            // check trùng xe trong deliveryItem
                            boolean isDup = deliveryItemRepository.existsByVehicleUnit_Id(vehicleUnit.getId());
                            if (isDup) {
                                throw new GlobalException(ErrorCode.VEHICLE_DUPLICATED);
                            }
                            DeliveryItem item = deliveryItemMapper.toDeliveryItem(itemReq);
                            item.setVehicleUnit(vehicleUnit);
                            item.setDelivery(delivery); // gắn quan hệ 2 chiều
                            return item;
                        })
                        .collect(Collectors.toSet());
                delivery.setDeliveryItems(items);
                delivery.setQuantity(items.size());
            } else {
                delivery.setQuantity(0);
            }
            delivery.setSaleContract(contract);
            delivery.setCreateAt(LocalDateTime.now());
            // trạng thái chưa giao xong
            delivery.setStatus(DeliveryStatus.IN_PROGRESS);
            deliveryRepository.save(delivery);
            Delivery savedDelivery = deliveryRepository.findById(delivery.getId())
                    .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

            DeliveryResponse deliveryResponse = deliveryMapper.toDeliveryResponse(savedDelivery);
            return APIResponse.success(deliveryResponse, "Create Delivery Successfully");
        } catch (DataIntegrityViolationException ex) {
            throw new GlobalException(ErrorCode.DATA_INVALID);
        } catch (DataAccessException ex) {
            throw new GlobalException(ErrorCode.DB_ERROR);
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            throw new GlobalException(ErrorCode.OTHER);
        }
    }

     @Override
     public APIResponse<DeliveryResponse> updateDeliveryDate(UpdateDeliveryRequest request, UUID id) {
         Delivery delivery = deliveryRepository.findById(id)
                 .filter((item) -> !item.isDeleted())
                 .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
         try {
             delivery.setDeliveryDate(request.getDeliveryDate());
             delivery.setUpdateAt(LocalDateTime.now());
             deliveryRepository.save(delivery);
             DeliveryResponse deliveryResponse = deliveryMapper.toDeliveryResponse(delivery);
             return APIResponse.success(deliveryResponse, "Update delivery date successfully");
         }  catch (DataIntegrityViolationException ex) {
             throw new GlobalException(ErrorCode.DATA_INVALID);
         } catch (DataAccessException ex) {
             throw new GlobalException(ErrorCode.DB_ERROR);
         } catch (Exception ex) {
             throw new GlobalException(ErrorCode.OTHER);
         }
     }

     @Override
     public APIResponse<DeliveryResponse> getDelivery(UUID id) {
         Delivery delivery = deliveryRepository.findById(id)
                 .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
         DeliveryResponse deliveryResponse = deliveryMapper.toDeliveryResponse(delivery);
         return APIResponse.success(deliveryResponse, "View Delivery Successfully");
     }

     @Override
     public APIResponse<PageResponse<DeliveryResponse>> getAllDeliveries(Pageable pageable) {
         Page<Delivery> deliveries = deliveryRepository.findAll(pageable);
         PageResponse<DeliveryResponse> pageResponse = pageMapper.toPageResponse(deliveries, deliveryMapper::toDeliveryResponse);
         return APIResponse.success(pageResponse, "View All Deliveries Item Successfully");
     }

     @Override
     public APIResponse<Void> deleteDelivery(UUID id) {
         Delivery delivery = deliveryRepository.findById(id)
                 .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
         delivery.setDeleted(true);
         deliveryRepository.save(delivery);
         return APIResponse.success(null, "Delete Delivery Successfully");
     }

//     @Override
//     public APIResponse<DeliveryItemResponse> createDeliveryItem(DeliveryItemRequest request, UUID deliveryId) {
//         Delivery delivery = deliveryRepository.findById(deliveryId)
//                 .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
//
//         VehicleUnit vehicleUnit = vehicleUnitRepository.findById(request.getVehicleId())
//                 .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
//         boolean isDup = deliveryItemRepository.existsByVehicleUnit_Id(vehicleUnit.getId());
//         if (isDup) {
//             throw new GlobalException(ErrorCode.VEHICLE_DUPLICATED);
//         }
//         try {
//             DeliveryItem deliveryItem = deliveryItemMapper.toDeliveryItem(request);
//             deliveryItem.setVehicleUnit(vehicleUnit);
//             deliveryItem.setDelivery(delivery);
//             // lấy ra số lượng xe đc thêm vào deliveryItem
//             int countDeliveryItem = deliveryItemRepository.findByDelivery_Id(delivery.getId()).size();
//             // check quantity
//             if (countDeliveryItem > delivery.getQuantity()) {
//                 throw new GlobalException(ErrorCode.INVALID_QUANTITY);
//             }
//             deliveryItem.setStatus(DeliveryItemStatus.PENDING);
//             deliveryItem.setCreateAt(LocalDateTime.now());
//             deliveryItemRepository.save(deliveryItem);
//             DeliveryItemResponse deliveryItemResponse = deliveryItemMapper.toDeliveryItemResponse(deliveryItem);
//             return APIResponse.success(deliveryItemResponse, "Create Delivery Item Successfully");
//         } catch (DataIntegrityViolationException ex) {
//             throw new GlobalException(ErrorCode.DATA_INVALID);
//         } catch (DataAccessException ex) {
//             throw new GlobalException(ErrorCode.DB_ERROR);
//         } catch (Exception ex) {
//             throw new GlobalException(ErrorCode.OTHER);
//         }
//     }
    // check delivery đã giao hết số lượng chưa
     public void checkQuantityInDelivery (DeliveryItem deliveryItem) {
         Delivery delivery = deliveryItem.getDelivery();
         long countDelivered = deliveryItemRepository.countNotDeliveredNative(delivery.getId());
         // không còn thằng nào chưa giao
         if (countDelivered == 0) {
             delivery.setStatus(DeliveryStatus.SUCCESS);
             delivery.setDeliveryDate(LocalDateTime.now());
             deliveryRepository.save(delivery);
         }
     }

     @Override
     @Transactional
     public APIResponse<DeliveryItemResponse> confirm(UUID id) {
         DeliveryItem deliveryItem = deliveryItemRepository.findById(id)
                 .filter((item) -> item.getStatus().equals(DeliveryItemStatus.PENDING))
                 .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
         // check trường hợp giao đủ rồi
         checkQuantityInDelivery(deliveryItem);

         deliveryItem.setStatus(DeliveryItemStatus.DELIVERED);
         deliveryItem.setConfirmAt(LocalDateTime.now());
         deliveryItemRepository.save(deliveryItem);

         DeliveryItemResponse deliveryItemResponse = deliveryItemMapper.toDeliveryItemResponse(deliveryItem);
         return APIResponse.success(deliveryItemResponse, "Confirm delivery successfully");
     }

     @Override
     public APIResponse<DeliveryItemResponse> viewDeliveryItem(UUID id) {
         DeliveryItem deliveryItem = deliveryItemRepository.findById(id)
                 .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
         DeliveryItemResponse deliveryItemResponse = deliveryItemMapper.toDeliveryItemResponse(deliveryItem);
         return APIResponse.success(deliveryItemResponse, "View delivery successfully");
     }

     @Override
     public APIResponse<PageResponse<DeliveryItemResponse>> viewDeliveriesItem(Pageable pageable) {
         Page<DeliveryItem> deliveryItems = deliveryItemRepository.findAll(pageable);
         PageResponse<DeliveryItemResponse> deliveryItemResponsePageResponse = pageMapper.toPageResponse(deliveryItems,
                 deliveryItemMapper::toDeliveryItemResponse);
         return APIResponse.success(deliveryItemResponsePageResponse, "View All Deliveries Item Successfully");
     }

     @Override
     public APIResponse<DeliveryItemResponse> cancelDeliveryItem(UUID id) {
         DeliveryItem deliveryItem = deliveryItemRepository.findById(id)
                 .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
         deliveryItem.setStatus(DeliveryItemStatus.CANCELED);
         deliveryItemRepository.save(deliveryItem);
         return APIResponse.success(null, "Cancel Delivery Item Successfully");
     }

    @Override
    public APIResponse<DeliveryItemResponse> updateDeliveryItem(UUID id, UpdateDeliveryItemRequest request) {
         DeliveryItem deliveryItem = deliveryItemRepository.findById(id)
                 .filter((item) -> !item.getStatus().equals(DeliveryItemStatus.DELIVERED))
                 .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

         // check trùng xe
        boolean isDup = deliveryItemRepository.existsByVehicleUnit_Id(request.getVehicleId());
        if (isDup) {
            throw new GlobalException(ErrorCode.VEHICLE_DUPLICATED);
        }
        VehicleUnit newVehicle = vehicleUnitRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        deliveryItem.setVehicleUnit(newVehicle);
        deliveryItem.setUpdateAt(LocalDateTime.now());
        deliveryItem.setRemarks(request.getRemarks());
        deliveryItemRepository.save(deliveryItem);
        DeliveryItemResponse deliveryItemResponse = deliveryItemMapper.toDeliveryItemResponse(deliveryItem);
        deliveryItemResponse.setRemarks(deliveryItem.getRemarks());
        return APIResponse.success(deliveryItemResponse, "Update Delivery Item Successfully");
    }
}
