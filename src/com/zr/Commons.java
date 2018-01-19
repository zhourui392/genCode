package com.zr;

import cn.teleus.mas.controller.BaseController;
import com.teleus.common.util.Root;
import com.teleus.common.util.page.PageQuery;
import com.teleus.common.util.page.PageResult;
import com.teleus.mapper.base.BaseMapper;
import com.teleus.service.base.BaseService;
import com.teleus.service.base.impl.BaseServiceImpl;

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
