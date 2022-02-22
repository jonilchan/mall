package com.atguigu.gulimall.ware.vo;


import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PurchaseDoneVo {

    /**
     * 采购单id
     */
    @NotNull(message = "id不允许为空")
    private Long id;
    /**
     * 采购项列表
     */
    private List<PurchaseItemDoneVo> items;
}
