package com.yuhao.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuhao.waimai.bean.OrderDetail;
import com.yuhao.waimai.bean.Orders;
import com.yuhao.waimai.common.BaseContext;
import com.yuhao.waimai.common.R;
import com.yuhao.waimai.dto.OrdersDto;
import com.yuhao.waimai.service.OrderDetailService;
import com.yuhao.waimai.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {

    @Resource
    OrdersService ordersService;

    @Resource
    OrderDetailService orderDetailService;

    /*请求网址: http://localhost:8083/order/submit
    请求方法: POST*/
    @PostMapping("/submit")
    public R<String> submitOrder(@RequestBody Orders orders, HttpSession session){
        log.info("订单数据{}",orders);
        ordersService.submit(orders,session);
        return R.success("提交订单成功");
    }
    //派送用户订单
    /*请求网址: http://localhost:8083/order
    请求方法: PUT   */
    @PutMapping
    public R<String> changeStatus(@RequestBody Orders orders){
        LambdaUpdateWrapper<Orders> ordersLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        ordersLambdaUpdateWrapper.eq(Orders::getId,orders.getId());
        ordersLambdaUpdateWrapper.set(Orders::getStatus,orders.getStatus());
        ordersService.update(ordersLambdaUpdateWrapper);
        return  R.success("派送成功!");
    }
    //用户订单分页查询
    /*请求网址: http://localhost:8083/order/userPage?page=1&pageSize=1
    请求方法: GET*/
    @GetMapping("/userPage")
    public R<Page> orderDetail(Integer page, Integer pageSize) {
        Page<Orders> pageInfo = new Page<>(page, pageSize);

        Page<OrdersDto> ordersDtoPage = new Page<OrdersDto>();

        Long currentId = BaseContext.getCurrentId();
//        查询订单数据
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", currentId);
        Page<Orders> ordersPage = ordersService.page(pageInfo, wrapper);

        BeanUtils.copyProperties(pageInfo, ordersDtoPage, "records");

        List<Orders> ordersList = ordersPage.getRecords();

        List<OrdersDto> ordersDtoList = ordersList.stream().map(item -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);

            Long orderId = item.getId();

            if (orderId != null) {
//                        查询订单明细表
                QueryWrapper<OrderDetail> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("order_id", orderId);
                List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);
                ordersDto.setOrderDetails(orderDetailList);
            }

            return ordersDto;

        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(ordersDtoList);

        return R.success(ordersDtoPage);


    }
    /*@GetMapping("/userPage")
    public R<Page<Orders>> pageQuery(int page, int pageSize){

        Page<Orders> pageinfo = new Page<>(page,pageSize);

        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Orders::getUserId,BaseContext.getCurrentId());
        ordersService.page(pageinfo,lambdaQueryWrapper);
        return R.success(pageinfo);
    }*/

    //后台展示订单
    /*请求网址: http://localhost:8083/order/page
        ?page=1&pageSize=10&number=1&beginTime=2022-06-07%2000%3A00%3A00&endTime=2022-06-30%2023%3A59%3A59
    请求方法: GET*/
    @GetMapping("/page")
    public R<Page> pageBackend(int page, int pageSize, String number, String beginTime, String endTime) throws ParseException {
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(number != null, Orders::getNumber,number);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (beginTime !=null || endTime !=null){
        Date begin = sdf.parse(beginTime);
        Date end = sdf.parse(endTime);
        lambdaQueryWrapper.between(Orders::getOrderTime,begin,end);
        }

        ordersService.page(pageInfo,lambdaQueryWrapper);
        return R.success(pageInfo);
    }

    /*请求网址: http://localhost:8083/order/again
    请求方法: POST*/
    //客户端点击再来一单
    /**
     * 前端点击再来一单是直接跳转到购物车的，所以为了避免数据有问题，再跳转之前我们需要把购物车的数据给清除
     * ①通过orderId获取订单明细
     * ②把订单明细的数据的数据塞到购物车表中，不过在此之前要先把购物车表中的数据给清除(清除的是当前登录用户的购物车表中的数据)，
     * 不然就会导致再来一单的数据有问题；
     * (这样可能会影响用户体验，但是对于外卖来说，用户体验的影响不是很大，电商项目就不能这么干了)
     */

    @PostMapping("/again")
    public R<String> orderAgain(@RequestBody Map<String,String> map){
        String ids = map.get("id");
        long id = Long.parseLong(ids);

        ordersService.orderAgain(id);

        return R.success("再吃一单!");
    }
}
