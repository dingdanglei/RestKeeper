package com.restkeeper.operator.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.restkeeper.operator.entity.EnterpriseAccount;
import com.restkeeper.operator.service.IEnterpriseAccountService;
import com.restkeeper.operator.vo.AddEnterpriseAccountVO;
import com.restkeeper.operator.vo.ResetPwdVO;
import com.restkeeper.operator.vo.UpdateEnterpriseAccountVO;
import com.restkeeper.response.vo.PageVO;
import com.restkeeper.utils.AccountStatus;
import com.restkeeper.utils.Result;
import com.restkeeper.utils.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * @author dinglei
 * @date 2022/5/2 19:13
 */
@Slf4j
@Api(tags = {"企业帐号管理"})
@RestController
@RequestMapping("/enterprise")
public class EnterpriseAccountController {

    @Reference(version = "1.0.0",check = false)
    private IEnterpriseAccountService enterpriseAccountService ;


    @ApiOperation("查询企业帐号列表")
    @GetMapping("/pageList/{page}/{pageSize}")
    public PageVO findListByPage(@PathVariable("page") int page ,
                                 @PathVariable("pageSize") int pageSize ,
                                 @RequestParam(value = "enterpriseName" , required = false) String name ){
        return new PageVO<EnterpriseAccount>(enterpriseAccountService.queryPageByName(page, pageSize, name));
    }



    @ApiOperation("新增账号")
    @PostMapping("/add")
    public boolean add(@RequestBody AddEnterpriseAccountVO enterpriseAccountVO){
        // bean 拷贝
        EnterpriseAccount account = new EnterpriseAccount();
        BeanUtils.copyProperties(enterpriseAccountVO,account);
        //设置时间
        LocalDateTime localDateTime = LocalDateTime.now();
        // 申请时间
        account.setApplicationTime(localDateTime);
        //过期时间
        LocalDateTime expireTime = null ;
        // 试用中
        if(enterpriseAccountVO.getStatus() == 0 ){
            expireTime = localDateTime.plusDays(7);
        }
        //正式
        if(enterpriseAccountVO.getStatus() == 1){
            expireTime = localDateTime.plusDays(enterpriseAccountVO.getValidityDay());
        }
        if(expireTime!=null){
            account.setExpireTime(expireTime);
        }else{
            throw new RuntimeException("账号类型信息设置有误");
        }
        return enterpriseAccountService.add(account);

    }



    @ApiOperation(value = "账户查看")
    @ApiImplicitParam(paramType = "path" , name = "id" , value = "主键" , required = true , dataType = "String")
    @GetMapping("/getById/{id}")
    public EnterpriseAccount getById(@PathVariable("id") String id){
        return enterpriseAccountService.getById(id);
    }

    @ApiOperation("账号编辑")
    @PostMapping("/update")
    public Result update(@RequestBody UpdateEnterpriseAccountVO updateEnterpriseAccountVO){
        Result result = new Result();
        //原账户信息
        EnterpriseAccount enterpriseAccount = enterpriseAccountService.getById(updateEnterpriseAccountVO.getEnterpriseId());
        if(enterpriseAccount == null ){
            result.setStatus(ResultCode.error);
            result.setDesc("修改的账户不存在");
            return result ;
        }
        // 修改状态校验
        if(updateEnterpriseAccountVO.getStatus()!=null){
            // 正式期不能改成试用
            if(updateEnterpriseAccountVO.getStatus() == 0 && enterpriseAccount.getStatus() == 1 ){
                result.setStatus(ResultCode.error);
                result.setDesc("不能将正式账号改为试用账号");
                return result ;
            }
            // 试用改成正式
            if(updateEnterpriseAccountVO.getStatus() == 1 && enterpriseAccount.getStatus() == 0){
                LocalDateTime now = LocalDateTime.now();
                //到期时间
                LocalDateTime expireTime = now.plusDays(updateEnterpriseAccountVO.getValidityDay());
                enterpriseAccount.setApplicationTime(now);
                enterpriseAccount.setExpireTime(expireTime);
            }
            // 正式改延期
            if(updateEnterpriseAccountVO.getStatus() == 1 && enterpriseAccount.getStatus() == 1) {
                LocalDateTime now = LocalDateTime.now();
                //到期时间
                LocalDateTime expireTime = now.plusDays(updateEnterpriseAccountVO.getValidityDay());
                enterpriseAccount.setExpireTime(expireTime);
            }
        }
        BeanUtils.copyProperties(updateEnterpriseAccountVO,enterpriseAccount);
        boolean flag = enterpriseAccountService.updateById(enterpriseAccount);
        if(flag){
            result.setStatus(ResultCode.success);
            result.setDesc("修改成功");
            return result;
        }else{
            result.setStatus(ResultCode.error);
            result.setDesc("修改失败");
            return result;
        }
    }


    @ApiOperation("账户删除(逻辑删除)")
    @ApiImplicitParam(paramType = "path" , name = "id" , value = "主键" , required = true , dataType = "String")
    @DeleteMapping("/deleteById/{id}")
    public boolean delete(@PathVariable("id") String id ){
        return enterpriseAccountService.removeById(id);
    }

    @ApiOperation("账户还原")
    @ApiImplicitParam(paramType = "path" , name = "id" , value = "主键" , required = true , dataType = "String")
    @PostMapping("/recovery/{id}")
    public boolean recovery(@PathVariable("id") String id){
        return enterpriseAccountService.recovery(id);
    }


    @ApiOperation("账户禁用")
    @ApiImplicitParam(paramType = "path" , name = "id" , value = "主键" , required = true , dataType = "String")
    @PostMapping("/forbidden/{id}")
    public boolean forbidden(@PathVariable("id") String id){
        EnterpriseAccount enterpriseAccount = enterpriseAccountService.getById(id);
        enterpriseAccount.setStatus(AccountStatus.Forbidden.getStatus());
        return enterpriseAccountService.updateById(enterpriseAccount);
    }

    @ApiOperation("重置密码")
    @PutMapping("/resetPwd")
    public boolean resetPwd(@RequestBody ResetPwdVO resetPwdVO){
        return enterpriseAccountService.resetPwd(resetPwdVO.getId(),resetPwdVO.getPassword());
    }
}
