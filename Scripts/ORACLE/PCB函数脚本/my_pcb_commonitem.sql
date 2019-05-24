drop table WA_SEALOCALCOMMON_ITEM;
CREATE TABLE "WA_SEALOCALCOMMON_ITEM" 
   (	"ITEM_CODE" VARCHAR2(30) DEFAULT '~', 
	"ITEM_NAME" VARCHAR2(50) DEFAULT '~', 
	"ITEMKEY" VARCHAR2(10), 
	"ITEM_CLASS" NUMBER(28,0), 
	"ITEM_TYPE" NUMBER(28,0), 
	"ITEM_LENGTH" NUMBER(28,0), 
	"ITEM_ISCLEARNEXTMONTH" CHAR(1) DEFAULT 'N'
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 262144 NEXT 262144 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "NNC_DATA01";
  
insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_bs', 'Basic Salary', 'f_500', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_ta', 'Total Allowance', 'f_501', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_tb', 'Total bonus', 'f_502', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_npl', 'NPL ', 'f_503', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_ot', 'Total OT', 'f_504', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_epf_er', 'EPF(employer)', 'f_505', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_epf_ee', 'EPF(employee)', 'f_506', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_eis_er', 'EIS(employer)', 'f_507', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_eis_ee', 'EIS(employee)', 'f_508', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_socso_eyer', 'SOCSO(employer)', 'f_509', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_socso_eyee', 'SOCSO(employee)', 'f_510', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_pcb', 'PCB', 'f_511', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_zakat', 'ZAKAT', 'f_512', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_pay', 'NET PAY', 'f_513', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_Y1', 'Taxable Income(Normal)', 'f_514', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_Yt', 'Taxable Income(Additional)', 'f_515', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_ti', 'Taxable Income', 'f_516', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_epf_nr', 'EPF(Normal remuneration)', 'f_517', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_epf_ar', 'EPF(Addtional remuneration)', 'f_518', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_K1', 'Normaml remuneration''s EPF and Other Approved Fund', 'f_519', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_Kt', 'Additional remuneration’s EPF', 'f_520', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_K1Kt', 'Taxable Deduction(K1+Kt)', 'f_521', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_LP1', 'Taxable Option Deduction', 'f_522', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_cf_epf_er', 'CF EPF(employer)', 'f_523', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_cf_epf_ee', 'CF EPF(employee)', 'f_524', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_cf_eis_er', 'CF EIS(employer)
', 'f_525', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_cf_eis_ee', 'CF EIS(employee)
', 'f_526', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_cf_socso_er
', 'CF SOCSO(employer)
', 'f_527', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_cf_socso_ee', 'CF SOCSO(employee)
', 'f_528', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_X', 'CF PCB', 'f_529', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_Y', 'CF Taxable Income', 'f_530', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_K', 'CF Taxable Deduction', 'f_531', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_LP', 'CF Taxable Option Deduction', 'f_532', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_Z', 'CF ZAKAT', 'f_533', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_ytd_epf_er', 'YTD EPF(employer)
', 'f_534', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_ytd_epf_ee', 'YTD EPF(employee)
', 'f_535', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_ytd eis_er
', 'YTD EIS(employer)', 'f_536', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_ytd eis_ee', 'YTD EIS(employee)', 'f_537', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_ytd_socso_er', 'YTD SOCSO(employer)
', 'f_538', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_ytd_socso_ee', 'YTD SOCSO(employee)
', 'f_539', 0, 2, 12, 'N');

insert into wa_sealocalcommon_item (ITEM_CODE, ITEM_NAME, ITEMKEY, ITEM_CLASS, ITEM_TYPE, ITEM_LENGTH, ITEM_ISCLEARNEXTMONTH)
values ('sealocal_ytd_pcb
', 'YTD PCB', 'f_540', 0, 2, 12, 'N');

commit;
