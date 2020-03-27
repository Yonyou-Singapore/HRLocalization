-- 公共薪资项目添加本地化/马来西亚字段
alter table wa_item add g_iproperty INT NULL;
alter table wa_item add my_isepf_n CHAR(1) NULL;
alter table wa_item add my_isepf_a CHAR(1) NULL;
alter table wa_item add my_iseis CHAR(1) NULL;
alter table wa_item add my_issocso CHAR(1) NULL;
alter table wa_item add my_ispcb_n CHAR(1) NULL;
alter table wa_item add my_ispcb_a CHAR(1) NULL;
alter table wa_item add my_isnplrate CHAR(1) NULL;
alter table wa_item add my_isotrate CHAR(1) NULL;
alter table wa_item add(g_fixeddays number(28, 8), sg_brp_or_grp varchar2(50), sg_aw_or_ow varchar2(50));
-- 薪资发放项目添加本地化/马来西亚字段
alter table wa_classitem add g_iproperty INT NULL;
alter table wa_classitem add my_isepf_n CHAR(1) NULL;
alter table wa_classitem add my_isepf_a CHAR(1) NULL;
alter table wa_classitem add my_iseis CHAR(1) NULL;
alter table wa_classitem add my_issocso CHAR(1) NULL;
alter table wa_classitem add my_ispcb_n CHAR(1) NULL;
alter table wa_classitem add my_ispcb_a CHAR(1) NULL;
alter table wa_classitem add my_isnplrate CHAR(1) NULL;
alter table wa_classitem add my_isotrate CHAR(1) NULL;
alter table wa_classitem add (g_fixeddays number(28, 8));
--sg_brp_or_grp
alter table wa_classitem add (sg_brp_or_grp varchar2(50));
--sg_aw_or_ow
alter table wa_classitem add (sg_aw_or_ow varchar2(50));

