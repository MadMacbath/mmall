package com.macbeth.service.impl;

import com.google.common.collect.Sets;
import com.macbeth.common.ServerResponse;
import com.macbeth.dao.CategoryMapper;
import com.macbeth.pojo.Category;
import com.macbeth.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse<String> addCategory(Category category) {
        int responseCount = categoryMapper.insert(category);
        if (responseCount == 0){
            return ServerResponse.createByErrorMessage("创建类别失败");
        }
        return ServerResponse.createBySuccessMessage("类别创建成功");
    }

    @Override
    public ServerResponse<String> updateCategoryNameById(Integer categoryId, String categoryName) {
        Category category = categoryMapper.selectCategoryById(categoryId);
        if (category == null){
            return ServerResponse.createByErrorMessage(categoryId + "对应的类别对象不存在");
        }
        category.setName(categoryName);
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount == 0){
            return ServerResponse.createByErrorMessage("类别名更新失败");
        }
        return ServerResponse.createBySuccessMessage("类别名更新成功");
    }

    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId) {
        List<Category> categories = categoryMapper.selectCategoryByParentId(categoryId);
        return ServerResponse.createBySuccess(categories);
    }

    @Override
    public ServerResponse<Set<Category>> getChildrenRecursionCategory(Integer parentId) {
        Set<Category> categories = Sets.newHashSet();
        recursionCategoryChildren(parentId,categories);
        return ServerResponse.createBySuccess(categories);
    }

    @Override
    public Category getCategoryById(Integer categoryId) {
        return categoryMapper.selectCategoryById(categoryId);
    }

    private void recursionCategoryChildren(Integer parentId,Set<Category> sets){

        List<Category> categories = this.getChildrenParallelCategory(parentId).getData();
        sets.addAll(categories);
        categories.stream()
                .map(Category::getId)
                .forEach(id -> recursionCategoryChildren(id,sets));
    }

}
