
package com.enterprise.erp.controller;

import com.enterprise.erp.dto.GoodsReceiptRequest;
import com.enterprise.erp.dto.GoodsReceiptResponse;
import com.enterprise.erp.service.GoodsReceiptService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goods-receipts")
public class GoodsReceiptController {

    private final GoodsReceiptService goodsReceiptService;

    public GoodsReceiptController(
            GoodsReceiptService goodsReceiptService) {
        this.goodsReceiptService = goodsReceiptService;
    }

    // Create a goods receipt and update inventory.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GoodsReceiptResponse createGoodsReceipt(
            @Valid @RequestBody GoodsReceiptRequest request) {

        return goodsReceiptService.createGoodsReceipt(request);
    }


    @GetMapping("/{receiptNumber}")
    public GoodsReceiptResponse getGoodsReceiptByReceiptNumber(
            @PathVariable String receiptNumber) {

        return goodsReceiptService
                .getGoodsReceiptByReceiptNumber(receiptNumber);
    }



    @GetMapping
    public List<GoodsReceiptResponse> getAllGoodsReceipts() {
        return goodsReceiptService.getAllGoodsReceipts();
    }

}
