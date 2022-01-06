package com.bitop.otcapi.fcg.entity.resp;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class BankCodeRespDto {

    @NotNull
    private String code;

    @NotNull
    private String name;
}
