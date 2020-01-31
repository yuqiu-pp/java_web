package com.test.smart.Controller;

import com.smart4j.framework.annotation.Action;
import com.smart4j.framework.annotation.Controller;
import com.smart4j.framework.annotation.Inject;
import com.smart4j.framework.bean.Param;
import com.smart4j.framework.bean.View;
import com.test.smart.Service.CustomerService;
import com.test.smart.model.Customer;

import java.util.List;


@Controller
public class CustomerController {

    @Inject
    private CustomerService customerService;


    @Action("get:/customer")
    public View index(Param param){
        List<Customer> customerList = customerService.getCustomerList();
        return new View("customer.jsp").addModel("customerList", customerList);
    }
}
