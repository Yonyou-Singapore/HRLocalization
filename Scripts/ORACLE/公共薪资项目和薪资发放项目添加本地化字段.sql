-- 公共薪资项目添加本地化/马来西亚字段
alter table wa_item add g_iproperty INT NULL;
alter table wa_item add my_isepf CHAR(1) NULL;
alter table wa_item add my_iseis CHAR(1) NULL;
alter table wa_item add my_issocso CHAR(1) NULL;
alter table wa_item add my_ispcb CHAR(1) NULL;
alter table wa_item add my_isnplrate CHAR(1) NULL;
alter table wa_item add my_isotrate CHAR(1) NULL;

-- 薪资发放项目添加本地化/马来西亚字段
alter table wa_classitem add g_iproperty INT NULL;
alter table wa_classitem add my_isepf CHAR(1) NULL;
alter table wa_classitem add my_iseis CHAR(1) NULL;
alter table wa_classitem add my_issocso CHAR(1) NULL;
alter table wa_classitem add my_ispcb CHAR(1) NULL;
alter table wa_classitem add my_isnplrate CHAR(1) NULL;
alter table wa_classitem add my_isotrate CHAR(1) NULL;

