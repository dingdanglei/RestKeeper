package com.restkeeper.dubbo;

import com.restkeeper.tenant.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

@Activate
@Slf4j
public class DubboConsumerContextFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {


        //从自定义的上下文对象中获取到令牌的相关信息，然后给他存入到RpcContext
        RpcContext.getContext().setAttachment("shopId", TenantContext.getShopId());

        //从自定义的上下文对象中获取到登录人的相关信息，然后给他存入到RpcContext
        RpcContext.getContext().setAttachment("loginUserId", TenantContext.getLoginUserId());

        //从自定义的上下文对象中获取到登录人的相关信息，然后给他存入到RpcContext
        RpcContext.getContext().setAttachment("loginUserName", TenantContext.getLoginUserName());

        RpcContext.getContext().setAttachment("storeId", TenantContext.getStoreId());

        return invoker.invoke(invocation);
    }
}
