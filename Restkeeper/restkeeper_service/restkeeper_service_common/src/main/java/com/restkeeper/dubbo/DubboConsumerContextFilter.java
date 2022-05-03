package com.restkeeper.dubbo;

import com.restkeeper.tenant.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

@Activate //开启了dubbo的扩展
@Slf4j
public class DubboConsumerContextFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        /*log.info("shopId-----------"+RpcContext.getContext().getAttachment("shopId"));

        log.info("ThreadName-------------"+Thread.currentThread().getName());*/

        //从自定义的上下文对象中获取到令牌的相关信息，然后给他存入到RPCCONTEXT
        RpcContext.getContext().setAttachment("shopId", TenantContext.getShopId());


        return invoker.invoke(invocation);
    }
}
