package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.fcg.entity.SysTips;
import com.bitop.otcapi.fcg.mapper.SysTipsMapper;
import com.bitop.otcapi.fcg.service.SysTipsService;
import org.springframework.stereotype.Service;

@Service
public class SysTipsServiceImpl extends ServiceImpl<SysTipsMapper, SysTips> implements SysTipsService {
}
