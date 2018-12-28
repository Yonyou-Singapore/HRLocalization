-- 为公共薪资项目和薪资发放项目添加汇总项功能相关字段
alter table wa_item add g_istotalitem CHAR(1) NULL;
alter table wa_item add g_totaltoitem VARCHAR(101) NULL;
alter table wa_classitem add g_istotalitem CHAR(1) NULL;
alter table wa_classitem add g_totaltoitem VARCHAR(101) NULL;

-- 插入参照模型
insert into bd_refinfo values (N'waitem_sealocal01', 0, NULL, NULL, NULL, N'hrwa', N'waitem', N'hrwa', N'Total Salary Item Reference Model', NULL, NULL, NULL, NULL, NULL, N'hrWAZ7100000000XAY10', N'nc.ui.wa.item.model.TotalWaItemRefModel', NULL, 0, NULL, NULL, NULL, 'hrlocal-000001', N'60130refinfo', '2018-10-11 15:11:10', NULL);
select * from bd_refinfo where pk_refinfo like 'hrWAZ7100000000XAY%'