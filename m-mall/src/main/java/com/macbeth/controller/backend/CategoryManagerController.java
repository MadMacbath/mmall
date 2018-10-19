package com.macbeth.controller.backend;

import com.macbeth.common.Constant;
import com.macbeth.common.ServerResponse;
import com.macbeth.pojo.Category;
import com.macbeth.pojo.User;
import com.macbeth.service.CategoryService;
import com.macbeth.to.manager.category.CategoryAdd;
import com.macbeth.to.manager.category.CategoryUpdateName;
import com.macbeth.util.ControllerUtils;
import com.macbeth.util.ObjectUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@Api(tags = "分类管理接口")
@RequestMapping(value = "managers")
public class CategoryManagerController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation(value = "增加分类")
    @RequestMapping(value = "category",method = RequestMethod.GET)
    public ServerResponse<String> addCategory(@Valid CategoryAdd categoryAdd,
                                                @ApiIgnore HttpSession session){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (! response.isSuccess())
            return response;
        User user = (User) response.getData();
        if (user.getRole() != Constant.Role.ROLE_ADMIN)
            return ServerResponse.createByErrorMessage("当前用户不是管理员");

        Category category = new Category();
        ObjectUtils.transferEntity(category,categoryAdd);
        return categoryService.addCategory(category);
    }

    @ApiOperation(value = "更新类别名称")
    @RequestMapping(value = "category/name",method = RequestMethod.PUT)
    public ServerResponse updateCategoryName(@Valid CategoryUpdateName categoryUpdateName,
                                                     @ApiIgnore HttpSession session){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (! response.isSuccess()){
            return response;
        }
        User user = (User) response.getData();
        if (user.getRole() != Constant.Role.ROLE_ADMIN){
            return ServerResponse.createByErrorMessage("当前用户不是管理员");
        }
        return categoryService.updateCategoryNameById(categoryUpdateName.getId(),categoryUpdateName.getName());
    }

    @ApiOperation(value = "查询平级子节点信息")
    @RequestMapping(value = "category/parallel/{parentId}",method = RequestMethod.GET)
    public ServerResponse<List<Category>> getChildrenParallelCategory(@ApiParam(value = "父类别ID",name = "parentId") @PathVariable("parentId") Integer parentId,
                                                                      @ApiIgnore HttpSession session){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (! response.isSuccess()) return response;
        return categoryService.getChildrenParallelCategory(parentId);
    }

    @ApiOperation(value = "递归查询所有子类别信息")
    @RequestMapping(value = "category/{parentId}",method = RequestMethod.GET)
    public ServerResponse<Set<Category>> getChildrenDeepCategory(@ApiParam(value = "父类别ID",name = "parentId") @PathVariable("parentId") Integer parentId,
                                                                      @ApiIgnore HttpSession session){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError() || ControllerUtils.isAdminLogin(response).isError())
            return response;
        return categoryService.getChildrenRecursionCategory(parentId);
    }
}
