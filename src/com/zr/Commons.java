package com.zr;

import cn.teleus.mas.controller.BaseController;
import cn.teleus.common.util.Root;
import cn.teleus.common.util.page.PageQuery;
import cn.teleus.common.util.page.PageResult;
import cn.teleus.mapper.base.BaseMapper;
import cn.teleus.service.base.BaseService;
import cn.teleus.service.base.impl.BaseServiceImpl;

/**
 * Created by zhourui on 2017/2/16.
 */
public class Commons {
    public static String NAME_BASE_CONTROLLER = BaseController.class.getName();

    public static String NAME_BASE_MAPPER = BaseMapper.class.getName();

    public static String NAME_BASE_SERVICE = BaseService.class.getName();

    public static String NAME_BASE_SERVICEIMPL = BaseServiceImpl.class.getName();

    public static String NAME_ROOT = Root.class.getName();

    public static String NAME_PAGE_QUERY = PageQuery.class.getName();

    public static String NAME_PAGE_RESULT = PageResult.class.getName();
}
