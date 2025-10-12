package com.example.emob.service;

import com.example.emob.constant.DeliveryItemStatus;
import com.example.emob.constant.DeliveryStatus;
import com.example.emob.constant.ErrorCode;
import com.example.emob.entity.Delivery;
import com.example.emob.entity.DeliveryItem;
import com.example.emob.entity.Inventory;
import com.example.emob.entity.VehicleUnit;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.DeliveryItemMapper;
import com.example.emob.mapper.PageMapper;
import com.example.emob.model.request.delivery.DeliveryItemRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.DeliveryItemResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.repository.DeliveryItemRepository;
import com.example.emob.repository.DeliveryRepository;
import com.example.emob.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;


public class DeliveryItemService {
    @Autowired
    PageMapper pageMapper;

    @Autowired
    DeliveryItemRepository deliveryItemRepository;

    @Autowired
    DeliveryItemMapper deliveryItemMapper;

    @Autowired
    DeliveryRepository deliveryRepository;





}
