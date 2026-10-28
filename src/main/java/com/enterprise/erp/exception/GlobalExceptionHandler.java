package com.enterprise.erp.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessPartnerAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessPartnerAlreadyExists(
            BusinessPartnerAlreadyExistsException exception,
            HttpServletRequest request) {

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(java.util.stream.Collectors.joining("; "));

        if (message.isBlank()) {
            message = "Validation failed";
        }

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }


    @ExceptionHandler(BusinessPartnerNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessPartnerNotFound(
            BusinessPartnerNotFoundException exception,
            HttpServletRequest request) {

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(DuplicateMaterialException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateMaterial(
            DuplicateMaterialException exception,
            HttpServletRequest request) {

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }

    @ExceptionHandler(MaterialNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleMaterialNotFound(
            MaterialNotFoundException exception,
            HttpServletRequest request) {

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }


    @ExceptionHandler(DuplicatePlantException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicatePlant(
            DuplicatePlantException exception,
            HttpServletRequest request) {

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }


    @ExceptionHandler(PlantNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handlePlantNotFound(
            PlantNotFoundException exception,
            HttpServletRequest request) {

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }


    @ExceptionHandler(DuplicateStorageLocationException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateStorageLocation(
            DuplicateStorageLocationException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }


    @ExceptionHandler(StorageLocationNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleStorageLocationNotFound(
            StorageLocationNotFoundException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }


    @ExceptionHandler(DuplicateInventoryException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateInventory(
            DuplicateInventoryException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }


    @ExceptionHandler(InventoryNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleInventoryNotFound(
            InventoryNotFoundException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }


    @ExceptionHandler(DuplicatePurchaseOrderException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicatePurchaseOrder(
            DuplicatePurchaseOrderException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }




    @ExceptionHandler(PurchaseOrderNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handlePurchaseOrderNotFound(
            PurchaseOrderNotFoundException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }



    @ExceptionHandler(InvalidPurchaseOrderStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidPurchaseOrderStatus(
            InvalidPurchaseOrderStatusException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }



    @ExceptionHandler(DuplicateGoodsReceiptException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateGoodsReceipt(
            DuplicateGoodsReceiptException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }



    @ExceptionHandler(GoodsReceiptNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleGoodsReceiptNotFound(
            GoodsReceiptNotFoundException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }


    @ExceptionHandler(ExcessGoodsReceiptQuantityException.class)
    public ResponseEntity<ApiErrorResponse> handleExcessGoodsReceiptQuantity(
            ExcessGoodsReceiptQuantityException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    @ExceptionHandler(GoodsReceiptPlantMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleGoodsReceiptPlantMismatch(
            GoodsReceiptPlantMismatchException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }



    @ExceptionHandler(DuplicateGoodsReceiptItemException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateGoodsReceiptItem(
            DuplicateGoodsReceiptItemException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    @ExceptionHandler(PurchaseOrderItemNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handlePurchaseOrderItemNotFound(
            PurchaseOrderItemNotFoundException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }


    @ExceptionHandler(PurchaseOrderItemMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handlePurchaseOrderItemMismatch(
            PurchaseOrderItemMismatchException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(
            Exception ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An unexpected error occurred. Please try again later.",
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }



    @ExceptionHandler(InvalidSalesOrderStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidSalesOrderStatus(
            InvalidSalesOrderStatusException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }


    @ExceptionHandler(SalesOrderNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleSalesOrderNotFound(
            SalesOrderNotFoundException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }



    @ExceptionHandler(InvalidSalesOrderCustomerException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidSalesOrderCustomer(
            InvalidSalesOrderCustomerException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }



    @ExceptionHandler(SalesOrderPlantNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleSalesOrderPlantNotFound(
            SalesOrderPlantNotFoundException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(SalesOrderMaterialNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleSalesOrderMaterialNotFound(
            SalesOrderMaterialNotFoundException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }


    @ExceptionHandler(DuplicateSalesOrderException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateSalesOrder(
            DuplicateSalesOrderException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }


    @ExceptionHandler(SalesOrderCustomerNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleSalesOrderCustomerNotFound(
            SalesOrderCustomerNotFoundException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }


    @ExceptionHandler(InsufficientInventoryException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficientInventory(
            InsufficientInventoryException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }


}