#!/bin/bash

if [ -z "$1" ]; then
  echo "Usage: $0 EntityName"
  exit 1
fi
ENTITY_NAME=$1
ENTITY_LOWER=$(echo "$ENTITY_NAME" | awk '{print tolower($0)}')
PATH_NAME="example/emob"
PACKAGE_NAME=$(echo "$PATH_NAME" | sed 's/\//./g')
# Tạo thư mục
mkdir -p src/main/java/com/${PATH_NAME}/entity
mkdir -p src/main/java/com/${PATH_NAME}/controller
mkdir -p src/main/java/com/${PATH_NAME}/service
mkdir -p src/main/java/com/${PATH_NAME}/service/impl
mkdir -p src/main/java/com/${PATH_NAME}/model
mkdir -p src/main/java/com/${PATH_NAME}/model/request
mkdir -p src/main/java/com/${PATH_NAME}/model/response
mkdir -p src/main/java/com/${PATH_NAME}/repository

#Entity
echo "
package com.${PACKAGE_NAME}.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ${ENTITY_NAME} {
    @Id
    @UuidGenerator
    UUID id;
}
"> src/main/java/com/${PATH_NAME}/entity/${ENTITY_NAME}.java

#Repository
echo "
package com.${PACKAGE_NAME}.repository;

import com.${PACKAGE_NAME}.entity.${ENTITY_NAME};
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ${ENTITY_NAME}Repository extends JpaRepository<${ENTITY_NAME}, UUID> {
}" > src/main/java/com/${PATH_NAME}/repository/${ENTITY_NAME}Repository.java

#Service
echo "
package com.${PACKAGE_NAME}.service;

import com.${PACKAGE_NAME}.repository.${ENTITY_NAME}Repository;
import com.${PACKAGE_NAME}.service.impl.I${ENTITY_NAME};
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ${ENTITY_NAME}Service implements I${ENTITY_NAME} {
      @Autowired
      private ${ENTITY_NAME}Repository ${ENTITY_LOWER}Repository;


}" > src/main/java/com/${PATH_NAME}/service/${ENTITY_NAME}Service.java

#API
echo "
package com.${PACKAGE_NAME}.controller;

import com.${PACKAGE_NAME}.entity.${ENTITY_NAME};
import com.${PACKAGE_NAME}.service.${ENTITY_NAME}Service;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(\"/api/${ENTITY_LOWER}\")
@CrossOrigin(\"*\")
@SecurityRequirement(name = \"api\")
public class ${ENTITY_NAME}API {

       @Autowired
       private ${ENTITY_NAME}Service ${ENTITY_LOWER}Service;



}" > src/main/java/com/${PATH_NAME}/controller/${ENTITY_NAME}API.java
#Mapper
echo "
package com.${PACKAGE_NAME}.mapper;

import com.${PACKAGE_NAME}.entity.${ENTITY_NAME};
import com.${PACKAGE_NAME}.model.request.${ENTITY_NAME}Request;
import com.${PACKAGE_NAME}.model.response.${ENTITY_NAME}Response;
import org.mapstruct.Mapper;


@Mapper(componentModel = \"spring\")
public interface ${ENTITY_NAME}Mapper {
    ${ENTITY_NAME} to${ENTITY_NAME}(${ENTITY_NAME}Request request);
    ${ENTITY_NAME}Response to${ENTITY_NAME}Response(${ENTITY_NAME} account);
}
" > src/main/java/com/${PATH_NAME}/mapper/${ENTITY_NAME}Mapper.java

#Model
#Request
echo "
package com.${PACKAGE_NAME}.model.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ${ENTITY_NAME}Request {

}
" > src/main/java/com/${PATH_NAME}/model/request/${ENTITY_NAME}Request.java
#Response
echo "
package com.${PACKAGE_NAME}.model.response;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ${ENTITY_NAME}Response {

}
" > src/main/java/com/${PATH_NAME}/model/response/${ENTITY_NAME}Response.java

#Implement Service
echo "
package com.${PACKAGE_NAME}.service.impl;


public interface I${ENTITY_NAME} {


}
" > src/main/java/com/${PATH_NAME}/service/impl/I${ENTITY_NAME}.java
echo "Successfully created ${ENTITY_NAME} file!"