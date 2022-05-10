package com.restkeeper.shop.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import com.google.common.collect.Lists;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MybatisPlusTenantConfig {

    /**定义当前的多租户标识字段*/
    private static final String SYSTEM_TENANT_ID="shop_id";
    private static final String SYSTEM_TENANT_ID2= "store_id";

    /**定义当前有哪些表要忽略多租户的操作*/
    private static final List<String> IGNORE_TENANT_TABLES = Lists.newArrayList("");

    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();

        //多租户sql解析器，sql解析处理拦截，增加租户处理回调
        TenantSqlParser tenantSqlParserShop = new TenantSqlParser().setTenantHandler(new TenantHandler() {

            //设置租户id
            @Override
            public Expression getTenantId(boolean where) {

                //暂时写死，用于测试
                //String shopId="test";
                //从RPCContext对象中获取shopid
                String shopId = RpcContext.getContext().getAttachment("shopId");

                if (shopId == null){
                    throw new RuntimeException("get tenantId error");
                }
                return new StringValue(shopId);
            }

            //设置租户id所对应的表字段
            @Override
            public String getTenantIdColumn() {
                return SYSTEM_TENANT_ID;
            }

            //表级过滤器
            @Override
            public boolean doTableFilter(String tableName) {
                return IGNORE_TENANT_TABLES.stream().anyMatch((e)->e.equalsIgnoreCase(tableName));
            }
        });

        // SQL解析处理拦截：增加租户处理回调。
        TenantSqlParser tenantSqlParserStore = new TenantSqlParser().setTenantHandler(new TenantHandler() {
            @Override
            public Expression getTenantId(boolean where) {
                // 从当前系统上下文中取出当前请求的服务商ID，通过解析器注入到SQL中。
                String storeId = RpcContext.getContext().getAttachment("storeId");
                if (null == storeId) {
                    throw new RuntimeException("get storeId error.");
                }
                return new StringValue(storeId);
            }

            @Override
            public String getTenantIdColumn() {
                return SYSTEM_TENANT_ID2;
            }

            @Override
            public boolean doTableFilter(String tableName) {
                // 忽略掉一些表：如租户表（provider）本身不需要执行这样的处理。
                return IGNORE_TENANT_TABLES.stream().anyMatch((e) -> e.equalsIgnoreCase(tableName));
            }
        });

        paginationInterceptor.setSqlParserList(Lists.newArrayList(tenantSqlParserShop,tenantSqlParserStore));

        return paginationInterceptor;
    }
}
