package com.bitop.otcapi.fcg.entity;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitop.otcapi.fcg.entity.Field;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class SearchModel<T> {

    @ApiModelProperty(value = "页码", required = true)
    @NotBlank(message = "{页码不能为空}")
    private Integer page;

    @ApiModelProperty(value = "每页条数", required = true)
    @NotBlank(message = "{每页条数不能为空}")
    private Integer limit;

    @ApiModelProperty(value = "条件字段")
    private List<Field> fields;

    @ApiModelProperty(value = "排序字段")
    private String orderField;

    @ApiModelProperty(value = "排序方式 true:升序 false：降序")
    private boolean isAsc;

    public Page<T> getPage() {
        Page<T> pageParam = new Page<>(page, limit);
        if (StringUtils.hasLength(orderField)) {
            OrderItem orderItem = new OrderItem();
            orderItem.setAsc(isAsc);
            orderItem.setColumn(orderField);
            pageParam.orders().add(orderItem);
        }
        return pageParam;

    }

    public QueryWrapper<T> getQueryModel() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (null != this.fields ) {
            this.fields.forEach(e -> {
                if (StringUtils.hasLength(e.getValue())){
                    switch (e.getQueryMethod()) {
                        case eq:
                            queryWrapper.eq(true, e.getName(), e.getValue());
                            break;
                        case like:
                            queryWrapper.like(true, e.getName(), e.getValue());
                    }
                }
            });
        }
        if (StringUtils.hasLength(orderField)) {
            queryWrapper.orderBy(true, isAsc, orderField);
        }
        return queryWrapper;
    }
}
