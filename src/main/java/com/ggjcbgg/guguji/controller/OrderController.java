package com.ggjcbgg.guguji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ggjcbgg.guguji.common.BaseContext;
import com.ggjcbgg.guguji.common.R;
import com.ggjcbgg.guguji.dto.OrdersDto;
import com.ggjcbgg.guguji.entity.OrderDetail;
import com.ggjcbgg.guguji.entity.Orders;
import com.ggjcbgg.guguji.entity.ShoppingCart;
import com.ggjcbgg.guguji.service.OrderDetailService;
import com.ggjcbgg.guguji.service.OrderService;
import com.ggjcbgg.guguji.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单管理
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);

        orderService.submit(orders);

        return R.success("下单成功");
    }

    /**
     * 个人中心：查看最新订单
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPage(int page,int pageSize){
        log.info("page={},pageSize={}",page,pageSize);

        //创建分页构造器
        Page<Orders> pageInfo = new Page(page,pageSize);

        //获取用户id
        Long userId = BaseContext.getCurrentId();

        //创建条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId,userId);
        queryWrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo,queryWrapper);

        //创建ordersDto的分页构造器
        Page<OrdersDto> orderDtoPage = new Page<>();
        //拷贝除了records的数据
        BeanUtils.copyProperties(pageInfo,orderDtoPage,"records");
        //取得pageInfo中的records数据
        List<Orders> records = pageInfo.getRecords();


        //使用流遍历records中的数据并计算菜品总数，添加订单明细
        List<OrdersDto> ordersDtoRecords = records.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            //根据订单号查找订单明细
            LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderDetail::getOrderId,item.getId());
            List<OrderDetail> orderDetails = orderDetailService.list(wrapper);
            //计算菜品总数
            int sumNum = 0;
            for (OrderDetail orderDetail:orderDetails){
                sumNum+= orderDetail.getNumber();
            }

            //拷贝item中的数据到ordersDto中
            BeanUtils.copyProperties(item,ordersDto);
            //设置订单明细和菜品总数
            ordersDto.setOrderDetails(orderDetails);
            ordersDto.setSumNum(sumNum);

            return ordersDto;
        }).collect(Collectors.toList());

        //设置数据
        orderDtoPage.setRecords(ordersDtoRecords);

        return R.success(orderDtoPage);
    }

    /**
     * 再来一单
     * @param orders
     * @return
     */
    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders){
        log.info("订单号为：{}",orders);
        //根据订单号获取订单菜品详细数据
        Long orderId =orders.getId();
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,orderId);
        List<OrderDetail> orderDetails = orderDetailService.list(queryWrapper);

        //遍历将订单的菜品详细信息封装到购物车对象中，再添加到购物车
        for(OrderDetail orderDetail:orderDetails){
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setDishFlavor(orderDetail.getDishFlavor());//设置口味
            shoppingCart.setDishId(orderDetail.getDishId());//设置菜品id
            shoppingCart.setSetmealId(orderDetail.getSetmealId());//设置套餐id
            shoppingCart.setNumber(orderDetail.getNumber());//设置数量
            shoppingCart.setAmount(orderDetail.getAmount());//设置数量
            shoppingCart.setImage(orderDetail.getImage());//设置图片
            shoppingCart.setName(orderDetail.getName());//设置名字
            shoppingCartService.add(shoppingCart);
        }

        return R.success("再来一单成功");
    }

    /**
     * 查询订单明细，可根据订单号，日期查找
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, Long number, String beginTime, String endTime){

        log.info("page={}，pageSize={},begin={},endTime={}",page,pageSize,beginTime,endTime);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime beginTime1 = null,endTime1=null;
        if(beginTime!=null&&endTime!=null) {
            beginTime1 = LocalDateTime.parse(beginTime, formatter);
            endTime1 = LocalDateTime.parse(endTime, formatter);
        }

        //创建分页构造器
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        //创建条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(number!=null,Orders::getId,number);
        queryWrapper.between(beginTime!=null&&endTime!=null,Orders::getOrderTime,beginTime1,endTime1);
        orderService.page(pageInfo,queryWrapper);



        return R.success(pageInfo);
    }


    /**
     * 派送订单
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> put(@RequestBody Orders orders){
        log.info("订单信息为：{}",orders);

        //创建条件构造器
        LambdaUpdateWrapper<Orders> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Orders::getStatus,4);
        updateWrapper.eq(Orders::getId,orders.getId());
        //执行更新
        orderService.update(updateWrapper);

        return R.success("派送订单成功");
    }


}
