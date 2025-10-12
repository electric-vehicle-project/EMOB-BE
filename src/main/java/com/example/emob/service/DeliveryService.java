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
import com.example.emob.model.request.delivery.UpdateDeliveryRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.DeliveryItemResponse;
import com.example.emob.model.response.DeliveryResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.repository.DeliveryItemRepository;
import com.example.emob.repository.DeliveryRepository;
import com.example.emob.repository.SaleContractRepository;
import com.example.emob.service.impl.IDelivery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

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

    // @Override
    // public APIResponse<DeliveryResponse> createDelivery(DeliveryRequest request) {
    //     SaleContract contract = contractRepository.findById(request.getContractId())
    //             .filter((item) -> item.getStatus().equals(ContractStatus.COMPLETED))
    //             .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    //     try {
    //         // mapper
    //         Delivery delivery = deliveryMapper.toDelivery(request);
    //         for (DeliveryItem item : delivery.getDeliveryItem()) {
    //             item.setDelivery(delivery);
    //         }
    //         // lấy ra tống deliveryItem
    //         int quantity = delivery.getDeliveryItem()
    //                 .stream()
    //                 .mapToInt(DeliveryItem::getQuantity)
    //                 .sum();
    //         delivery.setQuantity(quantity);
    //         delivery.setSaleContract(contract);
    //         delivery.setCreateAt(LocalDateTime.now());
    //         // trạng thái chưa giao xong
    //         delivery.setStatus(DeliveryStatus.FAIL);
    //         deliveryRepository.save(delivery);
    //         DeliveryResponse deliveryResponse = deliveryMapper.toDeliveryResponse(delivery);
    //         return APIResponse.success(deliveryResponse, "Create Delivery Successfully");
    //     } catch (DataIntegrityViolationException ex) {
    //         throw new GlobalException(ErrorCode.DATA_INVALID);
    //     } catch (DataAccessException ex) {
    //         throw new GlobalException(ErrorCode.DB_ERROR);
    //     } catch (Exception ex) {
    //         throw new GlobalException(ErrorCode.OTHER);
    //     }
    // }

    // @Override
    // public APIResponse<DeliveryResponse> updateDeliveryDate(UpdateDeliveryRequest request, UUID id) {
    //     Delivery delivery = deliveryRepository.findById(id)
    //             .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    //     try {
    //         delivery.setDeliveryDate(request.getDeliveryDate());
    //         delivery.setStatus(request.getStatus());
    //         delivery.setUpdateAt(LocalDateTime.now());
    //         deliveryRepository.save(delivery);
    //         DeliveryResponse deliveryResponse = deliveryMapper.toDeliveryResponse(delivery);
    //         return APIResponse.success(deliveryResponse, "Update delivery date successfully");
    //     }  catch (DataIntegrityViolationException ex) {
    //         throw new GlobalException(ErrorCode.DATA_INVALID);
    //     } catch (DataAccessException ex) {
    //         throw new GlobalException(ErrorCode.DB_ERROR);
    //     } catch (Exception ex) {
    //         throw new GlobalException(ErrorCode.OTHER);
    //     }
    // }

    // @Override
    // public APIResponse<DeliveryResponse> getDelivery(UUID id) {
    //     Delivery delivery = deliveryRepository.findById(id)
    //             .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    //     DeliveryResponse deliveryResponse = deliveryMapper.toDeliveryResponse(delivery);
    //     return APIResponse.success(deliveryResponse, "View Delivery Successfully");
    // }

    // @Override
    // public APIResponse<PageResponse<DeliveryResponse>> getAllDeliveries(Pageable pageable) {
    //     Page<Delivery> deliveries = deliveryRepository.findAll(pageable);
    //     PageResponse<DeliveryResponse> pageResponse = pageMapper.toPageResponse(deliveries, deliveryMapper::toDeliveryResponse);
    //     return APIResponse.success(pageResponse, "View All Deliveries Item Successfully");
    // }

    // @Override
    // public APIResponse<Void> deleteDelivery(UUID id) {
    //     Delivery delivery = deliveryRepository.findById(id)
    //             .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    //     delivery.setDeleted(true);
    //     deliveryRepository.save(delivery);
    //     return APIResponse.success(null, "Delete Delivery Successfully");
    // }

    // @Override
    // public APIResponse<DeliveryItemResponse> createDeliveryItem(DeliveryItemRequest request) {
    //     Delivery delivery = deliveryRepository.findById(request.getDeliveryId())
    //             .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

    //     try {
    //         DeliveryItem deliveryItem = deliveryItemMapper.toDeliveryItem(request);
    //         deliveryItem.setDelivery(delivery);
    //         // tính tổng số lượng item hiện có trong đợt giao trước
    //         int beforeTotal = deliveryItemRepository.findByDelivery_Id(delivery.getId())
    //                 .stream()
    //                 .mapToInt(DeliveryItem::getQuantity)
    //                 .sum();

    //         // tính đợt giao tiếp theo
    //         int afterAdd = beforeTotal + request.getQuantity();
    //         if (afterAdd > delivery.getQuantity()) {
    //             throw new GlobalException(ErrorCode.INVALID_QUANTITY);
    //         }
    //         // nếu đủ hàng sau khi thêm
    //         if (afterAdd == delivery.getQuantity()) {
    //             deliveryItem.setStatus(DeliveryItemStatus.IN_TRANSIT);
    //         } else {
    //             // còn thiếu
    //             deliveryItem.setStatus(DeliveryItemStatus.PENDING);
    //         }
    //         deliveryItem.setQuantity(request.getQuantity());
    //         deliveryItem.setCreateAt(LocalDateTime.now());

    //         deliveryItemRepository.save(deliveryItem);
    //         DeliveryItemResponse deliveryItemResponse = deliveryItemMapper.toDeliveryItemResponse(deliveryItem);
    //         return APIResponse.success(deliveryItemResponse, "Create Delivery Item Successfully");
    //     } catch (DataIntegrityViolationException ex) {
    //         throw new GlobalException(ErrorCode.DATA_INVALID);
    //     } catch (DataAccessException ex) {
    //         throw new GlobalException(ErrorCode.DB_ERROR);
    //     } catch (Exception ex) {
    //         throw new GlobalException(ErrorCode.OTHER);
    //     }
    // }

    // @Override
    // @Transactional
    // public APIResponse<DeliveryItemResponse> confirm(UUID id) {
    //     DeliveryItem deliveryItem = deliveryItemRepository.findById(id)
    //             .filter((item) -> item.getStatus().equals(DeliveryItemStatus.IN_TRANSIT) ||
    //                     item.getStatus().equals(DeliveryItemStatus.PENDING)
    //             )
    //             .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

    //     deliveryItem.setStatus(DeliveryItemStatus.DELIVERED);
    //     deliveryItem.setConfirmAt(LocalDateTime.now());
    //     deliveryItemRepository.save(deliveryItem);

    //     DeliveryItemResponse deliveryItemResponse = deliveryItemMapper.toDeliveryItemResponse(deliveryItem);
    //     return APIResponse.success(deliveryItemResponse, "Confirm delivery successfully");
    // }


    // @Override
    // public APIResponse<DeliveryItemResponse> viewDeliveryItem(UUID id) {
    //     DeliveryItem deliveryItem = deliveryItemRepository.findById(id)
    //             .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    //     DeliveryItemResponse deliveryItemResponse = deliveryItemMapper.toDeliveryItemResponse(deliveryItem);
    //     return APIResponse.success(deliveryItemResponse, "View delivery successfully");
    // }

    // @Override
    // public APIResponse<PageResponse<DeliveryItemResponse>> viewDeliveriesItem(Pageable pageable) {
    //     Page<DeliveryItem> deliveryItems = deliveryItemRepository.findAll(pageable);
    //     PageResponse<DeliveryItemResponse> deliveryItemResponsePageResponse = pageMapper.toPageResponse(deliveryItems,
    //             deliveryItemMapper::toDeliveryItemResponse);
    //     return APIResponse.success(deliveryItemResponsePageResponse, "View All Deliveries Item Successfully");
    // }

    // @Override
    // public APIResponse<DeliveryItemResponse> cancelDeliveryItem(UUID id) {
    //     DeliveryItem deliveryItem = deliveryItemRepository.findById(id)
    //             .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    //     deliveryItem.setStatus(DeliveryItemStatus.CANCELED);
    //     deliveryItemRepository.save(deliveryItem);
    //     return APIResponse.success(null, "Cancel Delivery Item Successfully");
    // }
}
