package com.youxuan.product.controller;

import com.youxuan.common.api.ApiResponse;
import com.youxuan.product.model.CategoryDO;
import com.youxuan.product.service.CategoryService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类目查询接口。
 */
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 获取启用的类目树。
     */
    @GetMapping("/tree")
    public ApiResponse<List<CategoryDO>> tree() {
        return ApiResponse.success(categoryService.getCategoryTree());
    }
}
