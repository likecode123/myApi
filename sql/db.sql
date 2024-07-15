use   my_api;
-- 接口信息

create table if not exists my_api.`interface_info`
(
    `id` bigint not null auto_increment comment '主键' primary key,
    `name` varchar(256) not null comment '用户名',
    `description` varchar(256) null comment '描述',
    `url` varchar(512) not null comment '接口地址',
    `requestHeader` text null comment '请求头',
    `responseHeader` text null comment '响应头',
    `status` int default 0 not null comment '接口状态（0-关闭,1-开启）',
    `method` varchar(256) not null comment '请求类型',
    `userId` varchar(256) not null comment '创建人',
    `createTime` dateTime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime` datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `isDeleted` tinyint default 0 not null comment '是否删除(0-未删, 1-已删)'
    ) comment '接口信息';

insert into my_api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('戴弘文', '雷雨泽', 'www.norris-carroll.io', '严瑞霖', '曹果', 0, '曾浩宇', '283');
insert into my_api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('何思', '顾弘文', 'www.mohamed-kozey.com', '孟烨磊', '顾鑫磊', 0, '郭伟祺', '36');
insert into my_api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('蔡浩', '顾鹏煊', 'www.benny-hammes.net', '贾建辉', '沈黎昕', 0, '邵思', '738');
insert into my_api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('贾文昊', '梁弘文', 'www.alexander-muller.co', '谢健雄', '贺晟睿', 0, '许智辉', '421077831');
insert into my_api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('贾振家', '傅哲瀚', 'www.monte-ward.com', '谭烨霖', '余笑愚', 0, '蔡熠彤', '11190');
insert into my_api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('何君浩', '邓煜城', 'www.elvis-ernser.net', '彭胤祥', '姜鹏', 0, '李梓晨', '479825');
insert into my_api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('黎越彬', '崔伟宸', 'www.colleen-collier.net', '赖楷瑞', '傅展鹏', 0, '苏熠彤', '757714');
insert into my_api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('金昊焱', '沈涛', 'www.krystina-macgyver.biz', '卢建辉', '尹明哲', 0, '阎伟泽', '7936108');
insert into my_api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('田越彬', '任志泽', 'www.neely-wuckert.org', '吴越彬', '侯嘉懿', 0, '熊风华', '26');
insert into my_api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('秦语堂', '方智宸', 'www.matha-christiansen.biz', '彭致远', '郭弘文', 0, '钱鹏飞', '26');
insert into my_api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('陶鹤轩', '邵笑愚', 'www.solomon-abbott.co', '石彬', '廖梓晨', 0, '余天磊', '3447');
insert into my_api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('徐琪', '叶思淼', 'www.maire-dibbert.biz', '王哲瀚', '董旭尧', 0, '何鑫磊', '2');
insert into my_api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('邓鹏飞', '毛弘文', 'www.melisa-wisozk.co', '廖晓啸', '孙煜城', 0, '梁修洁', '4736131325');
insert into my_api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('朱果', '陈昊强', 'www.sadye-jenkins.info', '郝鸿煊', '杜烨磊', 0, '丁思远', '10585');
insert into my_api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('雷峻熙', '陆立果', 'www.lyle-murphy.biz', '邱潇然', '宋晋鹏', 0, '覃思源', '672');
insert into my_api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('熊烨伟', '刘皓轩', 'www.emory-yost.biz', '崔明轩', '冯正豪', 0, '潘擎苍', '6');
insert into my_api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('袁天磊', '徐熠彤', 'www.tyrell-flatley.biz', '陶鹏煊', '覃立果', 0, '唐煜城', '264800');
insert into my_api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('夏果', '曾子骞', 'www.linnea-barton.com', '张靖琪', '韦弘文', 0, '贺烨磊', '92835830');
insert into my_api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('马语堂', '白建辉', 'www.cory-johns.com', '黄修洁', '孔文轩', 0, '叶正豪', '1126');
insert into my_api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('韩鸿煊', '孔乐驹', 'www.mike-wilkinson.co', '高子默', '孟伟泽', 0, '陶晓博', '39');