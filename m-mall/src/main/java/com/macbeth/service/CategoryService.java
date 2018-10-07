package com.macbeth.service;

import com.macbeth.common.ServerResponse;
import com.macbeth.pojo.Category;

import java.util.List;
import java.util.Set;

public interface CategoryService {
    ServerResponse<String> addCategory(Category category);

    ServerResponse<String> updateCategoryNameById(Integer categoryId, String categoryName);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServerResponse<Set<Category>> getChildrenRecursionCategory(Integer parentId);

    Category getCategoryById(Integer categoryId);
}
