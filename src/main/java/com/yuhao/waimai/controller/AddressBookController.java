package com.yuhao.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yuhao.waimai.bean.AddressBook;
import com.yuhao.waimai.common.BaseContext;
import com.yuhao.waimai.common.R;
import com.yuhao.waimai.service.AddressBookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Resource
    private AddressBookService addressBookService;

    //新增地址
    @PostMapping
    public R<String> addAddress(@RequestBody AddressBook addressBook/*, HttpSession session*/){
       /* Long userId = (Long) session.getAttribute("user");*/
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressBookService.save(addressBook);
        return R.success("添加成功!");
    }

    //展示所有地址
    /*请求网址: http://localhost:8083/addressBook/list
     请求方法: GET*/
    @GetMapping("/list")
    public R<List<AddressBook>> getList(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AddressBook::getUserId,userId);
        List<AddressBook> list = addressBookService.list(lambdaQueryWrapper);

        return R.success(list);
    }

    //设置为默认地址
    /*请求网址: http://localhost:8083/addressBook/default
        请求方法: PUT*/
    @PutMapping("/default")
    public R<String> setDefault(@RequestBody AddressBook addressBook){
        LambdaUpdateWrapper<AddressBook> lambdaUpdateWrapper1 = new LambdaUpdateWrapper<>();
        //先 取消原来的默认地址 再添加新的默认地址
        lambdaUpdateWrapper1.eq(AddressBook::getIsDefault,1);
        lambdaUpdateWrapper1.set(AddressBook::getIsDefault,0);
        addressBookService.update(lambdaUpdateWrapper1);

        LambdaUpdateWrapper<AddressBook> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(AddressBook::getIsDefault,1);
        lambdaUpdateWrapper.eq(AddressBook::getId,addressBook.getId());
        addressBookService.update(lambdaUpdateWrapper);
        return R.success("设置默认地址成功!");
    }
    /*请求网址: http://localhost:8083/addressBook/default
        请求方法: GET*/
    @GetMapping("/default")
    public R<AddressBook> showDefaultAddress(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AddressBook::getUserId,userId);
        lambdaQueryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookService.getOne(lambdaQueryWrapper);
        return R.success(addressBook);
    }

    //在修改地址页面回显数据
    /*请求网址: http://localhost:8083/addressBook/1539858168610324482
    请求方法: GET*/
    @GetMapping("/{id}")
    public R<AddressBook> getDatainUpdatePage(@PathVariable Long id){
        /*LambdaQueryWrapper<AddressBook> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AddressBook::getId,id);*/
        AddressBook addressBook = addressBookService.getById(id);
        return R.success(addressBook);
    }
    //修改地址
    /*请求网址: http://localhost:8083/addressBook
    请求方法: PUT*/
    @PutMapping
    public R<String> updateAddress(@RequestBody AddressBook addressBook){
        LambdaUpdateWrapper<AddressBook> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(AddressBook::getId,addressBook.getId());
        addressBookService.update(addressBook,lambdaUpdateWrapper);
        return R.success("修改成功!");
    }

    //删除地址
    /*请求网址: http://localhost:8083/addressBook?ids=1539874432074448898
请求方法: DELETE*/
    @DeleteMapping
    public R<String> deleteAddress(Long ids){
        addressBookService.removeById(ids);
        return R.success("删除成功!");
    }
}
