package com.sias.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sias.waimai.common.BaseContext;
import com.sias.waimai.mapper.AddressBookMapper;
import com.sias.waimai.pojo.AddressBook;
import com.sias.waimai.pojo.R;
import com.sias.waimai.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户地址信息管理
 * @author li+
 * @date 2024/11/24 15:55
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 查询用户全部地址
     * @return
     */
    @NotNull
    @GetMapping("/list")
    private R<List<AddressBook>> list(){
        Long id = BaseContext.getCurrentId();
//        log.info("addressBook:{}",addressBook);
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId,id);
        wrapper.orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> addressBookList = addressBookMapper.selectList(wrapper);
        return R.success(addressBookList);
    }

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook){
        Long userId = BaseContext.getCurrentId();//获取线程id
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId,userId);//设置公共查询条件
        //判断是否为第一个地址
        List<AddressBook> list = addressBookMapper.selectList(wrapper);
        if (list.isEmpty()){
            addressBook.setIsDefault(1);//设为默认地址
        }
        addressBook.setUserId(String.valueOf(userId));//设置用户id
        addressBookService.save(addressBook);//保存地址
        wrapper.eq(AddressBook::getPhone,addressBook.getPhone());
        AddressBook one = addressBookMapper.selectOne(wrapper);
        if (one == null){
            return R.error("新增地址失败,未成功保存至数据库");
        }
        return R.success(addressBook);
    }

    /**
     * 修改默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    private R<String> setDefault(@RequestBody AddressBook addressBook){
        Long uid = BaseContext.getCurrentId();
        if (uid != null){
            if (addressBookService.updateIsNotDefault(uid)){
                //修改成功，根据id设置默认地址
                if (addressBookService.updateIsDefault(addressBook.getId())){
                    return R.success("设置默认地址成功");
                }
                return R.error("设置默认地址失败");
            }
            return R.error("取消默认地址失败");
        }
        return R.error("获取线程uid失败");
    }

    /**
     * 获取默认地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault() {
        Long uid = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId,uid);
        wrapper.eq(AddressBook::getIsDefault,1);
        AddressBook one = addressBookService.getOne(wrapper);
        return R.success(one);
    }
}
